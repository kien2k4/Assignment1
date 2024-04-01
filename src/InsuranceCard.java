import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InsuranceCard {
    private final String cardNumber;
    private final String cardHolder;
    private final String policyOwner;
    private final Date expirationDate;
    private static final String INSURANCE_CARD_FILE = "insurance_cards.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public InsuranceCard(String cardNumber, String cardHolder, String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public static void manageInsuranceCards(Scanner scanner, List<Customer> customers, List<InsuranceCard> existingCards) {
        int option;
        do {
            System.out.println("\n--- Manage Insurance Cards ---");
            System.out.println("1. Add Insurance Card");
            System.out.println("2. Delete Insurance Card");
            System.out.println("3. Update Insurance Card");
            System.out.println("4. Main Page");
            System.out.print("Select an option: ");
            option = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character left by nextInt()

            switch (option) {
                case 1:
                    addInsuranceCard(scanner, customers, existingCards);
                    break;
                case 2:
                    deleteInsuranceCard(scanner, existingCards, customers);
                    break;
                case 3:
                    updateInsuranceCard(scanner, existingCards);
                    break;
                case 4:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please select a valid option.");
                    break;
            }
        } while (option != 4);
    }

    public static void addInsuranceCard(Scanner scanner, List<Customer> customers, List<InsuranceCard> existingCards) {
        String cardNumber;
        while (true) {
            System.out.print("Enter card number (10 digits): ");
            cardNumber = scanner.nextLine();
            if (!cardNumber.matches("\\d{10}")) {
                System.out.println("Invalid card number. Please enter a unique 10-digit card number.");
            } else if (!isUniqueCardNumber(cardNumber, existingCards)) {
                System.out.println("Duplicate card number. Please enter a unique 10-digit card number.");
            } else {
                break; // Exit loop if card number is valid and unique
            }
        }

        Customer customer;
        while (true) {
            System.out.print("Enter customer ID (format 'c-' followed by 7 numbers): ");
            String customerId = scanner.nextLine();
            customer = customers.stream()
                                .filter(c -> c.getId().equals(customerId))
                                .findFirst()
                                .orElse(null);

            if (customer == null) {
                System.out.println("No customer found with this ID. Please try again.");
            } else if (!Objects.equals(customer.getInsuranceCardId(), "null") && !customer.getInsuranceCardId().isEmpty()) {
                System.out.println("This customer already has an insurance card assigned. Please enter a different customer ID.");
            } else {
                break; // Valid customer found and does not have an insurance card
            }
        }

        String policyOwner;
        while (true) {
            System.out.print("Enter policy owner: ");
            policyOwner = scanner.nextLine();
            if (policyOwner.trim().isEmpty()) {
                System.out.println("Policy owner cannot be empty. Please enter the policy owner.");
            } else {
                break; // Exit loop if policy owner input is valid
            }
        }

        Date expirationDate = null;
        while (expirationDate == null) {
            System.out.print("Enter expiration date (DD/MM/YYYY): ");
            String expirationDateString = scanner.nextLine();
            try {
                expirationDate = dateFormat.parse(expirationDateString);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use DD/MM/YYYY format.");
            }
        }

        applyCardToFamily(customer, cardNumber, customers);
        InsuranceCard card = new InsuranceCard(cardNumber, customer.getId(), policyOwner, expirationDate);
        existingCards.add(card);
        saveToFile(existingCards);
        System.out.println("Insurance card added successfully.");
    }

    private static void applyCardToFamily(Customer customer, String cardNumber, List<Customer> customers) {
        if ("PH".equals(customer.getRole())) {
            // Apply card to the policyholder and all dependents
            customer.setInsuranceCardId(cardNumber);
            for (Customer dependent : customer.getDependents()) {
                dependent.setInsuranceCardId(cardNumber);
            }
        } else {
            // Apply card to the dependent's policyholder and all dependents of that policyholder
            Customer policyHolder = customers.stream()
                                             .filter(c -> c.getDependents().contains(customer))
                                             .findFirst()
                                             .orElse(null);
            if (policyHolder != null) {
                policyHolder.setInsuranceCardId(cardNumber);
                for (Customer dependent : policyHolder.getDependents()) {
                    dependent.setInsuranceCardId(cardNumber);
                }
                // Also set for the dependent if not already set
                customer.setInsuranceCardId(cardNumber);
            }
        }
        // Save changes for all customers to reflect the new card assignments
        Customer.saveCustomersToFile();
    }

    private static void deleteInsuranceCard(Scanner scanner, List<InsuranceCard> existingCards, List<Customer> customers) {
        String cardNumber;
        InsuranceCard cardToDelete;

        while (true) {
            System.out.print("Enter the card number of the insurance card to delete: ");
            cardNumber = scanner.nextLine();
            String finalCardNumber = cardNumber;
            cardToDelete = existingCards.stream()
                    .filter(card -> card.cardNumber.equals(finalCardNumber))
                    .findFirst()
                    .orElse(null);

            if (cardToDelete != null) {
                existingCards.remove(cardToDelete);
                System.out.println("Insurance card with number " + cardNumber + " has been successfully deleted.");

                for (Customer customer : customers) {
                    if (cardNumber.equals(customer.getInsuranceCardId())) {
                        customer.setInsuranceCardId(null);
                    }
                }
                saveToFile(existingCards);
                Customer.saveCustomersToFile();
                break; // Exit the loop after successful deletion
            } else {
                System.out.println("No insurance card found with number " + cardNumber + ". Please try again.");
                // Continue looping to ask for the card number again
            }
        }
    }

    private static void updateInsuranceCard(Scanner scanner, List<InsuranceCard> existingCards) {
        // Implementation of updateInsuranceCard method
    }


    private static boolean isUniqueCardNumber(String cardNumber, List<InsuranceCard> existingCards) {
        return existingCards.stream().noneMatch(card -> card.cardNumber.equals(cardNumber));
    }

    private static void saveToFile(List<InsuranceCard> cards) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INSURANCE_CARD_FILE, false))) {
            for (InsuranceCard card : cards) {
                writer.write(card.cardNumber + "," + card.cardHolder + "," + card.policyOwner + "," + dateFormat.format(card.expirationDate));
                writer.newLine();
            }
            System.out.println("Insurance card list saved successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving the insurance card list: " + e.getMessage());
        }
    }

    public static List<InsuranceCard> loadInsuranceCards() {
        List<InsuranceCard> cards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(INSURANCE_CARD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String cardNumber = parts[0];
                    String cardHolder = parts[1];
                    String policyOwner = parts[2];
                    Date expirationDate = dateFormat.parse(parts[3]);
                    InsuranceCard card = new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate);
                    cards.add(card);
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while loading insurance cards: " + e.getMessage());
        }
        return cards;
    }

    public static void viewInsuranceCards() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INSURANCE_CARD_FILE))) {
            String line;
            System.out.printf("%-12s %-14s %-14s %-12s\n", "Card Number", "Card Holder", "Policy Owner", "Expiration Date");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String cardNumber = parts[0];
                    String cardHolder = parts[1];
                    String policyOwner = parts[2];
                    Date expirationDate = dateFormat.parse(parts[3]);
                    System.out.printf("%-12s %-14s %-14s %-12s\n", cardNumber, cardHolder, policyOwner, dateFormat.format(expirationDate));
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while loading insurance cards: " + e.getMessage());
        }
    }
}
