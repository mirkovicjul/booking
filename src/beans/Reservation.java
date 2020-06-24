package beans;

public class Reservation {

	private Long id;
	private Apartment apartment;
	private User user;
	private Long startDate;
	private Long endDate;
	private Long price;
	private String message;
	private ReservationStatusEnum status;
		
	public Reservation() {
		
	}

	public Reservation(Long id, Apartment apartment, User user, Long startDate, Long endDate, Long price,
			String message, ReservationStatusEnum status) {
		super();
		this.id = id;
		this.apartment = apartment;
		this.user = user;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.message = message;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Apartment getApartment() {
		return apartment;
	}

	public void setApartment(Apartment apartment) {
		this.apartment = apartment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ReservationStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ReservationStatusEnum status) {
		this.status = status;
	}
	
}
