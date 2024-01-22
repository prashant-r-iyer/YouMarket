import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

/**
 * Client
 * <p>
 * Manages user interface and communication with server
 *
 * @author Trisha Godara, Maanas Karwa, Karsten Palm, Andrew Lu
 * @version December 12, 2022
 */

public class Client implements Runnable {
    //GUI
    JFrame frame;
    Container content;

    //runtime data
    String name;
    String email;
    String password;
    String userType;
    boolean isSeller;

    //networkIO
    Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    public static void main(String[] args) {
        //runs GUI code
        SwingUtilities.invokeLater(new Client());
    }

    public void run() {
        try {
            //prepare network IO
            socket = new Socket("localhost", 4242);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            //prepare GUI
            frame = new JFrame();
            frame.setTitle("YouMarket");

            content = frame.getContentPane();
            content.setLayout(new BorderLayout(20, 15));

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            //show welcome message
            showWelcome();

            //show login/create account message
            String input = loginOrCreate();

            if (input != null) {
                frame.setVisible(true);
                if (input.equals("Log in")) { //log in
                    showLogin();
                } else { //create account
                    showCreateAccount();
                }
            } else {
                //goodbye message (cancel or red x)
                writer.println("0");
                showFarewell();
                writer.close();
                reader.close();
                socket.close();
            }
        } catch (IOException e) {
            //could not connect, print error
            cannotConnect();
        }
    }

    //Sign-in processing ----------------------------------
    public void showLogin() {
        clearPanel();

        //instantiate elements
        JLabel emailLabel = new JLabel("Email");
        JLabel passLabel = new JLabel("Password");
        JTextField emailField = new JTextField(10);
        JTextField passwordField = new JTextField(10);
        JButton enterLogin = new JButton("Login");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterLogin) { //enter button
                //get text from email and password fields
                email = emailField.getText();
                password = passwordField.getText();

                if (email.isEmpty() || password.isEmpty()) {
                    loginError();
                } else {
                    //get credential check from server
                    try {
                        writer.println(String.join(",", "1", email, password));
                        String result = reader.readLine();

                        //process credential check
                        if (result.startsWith("0")) {
                            invalidCredentials();
                        } else {
                            showUserWelcome(result.substring(2));
                            isSeller = result.startsWith("1");
                            showUserScreen();
                        }
                    } catch (IOException ex) {
                        loginError();
                    }
                }
            }
            if (e.getSource() == backButton) { //back button
                //show previous menu
                String response = loginOrCreate();
                //go back
                if (response != null) {
                    if (response.equals("Log in")) { //log in
                        showLogin();
                    } else { //create account
                        showCreateAccount();
                    }
                } else { //cancel going back (in effect)
                    showLogin();
                }
            }
        };

        //assign functionality
        enterLogin.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel emailPanel = new JPanel(new FlowLayout());
        JPanel passPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        passPanel.add(passLabel);
        passPanel.add(passwordField);

        buttonPanel.add(enterLogin);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(emailPanel);
        infoPanel.add(passPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showCreateAccount() {
        clearPanel();

        //instantiate elements
        JLabel nameLabel = new JLabel(("Name"));
        JLabel emailLabel = new JLabel("Email");
        JLabel passLabel = new JLabel("Password");
        JLabel typeLabel = new JLabel("Account Type");
        JTextField nameField = new JTextField(10);
        JTextField emailField = new JTextField(10);
        JTextField passwordField = new JTextField(10);
        String[] options = {"Seller", "Customer"};
        JComboBox<String> dropDown = new JComboBox<>(options);
        JButton enterLogin = new JButton("Create Account");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterLogin) { //enter button
                name = nameField.getText();
                email = emailField.getText();
                password = passwordField.getText();
                userType = (String) dropDown.getSelectedItem();

                if (name != null && !name.isEmpty() && email != null && !email.isEmpty()
                        && password != null && !password.isEmpty()) {

                    //create account on server
                    try {
                        writer.println(String.join(",", "2", name, email, password, userType));
                        String result = reader.readLine();
                        if (result.startsWith("0")) {
                            takenEmail();
                        } else {
                            showUserWelcome(result.substring(2));
                            isSeller = result.startsWith("1");
                            showUserScreen();
                        }
                    } catch (IOException ex) {
                        createAccountError();
                    }
                } else {
                    createAccountError();
                }
            }
            if (e.getSource() == backButton) { //back button
                //show previous menu
                String response = loginOrCreate();
                //go back
                if (response != null) {
                    if (response.equals("Log in")) { //log in
                        showLogin();
                    } else { //create account
                        showCreateAccount();
                    }
                } else { //cancel going back (in effect)
                    showLogin();
                }
            }
        };

        //assign functionality
        enterLogin.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel namePanel = new JPanel(new FlowLayout());
        JPanel emailPanel = new JPanel(new FlowLayout());
        JPanel passPanel = new JPanel(new FlowLayout());
        JPanel typePanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        passPanel.add(passLabel);
        passPanel.add(passwordField);

        typePanel.add(typeLabel);
        typePanel.add(dropDown);

        buttonPanel.add(enterLogin);
        buttonPanel.add(backButton);

        enterLogin.setAlignmentX(emailPanel.getAlignmentX());

        infoPanel.add(Box.createGlue());
        infoPanel.add(namePanel);
        infoPanel.add(emailPanel);
        infoPanel.add(passPanel);
        infoPanel.add(typePanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    // Landing pages --------------------------------------

    public void showSeller() {
        clearPanel();

        //instantiate elements
        JLabel optionLabel = new JLabel("What would you like to do?");
        String[] options = {"Manage account", "Manage stores", "View shopping carts", "View dashboard"};
        JComboBox<String> dropDown = new JComboBox<>(options);
        JButton enterButton = new JButton("Enter");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterButton) { //enter button
                String selection = (String) dropDown.getSelectedItem();

                //create account on server
                switch (Objects.requireNonNull(selection)) {
                    case ("Manage account") -> showEditAccount();
                    case ("Manage stores") -> showManageStores();
                    case ("View shopping carts") -> showViewShoppingCarts();
                    case ("View dashboard") -> {
                        String input;
                        do {
                            input = selectSellerDashboard();
                        } while (input != null && input.equals("Select"));
                        showSellerDashboard(input, 0, 0);
                    }
                    default -> {
                    }
                }
            }
        };

        //assign functionality
        enterButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel = new JPanel(new FlowLayout());
        JPanel optionPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        labelPanel.add(Box.createHorizontalGlue());
        labelPanel.add(optionLabel);
        labelPanel.add(Box.createHorizontalGlue());

        optionPanel.add(Box.createHorizontalGlue());
        optionPanel.add(dropDown);
        optionPanel.add(Box.createHorizontalGlue());

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(enterButton);
        buttonPanel.add(Box.createHorizontalGlue());

        infoPanel.add(Box.createGlue());
        infoPanel.add(labelPanel);
        infoPanel.add(optionPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showCustomer() {
        clearPanel();

        //instantiate elements
        JLabel optionLabel = new JLabel("What would you like to do?");
        String[] options = {"Manage account", "View marketplace", "View purchase history",
                "View shopping cart", "View dashboard"};
        JComboBox<String> dropDown = new JComboBox<>(options);
        JButton enterButton = new JButton("Enter");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterButton) { //enter button
                String selection = (String) dropDown.getSelectedItem();

                //create account on server
                switch (Objects.requireNonNull(selection)) {
                    case ("Manage account") -> showEditAccount();
                    case ("View marketplace") -> showMarketplace(0, "");
                    case ("View purchase history") -> showHistory();
                    case ("View shopping cart") -> showShoppingCart();
                    case ("View dashboard") -> showCustomerDashboard(0);
                    default -> {
                    } //error message goes here
                }
            }
        };

        //assign functionality
        enterButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel = new JPanel(new FlowLayout());
        JPanel optionPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        labelPanel.add(Box.createHorizontalGlue());
        labelPanel.add(optionLabel);
        labelPanel.add(Box.createHorizontalGlue());

        optionPanel.add(Box.createHorizontalGlue());
        optionPanel.add(dropDown);
        optionPanel.add(Box.createHorizontalGlue());

        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(enterButton);
        buttonPanel.add(Box.createHorizontalGlue());

        infoPanel.add(Box.createGlue());
        infoPanel.add(labelPanel);
        infoPanel.add(optionPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    //Seller and Customer functions -----------------------

    public void showUserScreen() {
        if (isSeller) {
            showSeller();
        } else {
            showCustomer();
        }
    }

    public void showEditAccount() {
        clearPanel();

        //instantiate elements
        JLabel newLabel = new JLabel(("New information"));
        JLabel optionLabel = new JLabel(("Change: "));
        JTextField newField = new JTextField(10);
        String[] options = {"Name", "Password"};
        JComboBox<String> dropDown = new JComboBox<>(options);
        JButton enterLogin = new JButton("Confirm changes");
        JButton deleteButton = new JButton("Delete account");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterLogin) { //enter button
                String newInfo = newField.getText();
                String infoType = (String) dropDown.getSelectedItem();

                if (infoType != null) {
                    if (infoType.equals("Name")) { //name
                        name = newInfo;

                        writer.println(String.join(",", "3", newInfo, ""));
                        try {
                            reader.readLine();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        showUserScreen();
                    } else { //password
                        password = newInfo;

                        writer.println(String.join(",", "3", "", newInfo));
                        try {
                            reader.readLine();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        showUserScreen();
                    }
                }
            }
            if (e.getSource() == deleteButton) { //delete
                int response = confirmDeleteAccount();

                if (response == 0) { //yes pressed
                    writer.println("4,");
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    frame.dispose();
                }

            }
            if (e.getSource() == backButton) { //back button
                showUserScreen();
            }
        };

        //assign functionality
        enterLogin.addActionListener(actionListener);
        deleteButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel newPanel = new JPanel(new FlowLayout());
        JPanel typePanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        newPanel.add(newLabel);
        newPanel.add(newField);

        typePanel.add(optionLabel);
        typePanel.add(dropDown);

        buttonPanel.add(enterLogin);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(newPanel);
        infoPanel.add(typePanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    //Seller functions ------------------------------------

    public void showManageStores() {
        clearPanel();

        //instantiate elements
        JLabel storeLabel = new JLabel("Select a store");

        String[] stores;
        writer.println("17,");
        try {
            int size = Integer.parseInt(reader.readLine());
            stores = new String[size + 1];
            stores[0] = "Select";
            for (int i = 1; i <= size; i++) {
                stores[i] = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JComboBox<String> dropDown = new JComboBox<>(stores);

        JButton editButton = new JButton("Edit store");
        JButton deleteButton = new JButton("Delete store");
        JButton exportButton = new JButton("Export product info to file");

        JButton createButton = new JButton("Create new store");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == editButton) { //edit store
                String storeName = (String) dropDown.getSelectedItem();

                int storeId = indexOf(stores, storeName) - 1;
                if (storeName != null) {
                    if (!storeName.equals("Select")) {
                        showEditStores(storeId);
                    }
                }
            }
            if (e.getSource() == deleteButton) { //delete store
                int response = confirmDeleteStore();

                if (response == 0) { //yes pressed
                    String storeName = (String) dropDown.getSelectedItem();
                    int storeId = indexOf(stores, storeName) - 1;
                    writer.println(String.format("16,%d", storeId));
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            if (e.getSource() == exportButton) { //export products
                String storeName = (String) dropDown.getSelectedItem();
                if (storeName != null) {
                    if (!storeName.equals("Select")) {
                        int storeId = indexOf(stores, storeName) - 1;
                        String fileName = chooseFile();
                        if (!fileName.isBlank()) {
                            writer.println(String.format("10,%d", storeId));
                            try {
                                PrintWriter pw = new PrintWriter(fileName);
                                int size = Integer.parseInt(reader.readLine());
                                for (int i = 0; i < size; i++) {
                                    pw.println(reader.readLine());
                                }
                                pw.close();
                            } catch (Exception ex) {
                                invalidFile();
                                throw new RuntimeException(ex);
                            }
                            exportStore();
                        }
                    }
                }
            }
            if (e.getSource() == createButton) { //create store
                String storeName = createStore();

                if (storeName != null && !storeName.isEmpty()) {
                    writer.println("11," + storeName);
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    showManageStores();
                }
            }
            if (e.getSource() == backButton) { //back button
                showSeller();
            }
        };

        //assign functionality
        editButton.addActionListener(actionListener);
        deleteButton.addActionListener(actionListener);
        exportButton.addActionListener(actionListener);
        createButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        JPanel labelPanel = new JPanel(new FlowLayout());
        JPanel storePanel = new JPanel();
        storePanel.setLayout(new BoxLayout(storePanel, BoxLayout.PAGE_AXIS));
        JPanel dropDownPanel = new JPanel(new FlowLayout());
        JPanel selectButtonPanel = new JPanel(new FlowLayout());
        JPanel generalButtonPanel = new JPanel(new FlowLayout());

        labelPanel.add(storeLabel);
        storePanel.add(labelPanel);
        dropDownPanel.add(Box.createHorizontalGlue());
        dropDownPanel.add(dropDown);
        dropDownPanel.add(Box.createHorizontalGlue());
        storePanel.add(dropDownPanel);
        selectButtonPanel.add(editButton);
        selectButtonPanel.add(deleteButton);
        selectButtonPanel.add(exportButton);
        storePanel.add(selectButtonPanel);

        generalButtonPanel.add(createButton);
        generalButtonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(storePanel);
        infoPanel.add(Box.createGlue());
        infoPanel.add(generalButtonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showEditStores(int storeId) {

        clearPanel();

        //instantiate elements
        JLabel storeLabel = new JLabel("Select a product");

        String[] stores;
        writer.println("10," + storeId);
        try {
            int size = Integer.parseInt(reader.readLine());
            stores = new String[size + 1];
            stores[0] = "Select";
            for (int i = 1; i <= size; i++) {
                String[] productInfo = reader.readLine().split(",");
                stores[i] = productInfo[1];
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        JComboBox<String> dropDown = new JComboBox<>(stores);

        JButton editButton = new JButton("Edit product");
        JButton deleteButton = new JButton("Delete product");

        JButton storeNameButton = new JButton("Edit store name");
        JButton createButton = new JButton("Create new product");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == editButton) { //edit product
                String productName = (String) dropDown.getSelectedItem();

                int productId = indexOf(stores, productName) - 1; //placeholder

                if (productName != null && !productName.equals("Select")) {
                    showEditProduct(storeId, productId);
                }
            }
            if (e.getSource() == deleteButton) { //delete product
                int response = confirmDeleteProduct();
                String productName = (String) dropDown.getSelectedItem();
                int productId = indexOf(stores, productName) - 1; //placeholder
                if (response == 0) { //yes pressed
                    writer.println(String.format("15,%d,%d", storeId, productId));
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (e.getSource() == storeNameButton) { //change store name
                //display name change popup
                String newName = changeStoreName();

                if (newName != null && !newName.isEmpty()) {
                    writer.println(String.format("12,%d,%s", storeId, newName));
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Name accepted");
                }

            }
            if (e.getSource() == createButton) { //create product
                showCreateProduct(storeId);
            }
            if (e.getSource() == backButton) { //back button
                clearPanel();

                showManageStores();
            }
        };

        //assign functionality
        editButton.addActionListener(actionListener);
        deleteButton.addActionListener(actionListener);
        storeNameButton.addActionListener(actionListener);
        createButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        JPanel labelPanel = new JPanel(new FlowLayout());
        JPanel storePanel = new JPanel();
        storePanel.setLayout(new BoxLayout(storePanel, BoxLayout.PAGE_AXIS));
        JPanel dropDownPanel = new JPanel(new FlowLayout());
        JPanel selectButtonPanel = new JPanel(new FlowLayout());
        JPanel generalButtonPanel = new JPanel(new FlowLayout());

        labelPanel.add(storeLabel);
        storePanel.add(labelPanel);
        dropDownPanel.add(Box.createHorizontalGlue());
        dropDownPanel.add(dropDown);
        dropDownPanel.add(Box.createHorizontalGlue());
        storePanel.add(dropDownPanel);
        selectButtonPanel.add(editButton);
        selectButtonPanel.add(deleteButton);
        storePanel.add(selectButtonPanel);

        generalButtonPanel.add(storeNameButton);
        generalButtonPanel.add(createButton);
        generalButtonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(storePanel);
        infoPanel.add(Box.createGlue());
        infoPanel.add(generalButtonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showEditProduct(int storeId, int productId) {
        clearPanel();

        String productName = "";
        String productDesc = "";
        String productQuan = "";
        String productPrice = "";
        writer.println("10," + storeId);
        try {
            int size = Integer.parseInt(reader.readLine());
            for (int i = 0; i < size; i++) {
                String[] productInfo = reader.readLine().split(",");
                if (i == productId) {
                    productName = productInfo[1];
                    productDesc = productInfo[3];
                    productQuan = productInfo[4];
                    productPrice = productInfo[5];
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //instantiate elements
        JLabel nameLabel = new JLabel(("Name:"));
        JTextField nameField = new JTextField(productName, 10);
        JLabel descLabel = new JLabel(("Description:"));
        JTextField descField = new JTextField(productDesc, 20);
        JLabel quanLabel = new JLabel(("Quantity"));
        JTextField quanField = new JTextField(productQuan, 10);
        JLabel priceLabel = new JLabel(("Price"));
        JTextField priceField = new JTextField(productPrice, 10);
        JButton enterButton = new JButton("Confirm changes");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == enterButton) { //enter button
                String newName = nameField.getText();
                String newDesc = descField.getText();
                String newQuan = quanField.getText();
                String newPrice = priceField.getText();

                int intQuan = -1;
                double dubPrice = -1;

                boolean validN;
                boolean validD;
                boolean validQ = false;
                boolean validP = false;

                validN = newName != null && !newName.isEmpty();

                validD = newDesc != null && !newDesc.isEmpty();

                try {
                    intQuan = Integer.parseInt(newQuan);
                    validQ = true;
                } catch (NumberFormatException ex) {
                    invalidQuan();
                }

                try {
                    dubPrice = Double.parseDouble(newPrice);
                    validP = true;
                } catch (NumberFormatException ex) {
                    invalidPrice();
                }

                if (validN && validD && validQ && validP) {
                    writer.println(String.format("14,%d,%d,%s,%s,%s,%s", storeId, productId, newName, newDesc,
                            intQuan, dubPrice
                    ));
                    try {
                        reader.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    showEditStores(storeId);
                }
            }
            if (e.getSource() == backButton) { //back button
                showEditStores(storeId);
            }
        };

        //assign functionality
        enterButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel namePanel = new JPanel(new FlowLayout());
        JPanel descPanel = new JPanel(new FlowLayout());
        JPanel quanPanel = new JPanel(new FlowLayout());
        JPanel pricePanel = new JPanel(new FlowLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        descPanel.add(descLabel);
        descPanel.add(descField);

        quanPanel.add(quanLabel);
        quanPanel.add(quanField);

        pricePanel.add(priceLabel);
        pricePanel.add(priceField);

        buttonPanel.add(enterButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(namePanel);
        infoPanel.add(descPanel);
        infoPanel.add(quanPanel);
        infoPanel.add(pricePanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showCreateProduct(int storeId) {
        clearPanel();

        //instantiate elements
        JLabel nameLabel = new JLabel(("Name:"));
        JTextField nameField = new JTextField(10);
        JLabel descLabel = new JLabel(("Description:"));
        JTextField descField = new JTextField(20);
        JLabel quanLabel = new JLabel(("Quantity"));
        JTextField quanField = new JTextField(10);
        JLabel priceLabel = new JLabel(("Price"));
        JTextField priceField = new JTextField(10);
        JButton importButton = new JButton("Import products from file");
        JButton enterButton = new JButton("Create product");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == importButton) { //import button
                String filename = chooseFile();
                boolean formatting = true;
                if (!filename.isBlank()) {
                    try (BufferedReader bfr = new BufferedReader(new FileReader(filename))) {
                        String line;
                        while ((line = bfr.readLine()) != null) {
                            String[] s = line.split(",");
                            try {
                                writer.println(String.format("13,%d,%s,%s,%s,%s", storeId, s[0], s[1], s[2], s[3]));
                                reader.readLine();

                                importSuccess();
                            } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                                ex.printStackTrace();
                                formatting = false;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        invalidFile();
                    }
                    if (!formatting) {
                        invalidFormatting();
                    }
                }
            }
            if (e.getSource() == enterButton) { //enter button
                String newName = nameField.getText();
                String newDesc = descField.getText();
                String newQuan = quanField.getText();
                String newPrice = priceField.getText();

                int intQuan = -1;
                double dubPrice = -1;

                boolean validN;
                boolean validD;
                boolean validQ = false;
                boolean validP = false;

                if (!(validN = newName != null && !newName.isEmpty())) {
                    invalidName();
                }

                if (!(validD = newDesc != null && !newDesc.isEmpty())) {
                    invalidDesc();
                }

                try {
                    intQuan = Integer.parseInt(newQuan);
                    validQ = true;
                } catch (NumberFormatException ex) {
                    invalidQuan();
                }

                try {
                    dubPrice = Double.parseDouble(newPrice);
                    validP = true;
                } catch (NumberFormatException ex) {
                    invalidPrice();
                }

                if (validN && validD && validQ && validP) {
                    writer.println(String.format("13,%d,%s,%s,%s,%s", storeId, newName, newDesc, intQuan, dubPrice));
                    try {
                        reader.readLine();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    showEditStores(storeId);
                }
            }
            if (e.getSource() == backButton) { //back button
                showEditStores(storeId);
            }
        };

        //assign functionality
        importButton.addActionListener(actionListener);
        enterButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel namePanel = new JPanel(new FlowLayout());
        JPanel descPanel = new JPanel(new FlowLayout());
        JPanel quanPanel = new JPanel(new FlowLayout());
        JPanel pricePanel = new JPanel(new FlowLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        descPanel.add(descLabel);
        descPanel.add(descField);

        quanPanel.add(quanLabel);
        quanPanel.add(quanField);

        pricePanel.add(priceLabel);
        pricePanel.add(priceField);

        buttonPanel.add(importButton);
        buttonPanel.add(enterButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(namePanel);
        infoPanel.add(descPanel);
        infoPanel.add(quanPanel);
        infoPanel.add(pricePanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showViewShoppingCarts() {
        clearPanel();

        //instantiate elements
        JLabel cartLabel = new JLabel("Shopping Carts");
        JLabel formatLabel = new JLabel("Customer - Product - Quantity - Store - Description - Price");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == backButton) { //back button
                showSeller();
            }
        };

        //assign functionality
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        writer.println("18,");
        try {
            int size = Integer.parseInt(reader.readLine());
            for (int i = 0; i < size; i++) {
                String cartInfo = reader.readLine();
                JLabel label = new JLabel(cartInfo);
                cartPanel.add(label);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        scrollPanel.add(cartLabel);
        scrollPanel.add(formatLabel);
        scrollPanel.add(new JScrollPane(cartPanel));

        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(scrollPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showSellerDashboard(String input, int sortProduct, int sortCustomer) {
        clearPanel();

        if (input != null) {
            // find store index
            int storeIndex = 0;
            writer.println("17,");
            try {
                int size = Integer.parseInt(reader.readLine());
                for (int i = 0; i < size; i++) {
                    if (input.equals(reader.readLine())) {
                        storeIndex = i;
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            //instantiate elements
            JLabel storeLabel = new JLabel(input);
            JLabel productFormatLabel = new JLabel("Products - Revenue");
            JLabel customerFormatLabel = new JLabel("Customers - Purchases");
            JButton sortProductAscending = new JButton("Ascending");
            JButton sortProductDescending = new JButton("Descending");
            JButton sortCustomerAscending = new JButton("Ascending");
            JButton sortCustomerDescending = new JButton("Descending");
            JButton backButton = new JButton("Go back");

            //create functionality
            ActionListener actionListener = e -> {
                if (e.getSource() == sortProductAscending) { //sort price ascending button
                    showSellerDashboard(input, 1, sortCustomer);
                }
                if (e.getSource() == sortProductDescending) { //sort price descending button
                    showSellerDashboard(input, 2, sortCustomer);
                }
                if (e.getSource() == sortCustomerAscending) { //sort quantity ascending button
                    showSellerDashboard(input, sortProduct, 1);
                }
                if (e.getSource() == sortCustomerDescending) { //sort quantity descending button
                    showSellerDashboard(input, sortProduct, 2);
                }
                if (e.getSource() == backButton) { //back button
                    showSeller();
                }
            };

            //assign functionality
            sortProductAscending.addActionListener(actionListener);
            sortProductDescending.addActionListener(actionListener);
            sortCustomerAscending.addActionListener(actionListener);
            sortCustomerDescending.addActionListener(actionListener);
            backButton.addActionListener(actionListener);

            //draw elements
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

            JPanel labelPanel = new JPanel();
            JPanel productScrollPanel = new JPanel();
            productScrollPanel.setLayout(new BoxLayout(productScrollPanel, BoxLayout.Y_AXIS));
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
            JPanel productButtonPanel = new JPanel(new FlowLayout());
            JPanel customerScrollPanel = new JPanel();
            customerScrollPanel.setLayout(new BoxLayout(customerScrollPanel, BoxLayout.Y_AXIS));
            JPanel customerPanel = new JPanel();
            customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
            JPanel customerButtonPanel = new JPanel(new FlowLayout());
            JPanel buttonPanel = new JPanel(new FlowLayout());

            labelPanel.add(storeLabel);

            writer.println("19," + storeIndex + ",1," + sortProduct);
            try {
                int size = Integer.parseInt(reader.readLine());
                for (int i = 0; i < size; i++) {
                    JLabel label = new JLabel(reader.readLine());
                    productPanel.add(label);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            productScrollPanel.add(productFormatLabel);
            productScrollPanel.add(new JScrollPane(productPanel));

            productButtonPanel.add(sortProductAscending);
            productButtonPanel.add(sortProductDescending);

            writer.println("19," + storeIndex + ",2," + sortCustomer);
            try {
                int size = Integer.parseInt(reader.readLine());
                for (int i = 0; i < size; i++) {
                    JLabel label = new JLabel(reader.readLine());
                    customerPanel.add(label);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            customerScrollPanel.add(customerFormatLabel);
            customerScrollPanel.add(new JScrollPane(customerPanel));

            customerButtonPanel.add(sortCustomerAscending);
            customerButtonPanel.add(sortCustomerDescending);

            buttonPanel.add(backButton);

            infoPanel.add(Box.createGlue());
            infoPanel.add(labelPanel);
            infoPanel.add(productScrollPanel);
            infoPanel.add(productButtonPanel);
            infoPanel.add(customerScrollPanel);
            infoPanel.add(customerButtonPanel);
            infoPanel.add(buttonPanel);
            infoPanel.add(Box.createGlue());

            content.add(infoPanel, BorderLayout.CENTER);

            //refresh
            frame.setVisible(true);
        } else {
            showSeller();
        }
    }

    //Customer functions ----------------------------------
    public void showMarketplace(int sortType, String search) {
        //sort type will tell showMarketPlace if any of the sort buttons have been pressed (0 by default)
        //search will tell showMarketPlace if a search term has been entered ("" by default);

        clearPanel();

        //instantiate elements
        JLabel marketLabel = new JLabel("Marketplace");
        JLabel formatLabel = new JLabel("Product - Store - Price - Quantity");
        JLabel selectLabel = new JLabel("Select product:");
        JLabel sortPriceLabel = new JLabel("Sort by price:");
        JLabel sortQuanLabel = new JLabel("Sort by quantity:");

        JTextField searchField = new JTextField(search, 10);

        String[] options = {"Select"};
        int[] ids = {};
        String[] productDetails = {};
        writer.println(String.format("21,%d,%s", sortType, search));
        try {
            String response = reader.readLine();
            int t = response.indexOf(',');
            int size = Integer.parseInt(t > 0 ? response.substring(0, t) : response);
            options = new String[size + 1];
            ids = new int[size];
            productDetails = new String[size];
            options[0] = "Select";
            for (int i = 1; i <= size; i++) {
                String[] productInfo = reader.readLine().split(",");
                options[i] = productInfo[0];
                ids[i - 1] = Integer.parseInt(productInfo[4]);
                productDetails[i - 1] = String.join(" - ", productInfo[0], productInfo[1],
                        productInfo[2], productInfo[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JComboBox<String> dropDown = new JComboBox<>(options);

        JButton searchButton = new JButton("Search:");
        JButton sortPriceAscButton = new JButton("Ascending"); //1
        JButton sortPriceDecButton = new JButton("Descending"); //2
        JButton sortQuanAscButton = new JButton("Ascending"); //3
        JButton sortQuanDecButton = new JButton("Descending"); //4
        JButton enterButton = new JButton("Select Product");
        JButton refreshButton = new JButton("Refresh market");
        JButton backButton = new JButton("Go back");

        //create functionality
        int[] finalIds = ids;
        ActionListener actionListener = e -> {
            if (e.getSource() == searchButton) { //search button
                String searchTerm = searchField.getText();
                System.out.println(searchTerm);

                showMarketplace(0, searchTerm);
            }
            if (e.getSource() == sortPriceAscButton) { //sort price ascending button
                showMarketplace(1, search);
            }
            if (e.getSource() == sortPriceDecButton) { //sort price descending button
                showMarketplace(2, search);
            }
            if (e.getSource() == sortQuanAscButton) { //sort quantity ascending button
                showMarketplace(3, search);
            }
            if (e.getSource() == sortQuanDecButton) { //sort quantity descending button
                showMarketplace(4, search);
            }
            if (e.getSource() == enterButton) { //enter button
                //get entered data
                int productIndex = dropDown.getSelectedIndex();

                if (productIndex != 0) {
                    productIndex--; //converts into scroll panel indexes

                    int productId = finalIds[productIndex];

                    //go to product page
                    showProductPage(productId);
                }
            }
            if (e.getSource() == refreshButton) { //enter button
                showMarketplace(0, "");
            }
            if (e.getSource() == backButton) { //back button
                //go to previous page
                showCustomer();
            }
        };

        //assign functionality
        searchButton.addActionListener(actionListener);
        sortPriceAscButton.addActionListener(actionListener);
        sortPriceDecButton.addActionListener(actionListener);
        sortQuanAscButton.addActionListener(actionListener);
        sortQuanDecButton.addActionListener(actionListener);
        enterButton.addActionListener(actionListener);
        refreshButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout());

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

        JPanel selectPanel = new JPanel(new FlowLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());

        JPanel sortPricePanel = new JPanel(new FlowLayout());
        JPanel sortQuanPanel = new JPanel(new FlowLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout()); //enter, go back

        titlePanel.add(marketLabel);

        for (String s : productDetails) {
            JLabel label = new JLabel(s);
            productPanel.add(label);
        }
        scrollPanel.add(formatLabel);
        scrollPanel.add(new JScrollPane(productPanel));

        selectPanel.add(selectLabel);
        selectPanel.add(dropDown);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        sortPricePanel.add(sortPriceLabel);
        sortPricePanel.add(sortPriceAscButton);
        sortPricePanel.add(sortPriceDecButton);
        sortQuanPanel.add(sortQuanLabel);
        sortQuanPanel.add(sortQuanAscButton);
        sortQuanPanel.add(sortQuanDecButton);

        buttonPanel.add(enterButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(titlePanel);
        infoPanel.add(scrollPanel);
        infoPanel.add(selectPanel);
        infoPanel.add(searchPanel);
        infoPanel.add(sortPricePanel);
        infoPanel.add(sortQuanPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showProductPage(int id) {
        clearPanel();

        String productName = "test";
        String productStore = "test";
        String productDesc = "test";
        int productQuan = 0;
        double productPrice = 0.0;

        writer.println("24," + id);
        try {
            String response = reader.readLine();
            if (response.startsWith("1")) {
                String[] productInfo = response.substring(2).split(",");
                productName = productInfo[0];
                productStore = productInfo[1];
                productDesc = productInfo[2];
                productQuan = Integer.parseInt(productInfo[3]);
                productPrice = Double.parseDouble(productInfo[4]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //instantiate elements
        JLabel headingLabel = new JLabel(productName + " - " + productStore);
        JLabel descLabel = new JLabel(productDesc);
        JLabel dataLabel = new JLabel(productQuan + " for sale at $" +
                String.format("%.2f", productPrice) + " each");
        JLabel quanLabel = new JLabel("Amount to purchase:");
        JTextField quanField = new JTextField(10);
        JButton buyButton = new JButton("Buy");
        JButton cartButton = new JButton("Put item in cart");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == buyButton) { //buy button
                try {
                    int numPurchase = Integer.parseInt(quanField.getText());

                    if (numPurchase > 0) {
                        writer.println(String.format("22,%d,%d", id, numPurchase));
                        try {
                            String result = reader.readLine();
                            if (result.startsWith("0")) {
                                insufficientInventory(result.substring(2));
                                showProductPage(id);
                            } else {
                                purchaseSuccess();
                                showProductPage(id);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        purchaseQuanError();
                    }
                } catch (NumberFormatException ex) {
                    purchaseQuanError();
                }
            }
            if (e.getSource() == cartButton) { //cart button
                try {
                    int numPurchase = Integer.parseInt(quanField.getText());

                    if (numPurchase > 0) {
                        writer.println(String.format("23,%d,%d", id, numPurchase));
                        try {
                            String result = reader.readLine();
                            if (result.startsWith("0")) {
                                insufficientInventory(result.substring(2));
                                showProductPage(id);
                            } else {
                                showMarketplace(0, "");
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        purchaseQuanError();
                    }
                } catch (NumberFormatException ex) {
                    purchaseQuanError();
                }
            }
            if (e.getSource() == backButton) { //back button
                showMarketplace(0, "");
            }
        };

        //assign functionality
        buyButton.addActionListener(actionListener);
        cartButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        JPanel headingPanel = new JPanel(new FlowLayout());
        JPanel descPanel = new JPanel(new FlowLayout());
        JPanel dataPanel = new JPanel(new FlowLayout());
        JPanel quanPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        headingPanel.add(headingLabel);
        descPanel.add(descLabel);
        dataPanel.add(dataLabel);

        quanPanel.add(quanLabel);
        quanPanel.add(quanField);

        buttonPanel.add(buyButton);
        buttonPanel.add(cartButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(headingPanel);
        infoPanel.add(descPanel);
        infoPanel.add(dataPanel);
        infoPanel.add(quanPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showHistory() {
        clearPanel();

        writer.println("26,");
        String[] purchaseHistory = {};
        try {
            int size = Integer.parseInt(reader.readLine());
            purchaseHistory = new String[size];
            for (int i = 0; i < size; i++) {
                purchaseHistory[i] = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //instantiate elements
        JLabel historyLabel = new JLabel("Purchase history");
        JLabel formatLabel = new JLabel("Product - Store - Quantity - Description - Price");
        JButton exportButton = new JButton("Export to file");
        JButton backButton = new JButton("Go back");

        //create functionality
        String[] finalPurchaseHistory = purchaseHistory;
        ActionListener actionListener = e -> {
            if (e.getSource() == exportButton) { //export button
                String file = chooseFile();

                try (PrintWriter pw = new PrintWriter(file)) {
                    for (String s : finalPurchaseHistory) {
                        pw.println(s);
                    }
                    exportHistory();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    invalidFile();
                }
            }
            if (e.getSource() == backButton) { //back button
                showCustomer();
            }
        };

        //assign functionality
        exportButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        for (String s : purchaseHistory) {
            JLabel label = new JLabel(s.replace(",", " - "));
            historyPanel.add(label);
        }

        scrollPanel.add(historyLabel);
        scrollPanel.add(formatLabel);
        scrollPanel.add(new JScrollPane(historyPanel));

        buttonPanel.add(exportButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(scrollPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showShoppingCart() {
        clearPanel();

        //instantiate elements
        JLabel cartLabel = new JLabel("Shopping Cart");
        JLabel formatLabel = new JLabel("Product - Store - Quantity - Price");
        JButton purchaseButton = new JButton("Purchase all");
        JButton removeButton = new JButton("Remove item");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == purchaseButton) { //purchase all button
                writer.println("28,");
                try {
                    String response = reader.readLine();
                    if (response.startsWith("0")) {
                        checkOutError(response.substring(2));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                showShoppingCart();
            }
            if (e.getSource() == removeButton) { //remove button
                removeFromCart();

                showShoppingCart();
            }
            if (e.getSource() == backButton) { //back button
                showCustomer();
            }
        };

        //assign functionality
        purchaseButton.addActionListener(actionListener);
        removeButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        writer.println("27,");
        try {
            int size = Integer.parseInt(reader.readLine());
            for (int i = 0; i < size; i++) {
                JLabel label = new JLabel(reader.readLine().replace(",", " - "));
                cartPanel.add(label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPanel.add(cartLabel);
        scrollPanel.add(formatLabel);
        scrollPanel.add(new JScrollPane(cartPanel));

        buttonPanel.add(purchaseButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(scrollPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    public void showCustomerDashboard(int sortType) {
        //sort type will tell showCustomerDashboard if any of the sort buttons have been pressed (0 by default)

        clearPanel();

        //instantiate elements
        JLabel marketLabel = new JLabel("Dashboard");
        JLabel formatLabel = new JLabel("Store - Products");
        JLabel sortSoldLabel = new JLabel("View total products sold:");
        JLabel sortBoughtLabel = new JLabel("View products bought by you:");

        JButton sortSoldAscButton = new JButton("Ascending"); //1
        JButton sortSoldDecButton = new JButton("Descending"); //2
        JButton sortBoughtAscButton = new JButton("Ascending"); //3
        JButton sortBoughtDecButton = new JButton("Descending"); //4
        JButton refreshButton = new JButton("Refresh dashboard");
        JButton backButton = new JButton("Go back");

        //create functionality
        ActionListener actionListener = e -> {
            if (e.getSource() == sortSoldAscButton) { //sort sold ascending button
                showCustomerDashboard(1);
            }
            if (e.getSource() == sortSoldDecButton) { //sort sold descending button
                showCustomerDashboard(2);
            }
            if (e.getSource() == sortBoughtAscButton) { //sort bought ascending button
                showCustomerDashboard(3);
            }
            if (e.getSource() == sortBoughtDecButton) { //sort bought descending button
                showCustomerDashboard(4);
            }
            if (e.getSource() == refreshButton) { //refresh button
                showCustomerDashboard(sortType);
            }
            if (e.getSource() == backButton) { //back button
                //go to previous page
                showCustomer();
            }
        };

        //assign functionality
        sortSoldAscButton.addActionListener(actionListener);
        sortSoldDecButton.addActionListener(actionListener);
        sortBoughtAscButton.addActionListener(actionListener);
        sortBoughtDecButton.addActionListener(actionListener);
        refreshButton.addActionListener(actionListener);
        backButton.addActionListener(actionListener);

        //draw elements
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout());

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

        JPanel sortSoldPanel = new JPanel(new FlowLayout());
        JPanel sortBoughtPanel = new JPanel(new FlowLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout()); //enter, go back

        titlePanel.add(marketLabel);

        int type = 2;
        int order = 0;
        switch (sortType) {
            case 1 -> order = 1;
            case 2 -> order = 2;
            case 3 -> {
                type = 1;
                order = 1;
            }
            case 4 -> {
                type = 1;
                order = 2;
            }
        }
        writer.println(String.format("25,%d,%d", type, order));
        try {
            int size = Integer.parseInt(reader.readLine());
            for (int i = 0; i < size; i++) {
                JLabel label = new JLabel(reader.readLine());
                productPanel.add(label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollPanel.add(formatLabel);
        scrollPanel.add(new JScrollPane(productPanel));

        sortSoldPanel.add(sortSoldLabel);
        sortSoldPanel.add(sortSoldAscButton);
        sortSoldPanel.add(sortSoldDecButton);
        sortBoughtPanel.add(sortBoughtLabel);
        sortBoughtPanel.add(sortBoughtAscButton);
        sortBoughtPanel.add(sortBoughtDecButton);

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        infoPanel.add(Box.createGlue());
        infoPanel.add(titlePanel);
        infoPanel.add(scrollPanel);
        infoPanel.add(sortSoldPanel);
        infoPanel.add(sortBoughtPanel);
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createGlue());

        content.add(infoPanel, BorderLayout.CENTER);

        //refresh
        frame.setVisible(true);
    }

    //Miscellaneous functions -------------------------
    public void clearPanel() {
        content.removeAll();
        frame.setVisible(true);
    }

    public int indexOf(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    //Popups ----------------------------------------------
    public void showWelcome() {
        JOptionPane.showMessageDialog(null, "Welcome to YouMarket, an all-purpose market!",
                "Welcome!", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showUserWelcome(String name) {
        JOptionPane.showMessageDialog(null, "Welcome, " + name + "!",
                "Welcome!", JOptionPane.INFORMATION_MESSAGE);

    }

    public void showFarewell() {
        frame.dispose();
        JOptionPane.showMessageDialog(null, "Thank you for using YouMarket!",
                "Goodbye!", JOptionPane.INFORMATION_MESSAGE);
    }

    public String loginOrCreate() {
        String[] options = {"Log in", "Create an account"};
        return (String) JOptionPane.showInputDialog(null,
                "Would you like to log in or create an account?", "YouMarket",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public int confirmDeleteAccount() {
        return JOptionPane.showConfirmDialog(null,
                "Are you sure you wish to delete this account?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    public int confirmDeleteStore() {
        return JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete this store?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    public int confirmDeleteProduct() {
        return JOptionPane.showConfirmDialog(null,
                "Are you sure you wish to delete this product?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    public String changeStoreName() {
        return JOptionPane.showInputDialog(null, "What will you change the store name to?",
                "Change name", JOptionPane.QUESTION_MESSAGE);
    }

    public void exportStore() {
        JOptionPane.showMessageDialog(null, "Store successfully exported!", "Exported",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void importSuccess() {
        JOptionPane.showMessageDialog(null, "Products successfully imported!", "Imported",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void purchaseSuccess() {
        JOptionPane.showMessageDialog(null, "Products successfully purchased!", "Purchased",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public String chooseFile() {
        JFileChooser fc = new JFileChooser();
        int r = fc.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        return "";
    }

    public String createStore() {
        return JOptionPane.showInputDialog(null, "Please enter the name of your new store:",
                "Create store", JOptionPane.QUESTION_MESSAGE);
    }

    public String selectSellerDashboard() {
        String[] options;
        writer.println("17,");
        try {
            int size = Integer.parseInt(reader.readLine());
            options = new String[size + 1];
            options[0] = "Select";
            for (int i = 1; i <= size; i++) {
                options[i] = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return (String) JOptionPane.showInputDialog(null, "Please select a store",
                "YouMarket", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public void exportHistory() {
        JOptionPane.showMessageDialog(null, "History successfully exported!", "Exported",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void removeFromCart() {
        String[] options = {"Select"};
        writer.println("27,");
        try {
            int size = Integer.parseInt(reader.readLine());
            options = new String[size + 1];
            options[0] = "Select";
            for (int i = 0; i < size; i++) {
                String[] cartInfo = reader.readLine().split(",");
                options[i + 1] = cartInfo[0];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String delete = (String) JOptionPane.showInputDialog(null,
                "Please select a product to remove", "YouMarket", JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (delete != null && !delete.equals("Select")) {
            int index = indexOf(options, delete) - 1;
            writer.println("29," + index);
            try {
                reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Error Popups ----------------------------------------

    public void cannotConnect() {
        JOptionPane.showMessageDialog(null, "Could not connect to server!",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidCredentials() {
        JOptionPane.showMessageDialog(null, "Credentials are invalid.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void loginError() {
        JOptionPane.showMessageDialog(null, "Cannot log in!", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void takenEmail() {
        JOptionPane.showMessageDialog(null, "That email is taken, please enter a different one.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void createAccountError() {
        JOptionPane.showMessageDialog(null, "Cannot create account!",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidQuan() {
        JOptionPane.showMessageDialog(null, "Quantity must be a valid integer!",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidPrice() {
        JOptionPane.showMessageDialog(null, "Price must be a valid number!",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void purchaseQuanError() {
        JOptionPane.showMessageDialog(null, "Number to be purchased must be a valid integer!",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void insufficientInventory(String errorMessage) {
        JOptionPane.showMessageDialog(null, "There are not enough products to purchase!\n" +
                errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void checkOutError(String errorMessage) {
        JOptionPane.showMessageDialog(null, "Not all products could be checked out!\n" +
                errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidFile() {
        JOptionPane.showMessageDialog(null, "File is invalid!", "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void invalidFormatting() {
        JOptionPane.showMessageDialog(null,
                "Not all products could be imported! Check your file formatting.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidName() {
        JOptionPane.showMessageDialog(null, "Name cannot be blank.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void invalidDesc() {
        JOptionPane.showMessageDialog(null, "Description cannot be blank.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

}