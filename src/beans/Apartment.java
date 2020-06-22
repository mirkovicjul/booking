package beans;

import java.util.List;

public class Apartment {
	
	private Long id;
	private String name;
	private ApartmentTypeEnum apartmentType;
	private int numberOfRooms;
	private int capacity;
	private Location location;
	private List<DisabledDate> disabledDates;
	private User host;
	private Long price;
	private String checkIn;
	private String checkOut;
	private Boolean active;
	private List<Amenity> amenities;
	private List<Comment> comments;
	private Boolean deleted;
	
	public Apartment() {
		
	}
	
	public Apartment(Long id, String name, ApartmentTypeEnum apartmentType, int numberOfRooms, int capacity, Location location,
			User host, Long price, String checkIn, String checkOut, Boolean active, List<Amenity> amenities, List<Comment> comments, Boolean deleted) {
		super();
		this.id = id;
		this.name = name;
		this.apartmentType = apartmentType;
		this.numberOfRooms = numberOfRooms;
		this.capacity = capacity;
		this.location = location;
		this.host = host;
		this.price = price;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.active = active;
		this.amenities = amenities;
		this.comments = comments;
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ApartmentTypeEnum getApartmentType() {
		return apartmentType;
	}

	public void setApartmentType(ApartmentTypeEnum apartmentType) {
		this.apartmentType = apartmentType;
	}

	public int getNumberOfRooms() {
		return numberOfRooms;
	}

	public void setNumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<DisabledDate> getDisabledDates() {
		return disabledDates;
	}

	public void setDisabledDates(List<DisabledDate> disabledDates) {
		this.disabledDates = disabledDates;
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(String checkIn) {
		this.checkIn = checkIn;
	}

	public String getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(String checkOut) {
		this.checkOut = checkOut;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Amenity> getAmenities() {
		return amenities;
	}

	public void setAmenities(List<Amenity> amenities) {
		this.amenities = amenities;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

}
