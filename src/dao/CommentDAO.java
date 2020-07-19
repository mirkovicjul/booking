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
import java.util.Optional;
import java.util.StringTokenizer;

import beans.Comment;
import beans.User;
import beans.dto.CommentStatus;

//csv format: comment id;apartment id;commentator username;comment;rating;approved
public class CommentDAO {

	private Map<Long, Comment> comments = new HashMap<>();
	private Map<Long, List<Comment>> commentsByApartments = new HashMap<>();

	private UserDAO userDAO;
	
	public CommentDAO() {

	}

	public CommentDAO(String contextPath, UserDAO userDAO) {
		this.userDAO = userDAO;
		loadComments(contextPath);
	}

	public Comment findById(Long id) {
		return comments.get(id);
	}
	
	public Collection<Comment> findAll() {
		return comments.values();
	}
	
	public List<Comment> findByApartment(Long apartmentId) {
		return Optional.ofNullable(commentsByApartments.get(apartmentId)).orElseGet(() -> new ArrayList<Comment>());
	}
	
	public Boolean save(String contextPath, Comment comment) {
		Long maxId = -1L;
		for (Long id : comments.keySet()) {
			if (id > maxId) {
				maxId = id;
			}
		}
		maxId++;

		String commentCsv = maxId + ";" + comment.getApartmentId() + ";" + comment.getCommentator().getUsername() + ";" + comment.getComment() + ";" + comment.getRating() + ";false";

		try {
			FileWriter fw = new FileWriter(contextPath + "/comments.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(commentCsv);
			out.close();
			Comment newComment = new Comment(maxId, comment.getApartmentId(), userDAO.findByUsername(comment.getCommentator().getUsername()), comment.getComment(), comment.getRating(), false);
			comments.put(maxId, newComment);
			for(Long id : commentsByApartments.keySet()) {
				if(id.equals(comment.getApartmentId())) {
					commentsByApartments.get(id).add(newComment);
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean updateCommentStatus(String contextPath, CommentStatus commentStatus) {
		try {
			File file = new File(contextPath + "/comments.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			StringTokenizer st;
			while ((line = reader.readLine()) != null) {
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					Long id = Long.parseLong(st.nextToken().trim());
					Long apartmentId = Long.parseLong(st.nextToken().trim());
					String user = st.nextToken().trim();
					String comment = st.nextToken().trim();
					Long rating = Long.parseLong(st.nextToken().trim());
					Boolean approved = Boolean.parseBoolean(st.nextToken().trim());
					if (commentStatus.getCommentId().equals(id))
						oldtext += id + ";" + apartmentId + ";" + user + ";" + comment + ";" + rating + ";" + commentStatus.getApproved() + "\r\n";
					else
						oldtext += line  + "\r\n";
				}
			}
			reader.close();
			FileWriter writer = new FileWriter(contextPath + "/comments.txt");
			writer.write(oldtext);
			writer.close();
			for(Long id : comments.keySet()) {
				if(id.equals(commentStatus.getCommentId())) {
					(comments.get(id)).setApproved(commentStatus.getApproved());
				}
			}
			for(Long apartmentId : commentsByApartments.keySet()) {
				for(Comment comment : commentsByApartments.get(apartmentId)) {
					if(comment.getId().equals(commentStatus.getCommentId())) {
						comment.setApproved(commentStatus.getApproved());
					}
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void loadComments(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/comments.txt");
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
					String commentatorUsername = st.nextToken().trim();
					User commentator = userDAO.findByUsername(commentatorUsername);
					String comment = st.nextToken().trim();
					int rating = Integer.parseInt(st.nextToken().trim());
					Boolean approved = Boolean.parseBoolean(st.nextToken().trim());
					List<Comment> commentsByApartmentId;
					if(commentsByApartments.containsKey(apartmentId)) {
						commentsByApartmentId = commentsByApartments.get(apartmentId);
						commentsByApartmentId.add(new Comment(id, apartmentId, commentator, comment, rating, approved));
					} else {
						commentsByApartmentId = new ArrayList<Comment>();
						commentsByApartmentId.add(new Comment(id, apartmentId, commentator, comment, rating, approved));
						commentsByApartments.put(apartmentId, commentsByApartmentId);
					}
					comments.put(id, new Comment(id, apartmentId, commentator, comment, rating, approved));
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
