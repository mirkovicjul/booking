package beans;

public class DisabledDate {

	private Long id;
	private Long apartmentId;
	private Long startDate;
	private Long endDate;
	
	public DisabledDate() {
		
	}
	
	public DisabledDate(Long id, Long apartmentId, Long startDate, Long endDate) {
		super();
		this.id = id;
		this.apartmentId = apartmentId;
		this.startDate = startDate;
		this.endDate = endDate;
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
	
}
