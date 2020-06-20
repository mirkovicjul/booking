package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Location;
import dao.LocationDAO;

@Path("/location")
public class LocationService {
	
	@Context
	ServletContext ctx;

	@PostConstruct
	public void init() {
		System.out.println("===============locationService init() started");
		if (ctx.getAttribute("locationDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("locationDAO", new LocationDAO(contextPath));
		}
		
		System.out.println("===========locationService " + ctx.getAttribute("addressDAO"));
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocations() {
		LocationDAO dao = (LocationDAO) ctx.getAttribute("locationDAO");
		Collection<Location> locations = dao.findAll();
		return Response
			      .status(Response.Status.OK)
			      .entity(locations)
			      .build();
	}
	
}
