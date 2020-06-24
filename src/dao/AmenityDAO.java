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

//csv format: id;name;deleted
public class AmenityDAO {

	private Map<Long, Amenity> amenities = new HashMap<>();

	private Map<Long, List<Amenity>> amenitiesByApartments = new HashMap<>();

	public AmenityDAO() {

	}

	public AmenityDAO(String contextPath) {
		loadAmenities(contextPath);
		loadAmenitiesByApartments(contextPath);
	}

	public Collection<Amenity> findAll() {
		return amenities.values();
	}

	public Amenity findById(Long id) {
		return amenities.get(id);
	}

	public List<Amenity> findByApartment(Long id) {
		return amenitiesByApartments.get(id);
	}

	public Boolean save(String contextPath, Amenity amenity) {
		Long maxId = -1L;
		for (Long id : amenities.keySet()) {
			if (id > maxId) {
				maxId = id;
			}
		}
		maxId++;

		String amenityCsv = maxId + ";" + amenity.getName() + ";false";

		try {
			FileWriter fw = new FileWriter(contextPath + "/amenities.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(amenityCsv);
			out.close();
			Amenity newAmenity = new Amenity(maxId, amenity.getName(), false);
			amenities.put(maxId, newAmenity);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean updateAmenityName(String contextPath, Amenity amenity) {
		try {
			File file = new File(contextPath + "/amenities.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long id = Long.parseLong(st.nextToken().trim());
					String name = st.nextToken().trim();
					Boolean deleted = Boolean.parseBoolean(st.nextToken().trim());
					if (amenity.getId().equals(id))
						oldtext += id + ";" + amenity.getName() + ";" + deleted + "\r\n";
					else
						oldtext += line  + "\r\n";
				}
			}
			reader.close();
			FileWriter writer = new FileWriter(contextPath + "/amenities.txt");
			writer.write(oldtext);
			writer.close();
			loadAmenities(contextPath);
			loadAmenitiesByApartments(contextPath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean deleteAmenity(String contextPath, Amenity amenity) {
		try {
			File file = new File(contextPath + "/amenities.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long id = Long.parseLong(st.nextToken().trim());
					String name = st.nextToken().trim();
					Boolean deleted = Boolean.parseBoolean(st.nextToken().trim());
					if (amenity.getId().equals(id)) {
						oldtext += id + ";" + amenity.getName() + ";true" + "\r\n";
						amenities.get(id).setDeleted(true);
					}else {
						oldtext += line  + "\r\n";
					}
				}
			}
			reader.close();
			FileWriter writer = new FileWriter(contextPath + "/amenities.txt");
			writer.write(oldtext);
			writer.close();
			deleteAmenityByApartments(contextPath, amenity);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Boolean deleteAmenityByApartments(String contextPath, Amenity amenity) {
		try {
			File file = new File(contextPath + "/apartments_amenities.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long apartmentId = Long.parseLong(st.nextToken().trim());
					Long amenityId = Long.parseLong(st.nextToken().trim());
					if (!amenity.getId().equals(amenityId)) {
						oldtext += line  + "\r\n";
					}else {
						List<Amenity> aa = amenitiesByApartments.get(apartmentId);
						for(Amenity a : aa) {
							if(a.getId() == amenityId) {
								aa.remove(a);
								break;
							}
						}
					}
				}
			}
			reader.close();
			FileWriter writer = new FileWriter(contextPath + "/apartments_amenities.txt");
			writer.write(oldtext);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean updateAmenitiesByApartment(String contextPath, Apartment apartment) {
		try {
			File file = new File(contextPath + "/apartments_amenities.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			List<Amenity> newAmenityList = apartment.getAmenities();
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				
				Long apartmentId = Long.parseLong(st.nextToken().trim());
				Long amenityId = Long.parseLong(st.nextToken().trim());
				
				if (!apartmentId.equals(apartment.getId())) {
					oldtext += line  + "\r\n";
				}			
			}
			reader.close();
			if(newAmenityList != null) {
				for(Amenity amenity : newAmenityList) {
					oldtext += apartment.getId() + ";" + amenity.getId() + "\r\n";
				}
			}
			FileWriter writer = new FileWriter(contextPath + "/apartments_amenities.txt");
			writer.write(oldtext);
			writer.close();
			loadAmenitiesByApartments(contextPath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void loadAmenities(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/amenities.txt");
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
					Boolean deleted = Boolean.parseBoolean(st.nextToken().trim());
					amenities.put(id, new Amenity(id, name, deleted));
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

	private void loadAmenitiesByApartments(String contextPath) {
		BufferedReader in = null;
		Map<Long, List<Amenity>> amenitiesByApartmentsMap = new HashMap<>();
		try {
			File file = new File(contextPath + "/apartments_amenities.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				Long apartmentId = Long.parseLong(st.nextToken().trim());
				Long amenityId = Long.parseLong(st.nextToken().trim());	
				Amenity amenity = amenities.get(amenityId);
				if (amenitiesByApartmentsMap.containsKey(apartmentId)) {
					amenitiesByApartmentsMap.get(apartmentId).add(amenity);
				} else {
					List<Amenity> apartmentAmenities = new ArrayList<Amenity>();
					apartmentAmenities.add(amenity);
					amenitiesByApartmentsMap.put(apartmentId, apartmentAmenities);
				}
			}
			this.amenitiesByApartments = amenitiesByApartmentsMap;
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

}
