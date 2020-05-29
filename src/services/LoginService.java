package services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import beans.UserRoleEnum;
import beans.dto.LoginResponse;
import dao.UserDAO;
import io.jsonwebtoken.Jwts;
import misc.Authorization;

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
	
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		User foundUser = dao.findByUsernamePassword(user.getUsername(), user.getPassword());
		LoginResponse res;
		if(foundUser != null) {
			Map<String, String> claims = new HashMap<String, String>();
			claims.put("username", foundUser.getUsername());
			claims.put("role", foundUser.getRole().toString());
			String jws = Jwts.builder().setClaims(claims).setExpiration(new Date(new Date().getTime() + 1000*43200L)).setIssuedAt(new Date()).signWith(Authorization.getKey()).compact();	
			res = new LoginResponse(true, foundUser.getUsername(), foundUser.getRole().toString(), jws);
			return Response
				      .status(Response.Status.OK)
				      .entity(res)
				      .build();
		}
		res = new LoginResponse();
		res.setSuccess(false);
		return Response
			      .status(Response.Status.OK)
			      .entity(res)
			      .build();
	}
	
	@GET
	@Path("/testAuthorization")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@Context HttpServletRequest request) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) {
			UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
			return Response
					.status(Response.Status.OK)
					.entity(dao.findAll())
					.build();
		} else {
			String message = "You are not authorized to view this page.";
		    return Response
		      .status(Response.Status.FORBIDDEN)
		      .entity(message)
		      .build();
		}
	}

}
