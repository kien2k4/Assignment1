/**
 * @author <Dang Trung Kien - s3979510>
 */
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Customer.loadCustomerFromFile(); // This loads customers into the static list in the Customer class
        InsuranceCard.loadInsuranceCards(); // This loads cards into the static list in the Card class
        Claim.loadClaimsFromFile(); // This loads claims into the list in the Claim class

        System.out.println("\nWelcome to insurance claims management system");
        System.out.println("=============================================");
        System.out.println();
        System.out.println("1. Customer information:");
        Customer.viewCustomers();
        System.out.println();
        System.out.println("2. Insurance card information:");
        InsuranceCard.viewInsuranceCards();
        System.out.println();
        System.out.println("3. Claim Information:");
        Claim claim = new Claim();
        claim.viewClaims();

        try (Scanner scanner = new Scanner(System.in)) {
            int option = 0;
            do {
                System.out.println("\n1. Manage Customers \n2. Manage Insurance Cards \n3. Manage Claims \n4. Exit");
                System.out.print("Please select an option (1-4): ");
                String input = scanner.nextLine();
                try {
                    option = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, please enter a number between 1 and 4.");
                    continue; // Continue to the next iteration of the loop if input is not an integer
                }
                switch (option) {
                    case 1:
                        Customer.handleCustomerOperations(scanner);
                        break;
                    case 2:
                        List<InsuranceCard> existingCards = InsuranceCard.loadInsuranceCards(); // Ensure this list is updated
                        InsuranceCard.manageInsuranceCards(scanner, Customer.customers, existingCards);
                        break;
                    case 3:
                        Claim.manageClaims(scanner, Customer.customers);
                        break;
                    case 4:
                        System.out.println("Exiting the system...");
                        break;
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            } while (option != 4);
        }
    }
}
