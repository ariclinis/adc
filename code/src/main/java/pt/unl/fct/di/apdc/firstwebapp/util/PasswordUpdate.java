package pt.unl.fct.di.apdc.firstwebapp.util;

public class PasswordUpdate {
	public String userid;
	public String old_pwa;
	public String new_pwa;
	public String check_pwa;
	
	public PasswordUpdate() {}
	public PasswordUpdate(String userid,String old_pwa, String new_pwa, String check_pwa) {
		this.userid = userid;
		this.old_pwa = old_pwa;
		this.new_pwa = new_pwa;
		this.check_pwa= check_pwa;
	}
}
