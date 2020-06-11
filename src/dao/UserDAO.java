package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import beans.User;
import beans.UserGenderEnum;
import beans.UserRoleEnum;

public class UserDAO {
	
	private Map<String, User> users = new HashMap<>();

	public UserDAO() {
		
	}
	
	public UserDAO(String contextPath) {
		loadUsers(contextPath);
	}
	
	public Collection<User> findAll() {
		return users.values();
	}
	
	public User save(String contextPath, User user) {
		FileWriter myWriter;
		String userCsv = user.getUsername() + ";" + user.getPassword() + ";" + user.getFirstName() + ";" + user.getLastName()
		 		+ ";" + user.getGender().toString() + ";" + user.getRole().toString();
		try {
			System.out.println(userCsv);
			FileWriter fw = new FileWriter(contextPath + "users.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw);
		    out.println(userCsv);
		    out.close();
		    loadUsers(contextPath);
		    return user;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	      
	}
	
	public User findByUsernamePassword(String username, String password) {
		if (!users.containsKey(username)) {
			return null;
		}
		User user = users.get(username);
		if (!user.getPassword().equals(password)) {
			return null;
		}
		return user;
	}
	
	public Boolean usernameAvailable(String username) {
		if(!users.containsKey(username))
			return true;
		else
			return false;
	}
	
	
	private void loadUsers(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/users.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String username = st.nextToken().trim();
					String password = st.nextToken().trim();
					String firstName = st.nextToken().trim();
					String lastName = st.nextToken().trim();
					String gender = st.nextToken().trim();
					String role = st.nextToken().trim();
					users.put(username, new User(username, password, firstName, lastName, UserGenderEnum.valueOf(gender), UserRoleEnum.valueOf(role)));
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
}
