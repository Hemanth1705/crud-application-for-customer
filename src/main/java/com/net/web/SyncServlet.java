package com.net.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/SyncServlet")
public class SyncServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // Step 1: Authenticate and get Bearer token
            String authToken = authenticateUser();
            if (authToken == null) {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to authenticate");
                out.print(result.toString());
                return;
            }

            // Step 2: Fetch customer list
            JsonArray customerList = fetchCustomerList(authToken);
            if (customerList == null) {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to fetch customer list");
                out.print(result.toString());
                return;
            }

            // Step 3: Process customer list and update database
            boolean dbUpdateSuccess = processCustomerList(customerList);
            if (!dbUpdateSuccess) {
                result.addProperty("success", false);
                result.addProperty("message", "Failed to update database");
                out.print(result.toString());
                return;
            }

            result.addProperty("success", true);
            result.addProperty("message", "Customers synced successfully");
            out.print(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", "An error occurred: " + e.getMessage());
            out.print(result.toString());
        } finally {
            out.close();
        }
    }

    private String authenticateUser() {
        try {
            URL url = new URL("https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"login_id\":\"test@sunbasedata.com\",\"password\":\"Test@123\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                return jsonResponse.get("token").toString();
            } else {
                System.out.println("Failed to authenticate. Response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonArray fetchCustomerList(String token) {
        try {
            URL url = new URL("https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                return JsonParser.parseString(response).getAsJsonArray();
            } else {
                System.out.println("Failed to fetch customer list. Response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean processCustomerList(JsonArray customerList) {
        try {
            // Database connection setup
            String jdbcURL = "jdbc:mysql://localhost:3306/your_database_name"; // Replace with your database URL
            String dbUser = "your_db_username"; // Replace with your database username
            String dbPassword = "your_db_password"; // Replace with your database password
            Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            for (int i = 0; i < customerList.size(); i++) {
                JsonObject customer = ((JsonElement) customerList.get(i)).getAsJsonObject();
                String uuid = customer.get("uuid").toString();
                String firstName = customer.get("first_name").toString();
                String lastName = customer.get("last_name").toString();
                String street = customer.get("street").toString();
                String address = customer.get("address").toString();
                String city = customer.get("city").toString();
                String state = customer.get("state").toString();
                String email = customer.get("email").toString();
                String phone = customer.get("phone").toString();

                // Check if customer exists
                String selectQuery = "SELECT * FROM customers WHERE uuid = ?";
                PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                selectStmt.setString(1, uuid);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    // Customer exists, update the record
                    String updateQuery = "UPDATE customers SET first_name = ?, last_name = ?, street = ?, address = ?, city = ?, state = ?, email = ?, phone = ? WHERE uuid = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, firstName);
                    updateStmt.setString(2, lastName);
                    updateStmt.setString(3, street);
                    updateStmt.setString(4, address);
                    updateStmt.setString(5, city);
                    updateStmt.setString(6, state);
                    updateStmt.setString(7, email);
                    updateStmt.setString(8, phone);
                    updateStmt.setString(9, uuid);
                    updateStmt.executeUpdate();
                } else {
                    // Customer does not exist, insert new record
                    String insertQuery = "INSERT INTO customers (uuid, first_name, last_name, street, address, city, state, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                    insertStmt.setString(1, uuid);
                    insertStmt.setString(2, firstName);
                    insertStmt.setString(3, lastName);
                    insertStmt.setString(4, street);
                    insertStmt.setString(5, address);
                    insertStmt.setString(6, city);
                    insertStmt.setString(7, state);
                    insertStmt.setString(8, email);
                    insertStmt.setString(9, phone);
                    insertStmt.executeUpdate();
                }

                rs.close();
                selectStmt.close();
            }

            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
