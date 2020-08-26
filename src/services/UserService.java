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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Apartment;
import beans.User;
import beans.UserGenderEnum;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import dao.AmenityDAO;
import dao.ApartmentDAO;
import dao.CommentDAO;
import dao.LocationDAO;
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
		if (ctx.getAttribute("locationDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("locationDAO", new LocationDAO(contextPath));
		}
		if (ctx.getAttribute("commentDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("commentDAO", new CommentDAO(contextPath, (UserDAO) ctx.getAttribute("userDAO")));
		}
		if (ctx.getAttribute("amenityDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("amenityDAO", new AmenityDAO(contextPath));
		}
		if (ctx.getAttribute("reservationDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("reservationDAO", new ReservationDAO(contextPath));
		}
		if (ctx.getAttribute("apartmentDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("apartmentDAO", new ApartmentDAO(contextPath, (LocationDAO) ctx.getAttribute("locationDAO"), (UserDAO) ctx.getAttribute("userDAO"), (CommentDAO) ctx.getAttribute("commentDAO"), (AmenityDAO) ctx.getAttribute("amenityDAO"), (ReservationDAO) ctx.getAttribute("reservationDAO")));
		}
	
	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(@Context HttpServletRequest request, User user) {
		UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
		MsgResponse response;
		Response.Status status;
		if(dao.usernameAvailable(user.getUsername())) {
			User newUser = dao.save(ctx.getRealPath(""), user);
			if(newUser != null) {
				if(Authorization.getUserRole(request).equals("ADMIN")) {
					response = new MsgResponse(true, "New host added.");
					status = Response.Status.OK;
				} else {
					return Response
					      .status(Response.Status.OK)
					      .entity(newUser)
					      .build();
				}
			} else {
				response = new MsgResponse(false, "Something went wrong. Please try again.");
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		} else {	
			response = new MsgResponse(false, "Username already exists.");
			status = Response.Status.OK;
		}
		return Response
		      .status(status)
		      .entity(response)
		      .build();
	
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
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoles() {
		List<String> enumNames = Stream.of(UserRoleEnum.values())
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
						.map(u -> new User(u.getUsername(), "", u.getFirstName(), u.getLastName(), u.getGender(), u.getRole()))
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
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchUsers(@Context HttpServletRequest request, 
			@QueryParam("username") String username,
			@QueryParam("role") String role,
			@QueryParam("gender") String gender) {
		
		UserRoleEnum[] roles = {UserRoleEnum.HOST, UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) {				
			Response res = this.getAllUsers(request);
			Collection<User> allUsers = (Collection<User>) res.getEntity();
			
			if(username != null) {
				allUsers = allUsers.stream()
									.filter(u -> u.getUsername().toLowerCase().contains(username.toLowerCase()))
									.collect(Collectors.toList());
			}
			if(gender != null) {
				allUsers = allUsers.stream()
									.filter(u -> u.getGender().equals(UserGenderEnum.valueOf(gender)))
									.collect(Collectors.toList());
			}
			if(role != null) {
				allUsers = allUsers.stream()
									.filter(u -> u.getRole().equals(UserRoleEnum.valueOf(role)))
									.collect(Collectors.toList());
			}
			
			return Response
				      .status(Response.Status.OK)
				      .entity(allUsers)
				      .build();
		}
		MsgResponse res = new MsgResponse(false, "You are not authorized to see this page.");
		return Response
				.status(Response.Status.FORBIDDEN)
				.entity(res)
				.build();
	}
}
