import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Customer.loadCustomerFromFile(); // This loads customers into the static list in the Customer class
        InsuranceCard.loadInsuranceCards(); // This loads cards into the static list in the Card class

        System.out.println("\nWelcome to insurance claims management system");
        System.out.println("=============================================");
        System.out.println("Below is all data in the system");
        System.out.println();
        System.out.println("1. Customer information:");
        Customer.viewCustomers();
        System.out.println();
        System.out.println("2. Insurance card information:");
        InsuranceCard.viewInsuranceCards();
        System.out.println();
        System.out.println("3. Claim Information:");


        try (Scanner scanner = new Scanner(System.in)) {
            int option;
            do {
                System.out.println("\n1. Manage Customers \n2. Manage Insurance Cards \n3. Manage Claims \n4. Exit \nPlease select an option(1-4): ");
                option = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (option) {
                    case 1:
                        Customer.handleCustomerOperations(scanner);
                        break;
                    case 2:
                        List<InsuranceCard> existingCards = InsuranceCard.loadInsuranceCards(); // Ensure this list is updated
                        InsuranceCard.addInsuranceCard(scanner, Customer.customers, existingCards);
                        break;
                    case 3:
                        // Placeholder for claim operations
                        break;
                    case 4:
                        System.out.println("Exiting the system...");
                        break;
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            } while (option != 4);
        } finally {
            Customer.saveCustomersToFile(); // Save data to the file when exiting
        }
    }
}
