package com.net.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.InputStream;

import com.net.model.Customer;

public class CustomerDao {
    static Logger log = Logger.getLogger(CustomerDao.class.getName());

    // SQL queries used in the DAO
    private static final String INSERT_CUSTOMERS_SQL = "INSERT INTO customers" +
            " (firstName, lastName, street, address, city, state, email, phone) VALUES " +
            " (?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_CUSTOMER_BY_ID = "SELECT id, firstName, lastName, street, address, city, state, email, phone FROM customers WHERE id = ?";
    private static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM customers ORDER BY id";
    private static final String DELETE_CUSTOMERS_SQL = "DELETE FROM customers WHERE id = ?;";
    private static final String UPDATE_CUSTOMERS_SQL = "UPDATE customers SET firstName = ?, lastName = ?, street = ?, address = ?, city = ?, state = ?, email = ?, phone = ? WHERE id = ?;";
    private static final String RESET_AUTO_INCREMENT_SQL = "ALTER TABLE customers AUTO_INCREMENT = ?";

    // Method to establish a connection to the database
    protected Connection getConnection() {
        Connection connection = null;
        try {
            // Fetch database credentials from environment variables
            String jdbcURL = System.getenv("JDBC_URL");
            String jdbcUsername = System.getenv("JDBC_USERNAME");
            String jdbcPassword = System.getenv("JDBC_PASSWORD");
            
            // Check if environment variables are null, if so, load properties from file
            if (jdbcURL == null || jdbcUsername == null || jdbcPassword == null) {
                Properties props = new Properties();
                try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                    if (input == null) {
                        throw new SQLException("Unable to find db.properties");
                    }
                    props.load(input);
                    jdbcURL = props.getProperty("jdbc.url");
                    jdbcUsername = props.getProperty("jdbc.username");
                    jdbcPassword = props.getProperty("jdbc.password");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Load and register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Method to insert a new customer into the database
    public void insertCustomer(Customer customer) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMERS_SQL)) {
            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getStreet());
            preparedStatement.setString(4, customer.getAddress());
            preparedStatement.setString(5, customer.getCity());
            preparedStatement.setString(6, customer.getState());
            preparedStatement.setString(7, customer.getEmail());
            preparedStatement.setString(8, customer.getPhone());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    // Method to select a customer by their ID
    public Customer selectCustomer(int id) {
        Customer customer = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            // Retrieve customer details from the result set
            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String street = rs.getString("street");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                customer = new Customer(id, firstName, lastName, street, address, city, state, email, phone);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customer;
    }

    // Method to select all customers from the database
    public List<Customer> selectAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CUSTOMERS)) {
            ResultSet rs = preparedStatement.executeQuery();

            // Retrieve customer details from the result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String street = rs.getString("street");
                String address = rs.getString("address");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                customers.add(new Customer(id, firstName, lastName, street, address, city, state, email, phone));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customers;
    }

    // Method to delete a customer by their ID
    public boolean deleteCustomer(int id) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_CUSTOMERS_SQL)) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }

        if (rowDeleted) {
            // Renumber remaining customers to ensure continuous ID sequence
            renumberCustomers();

            // Reset auto-increment value
            resetAutoIncrement();
        }

        return rowDeleted;
    }

    // Method to reset the auto-increment value of the customer table
    private void resetAutoIncrement() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // Get the maximum current ID
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM customers");
            if (rs.next()) {
                int maxId = rs.getInt(1) + 1;
                try (PreparedStatement resetStatement = connection.prepareStatement(RESET_AUTO_INCREMENT_SQL)) {
                    resetStatement.setInt(1, maxId);
                    resetStatement.executeUpdate();
                }
            }
        }
    }

    // Method to renumber customer IDs to ensure continuous sequence
    private void renumberCustomers() throws SQLException {
        List<Customer> customers = selectAllCustomers();
        try (Connection connection = getConnection();
             PreparedStatement updateStatement = connection.prepareStatement("UPDATE customers SET id = ? WHERE id = ?")) {
            int newId = 1;
            for (Customer customer : customers) {
                updateStatement.setInt(1, newId);
                updateStatement.setInt(2, customer.getId());
                updateStatement.executeUpdate();
                newId++;
            }
        }
    }

    // Method to update customer details in the database
    public boolean updateCustomer(Customer customer) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_CUSTOMERS_SQL)) {
            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getStreet());
            statement.setString(4, customer.getAddress());
            statement.setString(5, customer.getCity());
            statement.setString(6, customer.getState());
            statement.setString(7, customer.getEmail());
            statement.setString(8, customer.getPhone());
            statement.setInt(9, customer.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    // Method to print SQL exceptions for debugging purposes
    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
