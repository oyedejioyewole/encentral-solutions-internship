package Week4;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Question5 {
    static class Customer {
        String firstName;
        String lastName;
        String accountNumber;
        String phoneNumber;

        Customer(String firstName, String lastName, String accountNumber, String phoneNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.accountNumber = accountNumber;
            this.phoneNumber = phoneNumber;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    static class Transaction {
        int transactionId;
        String accountNumber;
        int amount;
        String type; // It can either be "credit" or "debit"

        Transaction(int transactionId, String accountNumber, int amount, String type) {
            this.transactionId = transactionId;
            this.accountNumber = accountNumber;
            this.amount = amount;
            this.type = type;
        }
    }

    static class USSDApp {
        HashMap<String, Customer> customers = new HashMap<>();
        ArrayList<Transaction> transactions = new ArrayList<>();

        USSDApp() {
            this.loadCustomerData();
            this.loadTransactionData();
        }

        private void loadCustomerData() {
            String customerData = """
            First Name,Last Name,Account Number,Phone Number
            Onome,Ehigiator,6152,08148975238
            Adegoke,Akeem-omosanya,6972,07015181324
            Bukola,Ehigiator,8467,07029300358
            Olufunmi,Aremu,3976,08010170877
            Ifeanyichukwu,Ekwueme,8965,07021118253
            Isioma,Mustapha,8555,09164393835
            Ayebatari,Joshua,8657,09050143877
            Nnamdi,Olawale,3587,07021899665
            Lola,Abosede,6807,07062943330
            Emeka,Oyelude,6701,08190576207
            """;

            String[] lines = customerData.trim().split("\n");
            // Skip header
            for (int index = 1; index < lines.length; index++) {
                String[] parts = lines[index].split(",");
                Customer customer = new Customer(parts[0], parts[1], parts[2], parts[3]);
                customers.put(parts[2], customer); // Use account number as key
            }
        }

        private void loadTransactionData() {
            String transactionData = """
            SN,Account Number,Amount,Credit/Debit
            1,6152,2008,Credit
            2,6152,1173,Credit
            3,6152,2994,Credit
            4,6152,2147,Debit
            5,6152,4989,Debit
            6,6972,4344,Credit
            7,6972,4545,Credit
            8,6972,4021,Credit
            9,6972,4991,Debit
            10,6972,2038,Credit
            11,8467,2243,Credit
            12,8467,3216,Credit
            13,8467,2417,Debit
            14,8467,2106,Credit
            15,8467,4533,Debit
            16,3976,4616,Credit
            17,3976,4941,Credit
            18,3976,1439,Debit
            19,3976,4082,Credit
            20,3976,2022,Debit
            21,8965,3248,Credit
            22,8965,3921,Credit
            23,8965,3309,Debit
            24,8965,1880,Credit
            25,8965,3936,Debit
            26,8555,4511,Credit
            27,8555,1902,Credit
            28,8555,1097,Debit
            29,8555,2007,Credit
            30,8555,3289,Credit
            31,8657,3530,Debit
            32,8657,4565,Debit
            33,8657,1669,Credit
            34,8657,1054,Credit
            35,8657,4723,Debit
            36,3587,4673,Debit
            37,3587,2722,Credit
            38,3587,3554,Credit
            39,3587,2891,Debit
            40,3587,3590,Credit
            41,6807,1711,Credit
            42,6807,4020,Credit
            43,6807,1594,Debit
            44,6807,4692,Debit
            45,6807,1774,Credit
            46,6701,4629,Credit
            47,6701,3602,Debit
            48,6701,1010,Credit
            49,6701,3596,Credit
            50,6701,1632,Debit
            """;

            String[] lines = transactionData.trim().split("\n");
            for (int index = 1; index < lines.length; index++) { // Skip header
                String[] parts = lines[index].split(",");
                Transaction transaction = new Transaction(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Integer.parseInt(parts[2]),
                        parts[3]
                );
                transactions.add(transaction);
            }
        }

        public void checkAccountBalance(String accountNumber) {
            Customer customer = customers.get(accountNumber);
            if (customer == null) {
                System.out.println("Couldn't find an account, check the account number you provided :(");
                return;
            }

            int balance = 0;
            for (Transaction transaction : transactions) {
                if (transaction.accountNumber.equals(accountNumber)) {
                    if (transaction.type.equals("Credit")) {
                        balance += transaction.amount;
                    } else {
                        balance -= transaction.amount;
                    }
                }
            }

            System.out.printf("%s, your account %s %d%n", customer.getFullName(), (balance < -1) ? "down": "up",  Math.abs(balance));
        }

        public void checkAccountDetails(String accountNumber) {
            Customer customer = customers.get(accountNumber);
            if (customer == null) {
                System.out.println("Couldn't find an account, check the account number you provided :(");
                return;
            }

            System.out.printf("""
            Your account details are:

            Account number: %s
            First Name: %s
            Last Name: %s
            Phone Number: %s%n
            """, accountNumber, customer.firstName, customer.lastName, customer.phoneNumber);
        }

        public void printLastThreeTransactions(String accountNumber) {
            Customer customer = customers.get(accountNumber);
            if (customer == null) {
                System.out.println("Account not found!");
                return;
            }

            List<Transaction> accountTransactions = transactions.stream()
                    .filter(transaction -> transaction.accountNumber.equals(accountNumber))
                    .toList();

            // Get last 3 transactions
            List<Transaction> lastThree = accountTransactions.stream()
                    .skip(Math.max(0, accountTransactions.size() - 3))
                    .toList();

            System.out.printf("""
            These are your last 3 transactions, %s%n
            --------------------------------------
            | Amount |      Transaction Type     |
            --------------------------------------
            """, customer.getFullName());

            for (Transaction transaction : lastThree) {
                System.out.printf("| %d | %s |%n", transaction.amount, transaction.type.toUpperCase());
                System.out.println("--------------------------------------");
            }
        }

        public void changePhoneNumber(String accountNumber, String oldPhone, String newPhone) {
            Customer customer = customers.get(accountNumber);
            if (customer == null) {
                System.out.println("Couldn't find an account, check the account number you provided :(");
                return;
            }

            if (!customer.phoneNumber.equals(oldPhone)) {
                System.out.printf("The phone number provided isn't correct for %s.%n",
                        customer.getFullName());
                return;
            }

            customer.phoneNumber = newPhone;
            System.out.println("Your phone number has been updated.");
        }

        public void showMenu() {
            System.out.println("""
            === USSD BANK APP ===
            1. Check account balance
            2. Check account details
            3. Print last three transactions
            4. Change phone number
            5. Exit
            """);

            System.out.print("Choose an option: ");
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                showMenu();
                int choice = scanner.nextInt();
                // Consume newline
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter your account number: ");
                        String accountNum1 = scanner.nextLine();
                        checkAccountBalance(accountNum1);
                        break;

                    case 2:
                        System.out.print("Enter your account number: ");
                        String accountNum2 = scanner.nextLine();
                        checkAccountDetails(accountNum2);
                        break;

                    case 3:
                        System.out.print("Enter your account number: ");
                        String accountNum3 = scanner.nextLine();
                        printLastThreeTransactions(accountNum3);
                        break;

                    case 4:
                        System.out.print("Enter your account number: ");
                        String accountNum4 = scanner.nextLine();
                        System.out.print("Enter old phone number: ");
                        String oldPhone = scanner.nextLine();
                        System.out.print("Enter new phone number: ");
                        String newPhone = scanner.nextLine();
                        changePhoneNumber(accountNum4, oldPhone, newPhone);
                        break;

                    case 5:
                        System.out.println("Thank you for using USSD banking.");
                        scanner.close();
                        return;

                    default:
                        System.out.println("The option you picked doesn't exist, please try again.");
                }
            }
        }
    }

    public static void main(String[] args) {
        USSDApp app = new USSDApp();
        app.run();
    }
}