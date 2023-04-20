package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
public class LoginResource {

	/**
	 * Gera os logs da classe LoginResource
	 * **/
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Attempt to login user: "+data.userid);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userid);
		Transaction tran = datastore.newTransaction();
		try {
			Entity user = tran.get(userKey);
			if(user==null) {
				return Response.status(Status.FORBIDDEN).entity("User don't exist").build();
			}
			Key tokenLogin = datastore.allocateId(
					datastore.newKeyFactory()
					.addAncestor(PathElement.of("User", data.userid))
					.setKind("Token").newKey()
					);
			String pwd = user.getString("password");
			if(pwd.equals(DigestUtils.sha3_512Hex(data.password))) {
				AuthToken at = new AuthToken(data.userid);
				Entity tokenEntity = Entity.newBuilder(tokenLogin)
						.set("token", at.tokenID)
						.set("created", at.creationData)
						.set("expiration", at.expirationData)
						.build();
				
				Query<Entity> queryToken = Query.newEntityQueryBuilder()
				        .setKind("Token")
				        .setFilter(
				            PropertyFilter.hasAncestor(userKey)
				            )
				        .build();
				QueryResults<Entity> tokens = datastore.run(queryToken);
				if(!tokens.hasNext()) {
					tran.add(tokenEntity);
				}
				tran.commit();
				return Response.ok(g.toJson(at)).build();
			}else {
				tran.rollback();
				return Response.status(Status.FORBIDDEN).entity("Password wrong").build();
			}
		}catch (Exception e) {
			// TODO: handle exception
			if(tran.isActive()) {
				tran.rollback();
			}
			return Response.status(Status.FORBIDDEN).entity("Somethings wrong. role:").build();
		}
		
	}
	
	@GET
	@Path("/{username}")
	public Response vx(@PathParam("username") String username) {
		if(username.equals("jleitao")) {
			return Response.ok().entity(g.toJson(false)).build();
		} else {
			return Response.ok().entity(g.toJson(true)).build();
		}
	}
}
