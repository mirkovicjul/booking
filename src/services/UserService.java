package services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import beans.UserGenderEnum;
import beans.UserRoleEnum;
import beans.dto.ErrorMessageResponse;
import dao.UserDAO;

@Path("/user")
public class UserService {
	
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
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		if(dao.usernameAvailable(user.getUsername())) {
			user.setRole(UserRoleEnum.GUEST);
			User res = dao.save(ctx.getRealPath(""), user);
			if(res != null)
				return Response
				      .status(Response.Status.OK)
				      .entity(res)
				      .build();
			else
				return Response
			      .status(Response.Status.OK)
			      .entity("Something went wrong. Please try again.")
			      .build();
		} else {
			String msg = "Username already exists.";
			ErrorMessageResponse err = new ErrorMessageResponse(true, msg);
			return Response
			      .status(Response.Status.OK)
			      .entity(err)
			      .build();
		}
	}
	
	@GET
	@Path("/genders")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGenders() {
		List<String> enumNames = Stream.of(UserGenderEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
		return Response
			      .status(Response.Status.OK)
			      .entity(enumNames)
			      .build();
	}
	
}
