import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerThread
 * <p>
 * Receive requests from the client and send back the appropriate responses
 *
 * @author Andrew Lu, Maanas Karwa
 * @version December 12, 2022
 */
public class ServerThread implements Runnable {

    Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String request;
        Seller sellerUser = new Seller(true, "", "", "", new ArrayList<>());
        Customer customerUser = new Customer(true, "", "", "");
        boolean isSeller = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            do {
                request = reader.readLine();
                System.out.println(request);
                if (request.startsWith("1,")) { // login
                    String[] loginInfo = request.substring(2).split(",");
                    String email = loginInfo[0];
                    String password = loginInfo[1];
                    if (Person.checkEmailIsUnused(email)) {
                        writer.println("0,No account found with this email!");
                    } else {
                        int check = Person.checkCredentials(email, password);
                        if (check == 1) { // seller
                            isSeller = true;
                            for (int i = 0; i < Seller.sellerList.size(); i++) {
                                if (Seller.sellerList.get(i).getEmail().equals(email)) {
                                    sellerUser = Seller.sellerList.get(i);
                                }
                            }
                            writer.println("1," + sellerUser.getName());
                        } else if (check == 2) { // customer
                            isSeller = false;
                            for (int i = 0; i < Customer.customerList.size(); i++) {
                                if (Customer.customerList.get(i).getEmail().equals(email)) {
                                    customerUser = Customer.customerList.get(i);
                                }
                            }
                            writer.println("2," + customerUser.getName());
                        } else {
                            writer.println("0,Incorrect password!");
                        }
                    }
                } else if (request.startsWith("2,")) { // create account
                    String[] accountInfo = request.substring(2).split(",");
                    String name = accountInfo[0];
                    String email = accountInfo[1];
                    String password = accountInfo[2];
                    String accountType = accountInfo[3];
                    if (!Person.checkEmailIsUnused(email)) {
                        writer.println("0,Email already in use!");
                    } else {
                        if (accountType.equals("Seller")) {
                            isSeller = true;
                            sellerUser = new Seller(false, name, email, password);
                            Seller.sellerList.add(sellerUser);
                            writer.println("1," + sellerUser.getName());
                        } else if (accountType.equals("Customer")) {
                            isSeller = false;
                            customerUser = new Customer(false, name, email, password);
                            Customer.customerList.add(customerUser);
                            writer.println("2," + customerUser.getName());
                        }
                    }
                } else if (request.startsWith("3,")) { // edit account
                    String[] editInfo = request.substring(2).split(",");
                    String name = editInfo[0];
                    Person editPerson = isSeller ? sellerUser : customerUser;
                    if (!name.isBlank()) { // if name isn't blank, change name
                        editPerson.editAccount(1, name, editPerson.getEmail(), editPerson.getPassword());
                    }
                    if (editInfo.length == 2) {
                        String password = editInfo[1];
                        if (!password.isBlank()) { // if password isn't blank, change password
                            editPerson.editAccount(1, editPerson.getName(),
                                    editPerson.getEmail(), password);
                        }
                    }
                    writer.println("1"); // should always be successful
                } else if (request.startsWith("4,")) { // delete account
                    if (isSeller) {
                        sellerUser.delete();
                    } else {
                        customerUser.delete();
                    }
                    writer.println("1"); // should always be successful
                } else if (request.startsWith("1")) { // seller operations
                    if (!isSeller) {
                        writer.println("0,Not a seller!"); // validate user is a seller
                        continue;
                    }
                    if (request.startsWith("11,")) { // create a store
                        String storeName = request.substring(3);
                        sellerUser.addStore(storeName);
                        writer.println("1");
                    } else if (request.startsWith("12,")) { // edit store name
                        String[] editInfo = request.substring(3).split(",");
                        int storeIndex = Integer.parseInt(editInfo[0]);
                        String storeName = editInfo[1];
                        sellerUser.getStores().get(storeIndex).setName(storeName);
                        writer.println("1");
                    } else if (request.startsWith("13,")) { // add product
                        String[] productInfo = request.substring(3).split(",");
                        int storeIndex = Integer.parseInt(productInfo[0]);
                        String name = productInfo[1];
                        String description = productInfo[2];
                        int quantity = Integer.parseInt(productInfo[3]);
                        double price = Double.parseDouble(productInfo[4]);
                        sellerUser.getStores().get(storeIndex).addProduct(name, description, quantity, price);
                        writer.println("1");
                    } else if (request.startsWith("14,")) { // modify product
                        String[] productInfo = request.substring(3).split(",");
                        int storeIndex = Integer.parseInt(productInfo[0]);
                        int productIndex = Integer.parseInt(productInfo[1]);
                        Product product = sellerUser.getStores().get(storeIndex).getProducts().get(productIndex);
                        if (!productInfo[2].isBlank()) {
                            product.setName(productInfo[2]);
                        }
                        if (!productInfo[3].isBlank()) {
                            product.setDescription(productInfo[3]);
                        }
                        if (!productInfo[4].isBlank()) {
                            product.setQuantity(Integer.parseInt(productInfo[4]));
                        }
                        if (!productInfo[5].isBlank()) {
                            product.setPrice(Double.parseDouble(productInfo[5]));
                        }
                        writer.println("1");
                    } else if (request.startsWith("15,")) { // delete product
                        String[] productInfo = request.substring(3).split(",");
                        int storeIndex = Integer.parseInt(productInfo[0]);
                        int productIndex = Integer.parseInt(productInfo[1]);
                        Product product = sellerUser.getStores().get(storeIndex).getProducts().get(productIndex);
                        sellerUser.getStores().get(storeIndex).deleteProduct(product);
                        writer.println("1");
                    } else if (request.startsWith("16,")) { // delete store
                        int storeIndex = Integer.parseInt(request.substring(3));
                        sellerUser.getStores().get(storeIndex).delete();
                        writer.println("1");
                    } else if (request.startsWith("17,")) { // view stores
                        writer.println(sellerUser.getStores().size()); // output # of lines client needs to receive
                        for (Store store : sellerUser.getStores()) {
                            writer.println(store.getName());
                            System.out.println(store.getName());
                        }
                    } else if (request.startsWith("18")) { // view customer carts
                        ArrayList<String> customerInfo = new ArrayList<>();
                        for (Customer customer : Customer.customerList) {
                            ArrayList<String> cart = sellerUser.getProductsInCustomerCarts(customer);
                            if (cart.isEmpty()) continue;
                            for (String s : cart) {
                                customerInfo.add(customer.getName() + " - " + s);
                            }
                        }
                        writer.println(customerInfo.size());
                        for (String info : customerInfo) {
                            writer.println(info);
                        }
                    } else if (request.startsWith("19,")) { // view dashboard
                        String[] dashInfo = request.substring(3).split(",");
                        int storeIndex = Integer.parseInt(dashInfo[0]);
                        Store store = sellerUser.getStores().get(storeIndex);
                        boolean option = dashInfo[1].equals("1");
                        int order = Integer.parseInt(dashInfo[2]);
                        ArrayList<String> dashboard = Seller.viewDashboardForStore(option, order, store);
                        if (dashboard != null) {
                            writer.println(dashboard.size());
                            for (String info : dashboard) {
                                writer.println(info);
                            }
                        } else {
                            writer.println("0,Dashboard could not be generated!");
                        }
                    } else if (request.startsWith("10,")) { // export store
                        int storeIndex = Integer.parseInt(request.substring(3));
                        List<Product> products = sellerUser.getStores().get(storeIndex).getProducts();
                        writer.println(products.size());
                        for (Product p : products) {
                            writer.println(String.format("%d,%s,%d,%s,%d,%.2f",
                                    p.getId(), p.getName(), p.getStore().getId(),
                                    p.getDescription(), p.getQuantity(), p.getPrice()));
                        }
                    }
                } else if (request.startsWith("2")) { // customer operations
                    if (isSeller) {
                        writer.println("0,Not a customer!"); // validate user is a customer
                        continue;
                    }
                    if (request.startsWith("21,")) { // view/sort marketplace
                        String[] sortInfo = request.substring(3).split(",");
                        int sortOrder = Integer.parseInt(sortInfo[0]);
                        synchronized (Market.getProductList()) {
                            switch (sortOrder) {
                                case 0:
                                    break;
                                case 1:
                                    Market.sortByPriceAscending();
                                    break;
                                case 2:
                                    Market.sortByPriceDescending();
                                    break;
                                case 3:
                                    Market.sortByQuantityAscending();
                                    break;
                                case 4:
                                    Market.sortByQuantityDescending();
                            }
                            if (sortInfo.length == 2) {
                                List<Product> search = Market.searchProducts(sortInfo[1]);
                                writer.println(search.size());
                                for (Product product : search) {
                                    writer.println(String.join(",", product.getName(),
                                            product.getStore().getName(),
                                            String.format("%.2f", product.getPrice()),
                                            Integer.toString(product.getQuantity()), Integer.toString(product.getId())
                                    ));
                                }
                            } else {
                                writer.println(Market.getProductList().size());
                                for (Product product : Market.getProductList()) {
                                    writer.println(String.join(",", product.getName(),
                                            product.getStore().getName(),
                                            String.format("%.2f", product.getPrice()),
                                            Integer.toString(product.getQuantity()), Integer.toString(product.getId())
                                    ));
                                }
                            }
                            Market.sortByIdAscending();
                        }
                    } else if (request.startsWith("22,")) { // purchase product
                        String[] purchaseInfo = request.substring(3).split(",");
                        Product purchaseProduct = Product.getById(Integer.parseInt(purchaseInfo[0]));
                        assert purchaseProduct != null;
                        String purchaseSuccessful = Sale.addSale(purchaseProduct.getStore(), customerUser,
                                purchaseProduct, Integer.parseInt(purchaseInfo[1]), purchaseProduct.getPrice());
                        writer.println(purchaseSuccessful == null ? "1" : "0," + purchaseSuccessful);
                    } else if (request.startsWith("23,")) { // add to cart
                        String[] cartInfo = request.substring(3).split(",");
                        Product cartProduct = Product.getById(Integer.parseInt(cartInfo[0]));
                        int quantity = Integer.parseInt(cartInfo[1]);
                        assert cartProduct != null;
                        if (quantity > cartProduct.getQuantity()) {
                            writer.println(String.format("0,Only %d of %s available!", cartProduct.getQuantity(),
                                    cartProduct.getName()));
                        } else {
                            customerUser.addToCart(cartProduct, quantity);
                            writer.println("1");
                        }
                    } else if (request.startsWith("24,")) { // get product details
                        int id = Integer.parseInt(request.substring(3));
                        Product product = Product.getById(id);
                        if (product == null) {
                            writer.println("0");
                        } else {
                            writer.println(String.join(",", "1", product.getName(),
                                    product.getStore().getName(), product.getDescription(),
                                    Integer.toString(product.getQuantity()),
                                    Double.toString(product.getPrice())
                            ));
                        }
                    } else if (request.startsWith("25,")) { // view dashboard
                        String[] dashboardOptions = request.substring(3).split(",");
                        List<String> dashboard = customerUser.viewDashboard(dashboardOptions[0].equals("2"),
                                Integer.parseInt(dashboardOptions[1]));
                        writer.println(dashboard.size());
                        for (String line : dashboard) {
                            writer.println(line);
                        }
                    } else if (request.startsWith("26,")) { // view purchase history
                        ArrayList<Sale> userPurchaseHistory = customerUser.getPurchaseHistory();
                        writer.println(userPurchaseHistory.size());
                        for (Sale sale : userPurchaseHistory) {
                            writer.println(String.join(",", sale.getProduct().getName(),
                                    sale.getStore().getName(), Integer.toString(sale.getQuantity()),
                                    String.format("%.2f", sale.getPurchasePrice())
                            ));
                        }
                    } else if (request.startsWith("27,")) { // view cart
                        List<String> cart = customerUser.viewCart();
                        writer.println(cart.size());
                        for (String line : cart) {
                            writer.println(line);
                        }
                    } else if (request.startsWith("28,")) { // check out
                        String success = customerUser.checkout();
                        writer.println(success == null ? "1" : "0," + success);
                    } else if (request.startsWith("29,")) { // remove from cart
                        int cartIndex = Integer.parseInt(request.substring(3));
                        customerUser.deleteFromCart(customerUser.getShoppingCart().get(cartIndex));
                        writer.println("1");
                    }
                }
            } while (!request.equals("0")); // exit program
            socket.close();
        } catch (NullPointerException e) {
            System.out.println("User disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Store.writeToFile(false);
            Market.writeToFile(false);
            Sale.writeToFile(false);
            Person.writeToFile(false);
            Seller.writeToFile(false);
            Customer.writeToFile(false);
        }
    }
}
