package com.spacebank;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class RegisterBankServlet extends HttpServlet {

    private static final String db_url = "jdbc:mysql://localhost:3306/space_bank";
    private static final String db_username = "root";
    private static final String db_password = "anshsharma07";
    
    // Function to generate Account number of 10 digits
    private String generateAccountNumber() {
        Random r = new Random();
        // Number doesn't start with 0
        int firstDigit = r.nextInt(9) + 1; // Random first digit between 1 and 9
        long randomNumber = 100_000_000L + (long) (Math.random() * 900_000_000L); // Generate random number between 100,000,000 and 999,999,999

        return String.format("%d%09d", firstDigit, randomNumber); // Combine first digit and 9-digit number
    }

    // Function to check if an account number already exists
    private boolean accountNumberExist(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM account_users WHERE accountNumber = ?";
        
        try (Connection c = DriverManager.getConnection(db_url, db_username, db_password);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // If count > 0, account number exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Session check (if login required for registration)
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            res.sendRedirect("login.html");
            return;
        }

        // Retrieve form parameters
        String name = req.getParameter("name");
        String fname = req.getParameter("fname");
        String email = req.getParameter("email");
        String adhaar = req.getParameter("adhaar");
        String mobileno = req.getParameter("mobileno");
        String gender = req.getParameter("gender");
        String ibalance = req.getParameter("ibalance");

        // Validate if required fields are empty
        if (name == null || fname == null || email == null || adhaar == null || mobileno == null || gender == null || ibalance == null || 
            name.isEmpty() || fname.isEmpty() || email.isEmpty() || adhaar.isEmpty() || mobileno.isEmpty() || gender.isEmpty() || ibalance.isEmpty()) {
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

        // Validate email format (simple regex for validation)
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Invalid email format');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Validate mobile number format (simple check for length)
        if (!mobileno.matches("\\d{10}")) {
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Invalid mobile number format. It should be 10 digits');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        // Convert ibalance to BigDecimal and check validity
        BigDecimal initialBalance = null;
        try {
            initialBalance = new BigDecimal(ibalance);
        } catch (NumberFormatException e) {
            out.println("<html>");
            out.println("<head>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Invalid initial balance');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
            out.println("</head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        String accountNumber = generateAccountNumber();
        while (accountNumberExist(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        try (Connection c = DriverManager.getConnection(db_url, db_username, db_password)) {
            String sql = "INSERT INTO account_users (name, f_name, email, adhaar, mobileno, gender, ibalance, accountNumber, registration_date)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, fname);
                ps.setString(3, email);
                ps.setString(4, adhaar);
                ps.setString(5, mobileno);
                ps.setString(6, gender);
                ps.setBigDecimal(7, initialBalance);
                ps.setString(8, accountNumber);

    
                int result = ps.executeUpdate();

                if (result > 0) {
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<script type='text/javascript'>");
                    out.println("alert('Account successfully created with account number: " + accountNumber + "');");
                    out.println("window.location.href = 'customerDashboard.html';");
                    out.println("</script>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("</body>");
                    out.println("</html>");
                } else {
                    out.println("<p>There was an error while creating the account. Please try again.</p>");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("<p>Error: " + e.getMessage() + "</p>");
            }
        } catch (SQLException e1) {
			e1.printStackTrace();
		}

    }
}


