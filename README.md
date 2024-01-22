# YouMarket

YouMarket is an online marketplace designed for anyone to be able to buy or sell their products.

## Usage
To compile and run the project using the terminal, run the following commands.
```bash
javac Server.java
java Server

# YouMarket supports multiple users, so to access YouMarket concurrently, run the following commands in separate terminal windows.
javac Client.java
java Client
```
To compile and run the project using an IDE such as IntelliJ, run the `Server.java` followed by the `Client.java` file.  
To run more than one instance of `Client` through IntelliJ, right-click on `Client.java`, select _Modify Run Configuration_, click on _Modify Options_, and check the value of _Allow multiple instances_. You will then be able to run multiple instances of Client at one time.

## Submission

Report and video submitted by Prashant Rajesh Iyer.  
Code submitted by Andrew Lu.

## Classes

### Client
Client is the class that the user runs, and handles all interaction with the user through a GUI, including input validation.  
It interacts with `Person`, `Customer`, `Seller`, `Store`, `Sale`, `Product`, and `Market` on the "front-end" and with `Server` on the "back-end".

### Server
The Server class is a part of the back-end of YouMarket. It handles service calls from the `Client` made using a Socket.  
The Server class uses multi-threading to handle more than one user at a time. This is implemented through the `ServerThread` class. A new thread is created for each user.
It interacts with `ServerThread` and `Client`.

### ServerThread
The ServerThread class is a part of the back-end of YouMarket. It handles the application's data and returns the appropriate values to the `Client` class through Socket. All data handling can essentially be put into 4 categories: Create, Read, Update, and Delete.  
It interacts with `Person`, `Customer`, `Seller`, `Store`, `Sale`, `Product`, `Market`, and `Client`.

### Person
Person handles general operations for all users, most importantly validating logins and editing account information.  
Reads and writes to the `users.txt` file.  
To test this class, we have created a PersonTest junit test class, which runs multiple test cases: initialization, adding a user, editing a user, deleting a user, and writing to file. This way all the methods' functionality can be tested.

### Customer
Subclass of Person, which includes additional functionality for customers, such as their cart, stored as parallel lists of Products and quantities, and purchase history, stored as a list of Sales. Also helps render the customer dashboard.  
Reads and writes to the `customers.txt` file, and can additionally export purchase history.  
To test this class, we have created a CustomerTest junit test class, which runs multiple test cases: initialization, adding a Customer, modifying the shopping cart, managing the dashboard, and writing to file. This way all the methods' functionality can be tested.

### Seller
Subclass of Person, which includes additional functionality for sellers, such as managing Stores, viewing Customer carts, and viewing the seller dashboard.  
Reads and writes to the `sellers.txt` file.
To test this class, we have created a SellerTest junit test class, which runs multiple test cases: initialization, adding a Seller, modifying stores, managing the dashboard, and writing to file. This way all the methods' functionality can be tested.

### Store
Store represents a store in the market, and stores a list of Products, previous Customers, and a counter of total products sold.  
Reads and writes to the `stores.txt` file, and can additionally import and export Product information
from CSV files.
To test this class, we have created a StoreTest junit test class, which runs multiple test cases: initialization, adding a Store, modifying the Store, deleting the Store, and writing to file. This way all the methods' functionality can be tested.

### Sale
Sale represents a purchase, and stores the Product and quantity purchased, purchase price, Customer who purchased it,
and the Store it was purchased from.  
Reads and writes to the `sales.txt` file.
To test this class, we have created a SaleTest junit test class, which runs multiple test cases: initialization, adding a Sale, accessing Sale objects, and writing to file. This way all the methods' functionality can be tested.

### Product
Product represents a product on the market, and stores the product's name, description, quantity, and price, as well as the Store selling the product. Additionally, when a Customer tries to purchase a quantity of product, validates that
there is enough of that product in stock.  
Reads and writes to the `products.txt` file.  
To test this class, we have created a ProductTest junit test class, which runs multiple test cases: initialization, adding a Product, accessing Product objects, modifying a Product, deleting a Product, and writing to file. This way all the methods' functionality can be tested.

### Market
Market handles market operations, and includes functionality for sorting and searching the market for Products.  
To test this class, we have created a MarketTest junit test class, which runs multiple test cases: initialization, searching for products, and writing to file. This way all the methods' functionality can be tested.

## Contributors
Andrew Lu  
Karsten Palm  
Maanas Karwa  
Prashant Rajesh Iyer  
Trisha Godara