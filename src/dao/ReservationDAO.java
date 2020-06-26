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

import beans.Address;
import beans.Amenity;
import beans.Reservation;
import beans.ReservationStatusEnum;

public class ReservationDAO {
	
	private Map<Long, Reservation> reservations = new HashMap<>();

	public ReservationDAO(String contextPath) {
		loadReservations(contextPath);
	}
	
	public Reservation findById(Long id) {
		return reservations.get(id);
	}
	
	public Collection<Reservation> getReservationsByApartment(Long apartmentId){
		Collection<Reservation> reservationsByApartment = new ArrayList<Reservation>();
		for(Reservation reservation : reservations.values()) {
			if(reservation.getApartmentId().equals(apartmentId)) {
				reservationsByApartment.add(reservation);
			}
		}
		return reservationsByApartment;
	}
	
	
	public Collection<Reservation> getReservationsByGuest(String guestUsername){
		Collection<Reservation> reservationsByGuest = new ArrayList<Reservation>();
		for(Reservation reservation : reservations.values()) {
			if(reservation.getGuest().equals(guestUsername)) {
				reservationsByGuest.add(reservation);
			}
		}
		return reservationsByGuest;
	}
	
	public Collection<Reservation> getReservationsByHost(List<Long> apartmentIds){
		Collection<Reservation> reservationsByHost = new ArrayList<Reservation>();
		for(Long apartmentId : apartmentIds) {
			for(Reservation reservation : reservations.values()) {
				if(reservation.getApartmentId().equals(apartmentId)) {
					reservationsByHost.add(reservation);
				}
			}
		}	
		return reservationsByHost;
	}
	
	public Boolean save(String contextPath, Reservation reservation) {
		Long maxIdReservation = -1L;
		for (Long l : this.reservations.keySet()) {
			if (l > maxIdReservation) {
				maxIdReservation = l;
			}
		}
		maxIdReservation++;
		
		
		String reservationCsv = maxIdReservation + ";" + reservation.getApartmentId() + ";" + reservation.getGuest() + ";"
				+ reservation.getStartDate() + ";" + reservation.getEndDate() + ";" + reservation.getPrice() + ";"
				+ reservation.getMessage() + ";" + "CREATED";
		
		try {
			FileWriter fw = new FileWriter(contextPath + "/reservations.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(reservationCsv);
			out.close();
			reservation.setStatus(ReservationStatusEnum.valueOf("CREATED"));
			reservations.put(maxIdReservation, reservation);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	private void loadReservations(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/reservations.txt");
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
					String guest = st.nextToken().trim();
					Long startDate = Long.parseLong(st.nextToken().trim());
					Long endDate = Long.parseLong(st.nextToken().trim());
					Long price = Long.parseLong(st.nextToken().trim());
					String message = st.nextToken().trim();
					String status = st.nextToken().trim();					
					reservations.put(id, new Reservation(id, apartmentId, guest, startDate, endDate, price, message, ReservationStatusEnum.valueOf(status)));
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
