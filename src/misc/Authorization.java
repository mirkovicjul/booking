package misc;

import java.security.Key;

import javax.servlet.http.HttpServletRequest;

import beans.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class Authorization {
	
	static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public static Boolean authorizeUser(HttpServletRequest request, UserRoleEnum[] roles) {
		String auth = request.getHeader("Authorization");
		System.out.println("Authorization: " + auth);
		if ((auth != null) && (auth.contains("Bearer "))) {
			String jwt = auth.substring(auth.indexOf("Bearer ") + 7);
			try {
			    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
			    if(checkRole(roles, (String)claims.getBody().get("role")))
			    	return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
		return false;
	}
	
	private static Boolean checkRole(UserRoleEnum[] roles, String role) {
		for(UserRoleEnum r : roles) {
	    	if(role.equals(r.toString()))
	    		return true;
	    }
		return false;
	}
	
	public static String getUserRole(HttpServletRequest request) {
		String auth = request.getHeader("Authorization");
		System.out.println("Authorization: " + auth);
		if ((auth != null) && (auth.contains("Bearer "))) {
			String jwt = auth.substring(auth.indexOf("Bearer ") + 7);
			try {
			    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
			    String role = (String) claims.getBody().get("role");
			    return role;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
		}
		return "";
	}
	
	public static String getUsername(HttpServletRequest request) {
		String auth = request.getHeader("Authorization");
		System.out.println("Authorization: " + auth);
		if ((auth != null) && (auth.contains("Bearer "))) {
			String jwt = auth.substring(auth.indexOf("Bearer ") + 7);
			try {
			    Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
			    String username = (String) claims.getBody().get("username");
			    return username;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
		}
		return "";
	}
	
	public static Key getKey() {
		return key;
	}

}
