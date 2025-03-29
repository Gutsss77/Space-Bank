package com.spacebank;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ChangePasswordServlet extends HttpServlet {

    private static final String db_url = "jdbc:mysql://localhost:3306/space_bank";
    private static final String db_username = "root";
    private static final String db_password = "anshsharma07";

    // Function to verify password match
    private boolean checkConfirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String username = req.getParameter("username");
        String prevPassword = req.getParameter("prevPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // Validate input
        if (username == null || prevPassword == null || newPassword == null || confirmPassword == null || 
            username.isEmpty() || prevPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('All fields are required!');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Check if new password matches the confirm password
        if (!checkConfirmPassword(newPassword, confirmPassword)) {
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('New password and confirm password do not match!');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Explicitly load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('MySQL JDBC Driver not found!');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Verify the previous password in the database
        try (Connection c = DriverManager.getConnection(db_url, db_username, db_password)) {
            String sql = "SELECT password FROM users WHERE userName = ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (storedPassword.equals(prevPassword)) {
                    // Update the password if previous password matches
                    String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                    PreparedStatement updatePs = c.prepareStatement(updateSql);
                    updatePs.setString(1, newPassword);
                    updatePs.setString(2, username);
                    int rowsUpdated = updatePs.executeUpdate();

                    if (rowsUpdated > 0) {
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<script type='text/javascript'>");
                        out.println("alert('Password changed successfully!');");
                        out.println("window.location.href = 'customerDashboard.html';");
                        out.println("</script>");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("</body>");
                        out.println("</html>");
                    } else {
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<script type='text/javascript'>");
                        out.println("alert('Error updating password!');");
                        out.println("window.location.href = 'customerDashboard.html';");
                        out.println("</script>");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("</body>");
                        out.println("</html>");
                    }
                } else {
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<script type='text/javascript'>");
                    out.println("alert('Previous password is incorrect!');");
                    out.println("window.location.href = 'customerDashboard.html';");
                    out.println("</script>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("</body>");
                    out.println("</html>");
                }
            } else {
                out.println("<html>");
                out.println("<head>");
                out.println("<script type='text/javascript'>");
                out.println("alert('User not found!');");
                out.println("window.location.href = 'customerDashboard.html';");
                out.println("</script>");
                out.println("</head>");
                out.println("<body>");
                out.println("</body>");
                out.println("</html>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Database error occurred!');");
            out.println("window.location.href = 'customerDashboard.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}

