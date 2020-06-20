package beans;

public class Comment {
	
	private Long id;
	private Long apartmentId;
	private User commentator;
	private String comment;
	private int rating;
	private Boolean approved;
	
	public Comment(Long id, Long apartmentId, User commentator, String comment, int rating, Boolean approved) {
		super();
		this.id = id;
		this.apartmentId = apartmentId;
		this.commentator = commentator;
		this.comment = comment;		
		this.rating = rating;
		this.approved = approved;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public User getCommentator() {
		return commentator;
	}
	
	public void setCommentator(User commentator) {
		this.commentator = commentator;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Long getApartmentId() {
		return apartmentId;
	}
	
	public void setApartmentId(Long apartmentId) {
		this.apartmentId = apartmentId;
	}
	
	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	
}
