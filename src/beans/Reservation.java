package beans;

public class Reservation {

	private Long id;
	private Long apartmentId;
	private String guest;
	private Long startDate;
	private Long endDate;
	private Long price;
	private String message;
	private ReservationStatusEnum status;
		
	public Reservation() {
		
	}

	public Reservation(Long id, Long apartmentId, String guest, Long startDate, Long endDate, Long price,
			String message, ReservationStatusEnum status) {
		super();
		this.id = id;
		this.apartmentId = apartmentId;
		this.guest = guest;
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

	public Long getApartmentId() {
		return apartmentId;
	}

	public void setApartmentId(Long apartmentId) {
		this.apartmentId = apartmentId;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
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
