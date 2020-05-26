package services;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.User;
import beans.dto.LoginResponse;
import dao.UserDAO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Path("/login")
public class LoginService {
	
	@Context
	ServletContext ctx;

	@PostConstruct
	public void init() {
		if (ctx.getAttribute("userDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("userDAO", new UserDAO(contextPath));
		}
	}
	
	static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public LoginResponse login(User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		User foundUser = dao.findByUsernamePassword(user.getUsername(), user.getPassword());
		LoginResponse res;
		if(foundUser != null) {
			Map<String, String> claims = new HashMap<String, String>();
			claims.put("username", foundUser.getUsername());
			claims.put("role", foundUser.getRole().toString());
			String jws = Jwts.builder().setClaims(claims).setExpiration(new Date(new Date().getTime() + 1000*43200L)).setIssuedAt(new Date()).signWith(key).compact();	
			res = new LoginResponse(true, foundUser.getUsername(), foundUser.getRole().toString(), jws);
			return res;
		}
		res = new LoginResponse();
		res.setSuccess(false);
		return res;
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getUsers() {
		
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		return dao.findAll();
	}
	
}
