package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import beans.ApartmentTypeEnum;
import beans.Comment;
import beans.DisabledDate;
import beans.User;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import dao.AmenityDAO;
import dao.ApartmentDAO;
import dao.CommentDAO;
import dao.LocationDAO;
import dao.UserDAO;
import misc.Authorization;

@Path("/apartment")
public class ApartmentService {

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
		if (ctx.getAttribute("apartmentDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("apartmentDAO", new ApartmentDAO(contextPath, (LocationDAO) ctx.getAttribute("locationDAO"), (UserDAO) ctx.getAttribute("userDAO"), (CommentDAO) ctx.getAttribute("commentDAO"), (AmenityDAO) ctx.getAttribute("amenityDAO")));
		}
		
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllApartments(@Context HttpServletRequest request) {
		String role = Authorization.getUserRole(request);
		System.out.println(role);
		if(role != null) {
			if(role.equals("ADMIN")) {
				ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
				Collection<Apartment> apartments = dao.findAll();
				return Response
					      .status(Response.Status.OK)
					      .entity(apartments)
					      .build();
			} else if(role.equals("HOST")) {
				String username = Authorization.getUsername(request);
				ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
				Collection<Apartment> apartments = dao.findAll();
				Collection<Apartment> myApartments = new ArrayList<Apartment>();
				if(username != null) {
					for(Apartment a : apartments) {
						if(a.getHost().getUsername().equals(username) && a.getActive()) {
							myApartments.add(a);
						}
					}
				}
				return Response
					      .status(Response.Status.OK)
					      .entity(myApartments)
					      .build();
			}
		}
		
		ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
		Collection<Apartment> apartments = dao.findAll();
		Collection<Apartment> activeApartments = new ArrayList<Apartment>();
		for(Apartment a : apartments) {
			if(a.getActive()) {
				activeApartments.add(a);
			}
		}
		return Response
			      .status(Response.Status.OK)
			      .entity(activeApartments)
			      .build();
	}
	
	@GET
	@Path("/inactive")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMyInactiveApartments(@Context HttpServletRequest request) {
		UserRoleEnum[] roles = {UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) {
			ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			String username = Authorization.getUsername(request);
			Collection<Apartment> apartments = dao.findAll();
			Collection<Apartment> myInactiveApartments = new ArrayList<Apartment>();
			if(username != null) {
				for(Apartment a : apartments) {
					if(a.getHost().getUsername().equals(username) && !a.getActive()) {
						myInactiveApartments.add(a);
					}
				}
			}
			return Response
				      .status(Response.Status.OK)
				      .entity(myInactiveApartments)
				      .build();
			
		} else {
			return Response
				      .status(Response.Status.FORBIDDEN)
				      .build();
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApartment(@Context HttpServletRequest request, @PathParam("id") Long id) {
			ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			Apartment apartment = dao.findById(id);
			if(!apartment.getActive()) {
				if(Authorization.getUserRole(request).equals("ADMIN") || (Authorization.getUserRole(request).equals("HOST")
						&& Authorization.getUsername(request).equals(apartment.getHost().getUsername()))){
					return Response
						      .status(Response.Status.OK)
						      .entity(apartment)
						      .build();
				
				} else {
					return Response
						      .status(Response.Status.FORBIDDEN)
						      .entity(null)
						      .build();
				}
			}
			return Response
				      .status(Response.Status.OK)
				      .entity(apartment)
				      .build();
			
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateApartmentInfo(@Context HttpServletRequest request, Apartment apartment) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) { 
			if(Authorization.getUsername(request).equals(apartment.getHost().getUsername()) 
					|| Authorization.getUserRole(request).equals("ADMIN")) {
				ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");	
				if(dao.findById(apartment.getId())==null) {
					MsgResponse res = new MsgResponse(false, "Apartment doesn't exist.");
					return Response
							.status(Response.Status.NO_CONTENT)
							.entity(res)
							.build();
				}
				Boolean response = dao.updateApartmentInfo(ctx.getRealPath(""), apartment);
				if(response) {
					MsgResponse res = new MsgResponse(true, "Apartment info successfully updated.");
					return Response
							.status(Response.Status.OK)
							.entity(res)
							.build();
				} else {
					MsgResponse res = new MsgResponse(false, "Something went wrong.");
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity(res)
							.build();
				}
			} 
		}
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewApartment(@Context HttpServletRequest request, Apartment apartment) {
		UserRoleEnum[] roles = {UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) {
			String username = Authorization.getUsername(request);
			UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
			User user = userDAO.findByUsername(username);
			User host = new User(user.getUsername(),"",user.getFirstName(), user.getLastName(), user.getGender(), user.getRole());
			apartment.setHost(host);
			apartment.setActive(false);
			apartment.setDeleted(false);
			ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");	
			Boolean response = dao.save(ctx.getRealPath(""), apartment);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Apartment successfully created.");
				return Response
						.status(Response.Status.OK)
						.entity(res)
						.build();
			} else {
				MsgResponse res = new MsgResponse(false, "Something went wrong.");
				return Response
						.status(Response.Status.BAD_REQUEST)
						.entity(res)
						.build();
			}
		}
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .build();
	}
	
	@POST
	@Path("/disable")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addDisabledDate(@Context HttpServletRequest request, DisabledDate disabledDate) {
		UserRoleEnum[] roles = {UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) {	
			ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");	
			Boolean response = dao.addDisabledDate(ctx.getRealPath(""), disabledDate);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Apartment successfully disabled for selected dates.");
				return Response
						.status(Response.Status.OK)
						.entity(res)
						.build();
			} else {
				MsgResponse res = new MsgResponse(false, "Something went wrong.");
				return Response
						.status(Response.Status.BAD_REQUEST)
						.entity(res)
						.build();
			}
		}
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .build();
	}
	
	@POST
	@Path("/comment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComment(@Context HttpServletRequest request, Comment comment) {
		UserRoleEnum[] roles = {UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) {
			CommentDAO commentDAO = (CommentDAO) ctx.getAttribute("commentDAO");
			Boolean commentSaved = commentDAO.save(ctx.getRealPath(""), comment);
			if(commentSaved) {
				ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
				Apartment apartment = apartmentDAO.findById(comment.getApartmentId());
				apartment.setComments(commentDAO.findByApartment(apartment.getId()));
				MsgResponse res = new MsgResponse(true, "Comment successfully created.");
				return Response
						.status(Response.Status.OK)
						.entity(res)
						.build();
			} else {
				MsgResponse res = new MsgResponse(false, "Something went wrong.");
				return Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(res)
						.build();
			}
		}
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .build();
	}
	
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApartmentTypes(@Context HttpServletRequest request, @PathParam("id") Long id) {
		List<String> enumNames = Stream.of(ApartmentTypeEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
		return Response
			      .status(Response.Status.OK)
			      .entity(enumNames)
			      .build();
	}
	
}
