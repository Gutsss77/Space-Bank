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

public class CreditFundServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/space_bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "anshsharma07";

    // Utility method to get a connection to the database
    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Method to check if the account exists and retrieve the current balance
    private boolean isValidAccount(String accountNumber) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) FROM account_users WHERE accountNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Method to get the current balance for the given account number
    private double getCurrentBalance(String accountNumber) throws SQLException {
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

    // Method to update the account balance
    private boolean updateBalance(String accountNumber, double newBalance) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE account_users SET ibalance = ? WHERE accountNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, newBalance);
                stmt.setString(2, accountNumber);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    // Method to record the transaction in the transactions table
    private boolean recordTransaction(String senderAccountNumber, double amount) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO transactions (senderAccountNumber, amount, transactionType) VALUES (?, ?, 'credit')";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, senderAccountNumber);
                stmt.setDouble(2, amount);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get parameters from the form
        String accountNumber = request.getParameter("account_number");
        String amountStr = request.getParameter("amount");

        // Validate input
        if (accountNumber == null || accountNumber.isEmpty() || amountStr == null || amountStr.isEmpty()) {
            out.println("<p>Account number and amount are required.</p>");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            out.println("<p>Invalid amount entered.</p>");
            return;
        }

        // Check if the account exists
        try {
            if (!isValidAccount(accountNumber)) {
                out.println("<p>Invalid account number.</p>");
                return;
            }

            // Get the current balance
            double currentBalance = getCurrentBalance(accountNumber);

            // Calculate the new balance
            double newBalance = currentBalance + amount;

            // Update the account with the new balance
            if (updateBalance(accountNumber, newBalance)) {
                // Record the transaction in the transactions table
                if (recordTransaction(accountNumber, amount)) {
                    out.println("<html>");
                    out.println("<head><title>Funds Credited</title></head>");
                    out.println("<body>");
                    out.println("<script type='text/javascript'>");
                    out.println("alert('₹" + amount + " credited successfully. New balance: ₹" + newBalance + "');");
                    out.println("window.location.href = 'customerDashboard.html';"); 
                    out.println("</script>");
                    out.println("</body>");
                    out.println("</html>");
                } else {
                    out.println("<p>Error occurred while recording the transaction. Please try again.</p>");
                }
            } else {
                out.println("<p>Error occurred while crediting funds. Please try again.</p>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}

