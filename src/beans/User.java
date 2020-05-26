package beans;

public class User {
	
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private UserGenderEnum gender;
	private UserRoleEnum role;
	
	public User() {}
	
	public User(String username, String password, String firstName, String lastName, UserGenderEnum gender,
			UserRoleEnum role) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public UserGenderEnum getGender() {
		return gender;
	}
	
	public void setGender(UserGenderEnum gender) {
		this.gender = gender;
	}

	public UserRoleEnum getRole() {
		return role;
	}

	public void setRole(UserRoleEnum role) {
		this.role = role;
	}
	
}
