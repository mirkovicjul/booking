package beans.dto;

public class CommentStatus {

	private Long commentId;
	private Boolean approved;
	
	public CommentStatus() {
		super();
	}
	
	public CommentStatus(Long commentId, Boolean approved) {
		super();
		this.commentId = commentId;
		this.approved = approved;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	
}
