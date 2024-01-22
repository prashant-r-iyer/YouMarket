import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Seller
 * <p>
 * Keeps track of each seller in the market
 *
 * @author Prashant Rajesh Iyer, Maanas Karwa, Andrew Lu
 * @version November 13, 2022
 */
public class Seller extends Person {
    public static final String SELLER_FILENAME_TEST = "sellers_test.txt";
    private static final String SELLER_FILENAME = "sellers.txt";
    public volatile static List<Seller> sellerList = Collections.synchronizedList(new ArrayList<>());

    private final ArrayList<Integer> storeIDs = new ArrayList<>();
    private ArrayList<Store> stores = new ArrayList<>();

    /**
     * Creates new person with given parameters
     *
     * @param alreadyUser if the person is already a user or not
     * @param name        given name
     * @param email       given email
     * @param password    given password
     */
    public Seller(boolean alreadyUser, String name, String email, String password, ArrayList<Store> stores) {
        super(alreadyUser, name, email, password);
        this.stores = stores;
    }

    public Seller(boolean alreadyUser, String name, String email, String password, boolean isDeleted) {
        super(alreadyUser, name, email, password, isDeleted);
    }

    public Seller(boolean alreadyUser, String name, String email, String password) {
        super(alreadyUser, name, email, password);
    }

    public static void initializeBasic(boolean debugging) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(debugging ? SELLER_FILENAME_TEST :
                    SELLER_FILENAME));
            // each line is a seller
            String line = reader.readLine();
            while (line != null) {
                String[] details = line.split(";");
                if (details.length != 3) continue;
                String email = details[0];
                String name = Person.getInformation(email)[0];
                String password = Person.getInformation(email)[1];
                boolean isDeleted = Boolean.parseBoolean(details[2]);
                Seller seller = new Seller(true, name, email, password, isDeleted);
                String storeInfo = details[1];
                // if no stores
                if (storeInfo.equals("")) {
                    sellerList.add(seller);
                    line = reader.readLine();
                    continue;
                }
                String[] storeIDs = storeInfo.split(",");
                for (String s : storeIDs) {
                    try {
                        seller.storeIDs.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                sellerList.add(seller);
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
        for (Seller seller : sellerList) {
            seller.stores = Store.getBySeller(seller);
        }
    }

    public static Seller findByEmailId(String emailId) {
        for (Seller s : sellerList) {
            if (s.getEmail().equalsIgnoreCase(emailId)) return s;
        }
        return null;
    }

    public synchronized static void writeToFile(boolean debugging) {
        try {
            File f = new File(debugging ? SELLER_FILENAME_TEST : SELLER_FILENAME);
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);

            for (Seller seller : sellerList) {
                pw.write(seller.getEmail() + ';');
                for (int storeIndex = 0; storeIndex < seller.getStores().size(); storeIndex++) {
                    pw.write(Integer.toString(seller.getStores().get(storeIndex).getId()));
                    if (storeIndex != seller.getStores().size() - 1) {
                        pw.write(',');
                    }
                }
                pw.write(";" + seller.isDeleted());
                pw.println();
            }
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file in Seller");
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        Seller seller = (Seller) o;
//        return storeIDs.equals(seller.storeIDs) && stores.equals(seller.stores);
//    }

    // true for list of products with number of sales, false for list of customers with number of items purchased
    // 0 unsorted, 1 ascending by num items or sales, 2 descending by num items or sales
    public static ArrayList<String> viewDashboardForStore(boolean option, int sorted, Store store) {
        ArrayList<String> productOrCustomerList = new ArrayList<>();
        if (option) {
            // product_name,sale
            for (Product product : Product.productList) {
                if (product.getStore().equals(store)) {
                    ArrayList<Sale> salesList = Sale.getByProduct(product);
                    int totalSales = 0;
                    double totalRevenue = 0;
                    for (Sale sale : salesList) {
                        totalSales += sale.getQuantity();
                        totalRevenue += sale.getQuantity() * sale.getPurchasePrice();
                    }
                    productOrCustomerList.add(product.getName() + " - Number of sales: " + totalSales +
                            " - Total Revenue: $" + String.format("%.2f", totalRevenue));
                }
            }
        } else {
            // name,num_items
            for (Customer customer : Customer.customerList) {
                int numItems = 0;
                for (int productIndex = 0; productIndex < customer.getPurchaseHistory().size(); productIndex++) {
                    if (customer.getPurchaseHistory().get(productIndex).getStore().getId() == store.getId()) {
                        numItems += customer.getPurchaseHistory().get(productIndex).getQuantity();
                    }
                }

                if (numItems != 0) {
                    productOrCustomerList.add(customer.getName() + " - Number of products purchased: " + numItems);
                }
            }
        }

        if (sorted == 0) {
            return productOrCustomerList;
        } else if (sorted == 1) {
            productOrCustomerList.sort((string1, string2) -> {
                int quantity1 = Integer.parseInt(string1.split("-")[1].trim().split(":")[1].trim());
                int quantity2 = Integer.parseInt(string2.split("-")[1].trim().split(":")[1].trim());
                return quantity1 - quantity2;
            });
            return productOrCustomerList;
        } else if (sorted == 2) {
            if (productOrCustomerList.isEmpty() || productOrCustomerList.size() == 1) return productOrCustomerList;
            productOrCustomerList.sort((string1, string2) -> {
                int quantity1 = Integer.parseInt(string1.split("-")[1].trim().split(":")[1].trim());
                int quantity2 = Integer.parseInt(string2.split("-")[1].trim().split(":")[1].trim());
                return quantity2 - quantity1;
            });

            return productOrCustomerList;
        } else if (sorted == 3) {
            if (productOrCustomerList.isEmpty() || productOrCustomerList.size() == 1) return productOrCustomerList;
            productOrCustomerList.sort((string1, string2) -> {
                double quantity1 = Double.parseDouble(string1.split("-")[2].trim().split(":")[1].trim().
                        substring(1));
                double quantity2 = Double.parseDouble(string2.split("-")[2].trim().split(":")[1].trim().
                        substring(1));
                return Double.compare(quantity1, quantity2);
            });
            return productOrCustomerList;
        } else if (sorted == 4) {
            if (productOrCustomerList.isEmpty() || productOrCustomerList.size() == 1) return productOrCustomerList;
            productOrCustomerList.sort((string1, string2) -> {
                double quantity1 = Double.parseDouble(string1.split("-")[2].trim().split(":")[1].trim().
                        substring(1));
                double quantity2 = Double.parseDouble(string2.split("-")[2].trim().split(":")[1].trim().
                        substring(1));
                return Double.compare(quantity2, quantity1);
            });
            return productOrCustomerList;
        }

        return null;
    }

    public ArrayList<Store> getStores() {
        ArrayList<Store> result = new ArrayList<>();
        for (Store s : stores) {
            if (!s.isDeleted()) result.add(s);
        }
        return result;
    }

    public void addStore(String name) {
        addStore(Store.add(name, this));
    }

    public void updateStore(Store store) {
        Store.update(store);
    }

    private void addStore(Store s) {
        if (s.getSeller().equals(this)) stores.add(s);
    }

    public synchronized void delete() {
        this.setDeleted(true);
        for (Store s : stores) {
            s.delete();
        }
    }

    // Format: product - #in carts - store - description - price
    public ArrayList<String> getProductsInCustomerCarts(Customer customer) {
        ArrayList<String> customerShoppingCart = new ArrayList<>();
        ArrayList<Product> products = customer.getShoppingCart();
        ArrayList<Integer> quantities = customer.getProductQuantities();

        for (int cartIndex = 0; cartIndex < products.size(); cartIndex++) {
            Product currentProduct = products.get(cartIndex);
            int currentQuantity = quantities.get(cartIndex);
            if (this.stores.contains(currentProduct.getStore())) // Only showing sellers details about their products
                customerShoppingCart.add(String.format(
                        "%s - %d - %s - %s - %.2f",
                        currentProduct.getName(),
                        currentQuantity,
                        currentProduct.getStore().getName(),
                        currentProduct.getDescription(),
                        currentProduct.getPrice()
                ));
        }

        return customerShoppingCart;
    }
}