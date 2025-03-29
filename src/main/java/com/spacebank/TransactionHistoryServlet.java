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

public class TransactionHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/space_bank";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "anshsharma07";

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String accountNumber = request.getParameter("account_number");

        // Validate the account number
        if (accountNumber == null || accountNumber.isEmpty()) {
            out.println("<p>Account number is required.</p>");
            return;
        }

        out.println("<html>");
        out.println("<head><title>Transaction History</title></head>");
        out.println("<body>");
        out.println("<h1>Transaction History</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Sender Account</th><th>Receiver Account</th><th>Amount (₹)</th><th>Transaction Type</th><th>Date</th></tr>");

        try (Connection conn = getConnection()) {
            String sql = "SELECT sender_account, receiver_account, amount, transaction_type, transaction_date "
                       + "FROM transactions WHERE sender_account = ? OR receiver_account = ? ORDER BY transaction_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accountNumber);
                stmt.setString(2, accountNumber);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String senderAccount = rs.getString("sender_account");
                        String receiverAccount = rs.getString("receiver_account");
                        double amount = rs.getDouble("amount");
                        String transactionType = rs.getString("transaction_type");
                        String transactionDate = rs.getString("transaction_date");

                        out.println("<tr>");
                        out.println("<td>" + senderAccount + "</td>");
                        out.println("<td>" + receiverAccount + "</td>");
                        out.println("<td>" + amount + "</td>");
                        out.println("<td>" + transactionType + "</td>");
                        out.println("<td>" + transactionDate + "</td>");
                        out.println("</tr>");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error retrieving transaction history: " + e.getMessage() + "</p>");
        }

        out.println("</table>");
        out.println("<p><a href='customerDashboard.html'>Back to Dashboard</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
