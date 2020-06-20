package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import beans.Address;
import beans.Location;

//csv format: id;latitude;longitude;street;city;postal code;country
public class LocationDAO {
	
	private Map<Long, Location> locations = new HashMap<>();
	
	
	public LocationDAO() {

	}

	public LocationDAO(String contextPath) {
		loadLocations(contextPath);
	}

	public Collection<Location> findAll() {
		return locations.values();
	}
	
	public Location findById(Long id) {
		return locations.get(id);
	}
	
	private void loadLocations(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/locations.txt");
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
					String latitude = st.nextToken().trim();
					String longitude = st.nextToken().trim();
					String street = st.nextToken().trim();
					String city = st.nextToken().trim();
					String postalCode = st.nextToken();
					String country = st.nextToken();
					Address address = new Address(street, city, postalCode, country);
					locations.put(id, new Location(id, latitude, longitude, address));
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
	
}
