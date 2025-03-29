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

public class TransferFundServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/space_bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "anshsharma07";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found.");
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isAccountExists(String accountNumber) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) FROM account_users WHERE accountNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }
    }

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

    private boolean recordTransaction(String senderAccount, String receiverAccount, double amount, String transactionType) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO transactions (senderAccountNumber, receiverAccountNumber, amount, transactionType) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, senderAccount);
                stmt.setString(2, receiverAccount);
                stmt.setDouble(3, amount);
                stmt.setString(4, transactionType);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String senderAccount = request.getParameter("sender_account");
        String receiverAccount = request.getParameter("receiver_account");
        String amountStr = request.getParameter("amount");

        // Validate the input
        if (senderAccount == null || senderAccount.isEmpty() || receiverAccount == null || receiverAccount.isEmpty() || amountStr == null || amountStr.isEmpty()) {
            out.println("<p>All fields are required.</p>");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            out.println("<p>Invalid amount entered.</p>");
            return;
        }

        try {
            if (!isAccountExists(senderAccount)) {
                out.println("<p>Sender account does not exist.</p>");
                return;
            }

            if (!isAccountExists(receiverAccount)) {
                out.println("<p>Receiver account does not exist.</p>");
                return;
            }

            double senderBalance = getBalance(senderAccount);
            if (senderBalance < amount) {
                out.println("<p>Insufficient funds in your account.</p>");
                return;
            }

            double newSenderBalance = senderBalance - amount;
            if (!updateBalance(senderAccount, newSenderBalance)) {
                out.println("<p>Failed to update sender's balance.</p>");
                return;
            }

            double receiverBalance = getBalance(receiverAccount);
            double newReceiverBalance = receiverBalance + amount;
            if (!updateBalance(receiverAccount, newReceiverBalance)) {
                out.println("<p>Failed to update receiver's balance.</p>");
                return;
            }

            // Record transaction for sender (debit)
            if (!recordTransaction(senderAccount, receiverAccount, amount, "debit")) {
                out.println("<p>Failed to record debit transaction.</p>");
                return;
            }

            // Record transaction for receiver (credit)
            if (!recordTransaction(receiverAccount, senderAccount, amount, "credit")) {
                out.println("<p>Failed to record credit transaction.</p>");
                return;
            }

            out.println("<html>");
            out.println("<head><title>Transfer Funds</title></head>");
            out.println("<body>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Rs" + amount + " transferred successfully.');");
            out.println("window.location.href = 'customerDashboard.html';");
            out.println("</script>");
            out.println("</body>");
            out.println("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}


