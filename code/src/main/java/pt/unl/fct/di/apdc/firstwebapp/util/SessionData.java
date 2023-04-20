package pt.unl.fct.di.apdc.firstwebapp.util;

public class SessionData {
	public String user_id_session;
	public String token;
	public String role;
	public String user_id_to_use;
	
	public SessionData() {}
	
	public SessionData(String user_id_to_use, String user_id_session, String token, String role) {
		this.token = token;
		this.user_id_session = user_id_session;
		this.role = role;
		this.user_id_to_use = user_id_to_use;
	}
}
