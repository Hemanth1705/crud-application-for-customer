package com.net.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.net.dao.CustomerDao;
import com.net.model.Customer;

public class CustomerServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
    private CustomerDao customerDAO;
    
    public CustomerServlet() 
    {
        // Initialize CustomerDao
    	this.customerDAO = new CustomerDao();
	}
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        // Delegate POST requests to doGet method
    	this.doGet(req, resp);
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
        // Retrieve the action parameter from the request
		String action = request.getParameter("action");
		System.out.println("Action: " + action);
		
        // Default action if no action parameter is provided
		if (action == null) 
		{
            action = "listCustomers";
        }
		
        // Handle different actions based on the action parameter
        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "insert":
                    insertCustomer(request, response);
                    break;
                case "delete":
                    deleteCustomer(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updateCustomer(request, response);
                    break;
                default:
                    listCustomers(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
	
    // Show the form to create a new customer
	private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws SQLException,ServletException,IOException
	{
		RequestDispatcher dispatcher = request.getRequestDispatcher("CustomerList.jsp");
		dispatcher.forward(request, response);
	}
	
    // List all customers and forward to the customer list page
    private void listCustomers(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Customer> customers = customerDAO.selectAllCustomers();
        
        // Print each customer to the console for debugging
        for (Customer customer : customers) 
        {
            System.out.println(customer);
        } 
        
        // Set the customer list as a request attribute and forward to the JSP
        request.setAttribute("customerList", customers);
        request.getRequestDispatcher("CustomerList.jsp").forward(request, response);
    }

    // Show the form to edit an existing customer
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer existingCustomer = customerDAO.selectCustomer(id);
        request.setAttribute("customers", existingCustomer);
        request.getRequestDispatcher("EditCustomer.jsp").forward(request, response);
    }

    // Insert a new customer into the database
    private void insertCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String street = request.getParameter("street");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        Customer newCustomer = new Customer(0, firstName, lastName, street, address, city, state, email, phone);
        customerDAO.insertCustomer(newCustomer);
        
        // Redirect to the customer list page after insertion
        response.sendRedirect("CustomerServlet?action=listCustomers");
    }

    // Update an existing customer in the database
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String street = request.getParameter("street");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        Customer updatedCustomer = new Customer(id, firstName, lastName, street, address, city, state, email, phone);
        customerDAO.updateCustomer(updatedCustomer);
        
        // Redirect to the customer list page after updating
        response.sendRedirect("CustomerServlet?action=listCustomers");
    }

    // Delete a customer from the database
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        customerDAO.deleteCustomer(id);
        
        // Redirect to the customer list page after deletion
        response.sendRedirect("CustomerServlet?action=listCustomers");
    }
}
