import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Market
 * <p>
 * Market class
 *
 * @author Andrew Lu, Maanas Karwa
 * @version November 13, 2022
 */
public class Market {
    public volatile static List<Product> productList;

    // Constructs a Market from an existing list of Products
    public Market(List<Product> productList) {
        Market.productList = productList;
    }

    // Constructs a market with an empty list of Products
    public Market() {
        this(new ArrayList<>());
    }

    public Market(String fileName) {
        this();
        addProductsFromFile(fileName);
    }

    public static void initializeBasic() {
        productList = Product.productList;
    }

    public static List<Product> getProductList() {
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : productList) {
            if (p.getQuantity() > 0 && !p.isDeleted()) result.add(p);
        }
        return result;
    }

    // sorts by price in ascending order (i.e. least to greatest price)
    public static void sortByPriceAscending() {
        productList.sort((o1, o2) -> {
            if (o1.getPrice() > o2.getPrice()) return 1;
            else if (o1.getPrice() < o2.getPrice()) return -1;
            return 0;
        });
    }

    // sorts by price in descending order (i.e. greatest to least price)
    public static void sortByPriceDescending() {
        productList.sort((o1, o2) -> {
            if (o1.getPrice() > o2.getPrice()) return -1;
            else if (o1.getPrice() < o2.getPrice()) return 1;
            return 0;
        });
    }

    public static void sortByQuantityAscending() {
        productList.sort((o1, o2) -> {
            if (o1.getQuantity() > o2.getQuantity()) return 1;
            else if (o1.getQuantity() < o2.getQuantity()) return -1;
            return 0;
        });
    }

    public static void sortByQuantityDescending() {
        productList.sort((o1, o2) -> {
            if (o1.getQuantity() > o2.getQuantity()) return -1;
            else if (o1.getQuantity() < o2.getQuantity()) return 1;
            return 0;
        });
    }

    public static void sortByIdAscending() {
        productList.sort((o1, o2) -> {
            if (o1.getId() > o2.getId()) return 1;
            else if (o1.getId() < o2.getId()) return -1;
            return 0;
        });
    }

    public static Product getProduct(int id) {
        for (Product product : productList) {
            if (id == product.getId()) {
                return product;
            }
        }
        return null;
    }

    public static List<Product> searchProducts(String search) {
        search = search.toLowerCase();
        List<Product> results = new ArrayList<>();
        for (Product product : productList) {
            if (!product.isDeleted() &&
                    (product.getName().toLowerCase().contains(search)
                            || product.getStore().getName().toLowerCase().contains(search)
                            || product.getDescription().toLowerCase().contains(search))) {
                results.add(product);
            }
        }

        return results;
    }

    public static void addProduct(Product product) {
        productList.add(product);
    }

    public static void removeProduct(Product product) {
        productList.remove(product);
    }

    public static boolean addProductsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            while (line != null) {
                String[] productInfo = line.split(",");
                productList.add(new Product(Integer.parseInt(productInfo[0]), productInfo[1],
                        Integer.parseInt(productInfo[2]),
                        productInfo[3], Integer.parseInt(productInfo[4]), Double.parseDouble(productInfo[5]),
                        false
                ));
                line = reader.readLine();
            }
            Product.setCounter(Collections.max(productList, Comparator.comparingInt(Product::getId)).getId() + 1);
        } catch (Exception e) {
            System.out.println("Error reading file!");
            return false;
        }
        return true;
    }

    public synchronized static boolean writeToFile(boolean debugging) {
        return writeToFile(debugging ? "products_test.txt" : "products.txt");
    }

    public synchronized static boolean writeToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            for (Product product : productList) {
                writer.println(String.format("%d,%s,%d,%s,%d,%.2f,%b",
                        product.getId(),
                        product.getName(),
                        product.getStore().getId(),
                        product.getDescription(),
                        product.getQuantity(),
                        product.getPrice(),
                        product.isDeleted()
                ));
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}