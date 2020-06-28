package beans.dto;

import beans.ReservationStatusEnum;

public class NewReservationStatus {

	private Long reservationId;
	private String user;
	private ReservationStatusEnum status;
	
	public NewReservationStatus() {
		super();
	}

	public NewReservationStatus(Long reservationId, String user, ReservationStatusEnum status) {
		super();
		this.reservationId = reservationId;
		this.user = user;
		this.status = status;
	}
	
	public Long getReservationId() {
		return reservationId;
	}
	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public ReservationStatusEnum getStatus() {
		return status;
	}
	public void setStatus(ReservationStatusEnum status) {
		this.status = status;
	}
	
}
