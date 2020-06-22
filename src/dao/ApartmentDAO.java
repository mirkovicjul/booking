package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import beans.Amenity;
import beans.Apartment;
import beans.ApartmentTypeEnum;
import beans.Comment;
import beans.DisabledDate;
import beans.Location;
import beans.User;

//csv format: apartment id;apartment name;apartment type;number of rooms;capacity;location id;host;price;check in;check out;active;deleted
public class ApartmentDAO {

	private Map<Long, Apartment> apartments = new HashMap<>();

	private LocationDAO locationDAO;

	private UserDAO userDAO;

	private CommentDAO commentDAO;

	private AmenityDAO amenityDAO;

	public ApartmentDAO() {

	}

	public ApartmentDAO(String contextPath, LocationDAO locationDAO, UserDAO userDAO, CommentDAO commentDAO,
			AmenityDAO amenityDAO) {
		this.locationDAO = locationDAO;
		this.userDAO = userDAO;
		this.commentDAO = commentDAO;
		this.amenityDAO = amenityDAO;
		loadApartments(contextPath);
	}

	public Collection<Apartment> findAll() {
		return apartments.values();
	}

	public Apartment findById(Long id) {
		return apartments.get(id);
	}

	private void loadApartments(String contextPath) {
		System.out.println("loading apartments");
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/apartments.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long id = Long.parseLong(st.nextToken().trim());
					String name = st.nextToken().trim();
					String apartmentType = st.nextToken().trim();
					int numberOfRooms = Integer.parseInt(st.nextToken().trim());
					int capacity = Integer.parseInt(st.nextToken().trim());
					Long locationId = Long.parseLong(st.nextToken().trim());
					Location location = locationDAO.findById(locationId);
					String hostUsername = st.nextToken().trim();
					User host = userDAO.findByUsername(hostUsername);
					Long price = Long.parseLong(st.nextToken().trim());
					String checkIn = st.nextToken().trim();
					String checkOut = st.nextToken().trim();
					Boolean active = Boolean.parseBoolean(st.nextToken().trim());
					Boolean deleted = Boolean.parseBoolean(st.nextToken().trim());
					List<Comment> comments = commentDAO.findByApartment(id);
					List<Amenity> amenities = amenityDAO.findByApartment(id);
					apartments.put(id, new Apartment(id, name, ApartmentTypeEnum.valueOf(apartmentType), numberOfRooms,
							capacity, location, host, price, checkIn, checkOut, active, amenities, comments, deleted));
				}
			}
			Map<Long, List<DisabledDate>> disabledDatesByApartments = loadDisabledDatesByApartments(contextPath);
			for (Long apartmentId : apartments.keySet()) {
				if (disabledDatesByApartments.containsKey(apartmentId)) {
					apartments.get(apartmentId).setDisabledDates(disabledDatesByApartments.get(apartmentId));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Map<Long, List<DisabledDate>> loadDisabledDatesByApartments(String contextPath) {
		Map<Long, List<DisabledDate>> disabledDatesByApartments = new HashMap<Long, List<DisabledDate>>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/disabled_dates.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long id = Long.parseLong(st.nextToken().trim());
					Long apartmentId = Long.parseLong(st.nextToken().trim());
					Long startDate = Long.parseLong(st.nextToken().trim());
					Long endDate = Long.parseLong(st.nextToken().trim());
					if (disabledDatesByApartments.containsKey(apartmentId)) {
						disabledDatesByApartments.get(apartmentId)
								.add(new DisabledDate(id, apartmentId, startDate, endDate));
					} else {
						List<DisabledDate> disabledDates = new ArrayList<DisabledDate>();
						disabledDates.add(new DisabledDate(id, apartmentId, startDate, endDate));
						disabledDatesByApartments.put(apartmentId, disabledDates);
					}
				}
			}
			return disabledDatesByApartments;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Boolean save(String contextPath, Apartment apartment) {	
		Long locationId = locationDAO.save(contextPath, apartment.getLocation());
		if(locationId != -1L) {
			Long maxIdApartment = -1L;
			for (Long id : apartments.keySet()) {
				if (id > maxIdApartment) {
					maxIdApartment = id;
				}
			}
			maxIdApartment++;
			apartment.setId(maxIdApartment);
			String apartmentCsv = maxIdApartment + ";" + apartment.getName() + ";" + apartment.getApartmentType() + ";"
					+ apartment.getNumberOfRooms() + ";" + apartment.getCapacity() + ";" + locationId + ";"
					+ apartment.getHost().getUsername() + ";" + apartment.getPrice() + ";" + apartment.getCheckIn() + ";"
					+ apartment.getCheckOut() + ";false;false";
			
			List<Amenity> amenities = apartment.getAmenities();
			
			String amenityApartmentCsv = "";
			for(Amenity amenity : amenities) {
				amenityApartmentCsv += maxIdApartment + ";" + amenity.getId() + "\r\n";
			}
			
			try {
				FileWriter fw = new FileWriter(contextPath + "/apartments.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw);
				out.println(apartmentCsv);
				out.close();
				
				FileWriter fw2 = new FileWriter(contextPath + "/apartments_amenities.txt", true);
				BufferedWriter bw2 = new BufferedWriter(fw2);
				PrintWriter out2 = new PrintWriter(bw2);
				out.println(amenityApartmentCsv);
				out.close();
				apartments.put(apartment.getId(), apartment);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

}
