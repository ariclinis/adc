package pt.unl.fct.di.apdc.firstwebapp.util;

public class UserData {
	public String userid;
	public String password;
	public String pwd_confirmation;
	public String email;
	public String name;
	public String role= "USER";
	public String status= "INATIVO";
	
	// Optional informations
	public String profile;
	public String fix_number;
	public String phone;
	public String ocupation;
	public String work_place;
	//public AddressData address;
	public String street;
	public String location;
	public String postal_code;
	
	public String nif;
	//public String image;
	
	public UserData() {}
	
	public UserData(String userid, String email, String name, String password, String profile, 
			String phone, String fix_number, String ocupation, String work_place, String nif,
			String location, String postal_code, String street, String pwd_confirmation) {
		
		this.userid = userid;
		this.password = password;
		this.pwd_confirmation=pwd_confirmation;
		this.email = email;
		this.name = name;
		//Optional informations
		this.profile = profile;
		this.fix_number = fix_number;
		this.phone = phone;
		this.ocupation = ocupation;
		this.work_place = work_place;
		this.nif = nif;
			// adress
			this.location = location;
			this.postal_code = postal_code;
			this.street = street;
	}
	
	public UserData(String userid, String email, String name, String password, String profile, 
			String phone, String fix_number, String ocupation, String work_place, String nif,
			String location, String postal_code, String street, String role,String pwd_confirmation) {
		
		this.userid = userid;
		this.password = password;
		this.pwd_confirmation=pwd_confirmation;
		this.email = email;
		this.name = name;
		//Optional informations
		this.profile = profile;
		this.fix_number = fix_number;
		this.phone = phone;
		this.ocupation = ocupation;
		this.work_place = work_place;
		this.nif = nif;
			// adress
			this.location = location;
			this.postal_code = postal_code;
			this.street = street;
		this.role = role;
	}
	
	//Update
	public UserData(String userid, String email, String name, String profile, 
			String phone, String fix_number, String ocupation, String work_place, String nif,
			String location, String postal_code, String street) {
		
		this.userid = userid;

		this.email = email;
		this.name = name;
		//Optional informations
		this.profile = profile;
		this.fix_number = fix_number;
		this.phone = phone;
		this.ocupation = ocupation;
		this.work_place = work_place;
		this.nif = nif;
			// adress
			this.location = location;
			this.postal_code = postal_code;
			this.street = street;
	}
	//Update PWA
		public UserData(String email, String name, String profile, 
				String phone, String fix_number, String ocupation, String work_place, String nif,
				String location, String postal_code, String street) {
			
			this.email = email;
			this.name = name;
			//Optional informations
			this.profile = profile;
			this.fix_number = fix_number;
			this.phone = phone;
			this.ocupation = ocupation;
			this.work_place = work_place;
			this.nif = nif;
				// adress
				this.location = location;
				this.postal_code = postal_code;
				this.street = street;
		}

	
	public boolean validRegistration() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean validAddress() {
		// TODO Auto-generated method stub
		return true;
	}
}
