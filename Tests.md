# Test 1: User Creation
## Steps:
1. User launches application and clicks ok or the "x" button to move on from the welcome window.
2. User selects "create account" option from the drop-down menu and clicks ok.
3. User selects the name textbox.
4. User enters name via the keyboard.
5. User selects the email textbox.
6. User enters email via the keyboard.
7. User selects the password textbox.
8. User enters password via the keyboard.
9. User selects type of account ("Seller"/"Customer") from drop down menu.
10. User selects the "create account" button.
11. User selects the ok or "x" button.
12. User exits application by clicking the "x" button.

### Expected result:
Application verifies the user's email uniqueness, welcomes the user, and loads their new homepage.
(if Seller type of account, drop-down options regarding stores will be displayed;
if Customer type of account, drop-down options regarding market and shopping cart will be displayed;
both types will have account and dashboard options).

If email is taken, an error message popup shows up and tells user to choose a different email.

If the user presses the "x" button when asked to login/create account, a "thank you for using YouMarket" message 
appears before exit.

### Test Status: Passed

# Test 2: Existing User Login
## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User selects "Log in" option and clicks ok.
3. User selects the email textbox.
4. User enters email via the keyboard.
5. User selects the password textbox.
6. User enters password via the keyboard.
7. User selects the "Login" button.

### Expected result:
Application verifies the user's email and password and welcomes the user.

If email and/or password is incorrect, an error message popup prompts the user to try again.

### Test Status: Passed

# Test 3: Multiple People Logging In
Note: Before performing this test, make sure at least two users have been created, so you can login with different
credentials (however, logging in with the same account from two different clients works the same as well).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User selects "Log in" option and clicks ok.
3. User selects the email textbox.
4. User enters email via the keyboard.
5. User selects the password textbox.
6. User enters password via the keyboard.
7. User selects the "Login" button.
8. User opens another client and follows above instructions with a different account's login details.

### Expected result:
Application verifies the users' emails and passwords and loads their respective homepages automatically.

If email and/or password is incorrect, an error message popup prompts the user to try again.

### Test Status: Passed

# Test 4: Seller - Adding a Store and a Product Manually
Note: Please use previous tests for instructions on how to create an account and log in (this test assumes a
seller account has been created to test the features of a seller account).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a seller account.
3. User selects "manage stores" option and clicks enter.
4. User selects "create new store" button.
5. User enters store name and clicks ok.
6. User selects store to add products to from drop-down menu.
7. User selects "edit store" button.
8. User selects "create new product" button.
9. User enters name of product.
10. User enters description of product.
11. User enters quantity of product (total stock).
12. User enters price of product.
13. User selects "confirm changes" button.

### Expected result:
Application shows the names of all products in product drop-down menu.

When creating a product if product quantity or price entered is less than 0 or not a number, an error popup message 
will be shown and the user is prompted to enter a valid number.

### Test Status: Passed

# Test 5: Seller - Adding Products From File
Note: Please use previous tests for instructions on how to create an account and log in (this test assumes a
seller account has been created to test the features of a seller account). Please also make sure a store has been
created prior to this test to test adding products to that store. You will also need a file with product information
written in it (in correct format - Name,Description,Quantity,Price with each product on separate lines).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a seller account.
3. User selects "manage stores" option and clicks enter.
4. User selects "create new store" button.
5. User enters store name and clicks ok.
6. User selects store to add products to from drop-down menu.
7. User selects "edit store" button.
8. User selects "create new product" button.
9. User selects "import products from file" button.
10. User selects appropriate file from file explorer menu.
11. User clicks ok or "x" button on success message.
12. User clicks "go back" button to return to product page.
13. User selects the drop-down menu to see all products.
14. User selects one of the newly added products and selects "edit product".

### Expected result:
Once products are successfully imported, application will show them in the drop-down menu. Upon clicking "edit
product," the user will be able to see the information for that particular product (to ensure it is as entered in
the file).

If an invalid file is selected, application will show an error message regarding it.
If the file is formatted incorrectly in one or more lines, the program will try to extract any products that *are* 
formatted correctly and will notify the user with an error popup after importing that not all products were 
successfully imported.

### Test Status: Passed

# Test 6: Customer - Adding a Product to Cart and Checkout
Note: Please use previous tests for instructions on how to create an account and log in (this test assumes a
customer account has been created to test the features of a customer account). This test also requires at least 1
store and product to test out features (recommended to perform this test after test 4 and/or 5)

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks "enter".
4. User selects a product in drop down menu under the table of products and clicks "select product" button at bottom 
   left of screen.
5. User inputs quantity in "amount to purchase" textbox.
6. User selects "put item in cart" button.
7. User selects "go back" button to return to home page.
8. User selects "view shopping cart" option and clicks enter.
9. User selects "purchase all" button.
10. User selects "go back" button.
11. User selects "view purchase history" option and clicks "enter".

### Expected result:
Customer shopping cart is now empty, and the application shows the product and the amount purchased in the
customer's purchase history page.

If customer tries to add more quantity than available, an error popup notifies them.
If value inputted is not a valid number (must be integer greater than 0), an error popup notifies them.

### Test Status: Passed

# Test 7: Seller - Dashboard
Note: Please use previous tests for instructions on how to create an account and log in (this test assumes a
seller account has been created to test the features of a seller account). Please also make sure at least one store has
been created prior to this test to truly see the functionality of the dashboard (for more features, use customer
account(s) to buy products from the seller's store(s)). Recommended to perform this test after test 6.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a seller account.
3. User selects "view dashboard" option and clicks enter.
4. User selects the store they want to see stats of and clicks ok button.
5. User can select to sort both views in ascending or descending order by clicking the buttons.

### Expected result:
Application shows user a screen with all products of that store with number of sales and total revenue from that 
product in a neat format. User can also see which customer(s) have bought how many of their products from that store.
Sorting occurs in respect to amount of sales for product/customer.

### Test Status: Passed

# Test 8: Seller - View Customer Shopping Carts
Note: Please use previous tests for instructions on how to create an account and log in (this test assumes a
seller account has been created to test the features of a seller account). Please also make sure at least one store 
and one product has been created prior to this test to truly see the functionality of this feature (to ensure it is 
working, use customer account(s) to put some of the seller's products in cart -- before actually purchasing).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a seller account.
3. User selects "view shopping carts" option and clicks enter.

### Expected result:
Application shows a screen with product information per customer who has a product of that seller in their cart at 
the present moment.

### Test Status: Passed

# Test 9: Customer & Seller Interaction - Refreshing the Marketplace
Note: As with previous tests, you will need a seller account with at least one store and one product and at least 
one customer account.

This test involves sellers changing product details as a customer is viewing the marketplace to purchase a product. 
This test case also tests buying a product directly before adding it to cart (customer side) as well as editing a 
product (seller side).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks enter.
4. Meanwhile, user launches another client application and logs in with seller account.
5. User selects "manage stores" option and clicks enter.
6. User selects store in drop-down menu and clicks "edit store" button.
7. User selects product in drop-down menu and clicks "edit product" button.
8. User edits any of the details (for example, increasing the price).
9. User clicks "confirm changes" button.
10. Back on the customer application, user clicks "refresh market" button.
11. User selects product in drop-down menu and clicks "select product" button.
12. User enters quantity to purchase.
13. User clicks "buy" button.

### Expected result:
Customer side: Application shows marketplace with original details of product. Meanwhile, the seller edits the 
product. Therefore, after hitting the refresh button, the customer now sees the updated details. After clicking the 
buy button, a success message appears, notifying the user that products have been purchased. The quantity in stock 
remaining decreases respectively.

Seller side: After clicking edit product button, the seller is free to just view or modify any category, including 
name, description, quantity in stock, and the price.

### Test Status: Passed

# Test 10: Customer - Multiple Customers Buying Products
Note: As with previous tests, you will need a seller account with at least one store and one product (to show up 
in the marketplace) and at least two customer accounts.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks enter.
4. User selects a product and adds to cart (*see test 6 for details on adding to cart*).
5. Meanwhile, user launches another client application and logs in with another customer account.
6. User selects "view marketplace" option and clicks enter.
7. User selects the same product and adds to cart.
8. User goes back to customer 1 and checks out items from cart (*see test 6 for details*).
9. User goes back to customer 2 and checks out items from cart.

### Expected result:
Customer 1: Since this person checks out first, result does not change for them (*see test 6*).

Customer 2: Depending on how much this customer added to cart and how much is left in stock after the other person 
made their purchase, the purchase may or may not be successful. If there are enough items left in stock, the 
shopping cart will empty successfully. If there is not enough left in stock anymore, an error message will pop up, 
notifying the user which products were not able to be purchased at the quantity they wanted. User then has the 
option to remove the item from cart and go back to the marketplace to reorder the product at a different quantity if 
available.

### Test Status: Passed

# Test 11: Seller & Customer - Exporting Files
Note: For this test, you will need a seller account with at least one store and one product and at least 
one customer account that has purchased something.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view purchase history" option and clicks enter.
4. User clicks "export to file" button.
5. User picks destination and file name using file explorer.
6. User launches another client application and logs in with seller account.
7. User selects "manage stores" option and clicks enter.
8. User selects a store from drop-down menu.
9. User clicks "export product info to file" button.

### Expected result:
Customer: Purchase history will be available with comma-separated values in the file that user selected to save it in.

Seller: All product information of selected store will be available with comma-separated values in the file that user 
selected to save it in.

### Test Status: Passed

# Test 12: Customer - Dashboard
Note: For this test, you will need a customer account. To truly see and test all features, try using a customer 
account which has purchased items from at least two different stores.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view dashboard" option and clicks enter.
4. User clicks "ascending" for "view products bought by you".

### Expected result:
The dashboard can switch between showing stores sorted by products they've sold and showing stores based on how many 
of their products the user has bought. Both can be sorted in ascending and descending order, so after clicking the 
button in step 4, the application shows the products bought by user view in ascending order (default view is to show 
stores' products sold). Application also displays a refresh button if the user would like to check for new stores/data.

### Test Status: Passed

# Test 13: Seller - Deleting a Product
Note: For this test, you will need a seller account with at least one store and one product and at least
one customer (just to view the marketplace).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks enter.
4. Meanwhile, user launches another client application and logs in as a seller.
5. User selects "manage stores" option and clicks enter.
6. User selects store in drop-down menu and clicks "edit store" button.
7. User selects product from drop-down menu and clicks "delete product" button.
8. When asked to confirm, user selects yes.
9. User switches back to customer window and clicks "refresh market" button.

### Expected result:
The product will disappear from the marketplace as soon as it is refreshed. It is removed from the seller's product 
menu after the seller goes back a page or clicks any button (to refresh). If the product was already added to any customers' cart, it 
will be removed after a refresh (even if the user does not refresh before purchasing, they will still be notified that 
the purchase did not go through because the product was no longer available).

### Test Status: Passed

# Test 14: Seller - Editing Store Name
Note: For this test, you will need a seller account with at least one store and one product and at least
one customer (just to view the marketplace).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks enter.
4. Meanwhile, user launches another client application and logs in as a seller.
5. User selects "manage stores" option and clicks enter.
6. User selects store in drop-down menu and clicks "edit store" button.
7. User clicks "edit store name" button.
8. User enters new name and clicks ok.
9. User switches back to customer window and clicks "refresh market" button.

### Expected result:
All occurrences of the store name in the marketplace will be changed to the new store name.

# Test 15: Seller - Deleting a Store
Note: For this test, you will need a seller account with at least one store and one product and at least
one customer (just to view the marketplace).

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with a customer account.
3. User selects "view marketplace" option and clicks enter.
4. Meanwhile, user launches another client application and logs in as a seller.
5. User selects "manage stores" option and clicks enter.
6. User selects store in drop-down menu and clicks "delete store" button.
7. When asked to confirm, user selects yes.
8. User switches back to customer window and clicks "refresh market" button.

### Expected result:
All products associated with the store are removed from the marketplace and shopping carts. The store is removed from 
the seller's drop-down menu after the seller goes back a page or clicks any button (to refresh).

### Test Status: Passed

# Test 16: Changing User Name
Note: For this test, you can use a seller or a customer account.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with any account.
3. User selects "manage account" option and clicks enter.
4. User selects "name" from drop down menu.
5. User enters new name in textbox on top.
6. User clicks "confirm changes" button.
7. User exits application by clicking "x" button.
8. User launches application again and logs in.

### Expected result:
Application will welcome user with the new name.

### Test Status: Passed

# Test 17: Changing User Password
Note: For this test, you can use a seller or a customer account.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with any account.
3. User selects "manage account" option and clicks enter.
4. User selects "password" from drop down menu.
5. User enters new password in textbox on top.
6. User clicks "confirm changes" button.
7. User exits application by clicking "x" button.
8. User launches application again and logs in with new credentials.

### Expected result:
Application will welcome user and load their homepage. Entering the old credentials will result in an error message 
stating that those credentials are not valid.

### Test Status: Passed

# Test 18: Deleting an Account
Note: For this test, you can use a seller or a customer account.

## Steps:
1. User launches application and clicks ok or "x" on welcome message.
2. User logs in with any account.
3. User selects "manage account" option and clicks enter.
4. User clicks "delete account" button and clicks "yes" when asked to confirm.

### Expected result:
Application deletes user and exits. If user tries logging in after deleting the account, an error will pop up saying the
credentials are not valid.

If the user is a seller, all products and stores associated with them are removed from the marketplace and customer 
shopping carts. If the user is a customer, their shopping cart is deleted from any seller's "view shopping carts" 
page as well.

### Test Status: Passed