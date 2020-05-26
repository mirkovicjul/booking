package beans.dto;

public class LoginResponse {
	
	private Boolean success;
	private String username;
	private String role;
	private String jwt;
	
	public LoginResponse() {}

	public LoginResponse(Boolean success, String username, String role, String jwt) {
		super();
		this.success = success;
		this.username = username;
		this.role = role;
		this.jwt = jwt;
	}
	
	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	
}
