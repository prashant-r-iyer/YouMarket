import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Person
 * <p>
 * Keeps track of a person's identity, specifically their login and general profile details
 *
 * @author Trisha Godara, Maanas Karwa
 * @version November 13, 2022
 */
public class Person {
    /*
     users.txt formatting per line:
        type of user (Seller/Customer),name,email,password,if deleted user (true/false)
     */
    private static final String USER_FILENAME = "users.txt";

    public static final String USER_FILENAME_TEST = "users_test.txt";
    public volatile static List<Person> userList = Collections.synchronizedList(new ArrayList<>());
    private String name;
    private String email;
    private String password;

    private volatile boolean isDeleted = false;

    /**
     * Creates new person with given parameters
     *
     * @param alreadyUser if the person is already a user or not
     * @param name        given name
     * @param email       given email
     * @param password    given password
     */
    public Person(boolean alreadyUser, String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        // if person is a new user, write this data to the file and add to userList
        if (!alreadyUser) {
            userList.add(this);
        }
    }

    public Person(boolean alreadyUser, String name, String email, String password, boolean isDeleted) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isDeleted = isDeleted;
        // if person is a new user, write this data to the file and add to userList
        if (!alreadyUser) {
            userList.add(this);
        }
    }

    public static void initialize(boolean debugging) {
        try {
            File file = new File(debugging ? USER_FILENAME_TEST : USER_FILENAME);
            if (file.exists()) {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line = bfr.readLine();
                while (line != null) {
                    String[] details = line.split(",");
                    if (details[0].equals("Seller")) {
                        userList.add(new Seller(true, details[1], details[2], details[3],
                                Boolean.parseBoolean(details[4])));
                    } else if (details[0].equals("Customer")) {
                        userList.add(new Customer(true, details[1], details[2], details[3],
                                Boolean.parseBoolean(details[4])));
                    }
                    line = bfr.readLine();
                }
                bfr.close();
            }
        } catch (FileNotFoundException e) {
            //First time the program has been run
        } catch (IOException e) {
            System.out.println("Error reading user data file");
            e.printStackTrace();
        }
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public synchronized void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * Gets the person's name and password given their email
     *
     * @param email email to change/delete
     * @return array with name (index 0) and password (index 1)
     */
    public static String[] getInformation(String email) {
        String[] details = new String[2];
        if (userList.size() > 0) {
            for (Person person : userList) {
                if (person.getEmail().equals(email)) {
                    details[0] = person.getName();
                    details[1] = person.getPassword();
                    break;
                }
            }
        }
        return details;
    }

    /**
     * Checks if email address is unique (emails are not case sensitive)
     *
     * @param email given email
     * @return if email address is unique or not
     */
    public static boolean checkEmailIsUnused(String email) {
        for (Person p : userList) {
            if (p.email.equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if given credentials match with an existing user
     *
     * @param email    given email
     * @param password given password
     * @return 0 if invalid, 1 if person is a Seller, 2 if person is a Customer
     */
    public static int checkCredentials(String email, String password) {
        for (Person p : userList) {
            if (!p.isDeleted && p.email.equalsIgnoreCase(email) && p.password.equals(password)) {
                if (p instanceof Seller) return 1;
                return 2;
            }
        }
        return 0;
    }

    /**
     * Edits fields and data in user list
     *
     * @param typeOfChange 0 - deletion, 1 - modification
     * @param name         name to change/delete
     * @param email        email to change/delete
     * @param password     password to change/delete
     */
    public void editAccount(int typeOfChange, String name, String email, String password) {
        if (typeOfChange == 0) {
            int index = userList.indexOf(this);
            userList.get(index).setDeleted(true);
        } else {
            for (Person person : userList) {
                if (person.email.equalsIgnoreCase(this.email)) {
                    person.setName(name);
                    person.setEmail(email);
                    person.setPassword(password);
                    break;
                }
            }
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }

    /**
     * Writes all userList data to file
     */
    public synchronized static void writeToFile(boolean debugging) {
        try {
            // will be overwriting the entire file
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(debugging ? USER_FILENAME_TEST : USER_FILENAME));
            // write to file
            for (Person person : userList) {
                // write type of user
                if (person instanceof Seller) {
                    writer.write("Seller,");
                } else {
                    writer.write("Customer,");
                }
                writer.write(person.name + "," + person.email + "," + person.password +
                        "," + person.isDeleted + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Problem writing to user data file");
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Person)) {
            return false;
        } else {
            return (((Person) o).name.equals(this.name) &&
                    ((Person) o).email.equals(this.email) &&
                    ((Person) o).password.equals(this.password));
        }
    }
}