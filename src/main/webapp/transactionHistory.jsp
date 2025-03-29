<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.util.*" %>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> --%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History</title>
    <style>
        /* The same styles you provided */
    </style>
</head>

<body>

    <div class="container">
        <div class="transaction-container">
            <h1>Transaction History</h1>

            <!-- Form to input account number -->
            <form action="transactionHistory.jsp" method="GET">
                <label for="account_number">Enter Account Number:</label>
                <input type="text" id="account_number" name="account_number" required>
                <button type="submit">View History</button>
            </form>

            <%
                // Establish connection and fetch data only if account_number is provided
                String accountNumber = request.getParameter("account_number");
                List<Map<String, Object>> transactions = new ArrayList<>();
                
                if (accountNumber != null && !accountNumber.trim().isEmpty()) {
                    Connection conn = null;
                    PreparedStatement stmt = null;
                    ResultSet rs = null;

                    String dbUrl = "jdbc:mysql://127.0.0.1:3306/space_bank";
                    String dbUsername = "root";
                    String dbPassword = "anshsharma07";
                    
                    try {
                        // Load MySQL JDBC Driver
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        // Connect to database
                        conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

                        // SQL Query to fetch transaction data for the given account number
                        String sql = "SELECT senderAccountNumber, receiverAccountNumber, amount, transactionType, transactionDate " +
                                     "FROM transactions WHERE senderAccountNumber = ? OR receiverAccountNumber = ? ORDER BY transactionDate DESC";
                        stmt = conn.prepareStatement(sql);
                        stmt.setString(1, accountNumber);
                        stmt.setString(2, accountNumber);
                        rs = stmt.executeQuery();

                        while (rs.next()) {
                            Map<String, Object> transaction = new HashMap<>();
                            transaction.put("senderAccount", rs.getString("senderAccountNumber"));
                            transaction.put("receiverAccount", rs.getString("receiverAccountNumber"));
                            transaction.put("amount", rs.getDouble("amount"));
                            transaction.put("transactionType", rs.getString("transactionType"));
                            transaction.put("transactionDate", rs.getTimestamp("transactionDate"));
                            transactions.add(transaction);
                        }

                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                        out.println("<p>Error: " + e.getMessage() + "</p>");
                    } finally {
                        try {
                            if (rs != null) rs.close();
                            if (stmt != null) stmt.close();
                            if (conn != null) conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            %>

            <!-- Transaction History Table -->
            <table>
                <thead>
                    <tr>
                        <th>Sender Account</th>
                        <th>Receiver Account</th>
                        <th>Amount (₹)</th>
                        <th>Transaction Type</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Displaying the transaction data fetched from the database --%>
                    <c:forEach var="transaction" items="<%= transactions %>">
                        <tr>
                            <td><%= transaction.get("senderAccount") %></td>
                            <td><%= transaction.get("receiverAccount") %></td>
                            <td><%= transaction.get("amount") %></td>
                            <td><%= transaction.get("transactionType") %></td>
                            <td><%= transaction.get("transactionDate") %></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <a href="customerDashboard.html">Back to Dashboard</a>
        </div>
    </div>

</body>

</html>
