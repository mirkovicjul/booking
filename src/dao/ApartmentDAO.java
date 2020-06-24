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
		Collection<Apartment> apartmentsNotDeleted = new ArrayList<Apartment>();
		for(Apartment apartment : apartments.values()) {
			if(!apartment.getDeleted())
				apartmentsNotDeleted.add(apartment);
		}
		return apartmentsNotDeleted;
	}

	public Apartment findById(Long id) {
		Apartment apartment = apartments.get(id);
		if(!apartment.getDeleted())
			return apartment;
		else
			return null;
	}

	public Boolean save(String contextPath, Apartment apartment) {
		Long locationId = locationDAO.save(contextPath, apartment.getLocation());
		if (locationId != -1L) {
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
					+ apartment.getHost().getUsername() + ";" + apartment.getPrice() + ";" + apartment.getCheckIn()
					+ ";" + apartment.getCheckOut() + ";false;false";

			List<Amenity> amenities = apartment.getAmenities();

			String amenityApartmentCsv = "";
			for (Amenity amenity : amenities) {
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
				out2.println(amenityApartmentCsv);
				out2.close();
				apartments.put(apartment.getId(), apartment);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public Boolean updateApartmentInfo(String contextPath, Apartment newApartmentInfo) {
		Location newLocation = newApartmentInfo.getLocation();
		Location location = locationDAO.findById(newLocation.getId());
		Boolean newStreet = newLocation.getAddress().getStreet().equals(location.getAddress().getStreet()) ? false : true;
		Boolean newCity = newLocation.getAddress().getCity().equals(location.getAddress().getCity()) ? false : true;
		Boolean newPostalCode = newLocation.getAddress().getPostalCode().equals(location.getAddress().getPostalCode()) ? false : true;
		Boolean newCountry = newLocation.getAddress().getCountry().equals(location.getAddress().getCountry()) ? false : true;
		Boolean newLatitude = newLocation.getLatitude().equals(location.getAddress().getPostalCode()) ? false : true;
		Boolean newLongitude = newLocation.getLongitude().equals(location.getAddress().getPostalCode()) ? false : true;

		if(!(newStreet && newCity && newPostalCode && newCountry && newLatitude && newLongitude)) {
			try {
				File file = new File(contextPath + "/locations.txt");
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = "", oldtext = "";
				StringTokenizer st;
				while ((line = reader.readLine()) != null) {
					if (line.equals("") || line.indexOf('#') == 0)
						continue;
					st = new StringTokenizer(line, ";");
					
					Long id = Long.parseLong(st.nextToken().trim());

					if (location.getId().equals(id)) {
						oldtext += id + ";" + newApartmentInfo.getLocation().getLatitude() + ";" + newApartmentInfo.getLocation().getLongitude() + 
								";" + newApartmentInfo.getLocation().getAddress().getStreet() + ";" + newApartmentInfo.getLocation().getAddress().getCity() +
								";" + newApartmentInfo.getLocation().getAddress().getPostalCode() + ";" + newApartmentInfo.getLocation().getAddress().getCountry() + "\r\n";
					} else {
						oldtext += line  + "\r\n";
					}
					
				}
				reader.close();
				FileWriter writer = new FileWriter(contextPath + "/locations.txt");
				writer.write(oldtext);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}		
		amenityDAO.updateAmenitiesByApartment(contextPath, newApartmentInfo);
		try {
			File file = new File(contextPath + "/apartments.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");			
				Long id = Long.parseLong(st.nextToken().trim());
				if (newApartmentInfo.getId().equals(id))
					oldtext += newApartmentInfo.getId() + ";" + newApartmentInfo.getName() + ";" + newApartmentInfo.getApartmentType() + ";"
							+ newApartmentInfo.getNumberOfRooms() + ";" + newApartmentInfo.getCapacity() + ";" + newApartmentInfo.getLocation().getId() + ";"
							+ newApartmentInfo.getHost().getUsername() + ";" + newApartmentInfo.getPrice() + ";" + newApartmentInfo.getCheckIn() + ";"
							+ newApartmentInfo.getCheckOut() + ";" + newApartmentInfo.getActive() + ";" + newApartmentInfo.getDeleted() + "\r\n";
				else
					oldtext += line  + "\r\n";				
			}
			reader.close();
			FileWriter writer = new FileWriter(contextPath + "/apartments.txt");
			writer.write(oldtext);
			writer.close();
			loadApartments(contextPath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadApartments(String contextPath) {
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

}
