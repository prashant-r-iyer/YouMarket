import java.io.*;
import java.util.*;

/**
 * Customer
 * <p>
 * All properties and methods relating to the customer
 *
 * @author Trisha Godara, Maanas Karwa, Prashant Rajesh Iyer, Andrew Lu
 * @version November 13, 2022
 */
public class Customer extends Person {
    /*
      customers.txt formatting per line:
          email;shopping cart info (will look like: productId,quantity - productId,quantity - ...)
     */
    private static final String CUSTOMER_FILENAME = "customers.txt";
    public static final String CUSTOMER_FILENAME_TEST = "customers_test.txt";
    public volatile static List<Customer> customerList = Collections.synchronizedList(new ArrayList<>());
    private final ArrayList<Product> shoppingCart = new ArrayList<>();

    private final ArrayList<Integer> shoppingCartIDs = new ArrayList<>();

    // parallel arraylist to store the quantity of each product added in shoppingCart
    private final ArrayList<Integer> productQuantities = new ArrayList<>();
    private ArrayList<Sale> purchaseHistory = new ArrayList<>();

    public Customer(boolean alreadyUser, String name, String email, String password) {
        super(alreadyUser, name, email, password);
    }

    public Customer(boolean alreadyUser, String name, String email, String password, boolean isDeleted) {
        super(alreadyUser, name, email, password, isDeleted);
    }

    /**
     * Load all customers' data from file
     */
    public static void initializeBasic(boolean debugging) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(debugging ? CUSTOMER_FILENAME_TEST :
                    CUSTOMER_FILENAME));
            // each line is a customer
            String line = reader.readLine();
            while (line != null) {
                String[] details = line.split(";");
                if (details.length != 3) continue;
                String email = details[0];
                String name = Person.getInformation(email)[0];
                String password = Person.getInformation(email)[1];
                Customer c = new Customer(true, name, email, password);
                String shoppingCartInfo = details[1];
                String[] productDetails = shoppingCartInfo.split(" - ");
                // productDetails[0] - product id
                // productDetails[1] - quantity
                for (String productDetail : productDetails) {
                    if (!productDetail.split(",")[0].equals("")) {
                        c.shoppingCartIDs.add(Integer.parseInt(productDetail.split(",")[0]));
                        c.productQuantities.add(Integer.parseInt(productDetail.split(",")[1]));
                    }
                }
                c.setDeleted(Boolean.parseBoolean(details[2]));
                customerList.add(c);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            //First time the program has been run
        } catch (IOException e) {
            System.out.println("Error reading user data file");
            e.printStackTrace();
        }
    }

    public static void initializeAdvanced() {
        for (Customer customer : customerList) {
            customer.purchaseHistory = Sale.getByCustomer(customer);
            for (int i = customer.shoppingCartIDs.size() - 1; i >= 0; i--) {
                if (Market.getProduct(customer.shoppingCartIDs.get(i)) != null &&
                        !Objects.requireNonNull(Market.getProduct(customer.shoppingCartIDs.get(i))).isDeleted())
                    customer.shoppingCart.add(Market.getProduct(customer.shoppingCartIDs.get(i)));
                else customer.shoppingCartIDs.remove(i);
            }
        }
    }

    public static Customer getByEmailId(String emailId) {
        for (Customer c : customerList) {
            if (c.getEmail().toLowerCase().equals(emailId.toLowerCase().trim())) return c;
        }
        return null;
    }

    /**
     * Writes customerList data to file
     */
    public synchronized static void writeToFile(boolean debugging) {
        // will be overwriting the entire file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(debugging ? CUSTOMER_FILENAME_TEST :
                CUSTOMER_FILENAME))) {
            // write to file
            for (Customer customer : customerList) {
                writer.write(customer.getEmail() + ";");
                for (int j = 0; j < customer.shoppingCart.size(); j++) {
                    writer.write(Integer.toString(customer.shoppingCart.get(j).getId()));
                    writer.write(",");
                    writer.write(Integer.toString(customer.shoppingCart.get(j).getQuantity()));
                    if (j != customer.shoppingCart.size() - 1) {
                        writer.write(" - ");
                    }
                }
                writer.write(";" + customer.isDeleted());
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println("Problem writing to user data file");
            e.printStackTrace();
        }
    }

    /**
     * Adds selected product to shopping cart
     *
     * @param product  product to add to cart
     * @param quantity quantity of product added
     */
    public void addToCart(Product product, int quantity) {
        shoppingCart.add(product);
        shoppingCartIDs.add(product.getId());
        productQuantities.add(quantity);
    }

    /**
     * Removes selected product from shopping cart
     *
     * @param product product to add to cart
     */
    public void deleteFromCart(Product product) {
        int index = shoppingCart.indexOf(product);
        shoppingCart.remove(product);
        shoppingCartIDs.remove(index);
        productQuantities.remove(index);
    }

    /**
     * Displays items in cart
     *
     * @return arrayList of strings (formatting of each item: "productName,storeName,quantity,price")
     */
    public ArrayList<String> viewCart() {
        ArrayList<String> cartInfo = new ArrayList<>();
        for (int i = 0; i < shoppingCart.size(); i++) {
            if (!shoppingCart.get(i).isDeleted()) {
                String itemInfo = String.format("%s,%s,%d,%.2f", shoppingCart.get(i).getName(),
                        shoppingCart.get(i).getStore().getName(), productQuantities.get(i),
                        shoppingCart.get(i).getPrice());
                cartInfo.add(itemInfo);
            } else {
                shoppingCart.remove(i);
                shoppingCartIDs.remove(i);
                productQuantities.remove(i);
                i--;
            }
        }
        return cartInfo;
    }

    public String checkout() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shoppingCart.size(); i++) {
            String s = Sale.addSale(shoppingCart.get(i).getStore(), this, shoppingCart.get(i),
                    productQuantities.get(i), shoppingCart.get(i).getPrice());
            if (s != null) {
                sb.append(s);
            }
        }
        if (!sb.isEmpty()) {
            return sb.toString();
        } else {
            shoppingCart.clear();
            shoppingCartIDs.clear();
            productQuantities.clear();
            return null;
        }
    }

    /**
     * Allows customer to view their dashboard of statistics
     *
     * @param type   true - list stores by products purchased by customer, false - list stores by # products sold
     * @param sorted 0 - unsorted, 1 - sorted in ascending order, 2 - sorted in descending order
     * @return ArrayList of strings (formatting per item: "storeName,number of products sold/purchased")
     */
    public ArrayList<String> viewDashboard(boolean type, int sorted) {
        ArrayList<String> storeNames = new ArrayList<>();
        // parallel array with storyNames arrayList
        ArrayList<Integer> productAmount = new ArrayList<>();
        // if to be shown customer-purchased specific stores
        if (type) {
            for (Sale sale : purchaseHistory) {
                // if the store does not already exist in storeNames, add it and quantity of product purchased
                if (!storeNames.contains(sale.getStore().getName())) {
                    storeNames.add(sale.getStore().getName());
                    productAmount.add(sale.getQuantity());
                    // else, find the index of the store to increment number of products purchased from that store
                } else {
                    int index = storeNames.indexOf(sale.getStore().getName());
                    productAmount.set(index, productAmount.get(index) + sale.getQuantity());
                }
            }
            // if to be shown stores by told products sold
        } else {
            for (int i = 0; i < Store.storeList.size(); i++) {
                storeNames.add(Store.storeList.get(i).getName());
                productAmount.add(Store.storeList.get(i).getTotalSold());
            }
        }
        ArrayList<String> dashboard = new ArrayList<>();
        // if unsorted
        if (sorted == 0) {
            for (int i = 0; i < storeNames.size(); i++) {
                dashboard.add(storeNames.get(i) + " - " + productAmount.get(i));
            }
            return dashboard;
            // if sorted in ascending order
        } else if (sorted == 1) {
            // optimized bubble sort:
            int j = 0; // used for looping through array
            int counter = -1; // initialize counter for each round's swaps
            // check to see if there were any swaps
            while (j < productAmount.size() && counter != 0) {
                counter = 0; // reset the counter
                // iterate the array up to the last sorted element
                for (int i = 0; i < productAmount.size() - 1 - j; ++i) {
                    if (productAmount.get(i + 1) < productAmount.get(i)) {
                        int temp = productAmount.get(i + 1);
                        productAmount.set(i + 1, productAmount.get(i));
                        productAmount.set(i, temp);
                        String tempValue = storeNames.get(i + 1);
                        storeNames.set(i + 1, storeNames.get(i));
                        storeNames.set(i, tempValue);
                        counter++; // count the swaps of this round
                    }
                }
                j = j + 1; // increment
            }
            for (int i = 0; i < storeNames.size(); i++) {
                dashboard.add(storeNames.get(i) + " - " + productAmount.get(i));
            }
            return dashboard;
            // if sorted in descending order
        } else {
            // optimized bubble sort:
            int j = 0; // used for looping through array
            int counter = -1; // initialize counter for each round's swaps
            // check to see if there were any swaps
            while (j < productAmount.size() && counter != 0) {
                counter = 0; // reset the counter
                // iterate the array up to the last sorted element
                for (int i = 0; i < productAmount.size() - 1 - j; ++i) {
                    if (productAmount.get(i + 1) > productAmount.get(i)) {
                        int temp = productAmount.get(i + 1);
                        productAmount.set(i + 1, productAmount.get(i));
                        productAmount.set(i, temp);
                        String tempValue = storeNames.get(i + 1);
                        storeNames.set(i + 1, storeNames.get(i));
                        storeNames.set(i, tempValue);
                        counter++; // count the swaps of this round
                    }
                }
                j = j + 1; // increment
            }
            for (int i = 0; i < storeNames.size(); i++) {
                dashboard.add(storeNames.get(i) + " - " + productAmount.get(i));
            }
            return dashboard;
        }
    }

    public synchronized void delete() {
        this.setDeleted(true);
        shoppingCart.clear();
        shoppingCartIDs.clear();
        productQuantities.clear();
    }

    /**
     * Writes customer's purchase history data to a file
     * formatting per line: Product name: [name], Store name: [name], Quantity: [quantity], Price: [price]
     */
    public void exportPurchaseHistory() {
        // filename = "firstNamePurchaseHistory.txt"
        String filename = this.getName().split(" ")[0] + "PurchaseHistory.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Sale sale : purchaseHistory) {
                writer.write("Product name: ");
                writer.write(sale.getProduct().getName());
                writer.write(", ");
                writer.write("Store name: ");
                writer.write(sale.getStore().getName());
                writer.write(", ");
                writer.write("Quantity: ");
                writer.write(Integer.toString(sale.getQuantity()));
                writer.write(", ");
                writer.write("Price: $");
                writer.write(String.format("%.2f", sale.getPurchasePrice()));
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println("Problem exporting to file");
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getShoppingCart() {
        return shoppingCart;
    }

    public ArrayList<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }

    public ArrayList<Integer> getProductQuantities() {
        return productQuantities;
    }
}