package com.spacebank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/space_bank";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "anshsharma07";

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String userType = request.getParameter("userType");

		if (username == null || password == null || userType == null || username.isEmpty() || password.isEmpty()) {
			response.setContentType("text/html");
			response.getWriter().println("<html><body><h3>Error: All fields are required!</h3>");
			response.getWriter().println("<a href='login.html'>Go Back to Login</a></body></html>");
			return;
		}

		try (Connection conn = getConnection()) {
			String sql = "SELECT * FROM users WHERE userName = ? AND password = ? AND userType = ?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setString(3, userType);

				ResultSet rs = ps.executeQuery();

				// In LoginServlet.java
				if (rs.next()) {
				    HttpSession session = request.getSession();
				    session.setAttribute("username", username);  
				    session.setAttribute("userType", userType);

				    if ("user".equalsIgnoreCase(userType)) {
				        response.sendRedirect("customerDashboard.html");
				    } else if("admin".equalsIgnoreCase(userType)){
				    	response.sendRedirect("adminDashboard.html");
				    }else {
				        response.sendRedirect("login.html");
				    }
				} else {
				    response.setContentType("text/html");
				    response.getWriter().println("<html><body><h3>Invalid credentials or user type.</h3>");
				    response.getWriter().println("<a href='login.html'>Go Back to Login</a></body></html>");
				}



			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			response.setContentType("text/html");
			response.getWriter().println("<html><body><h3>Database error: " + e.getMessage() + "</h3></body></html>");
		}
	}
}
