import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Product
 * <p>
 * Product class
 *
 * @author Prashant Rajesh Iyer, Maanas Karwa, Andrew Lu
 * @version November 13, 2022
 */
public class Product {
    // Format: name,store,description,quantity,original price
    private static final String PRODUCT_FILENAME = "products.txt";

    public static final String PRODUCT_FILENAME_TEST = "products_test.txt";
    public static AtomicInteger counter = new AtomicInteger();
    public volatile static List<Product> productList = Collections.synchronizedList(new ArrayList<>());
    private volatile String name;
    private Store store;

    private final int storeId;
    private volatile String description;
    private volatile int quantity;
    private volatile double price;
    private final int id;

    private volatile boolean isDeleted;

    public boolean isDeleted() {
        return isDeleted;
    }

    public synchronized void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Product(int id, String name, Store store, String description,
                   int quantity, double price, boolean isDeleted) {
        this.name = name;
        this.store = store;
        this.storeId = store.getId();
        this.description = description;
        this.id = id;
        this.isDeleted = isDeleted;

        if (quantity < 0) {
            throw new IllegalArgumentException("Enter non-negative quantity");
        }
        this.quantity = quantity;

        if (price < 0) {
            throw new IllegalArgumentException("Enter non-negative price");
        }
        this.price = price;
    }

    public Product(String name, Store store, String description, int quantity, double price) {
        this(counter.intValue(), name, store.getId(), description, quantity, price, false);
        this.store = store;
    }

    public Product(int id, String name, int storeId, String description, int quantity, double price,
                   boolean isDeleted) {
        this.name = name;
        this.storeId = storeId;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.id = id;
        this.isDeleted = isDeleted;
    }

    public static Product getById(int productId) {
        for (Product p : productList) {
            if (p.id == productId) return p;
        }
        return null;
    }

    public static ArrayList<Product> getByStore(Store store) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.getStore().equals(store)) result.add(p);
        }
        return result;
    }

    public static void setCounter(int count) {
        counter.set(count);
    }

    public static void initializeBasic(boolean debugging) {
        try {
            File f = new File(debugging ? PRODUCT_FILENAME_TEST : PRODUCT_FILENAME);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            ArrayList<String> lines = new ArrayList<>();
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                lines.add(nextLine);
            }

            // empty file
            if (lines.size() == 0) {
                return;
            }

            for (String currentLine : lines) {
                int id = Integer.parseInt(currentLine.substring(0, currentLine.indexOf(',')));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                String name = currentLine.substring(0, currentLine.indexOf(','));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                int storeId = Integer.parseInt(currentLine.substring(0, currentLine.indexOf(',')));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                String description = currentLine.substring(0, currentLine.indexOf(','));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                int quantity = Integer.parseInt(currentLine.substring(0, currentLine.indexOf(',')));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                double price = Double.parseDouble(currentLine.substring(0, currentLine.indexOf(',')));
                currentLine = currentLine.substring(currentLine.indexOf(',') + 1);
                boolean isDeleted = Boolean.parseBoolean(currentLine);

                Product currentProduct = new Product(id, name, storeId, description, quantity, price, isDeleted);
                productList.add(currentProduct);
            }
            counter.set(Collections.max(productList, Comparator.comparingInt(Product::getId)).getId() + 1);
            br.close();
        } catch (FileNotFoundException e) {
            //First time the program has been run
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error initializing product list");
        }
    }

    public static void initializeAdvanced() {
        for (Product p : productList) {
            p.store = Store.getById(p.storeId);
        }
    }

    public static void addProduct(Product product) {
        if (!productList.contains(product)) {
            productList.add(product);
            counter.incrementAndGet();
        }
    }

    public static void updateProduct(Product product) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).id == product.id) {
                productList.remove(i);
                productList.add(i, product);
                break;
            }
        }
    }

    public synchronized static void delete(Product product) {
        if (productList.contains(product)) {
            productList.get(productList.indexOf(product)).setDeleted(true);
        }
    }

    public static void delete(int productId) {
        for (Product p : productList) {
            if (p.id == productId) delete(p);
        }
    }

    public Store getStore() {
        return store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id == product.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void decrementQuantity(int quantity) throws Exception {
        if (this.quantity - quantity < 0) {
            throw new Exception("Quantity has to remain non-negative");
        } else {
            synchronized (this) {
                this.quantity -= quantity;
            }
        }
    }
}