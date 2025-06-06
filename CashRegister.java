import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashRegister {
    static Scanner scan = new Scanner(System.in);
    static ArrayList<String> food = new ArrayList<>();
    static ArrayList<Integer> price = new ArrayList<>();
    static ArrayList<Integer> quant = new ArrayList<>();
    static ArrayList<String> usernameCreds = new ArrayList<>();
    static ArrayList<String> passwordCreds = new ArrayList<>();
    static String currentUser;

    public static void main(String[] args) {

        signUpWelcome();
        signUp();
        logIn();

        while (true) {
            food.clear();
            price.clear();
            quant.clear();
            displayMenu();
            takeOrders();

            boolean managing = true;
            while (managing && !food.isEmpty()) {
                System.out.println("\nOrder Management:");
                System.out.println("1. Update quantity");
                System.out.println("2. Remove item");
                System.out.println("3. Display order");
                System.out.println("4. Continue to checkout");
                System.out.print("Choose an option (1-4): ");

                try {
                    int choice = scan.nextInt();
                    scan.nextLine();
                    switch (choice) {
                        case 1:
                            updateQuantity();
                            printOrderSummary();
                            break;
                        case 2:
                            removeOrder();
                            if (!food.isEmpty()) {
                                printOrderSummary();
                            }
                            break;
                        case 3:
                            printOrderSummary();
                            break;
                        case 4:
                            managing = false;
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scan.nextLine();
                }
            }

            if (!food.isEmpty()) {
                printOrderSummary();
                processPayment();
            }

            System.out.print("Do you want to perform another transaction? (y/n): ");
            String again = scan.nextLine();

            if (!again.equalsIgnoreCase("y")) {
                break;
            }
        }
        thankYouMessage();
    }

    static void displayMenu() {
        System.out.println("---------- Welcome to McDonald's 🍟 ----------");
        System.out.println("|                 Menu 😍                    |");
        System.out.println("|       1. Burger      ; ₱99                 |");
        System.out.println("|       2. Chicken     ; ₱149                |");
        System.out.println("|       3. McFlurry    ; ₱59                 |");
        System.out.println("----------------------------------------------");
        System.out.println();
    }

    static void takeOrders() {
        System.out.print("Do you want to place an order? (y/n): ");
        String choice = scan.nextLine();

        if (choice.equalsIgnoreCase("n")) {
            System.out.println("Thank you for visiting McDonald's! Have a great day! 😊");
            System.exit(0);
        }

        while (choice.equalsIgnoreCase("y")) {
            System.out.print("Enter food item (1, 2, 3) or type (done) when finish: ");
            String itemAdd = scan.nextLine();

            if (itemAdd.equals("1")) {
                food.add("Burger");
                price.add(99);
            } else if (itemAdd.equals("2")) {
                food.add("Chicken");
                price.add(149);
            } else if (itemAdd.equals("3")) {
                food.add("McFlurry");
                price.add(59);
            } else if (itemAdd.equalsIgnoreCase("done")) {
                break;
            } else {
                System.out.println("Item is not on the menu");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantAdd = scan.nextInt();
            quant.add(quantAdd);
            scan.nextLine();
            System.out.println();
        }
    }

    static void printOrderSummary() {
        System.out.println();
        System.out.println("--------------------- Order Summary ---------------------");
        int total = 0;
        for (int i = 0; i < food.size(); i++) {
            System.out.println(quant.get(i) + " x " + food.get(i) + " - " + "₱" + (price.get(i) * quant.get(i)));
            total += (price.get(i) * quant.get(i));
        }
        System.out.println("---------------------------------------------------------");
        System.out.println("Total amount: ₱" + total);
    }

    static int calculateTotal() {
        int total = 0;
        for (int i = 0; i < food.size(); i++) {
            total += (price.get(i) * quant.get(i));
        }
        return total;
    }

    static void processPayment() {
        int total = calculateTotal();
        int payment = 0;
        boolean validPayment = false;

        while (!validPayment) {
            try {
                System.out.print("Enter payment amount: ");
                payment = scan.nextInt();
                scan.nextLine();

                if (payment < total) {
                    System.out.println("Insufficient payment. Please enter a valid amount.");
                    continue;
                }
                validPayment = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scan.nextLine();
            }
        }

        System.out.println("Change: ₱" + (payment - total));
        logTransaction(total, payment);
    }

    static void logTransaction(int total, int payment) {
        try {
            FileWriter fw = new FileWriter("transactions.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            out.println("----------------------------------------");
            out.println("Transaction Date: " + timestamp);
            out.println("Cashier: " + currentUser);
            out.println("Items:");
            for (int i = 0; i < food.size(); i++) {
                out.printf("%d x %s - ₱%d%n",
                        quant.get(i), food.get(i), (price.get(i) * quant.get(i)));
            }
            out.println("Total Amount: ₱" + total);
            out.println("Payment: ₱" + payment);
            out.println("Change: ₱" + (payment - total));
            out.println("----------------------------------------\n");

            out.close();
        } catch (IOException e) {
            System.out.println("Error writing to transaction log: " + e.getMessage());
        }
    }

    static void updateQuantity() {
        if (food.isEmpty()) {
            System.out.println("No items in order to update.");
            return;
        }

        try {
            System.out.println("\nEnter the item number to update (1 to " + food.size() + "):");
            int index = scan.nextInt() - 1;
            scan.nextLine();

            if (index >= 0 && index < food.size()) {
                System.out.println("Current quantity of " + food.get(index) + ": " + quant.get(index));
                System.out.print("Enter new quantity: ");
                int newQuant = scan.nextInt();
                scan.nextLine();

                if (newQuant > 0) {
                    quant.set(index, newQuant);
                    System.out.println("Quantity updated successfully!");
                } else {
                    System.out.println("Quantity must be greater than 0.");
                }
            } else {
                System.out.println("Invalid item number.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scan.nextLine();
        }
    }

    static void removeOrder() {
        if (food.isEmpty()) {
            System.out.println("No items in order to remove.");
            return;
        }

        try {
            System.out.println("\nEnter the item number to remove (1 to " + food.size() + "):");
            int index = scan.nextInt() - 1;
            scan.nextLine();

            if (index >= 0 && index < food.size()) {
                String removedItem = food.get(index);
                food.remove(index);
                price.remove(index);
                quant.remove(index);
                System.out.println(removedItem + " removed from order.");
            } else {
                System.out.println("Invalid item number.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scan.nextLine();
        }
    }

    static void thankYouMessage() {
        System.out.println("----------------------------------------------");
        System.out.println("Thank you for ordering at McDonald's! Enjoy your meal!");
    }

    static void signUpWelcome() {
        System.out.println("|---------------------------------------| ");
        System.out.println("|               Welcome!                |");
        System.out.println("|           Please sign up!             |");
        System.out.println("|---------------------------------------| ");
        System.out.println();
    }

    static void signUp() {
        while (true) {
            System.out.print("Enter username: ");
            String username = scan.nextLine();
            if (username.matches("^[a-zA-Z0-9]{5,15}$") && !usernameCreds.contains(username)) {
                usernameCreds.add(username);
                break;
            } else {
                System.out.println("Invalid Username. Username must be alphanumeric and 5–15 characters long.");
            }
        }

        while (true) {
            System.out.print("Enter password: ");
            String password = scan.nextLine();
            if (password.matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$")) {
                passwordCreds.add(password);
                System.out.println("Sign up successful!");
                break;
            } else {
                System.out.println("Invalid Password. Password must contain at least one uppercase letter, one number, and be 8–20 characters long.");
            }
        }

        System.out.println("|---------------------------------------| ");
        System.out.println("|        Proceeding to Login....        |");
        System.out.println("|---------------------------------------| ");
        System.out.println();
    }

    static void logIn() {
        while (true) {
            boolean loggedIn = false;
            System.out.print("Enter username: ");
            String userLogin = scan.nextLine();

            System.out.print("Enter password: ");
            String passLogin = scan.nextLine();

            for (int i = 0; i < usernameCreds.size(); i++) {
                if (usernameCreds.get(i).equals(userLogin) && passwordCreds.get(i).equals(passLogin)) {
                    loggedIn = true;
                    currentUser = userLogin;
                    break;
                }
            }
            if (loggedIn) {
                System.out.println("Login successful! Welcome back, " + userLogin);
                break;
            } else {
                System.out.println("Login credentials are incorrect. Please try again.");
            }
        }
    }
}
