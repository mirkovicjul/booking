package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import beans.Comment;
import beans.User;

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

	public Collection<Comment> findAll() {
		return comments.values();
	}
	
	public List<Comment> findByApartment(Long apartmentId) {
		return commentsByApartments.get(apartmentId);
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
