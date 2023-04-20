package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.PasswordUpdate;
import pt.unl.fct.di.apdc.firstwebapp.util.SessionData;
import pt.unl.fct.di.apdc.firstwebapp.util.UserData;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
public class UserResource {
	/**
	 * Gera os logs da classe LoginResource
	 * **/
	private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
	private final Gson g = new Gson();
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public UserResource() {} //nothing to be done here @GET

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response registerUser(UserData data) {
		LOG.fine("Attempt to user: "+data.name);
		
		if(data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.pwd_confirmation.equals(data.password)) {
			return Response.status(Status.BAD_REQUEST).entity("The password needs to be the same").build();
		}
		
		Transaction tran = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userid);
			Key addressKey = datastore.allocateId(
					datastore.newKeyFactory()
					.addAncestor(PathElement.of("User", data.userid))
					.setKind("Address").newKey()
					);
			Key profileKey = datastore.allocateId(
					datastore.newKeyFactory()
					.addAncestor(PathElement.of("User", data.userid))
					.setKind("Profile").newKey()
					);
			Entity user = tran.get(userKey);
			
			if(user!=null) {
				tran.rollback();
				return Response.status(Status.BAD_REQUEST).entity("There is a user with same Id").build();
			}
			
				user = Entity.newBuilder(userKey)
						.set("email", data.email)
						.set("full_name", data.name)
						.set("password", DigestUtils.sha3_512Hex(data.password))
						.set("id", data.userid)
						.set("role", data.role)
						.set("status", data.status)
						.build();
				
				Entity addressUser = Entity.newBuilder(addressKey)
									.set("street", data.street)
									.set("location", data.location)
									.set("postal_code", data.postal_code)
									.build();
				
				Entity profileUser = Entity.newBuilder(profileKey)
									.set("profile", data.profile)
									.set("nif", data.nif)
									.set("fix_number", data.fix_number)
									.set("ocupation", data.ocupation)
									.set("work_space", data.work_place)
									.build();
				tran.add(user,addressUser,profileUser);
				tran.commit();
				return Response.ok("User Created").build();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			if (tran.isActive()) {
				tran.rollback();
			}
		}
		
		return Response.status(Status.FORBIDDEN).entity("Somethings wrong.").build();
		
	}
	
	
	
	@DELETE
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response deleteUser(SessionData sessionData) {
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(sessionData.user_id_session);
		Key userToUseKey = datastore.newKeyFactory().setKind("User").newKey(sessionData.user_id_to_use);

		boolean permission =false;
		
		Transaction tran = datastore.newTransaction();
		try {
			Entity user = datastore.get(userKey);
			Entity user_to_use = datastore.get(userToUseKey);
			permission = checkPermission(user.getString("role"),user_to_use, user.getKey());
			if(user != null && permission) {
				
				Query<Entity> queryAddress = Query.newEntityQueryBuilder()
						        .setKind("Address")
						        .setFilter(
						            PropertyFilter.hasAncestor(userKey)
						            )
						        .build();
				Query<Entity> queryProfile = Query.newEntityQueryBuilder()
					        .setKind("Profile")
					        .setFilter(
					            PropertyFilter.hasAncestor(userKey)
					            )
					        .build();
				
				QueryResults<Entity> address = datastore.run(queryAddress);
				QueryResults<Entity> profile = datastore.run(queryProfile);

				while(address.hasNext()) {
					Entity a = address.next();
					datastore.delete(a.getKey());
				}
				while(profile.hasNext()) {
					Entity a = profile.next();
					datastore.delete(a.getKey());
				}
				datastore.delete(userKey);
				
				tran.commit();
				return Response.ok("User deleted").build();
			}else if(!permission) {
				return Response.status(Status.FORBIDDEN).entity("User dont have permission").build();
			}
			
			return Response.status(Status.FORBIDDEN).entity("User dont deleted.").build();
			
			
		} catch (Exception e) {
			// TODO: handle exception
			if (tran.isActive()) {
				tran.rollback();
			}
		}
		
		
		return Response.status(Status.FORBIDDEN).entity("Somethings wrong.").build();
	}
	
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response updateUserData(UserData data) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userid);
		
		Transaction tran = datastore.newTransaction();

		try {
			Entity user = datastore.get(userKey);
			Entity userEntity = Entity.newBuilder(user)
					.set("email", data.email)
					.set("full_name", data.name)
					.set("role", data.role)
					.set("status", data.status)
					.build();
			
			Query<Entity> queryAddress = Query.newEntityQueryBuilder()
			        .setKind("Address")
			        .setFilter(
			            PropertyFilter.hasAncestor(userKey)
			            )
			        .build();
			
			Query<Entity> queryProfile = Query.newEntityQueryBuilder()
			        .setKind("Profile")
			        .setFilter(
			            PropertyFilter.hasAncestor(userKey)
			            )
			        .build();
			
			QueryResults<Entity> address = datastore.run(queryAddress);
			QueryResults<Entity> profile = datastore.run(queryProfile);

			if(address.hasNext()) {
				Entity add = Entity.newBuilder(address.next())
								.set("location", data.location)
								.set("postal_code", data.postal_code)
								.set("street", data.street)
								.build();
				datastore.update(add);
			}
			if(profile.hasNext()) {
				Entity p = Entity.newBuilder(profile.next())
							.set("fix_number", data.fix_number)
							.set("nif", data.nif)
							.set("ocupation", data.ocupation)
							.set("profile", data.profile)
							.set("work_space", data.work_place)
							.build();
				datastore.update(p);
			}
			
			datastore.update(userEntity);
			tran.commit();
			return Response.ok("User updated").build();

		} catch (Exception e) {
			// TODO: handle exception
			if (tran.isActive()) {
				tran.rollback();
			}
		}
		
		return Response.status(Status.FORBIDDEN).entity("Somethings wrong. role:").build();
	}
	
	@PUT
	@Path("/update_password")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response updateUserPassword(PasswordUpdate data) {
		
		if(!data.new_pwa.equals(data.check_pwa)) {
			return Response.status(Status.FORBIDDEN).entity("The passwords is different").build();
		}
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userid);
		Transaction tran = datastore.newTransaction();
		try {
			
			Entity user = datastore.get(userKey);
			if(user!=null) {
				String pwa = user.getString("password");
				String pwa_old = DigestUtils.sha3_512Hex(data.old_pwa);
				if(pwa.equals(pwa_old)) {
					Entity userEntity = Entity.newBuilder(user)
							.set("password", DigestUtils.sha3_512Hex(data.new_pwa))
							.build();
					datastore.update(userEntity);
					tran.commit();
					return Response.ok("Password Updated").build();

				}else {
					tran.rollback();
					return Response.status(Status.FORBIDDEN).entity("The current password is wrong.").build();
				}
				
			}else {
				tran.rollback();
				return Response.status(Status.FORBIDDEN).entity("User don't exist").build();

			}
		} catch (Exception e) {
			// TODO: handle exception
			if (tran.isActive()) {
				tran.rollback();
			}
		}
		return Response.status(Status.FORBIDDEN).entity("Somethings wrong. role:").build();
	}
	
	@GET
	@Path("/{userid}")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
	public Response getUser(@PathParam("userid") String userid) {
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(userid);
		try {
			Entity user = datastore.get(userKey);
			if(user!=null) {
				UserData u = new UserData();
				u.email = user.getString("email");
				u.role = user.getString("role");
				u.name = user.getString("full_name");
				u.email = user.getString("email");
				u.status = user.getString("status");
				
				return Response.ok(g.toJson(u)).build();
			}
			return Response.status(Status.FORBIDDEN).entity("Somethings wrong. role:").build();
	
			
		}catch (Exception e) {
			return Response.status(Status.FORBIDDEN).entity("Somethings wrong. role:").build();
		}
		
	}
	
	private boolean checkPermission(String role, Entity user, Key userIdSession) {
		boolean permission = false;
		switch (role) {
			case "USER":
				if(user.getKey().equals(userIdSession)) {
					permission = true;
				}
				break;
			case "GBO":
				if(user.getString("role").equals("USER")) {
					permission = true;
				}
				break;
			case "GA":
				if(user.getString("role").equals("GBO") || user.getString("role").equals("USER")) {
					permission = true;
				}
				break;
			case "GS":
				if(user.getString("role").equals("GBO") || user.getString("role").equals("USER")) {
					permission = true;
				}
				break;
	
			default:
				permission = false;
		}
		return permission;
	}
}
