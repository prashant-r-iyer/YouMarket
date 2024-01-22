import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sale
 * <p>
 * Keeps track of each sale transaction
 *
 * @author Maanas Karwa, Andrew Lu
 * @version November 13, 2022
 */
public class Sale {
    public static final String SALES_FILENAME = "sales.txt";
    public static final String SALES_FILENAME_TEST = "sales_test.txt";
    public volatile static List<Sale> salesList = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger counter = new AtomicInteger();
    private final int id;

    private int storeId;
    private Store store;

    private String customerEmail;
    private Customer customer;

    private int productId;
    private Product product;

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    private final int quantity;

    private final double purchasePrice;

    public Sale(int id, Store store, Customer customer, Product product, int quantity, double purchasePrice) {
        this.id = id;
        this.store = store;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public Sale(int id, int storeId, String customerEmail, int productId, int quantity, double purchasePrice) {
        this.id = id;
        this.storeId = storeId;
        this.customerEmail = customerEmail;
        this.productId = productId;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public static String addSale(Store store, Customer customer, Product product,
                                 int quantity, double purchasePrice) {
        try {
            product.decrementQuantity(quantity);
            counter.incrementAndGet();
            Sale sale = new Sale(counter.intValue(), store, customer, product, quantity, purchasePrice);
            store.incrementTotalSold(quantity);
            salesList.add(sale);
            customer.getPurchaseHistory().add(sale);
            store.addCustomer(customer);
        } catch (Exception e) {
            return String.format("You wanted to purchase %d %s but only %d are in stock!",
                    quantity, product.getName(), product.getQuantity());
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public static void initializeBasic(boolean debugging) {
        try (BufferedReader bfr = new BufferedReader(
                new FileReader(debugging ? SALES_FILENAME_TEST : SALES_FILENAME))) {
            String line;
            while ((line = bfr.readLine()) != null) {
                String[] s = line.split(",");
                if (s.length != 6) continue;
                Sale sale = new Sale(Integer.parseInt(s[0]), Integer.parseInt(s[1]), s[2], Integer.parseInt(s[3]),
                        Integer.parseInt(s[4]), Double.parseDouble(s[5]));
                salesList.add(sale);
            }
            if (salesList.size() > 0) {
                counter.set(Collections.max(salesList, Comparator.comparingInt(Sale::getId)).getId() + 1);
            }
        } catch (FileNotFoundException e) {
            //First time the program has been run
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale sale)) return false;
        return id == sale.id;
    }

    public static void initializeAdvanced() {
        for (Sale sale : salesList) {
            sale.customer = Customer.getByEmailId(sale.customerEmail);
            sale.store = Store.getById(sale.storeId);
            sale.product = Product.getById(sale.productId);
        }
    }

    public synchronized static void writeToFile(boolean debugging) {
        try (PrintWriter pw = new PrintWriter(
                new FileOutputStream(debugging ? SALES_FILENAME_TEST : SALES_FILENAME))) {
            for (Sale sale : salesList) {
                List<String> output = new ArrayList<>();
                output.add(Integer.toString(sale.id));
                output.add(Integer.toString(sale.store.getId()));
                output.add(sale.customer.getEmail());
                output.add(Integer.toString(sale.product.getId()));
                output.add(Integer.toString(sale.quantity));
                output.add(String.format("%.2f", sale.purchasePrice));
                pw.println(String.join(",", output));
            }
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Sale> getBySeller(Seller seller) {
        ArrayList<Sale> result = new ArrayList<>();
        for (Sale s : salesList) {
            if (s.store.getSeller().equals(seller)) result.add(s);
        }
        return result;
    }

    public static ArrayList<Sale> getByCustomer(Customer customer) {
        ArrayList<Sale> result = new ArrayList<>();
        for (Sale s : salesList) {
            if (s.customer.equals(customer)) result.add(s);
        }
        return result;
    }

    public static ArrayList<Sale> getByProduct(Product product) {
        ArrayList<Sale> result = new ArrayList<>();
        for (Sale s : salesList) {
            if (s.product.equals(product)) result.add(s);
        }
        return result;
    }

    public static ArrayList<Sale> getByStore(Store store) {
        ArrayList<Sale> result = new ArrayList<>();
        for (Sale s : salesList) {
            if (s.store.equals(store)) result.add(s);
        }
        return result;
    }

    public Store getStore() {
        return store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }
}