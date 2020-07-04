package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Apartment;
import beans.DisabledDate;
import beans.Reservation;
import beans.ReservationStatusEnum;
import beans.UserRoleEnum;
import beans.dto.MsgResponse;
import beans.dto.NewReservationStatus;
import dao.ApartmentDAO;
import dao.ReservationDAO;
import misc.Authorization;

@Path("/reservation")
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
				Map<Long, List<DisabledDate>> disabledDatesByApartments = apartmentDAO.loadDisabledDatesByApartments(ctx.getRealPath(""));
				for(Long a : disabledDatesByApartments.keySet()) {
					if(a.equals(apartment.getId())) {
						apartment.setDisabledDates(disabledDatesByApartments.get(a));
					}
				}
//				DisabledDate disabledDate = new DisabledDate();
//				disabledDate.setApartmentId(reservation.getApartmentId());
//				disabledDate.setStartDate(reservation.getStartDate());
//				disabledDate.setEndDate(reservation.getEndDate() - 86400000);
				//Boolean disabledDateSaved = apartmentDAO.addDisabledDate(ctx.getRealPath(""), disabledDate, reservation.getApartmentId());
				//if(disabledDateSaved) {
					MsgResponse res = new MsgResponse(true, "Your booking request was sent to the host.");
					return Response
							.status(Response.Status.OK)
							.entity(res)
							.build();
				//}
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
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllReservations(@Context HttpServletRequest request) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST, UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) {
			ReservationDAO dao = (ReservationDAO) ctx.getAttribute("reservationDAO");
			ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			if(Authorization.getUserRole(request).equals("HOST")) {
				Collection<Apartment> apartments = apartmentDAO.findByHost(Authorization.getUsername(request));		
				List<Long> apartmentIds = new ArrayList<Long>();
				for(Apartment a : apartments) {
					apartmentIds.add(a.getId());
				}
				Collection<Reservation> reservations = dao.getReservationsByHost(apartmentIds);

				return Response
						.status(Response.Status.OK)
						.entity(reservations)
						.build();
			} else if(Authorization.getUserRole(request).equals("GUEST")) {
				Collection<Reservation> reservations = dao.getReservationsByGuest(Authorization.getUsername(request));
				return Response
						.status(Response.Status.OK)
						.entity(reservations)
						.build();
			} else {
				Collection<Reservation> reservations = dao.findAll();
				return Response
						.status(Response.Status.OK)
						.entity(reservations)
						.build();			}		
		}
		String message = "You are not authorized to view this page.";
		return Response
				.status(Response.Status.FORBIDDEN)
				.entity(message)
				.build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateReservationStatus(@Context HttpServletRequest request, NewReservationStatus newStatus) {
		UserRoleEnum[] roles = {UserRoleEnum.HOST, UserRoleEnum.GUEST};
		if(Authorization.authorizeUser(request, roles)) {			
			ReservationDAO dao = (ReservationDAO) ctx.getAttribute("reservationDAO");
			ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			
			Boolean updated = dao.updateReservationStatus(ctx.getRealPath(""), newStatus);
			if(updated) {
				if(newStatus.getStatus().equals(ReservationStatusEnum.CANCELLED) || newStatus.getStatus().equals(ReservationStatusEnum.DECLINED)) {
					Map<Long, List<DisabledDate>> disabledDatesByApartments = apartmentDAO.loadDisabledDatesByApartments(ctx.getRealPath(""));
					Long apartmentId = (dao.findById(newStatus.getReservationId())).getApartmentId();
					Apartment apartment = apartmentDAO.findById(apartmentId);
					for(Long a : disabledDatesByApartments.keySet()) {
						if(a.equals(apartmentId)) {
							apartment.setDisabledDates(disabledDatesByApartments.get(a));
						}
					}
				}
				MsgResponse res = new MsgResponse(true, "Reservation status updated.");
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
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchReservations(@Context HttpServletRequest request, 
			@QueryParam("username") String username,
			@QueryParam("status") String statuses) {
		
		UserRoleEnum[] roles = {UserRoleEnum.HOST, UserRoleEnum.ADMIN};
		if(Authorization.authorizeUser(request, roles)) {
			ReservationDAO dao = (ReservationDAO) ctx.getAttribute("reservationDAO");
			ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
			Collection<Reservation> reservations = new ArrayList<Reservation>();
			if(Authorization.getUserRole(request).equals("HOST")) {
				Collection<Apartment> apartments = apartmentDAO.findByHost(Authorization.getUsername(request));		
				List<Long> apartmentIds = new ArrayList<Long>();
				for(Apartment a : apartments) {
					apartmentIds.add(a.getId());
				}
				reservations = dao.getReservationsByHost(apartmentIds);				
			} else if (Authorization.getUserRole(request).equals("ADMIN")) {
				reservations = dao.findAll();		
			} 
			
			if(statuses != null) {
				List<String> statusesList = Arrays.asList(statuses.split(","));
			
			reservations = reservations.stream()
										.filter(r -> statusesList.stream()
																 .anyMatch(g -> g.equals(r.getStatus().toString())))				
										.collect(Collectors.toList());		
			}
			
			if(username != null) {
				reservations = reservations.stream()
									.filter(r -> r.getGuest().contains(username))
									.collect(Collectors.toList());
			}
			
			return Response
				      .status(Response.Status.OK)
				      .entity(reservations)
				      .build();

		}
		
		MsgResponse response = new MsgResponse(false, "You are not authorized to see this page.");
		return Response
			      .status(Response.Status.FORBIDDEN)
			      .entity(response)
			      .build();
	}
	
	
	@GET
	@Path("/status/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllReservationStatuses() {
		List<String> enumNames = Stream.of(ReservationStatusEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
		return Response
			      .status(Response.Status.OK)
			      .entity(enumNames)
			      .build();
	}
	
}
