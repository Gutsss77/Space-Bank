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

public class CheckBalanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/space_bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "anshsharma07";

    // Static block to explicitly load the MySQL JDBC driver
    static {
        try {
            // Register the MySQL driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found.");
        }
    }

    // Utility method to get a connection to the database
    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Method to get the balance of the account
    private double getBalance(String accountNumber) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "SELECT ibalance FROM account_users WHERE accountNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("ibalance");
                    }
                }
            }
        }
        return 0.0;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the account number from the form
        String accountNumber = request.getParameter("account_number");

        // Validate input
        if (accountNumber == null || accountNumber.isEmpty()) {
            out.println("<p>Account number is required.</p>");
            return;
        }

        try {
            // Get the balance from the database
            double balance = getBalance(accountNumber);
            
            if (balance == 0.0) {
                out.println("<p>Account does not exist or has no balance.</p>");
                return;
            }
            
         // Display the balance in a pop-up
            out.println("<html>");
            out.println("<head><title>Check Balance</title></head>");
            out.println("<body>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Your Balance is: ₹" + balance + "');");
            out.println("window.location.href = 'customerDashboard.html';");  // Redirect to the dashboard after the user clicks 'OK' on the pop-up
            out.println("</script>");
            out.println("</body>");
            out.println("</html>");


        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
