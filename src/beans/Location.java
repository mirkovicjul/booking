package beans;

public class Location {

	private Long id;
	private String latitude;
	private String longitude;	
	private Address address;
	
	public Location() {
		
	}
	
	public Location(Long id, String latitude, String longitude, Address address) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}
