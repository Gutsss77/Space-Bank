package com.spacebank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/space_bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "anshsharma07";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get form parameters
        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);


            String sql = "INSERT INTO users (userName, email, password, userType) VALUES (?, ?, ?, ?)";


            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, userType);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                response.getWriter().println("<script>alert('Registration Successful!'); window.location.href='login.html';</script>");
            } else {
                response.getWriter().println("<script>alert('Registration failed! Please try again later.'); window.location.href='register.html';</script>");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('JDBC Driver not found!'); window.location.href='register.html';</script>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('Database error: " + e.getMessage() + "'); window.location.href='register.html';</script>");
        } finally {

            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


