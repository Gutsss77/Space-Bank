package com.spacebank;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {
	
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 response.setContentType("text/html");
     HttpSession session = request.getSession(false);
     if (session != null) {
         session.invalidate();
     }
     PrintWriter out = response.getWriter();
     out.println("<html>");
     out.println("<head><title>Transfer Funds</title></head>");
     out.println("<body>");
     out.println("<script type='text/javascript'>");
     out.println("alert('Log out Successfully');");
     out.println("window.location.href = 'index.html';");
     out.println("</script>");
     out.println("</body>");
     out.println("</html>");
//     response.sendRedirect("index.html");
 }
}
