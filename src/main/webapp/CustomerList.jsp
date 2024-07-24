<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer List</title>
    <style>
        /* Basic styling for the page */
        body {
            font-family: Arial, sans-serif;
        }
        .container {
            width: 90%;
            margin: auto;
            overflow: hidden;
        }
        h2 {
            text-align: center;
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        table, th, td {
            border: 1px solid #dddddd;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .filter-container {
            margin-bottom: 20px;
        }
        .filter-container label,
        .filter-container select,
        .filter-container input {
            margin-right: 6px;
        }
        .filter-container input[type="text"] {
            padding: 5px;
        }
        .filter-container input[type="button"] {
            padding: 5px 10px;
        }
        .btn {
            padding: 5px 10px;
            text-decoration: none;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 4px;
        }
        .btn-warning {
            background-color: #f0ad4e;
        }
        .btn-danger {
            background-color: #d9534f;
        }
        .btn-primary {
            background-color: #FF6600;
        }
    </style>
    <script>
        // JavaScript function to filter the customer table based on the filter input
        function filterTable() {
            var filterValue = document.getElementById("filterValue").value.toLowerCase();
            var filterType = document.getElementById("filterType").value;
            var table = document.getElementById("customerTable");
            var tr = table.getElementsByTagName("tr");

            for (var i = 1; i < tr.length; i++) {
                var td = tr[i].getElementsByTagName("td")[filterType];
                if (td) {
                    var txtValue = td.textContent || td.innerText;
                    if (txtValue.toLowerCase().indexOf(filterValue) > -1) {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }
        }
    </script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        // JavaScript function to sync customers with the remote API
        function syncCustomers() {
            $.ajax({
                url: 'SyncServlet',
                type: 'GET',
                dataType: 'json',
                success: function(response) {
                    if (response.success) {
                        alert('Customers synced successfully');
                        // Reload the customer list or update the UI accordingly
                    } else {
                        alert('Sync failed: ' + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Sync Error:', status, error);
                    alert('An error occurred while syncing customers');
                }
            });
        }
    </script>
</head>
<body>
    <div class="container">
        <h2 style="margin-bottom: 30px;">Customer List</h2>
        
        <!-- Filter section with dropdown and input field -->
        <div class="filter-container">
            <a href="AddCustomer.jsp" class="btn btn-primary">Add Customer</a>
            
            <select id="filterType" style="margin-left:18px; background-color:#DDDDDD; height:27px;">
                <option value="1">First Name</option>
                <option value="5">City</option>
                <option value="7">Email</option>
                <option value="8">Phone</option>
            </select>
            <input type="text" id="filterValue" placeholder="Enter filter value">
            <input type="button" value="Search" onclick="filterTable()" style="background-color: #89E894;">
            <button onclick="syncCustomers()">Sync</button>
        </div>
        
        <!-- Customer table -->
        <table id="customerTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Street</th>
                    <th>Address</th>
                    <th>City</th>
                    <th>State</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <!-- Loop through the customer list and display each customer in a table row -->
                <c:forEach var="customer" items="${customerList}">
                    <tr>
                        <td>${customer.id}</td>
                        <td>${customer.firstName}</td>
                        <td>${customer.lastName}</td>
                        <td>${customer.street}</td>
                        <td>${customer.address}</td>
                        <td>${customer.city}</td>
                        <td>${customer.state}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phone}</td>
                        <td>
                            <a href="CustomerServlet?action=edit&id=${customer.id}" class="btn btn-warning">Edit</a>
                            <a href="CustomerServlet?action=delete&id=${customer.id}" class="btn btn-danger">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>
