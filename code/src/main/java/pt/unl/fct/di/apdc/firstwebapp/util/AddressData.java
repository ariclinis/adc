package pt.unl.fct.di.apdc.firstwebapp.util;

public class AddressData {
	public String street;
	public String location;
	public String postal_code;
	
	public AddressData() {
		
	}
	public AddressData(String street, String location, String postal_code) {
		this.street = street;
		this.location = location;
		this.postal_code = postal_code;
	}
}
