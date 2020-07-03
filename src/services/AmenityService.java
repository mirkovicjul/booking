package services;

import java.util.ArrayList;
import java.util.Collection;

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

import beans.Amenity;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import dao.AmenityDAO;
import misc.Authorization;

@Path("/amenity")
public class AmenityService {
	
	@Context
	ServletContext ctx;
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("amenityDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("amenityDAO", new AmenityDAO(contextPath));
		}
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAmenities(@Context HttpServletRequest request) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST, UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) { 
			AmenityDAO dao = (AmenityDAO) ctx.getAttribute("amenityDAO");
			Collection<Amenity> allAmenities = dao.findAll();
			Collection<Amenity> amenities = new ArrayList<Amenity>();
			for(Amenity a : allAmenities) {
				if(!a.getDeleted())
					amenities.add(a);
			}
			return Response
				      .status(Response.Status.OK)
				      .entity(amenities)
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
	public Response updateAmenityName(@Context HttpServletRequest request, Amenity amenity) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) { 
			AmenityDAO dao = (AmenityDAO) ctx.getAttribute("amenityDAO");			
			Boolean response = dao.updateAmenityName(ctx.getRealPath(""), amenity);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Amenity name successfully updated.");
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
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewAmenity(@Context HttpServletRequest request, Amenity amenity) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) { 
			AmenityDAO dao = (AmenityDAO) ctx.getAttribute("amenityDAO");		
			Boolean response = dao.save(ctx.getRealPath(""), amenity);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Amenity successfully created.");
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
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteAmenity(@Context HttpServletRequest request, Amenity amenity) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) { 
			AmenityDAO dao = (AmenityDAO) ctx.getAttribute("amenityDAO");			
			Boolean response = dao.deleteAmenity(ctx.getRealPath(""), amenity);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Amenity successfully deleted.");
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
	
}
