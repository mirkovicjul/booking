package beans.dto;

public class ErrorMessageResponse {
	
	private Boolean failed;
	private String message;
	
	
	public ErrorMessageResponse(Boolean failed, String message) {
		super();
		this.failed = failed;
		this.message = message;
	}
	public Boolean getFailed() {
		return failed;
	}
	public void setFailed(Boolean failed) {
		this.failed = failed;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	} 
	
}
