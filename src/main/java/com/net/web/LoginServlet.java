package com.net.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.jsonwebtoken.io.IOException;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, java.io.IOException {
        String loginid = request.getParameter("loginid");
        String password = request.getParameter("password");
        
        HttpSession session = request.getSession();
        session.setAttribute("user", loginid);

        if ("test@sunbasedata.com".equals(loginid) && "Test@123".equals(password)) 
        {   
		response.sendRedirect(("CustomerServlet?action=listCustomers"));
        } 
        else 
        {
		response.sendRedirect("index.html");
        }
    }
}

