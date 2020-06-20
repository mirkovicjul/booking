package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Apartment;
import beans.UserRoleEnum;
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
		return null;
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
			return Response
				      .status(Response.Status.OK)
				      .entity(apartment)
				      .build();
	}
}