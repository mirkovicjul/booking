package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Apartment;
import beans.Reservation;
import beans.User;
import beans.UserGenderEnum;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import dao.ApartmentDAO;
import dao.ReservationDAO;
import dao.UserDAO;
import misc.Authorization;

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
			MsgResponse err = new MsgResponse(false, msg);
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
	
	@GET
	@Path("/account/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserAccountData(@Context HttpServletRequest request, @PathParam("username") String username) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST, UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) {
			if(Authorization.getUsername(request).equals(username)) {
				UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
				User user = dao.findByUsername(username);
				User userData = new User(user.getUsername(),"",user.getFirstName(), user.getLastName(), user.getGender(), user.getRole());
				return Response
						.status(Response.Status.OK)
						.entity(userData)
						.build();
			}			
		}
		String message = "You are not authorized to view this page.";
		return Response
				.status(Response.Status.FORBIDDEN)
				.entity(message)
				.build();
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@Context HttpServletRequest request) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) { 
			UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
			ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			ReservationDAO reservationDAO = (ReservationDAO) ctx.getAttribute("reservationDAO");
			Collection<User> allUsers = new ArrayList<User>();
			if(Authorization.getUserRole(request).equals("ADMIN")){			
				Collection<User> users = dao.findAll();			
				for(User u : users) {
					User newUser = new User(u.getUsername(), "", u.getFirstName(), u.getLastName(), u.getGender(), u.getRole());
					allUsers.add(newUser);
				}
			} else {
				Collection<Apartment> apartments = apartmentDAO.findByHost(Authorization.getUsername(request));
				List<String> allUsernames = apartments.stream()
				.flatMap(a -> reservationDAO.getReservationsByApartment(a.getId()).stream())
				.map(r -> r.getGuest())
				.distinct().collect(Collectors.toList());				
				
			
				allUsers = allUsernames.stream()
						.map(u -> Optional.ofNullable(dao.findByUsername(u)))
						.filter(u -> u.isPresent())
						.map(u -> u.get())
						.collect(Collectors.toList());			
			}
			return Response
				      .status(Response.Status.OK)
				      .entity(allUsers)
				      .build();
		}
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUserInfo(User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");	
		Boolean response = dao.updateUserInfo(ctx.getRealPath(""), user);
		MsgResponse res;
		if(response) {
			res = new MsgResponse(true, "Profile information successfully updated.");
		} else {
			res = new MsgResponse(false, "Something went wrong.");		
		}
		return Response
				.status(Response.Status.OK)
				.entity(res)
				.build();
	}
	
	@POST
	@Path("/updatePassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePassword(User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");		
		Boolean response = dao.updateUserPassword(ctx.getRealPath(""), user);
		MsgResponse res;
		if(response) {
			res = new MsgResponse(true, "Password successfully updated.");			
		} else {
			res = new MsgResponse(false, "Something went wrong.");		
		}
		return Response
				.status(Response.Status.OK)
				.entity(res)
				.build();
	}
	
}
