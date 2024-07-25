# CRUD Application for Customer

## Overview
This project is a CRUD (Create, Read, Update, Delete) application for managing customer information. It is built using Java Servlets, JSP, and MySQL. The application allows users to add, view, edit, delete, and sync customer details from a remote API. The customer details include first name, last name, street, address, city, state, email, and phone number.

## Features
* Add new customers
* View a list of all customers
* Update existing customer details
* Delete customers
* Sync customer details with a remote API
* Filter customers based on first name, city, email, and phone number

## Technologies Used
* Java Servlets
* JSP (JavaServer Pages)
* MySQL
* JDBC (Java Database Connectivity)
* HTML, CSS, JavaScript
* jQuery

## Prerequisites
Before you begin, ensure you have met the following requirements:
* Java Development Kit (JDK) installed
* Apache Tomcat server installed
* MySQL server installed
* A MySQL database created for the project
* Git

## Getting Started
### Clone the Repository
    1. Open a terminal or command prompt.
    2. Navigate to the directory where you want to clone the repository.
    3. Run the following command:
        git clone https://github.com/Hemanth1705/crud-application-for-customer.git
### Import the Project into Eclipse
    1. Open Eclipse IDE.
    2. Go to File > Import
    3. Select General > Existing Projects into Workspace and click Next.
    4. Click Browse and select the directory where you cloned the repository.
    5. Ensure the project is checked in the Projects list and click Finish.
### Configure MySQL Database
    1. Create a new MySQL database called `customerdb`.
    2. Run the following SQL script to create the `customers` table:
       CREATE DATABASE customerdb;
       USE customerdb;
       
       CREATE TABLE customers (
        id INT NOT NULL AUTO_INCREMENT,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        street VARCHAR(100),
        address VARCHAR(100),
        city VARCHAR(50),
        state VARCHAR(50),
        email VARCHAR(100),
        phone VARCHAR(15),
        PRIMARY KEY (id)
        );
    3. As per the guidelines i didn't hardcode the login credentials of the database like url,username,password. I had created a seperate file with the name of `db.properties` and in that i had preserved the login credentials. And the file is located in build/classes.
### Deploy the Project
* Right-click on the project in Eclipse.
* Go to `Run As > Run on Server`.
* Select the Apache Tomcat server and click Finish.

## Project Structure
src
└── main
    └── java
        └── com
            └── net
                ├── dao
                │   └── CustomerDao.java
                ├── model
                │   └── Customer.java
                └── web
                    ├── CustomerServlet.java
                    ├── LoginServlet.java
                    └── SyncServlet.java
                ├── db.properties
    └── resources
    └── webapp
        ├── WEB-INF
        │   └── web.xml
        ├── AddCustomer.jsp
        ├── CustomerList.jsp
        ├── EditCustomer.jsp
        └── index.html

## Usage
1. ### Add a Customer
   * Click on the Add Customer button on the customer list page.
   * Fill in the customer details and submit the form.
2. ### View Customer List
    * The main page displays a list of all customers in the database.
3. ### Edit a Customer
    * Click on the Edit button next to a customer to modify their details.
    * Update the information and submit the form.
4. ### Delete a Customer
    * Click on the Delete button next to a customer to remove them from the database.

## Syncing Customers
    * The `Sync` button on the customer list page calls a remote API to fetch customer details and updates the local database. If a customer already exists, their details are updated; otherwise, a new customer is added.

## Filtering Customers
    * Use the filter dropdown and input field to filter customers based on first name, city, email, or phone number. Click the Search button to apply the filter.

## Troubleshooting
    * Database Connection Issues: Ensure your database URL, username, and password in `CustomerDao.java` are correct.
    * Server Deployment Issues: Ensure Apache Tomcat is properly configured in Eclipse.

### Important Note:
    * The login credentials are assigned as per the assignment so the loginid is `test@sunbasedata.com` and password is `Test@123`.
    * If you have any queries regarding the project please contact me through `rayapeduhemanthsrinivas@gmail.com `.
