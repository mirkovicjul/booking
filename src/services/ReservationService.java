package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Apartment;
import beans.Reservation;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import dao.ApartmentDAO;
import dao.ReservationDAO;
import misc.Authorization;

@Path("reservation")
public class ReservationService {
	

	@Context
	ServletContext ctx;

	@PostConstruct
	public void init() {
		if (ctx.getAttribute("reservationDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("reservationDAO", new ReservationDAO(contextPath));
		}
	
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addReservation(@Context HttpServletRequest request, Reservation reservation) {
		UserRoleEnum[] roles = {UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) {
			String username = Authorization.getUsername(request);
			ReservationDAO dao = (ReservationDAO) ctx.getAttribute("reservationDAO");
			ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");

			if(!username.equals(reservation.getGuest())) {
				return Response
					      .status(Response.Status.FORBIDDEN)
					      .build();
			}
			
			Apartment apartment = apartmentDAO.findById(reservation.getApartmentId());
			Boolean available = apartmentDAO.checkReservationAvailabilty(apartment, reservation);
			if(!available) {
				MsgResponse res = new MsgResponse(false, "Apartment not available for selected dates.");
				return Response
						.status(Response.Status.OK)
						.entity(res)
						.build();
			}
			
			Boolean response = dao.save(ctx.getRealPath(""), reservation);
			if(response) {
				MsgResponse res = new MsgResponse(true, "Your booking request was sent to the host.");
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
