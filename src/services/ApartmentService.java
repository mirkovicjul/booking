package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import beans.Amenity;
import beans.Apartment;
import beans.ApartmentTypeEnum;
import beans.Comment;
import beans.DisabledDate;
import beans.User;
import beans.UserRoleEnum;
import beans.dto.CommentStatus;
import beans.dto.MsgResponse;
import dao.AmenityDAO;
import dao.ApartmentDAO;
import dao.CommentDAO;
import dao.LocationDAO;
import dao.ReservationDAO;
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
		if (ctx.getAttribute("reservationDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("reservationDAO", new ReservationDAO(contextPath));
		}
		if (ctx.getAttribute("apartmentDAO") == null) {
	    	String contextPath = ctx.getRealPath("");
			ctx.setAttribute("apartmentDAO", new ApartmentDAO(contextPath, (LocationDAO) ctx.getAttribute("locationDAO"), (UserDAO) ctx.getAttribute("userDAO"), (CommentDAO) ctx.getAttribute("commentDAO"), (AmenityDAO) ctx.getAttribute("amenityDAO"), (ReservationDAO) ctx.getAttribute("reservationDAO")));
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
	@Path("/{id}/upload")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImages(@Context HttpServletRequest request, @PathParam("id") Long id,
			FormDataMultiPart multipart) {
		UserRoleEnum[] roles = {UserRoleEnum.ADMIN, UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) { 
			ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");	
			String UPLOAD_PATH = ctx.getRealPath("");
		    try{	    	
		    	Map<String, List<FormDataBodyPart>> map = multipart.getFields();

		        for (Map.Entry<String, List<FormDataBodyPart>> entry : map.entrySet()) {

		            for (FormDataBodyPart part : entry.getValue()) {
		                InputStream in = part.getEntityAs(InputStream.class);
		                Long timestamp = System.currentTimeMillis();
		                int read = 0;
				        byte[] bytes = new byte[1024];
				 
				        String imagePath = "photos" + File.separator + id + File.separator + id + timestamp + (int)Math.random() +".jpg";
				        File file = new File(UPLOAD_PATH + imagePath);
				        file.getParentFile().mkdirs();
				        OutputStream out = new FileOutputStream(file);
		                while ((read = in.read(bytes)) != -1) 
				        {
				            out.write(bytes, 0, read);
				        }
				        out.flush();
				        out.close();
				        
				        dao.saveImages(ctx.getRealPath(""), imagePath, id);
				        
		            }
		        }	        
		    } catch (IOException e) {
		        e.printStackTrace();
		        MsgResponse msg = new MsgResponse(true, "Error while uploading file.");
			    return Response
					      .status(Response.Status.INTERNAL_SERVER_ERROR)
					      .entity(msg)
					      .build();
		    }
		    MsgResponse msg = new MsgResponse(true, "Images uploaded.");
		    return Response
				      .status(Response.Status.OK)
				      .entity(msg)
				      .build();
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
			apartment.setComments(new ArrayList<Comment>());
			apartment.setImages(new ArrayList<String>());
			apartment.setDisabledDates(new ArrayList<DisabledDate>());
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
	@Path("/{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteApartment(@Context HttpServletRequest request, @PathParam("id") Long id) {
		
		ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");			
		String apartmentHost = dao.findById(id).getHost().getUsername();
		if((Authorization.getUserRole(request).equals("HOST") && Authorization.getUsername(request).equals(apartmentHost)) || (Authorization.getUserRole(request).equals("ADMIN"))) {
			Boolean deleted = dao.deleteApartment(ctx.getRealPath(""), id);
			if(deleted) {
				MsgResponse res = new MsgResponse(true, "Apartment successfully deleted.");
				return Response
						.status(Response.Status.OK)
						.entity(res)
						.build();
			} else {
				MsgResponse res = new MsgResponse(false, "Something went wrong.");
				return Response
						.status(Response.Status.OK)
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
	
	@POST
	@Path("/comment/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateComment(@Context HttpServletRequest request, CommentStatus commentStatus) {
		UserRoleEnum[] roles = {UserRoleEnum.HOST};
		if(Authorization.authorizeUser(request, roles)) {
			CommentDAO commentDAO = (CommentDAO) ctx.getAttribute("commentDAO");
			Boolean commentUpdated = commentDAO.updateCommentStatus(ctx.getRealPath(""), commentStatus);
			if(commentUpdated) {
				Comment comment = commentDAO.findById(commentStatus.getCommentId());
				ApartmentDAO apartmentDAO = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
				Apartment apartment = apartmentDAO.findById(comment.getApartmentId());
				apartment.setComments(commentDAO.findByApartment(apartment.getId()));
				MsgResponse res = new MsgResponse(true, "Comment status successfully updated.");
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
	@Path("/{id}/comment/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllComments(@Context HttpServletRequest request, @PathParam("id") Long id) {
		ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
		Apartment apartment = dao.findById(id);
		List<Comment> comments = apartment.getComments();
				return Response
					      .status(Response.Status.OK)
					      .entity(comments)
					      .build();
	}
	
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchApartments(@Context HttpServletRequest request, 
			@QueryParam("location") String location,
			@QueryParam("checkIn") Long checkIn,
			@QueryParam("checkOut") Long checkOut,
			@QueryParam("rooms") Long rooms,
			@QueryParam("guests") Long guests,
			@QueryParam("priceMin") Long priceMin,
			@QueryParam("priceMax") Long priceMax,
			@QueryParam("type") String type,
			@QueryParam("status") String status,
			@QueryParam("amenities") String amenityIds) {
		
		ApartmentDAO dao = (ApartmentDAO) ctx.getAttribute("apartmentDAO");
		Collection<Apartment> apartments = dao.findAll();
		
		if(Authorization.getUserRole(request).equals("GUEST") || Authorization.getUserRole(request).equals("")) {
			apartments = apartments.stream()
					.filter(a -> a.getActive())
					.collect(Collectors.toList());
		} else if(Authorization.getUserRole(request).equals("HOST")) {
			apartments = dao.findByHost(Authorization.getUsername(request));
			apartments = apartments.stream()
					.filter(a -> a.getActive())
					.collect(Collectors.toList());
		}
		
		if(location != null) {
			apartments = apartments.stream()
				.filter(l -> l.getLocation().getAddress().getCity().toLowerCase().contains(location.toLowerCase()) || l.getLocation().getAddress().getCountry().toLowerCase().contains(location.toLowerCase()))
				.collect(Collectors.toList());
		}
		
		if(priceMin != null) {
			apartments = apartments.stream()
				.filter(a -> a.getPrice() >= priceMin)
				.collect(Collectors.toList());
		}
		if(priceMax != null) {
			apartments = apartments.stream()
				.filter(a -> a.getPrice() <= priceMax)
				.collect(Collectors.toList());
		}
		if(guests != null) {
			apartments = apartments.stream()
				.filter(a -> a.getCapacity() >= guests)
				.collect(Collectors.toList());
		}
		if(rooms != null) {
			apartments = apartments.stream()
				.filter(a -> a.getNumberOfRooms() >= rooms)
				.collect(Collectors.toList());
		}
		
		if(checkIn != null && checkOut != null) {

			Calendar cin = Calendar.getInstance();
			cin.setTimeInMillis(checkIn);
			Calendar cinRestarted = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cinRestarted.clear();
			cinRestarted.set(cin.get(Calendar.YEAR), cin.get(Calendar.MONTH), cin.get(Calendar.DATE));
			Long cinTimestamp = cinRestarted.getTimeInMillis();
			
			Calendar cout = Calendar.getInstance();
			cout.setTimeInMillis(checkOut);
			Calendar coutRestarted = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			coutRestarted.clear();
			coutRestarted.set(cout.get(Calendar.YEAR), cout.get(Calendar.MONTH), cout.get(Calendar.DATE));
			Long coutTimestamp = coutRestarted.getTimeInMillis();
			
			apartments = apartments.stream()
					.filter(apartment -> {
							final List<DisabledDate> dates = apartment.getDisabledDates().stream()
									.filter(d -> (d.getStartDate() <= cinTimestamp && d.getEndDate() >= cinTimestamp)
											|| (d.getStartDate() >= cinTimestamp && d.getEndDate() <= coutTimestamp)
											|| (d.getStartDate() <= coutTimestamp && d.getEndDate() >= coutTimestamp))
									.collect(Collectors.toList());
							return dates.isEmpty();
					}).collect(Collectors.toList());			
		}
		
		if(type != null) {
			apartments = apartments.stream()
					.filter(a -> a.getApartmentType().equals(ApartmentTypeEnum.valueOf(type)))
					.collect(Collectors.toList());
		}
		
		if(status != null) {
			apartments = apartments.stream()
					.filter(a -> String.valueOf(a.getActive()).equals(status))
					.collect(Collectors.toList());
		}
		
		if(amenityIds != null) {
			List<String> amenities = Arrays.asList(amenityIds.split(","));
			List<Long> queryAmenityIds;
			queryAmenityIds = amenities.stream()
					.map(a -> Long.parseLong(a))
					.collect(Collectors.toList());
			
			apartments = apartments.stream()
				.filter(apartment -> 
					queryAmenityIds.stream()
						.map(queryAmenityId -> apartment.getAmenities().stream()
													.map(a -> a.getId())
													.anyMatch(apartmentAmenityId -> apartmentAmenityId.equals(queryAmenityId)))
						.allMatch(b -> b)
				)
				.collect(Collectors.toList());			
		}
		
		return Response
			      .status(Response.Status.OK)
			      .entity(apartments)
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
