import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Store
 * <p>
 * Keeps track of each store in the market
 *
 * @author Andrew Lu, Maanas Karwa
 * @version November 13, 2022
 */
public class Store {
    public static final String STORES_FILENAME = "stores.txt";
    public static final String STORES_FILENAME_TEST = "stores_test.txt";
    public volatile static List<Store> storeList = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger counter = new AtomicInteger();
    private final int id;
    private volatile int totalSold;
    private String name;

    private final String sellerEmail;
    private Seller seller;

    private volatile boolean isDeleted = false;

    private ArrayList<Integer> productIDs = new ArrayList<>();
    private ArrayList<String> customerEmailIDs = new ArrayList<>();

    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();

    public Store(int id, String name, Seller seller, ArrayList<Product> products, ArrayList<Customer> customers,
                 int totalSold) {
        this.id = id;
        this.totalSold = totalSold;
        this.name = name;
        this.seller = seller;
        this.sellerEmail = seller.getEmail();
        this.products = products;
        this.customers = customers;
    }

    public void addProduct(String name, String description, int quantity, double price) {
        Product product = new Product(name, this, description, quantity, price);
        products.add(product);
        Product.addProduct(product);
    }

    public void addProducts(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) throw new IOException("File does not exist!");
        BufferedReader bfr = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bfr.readLine()) != null) {
            String[] s = line.split(",");
            try {
                addProduct(s[0].trim(), s[1].trim(), Integer.parseInt(s[2].trim()), Double.parseDouble(s[3].trim()));
                counter.incrementAndGet();
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                e.printStackTrace();
                continue;
            }

        }
    }

    public Store(int id, String name, String sellerEmail, ArrayList<Integer> productIDs,
                 ArrayList<String> customerEmailIDs, int totalSold) {
        this.id = id;
        this.totalSold = totalSold;
        this.name = name;
        this.sellerEmail = sellerEmail;
        this.productIDs = productIDs;
        this.customerEmailIDs = customerEmailIDs;
    }

    public Store(int id, String name, String sellerEmail, ArrayList<Integer> productIDs,
                 ArrayList<String> customerEmailIDs, int totalSold, boolean isDeleted) {
        this.id = id;
        this.totalSold = totalSold;
        this.name = name;
        this.sellerEmail = sellerEmail;
        this.productIDs = productIDs;
        this.customerEmailIDs = customerEmailIDs;
        this.isDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public synchronized void delete() {
        isDeleted = true;
        for (Product product : this.products) {
            product.setDeleted(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store store)) return false;
        return id == store.id;
    }

    public static ArrayList<Store> getBySeller(Seller seller) {
        ArrayList<Store> result = new ArrayList<>();
        for (Store s : storeList) {
            if (s.seller.equals(seller)) {
                result.add(s);
            }
        }
        return result;
    }

    public static void initializeBasic(boolean debugging) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(debugging ? STORES_FILENAME_TEST :
                STORES_FILENAME))) {
            String line;
            while ((line = bfr.readLine()) != null) {
                String[] item = line.split(";");
                if (item.length != 7) continue;
                String[] prodIds = item[3].split(",");
                ArrayList<Integer> productIdArrayList = new ArrayList<>(prodIds.length);
                for (String s : prodIds) {
                    try {
                        productIdArrayList.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                String[] customerEmailIDs = item[4].split(",");
                ArrayList<String> customerEmailArrayList = new ArrayList<>(customerEmailIDs.length);
                Collections.addAll(customerEmailArrayList, customerEmailIDs);
                Store store = new Store(
                        Integer.parseInt(item[0]), item[1], item[2], productIdArrayList, customerEmailArrayList,
                        Integer.parseInt(item[5]), Boolean.parseBoolean(item[6])
                );
                storeList.add(store);
            }
            // empty file
            if (storeList.isEmpty()) {
                return;
            }
            counter.set(Collections.max(storeList, Comparator.comparingInt(Store::getId)).getId() + 1);
        } catch (FileNotFoundException e) {
            //First time the program has been run
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeAdvanced() {
        for (Store store : storeList) {
            store.seller = Seller.findByEmailId(store.sellerEmail);
            store.products = Product.getByStore(store);
            for (String email : store.customerEmailIDs) {
                Customer customer = Customer.getByEmailId(email);
                if (customer != null) {
                    store.customers.add(customer);
                }
            }
        }
    }

    public synchronized static void writeToFile(boolean debugging) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(debugging ? STORES_FILENAME_TEST :
                STORES_FILENAME))) {
            for (Store store : storeList) {
                List<String> output = new ArrayList<>();
                output.add(Integer.toString(store.getId()));
                output.add(store.getName());
                output.add(store.sellerEmail);
                List<String> productIds = store.products.stream().map(product ->
                        Integer.toString(product.getId())).toList();
                output.add(String.join(",", productIds));
                List<String> customerEmails = store.customers.stream().map(Person::getEmail).toList();
                output.add(String.join(",", customerEmails));
                output.add(Integer.toString(store.getTotalSold()));
                output.add(Boolean.toString(store.isDeleted));
                pw.println(String.join(";", output));
            }
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Store add(String name, Seller seller) {
        counter.incrementAndGet();
        Store store = new Store(counter.intValue(), name, seller, new ArrayList<>(), new ArrayList<>(), 0);
        storeList.add(store);
        return store;
    }

    public static void update(Store store) {
        for (int i = 0; i < storeList.size(); i++) {
            if (storeList.get(i).id == store.id) {
                storeList.remove(i);
                storeList.add(i, store);
                break;
            }
        }
    }

    public static Store getById(int storeId) {
        for (Store s : storeList) {
            if (s.id == storeId) return s;
        }
        return null;
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (!product.isDeleted()) {
                result.add(product);
            }
        }
        return result;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        if (!customers.contains(customer)) customers.add(customer);
    }

    public void addCustomer(String customerEmail) {
        Customer c = Customer.getByEmailId(customerEmail);
        if (!customers.contains(c)) customers.add(c);
    }

    public int getId() {
        return id;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public synchronized void incrementTotalSold(int quantity) {
        this.totalSold += quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public Seller getSeller() {
        return seller;
    }

    public void deleteProduct(Product product) {
        int index = this.products.indexOf(product);
        if (index != -1) {
            this.products.get(index).setDeleted(true);
            this.products.get(index).setQuantity(0);
            Product.delete(product);
        }
    }

    public void exportProducts(String fileName) {
        String newFileName;
        if (fileName.toLowerCase().endsWith(".csv")) newFileName = fileName;
        else newFileName = fileName + ".csv";
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(newFileName, false))) {
            for (Product p : products) {
                if (p.isDeleted()) continue;
                pw.println(String.format("%d,%s,%d,%s,%d,%.2f",
                        p.getId(), p.getName(), p.getStore().id, p.getDescription(), p.getQuantity(), p.getPrice()));
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}