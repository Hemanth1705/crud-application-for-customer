<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Add Customer</title>
<style>
	.btn {
            
            padding: 10px 20px;
            text-align: center;
            vertical-align: middle;
            cursor: pointer;
            background-color: #f0ad4e;
            border-radius: 4px;
            color: white;
            border: 2px solid black;
            margin-left: 8px;
        }
</style>
</head>
<body>
	<form action="CustomerServlet?action=insert" method="post">
		<div style="margin-left:550px; margin-top:160px">
		<div style="margin-left:10px;"><h1>Customer Details</h1></div>
		<div><input type="text" name="firstName" style="margin-left:-140px; height:22px; width:250px" placeholder="First Name" required><input type="text" name="lastName" style="margin-left:40px; height:22px; width:250px" placeholder="Last Name" required></div>
		<div style="margin-top:15px"><input type="text" name="street" style="margin-left:-140px; height:22px; width:250px" placeholder="Street" required><input type="text" name="address" style="margin-left:40px; height:22px; width:250px" placeholder="Address" required></div>
		<div style="margin-top:15px"><input type="text" name="city" style="margin-left:-140px; height:22px; width:250px" placeholder="City" required><input type="text" name="state" style="margin-left:40px; height:22px; width:250px" placeholder="State" required></div>
		<div style="margin-top:15px"><input type="text" name="email" style="margin-left:-140px; height:22px; width:250px" placeholder="Email" required><input type="number" name="phone" style="margin-left:40px; height:22px; width:250px" placeholder="Phone" required></div>
		<div style="margin-top:20px; margin-left:230px">
			<input type="submit" style="background-color:skyblue; color: white; border-radius: 4px; height:42px; width:90px; cursor:pointer">
			<a href="CustomerServlet?action=listCustomers" class="btn btn-warning">Cancel</a>
		</div>
		</div>
	</form>
</body>
</html>