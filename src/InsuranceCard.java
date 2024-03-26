import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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

    public static void addInsuranceCard(Scanner scanner, List<Customer> customers, List<InsuranceCard> existingCards) {
        String cardNumber;
        while (true) {
            System.out.print("Enter card number (10 digits): ");
            cardNumber = scanner.nextLine();
            if (!cardNumber.matches("\\d{10}") || !isUniqueCardNumber(cardNumber, existingCards)) {
                System.out.println("Invalid or duplicate card number. Please enter a unique 10-digit card number.");
            } else {
                break; // Valid and unique card number, exit the loop
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
            } else {
                break; // Valid customer found, exit the loop
            }
        }

        String policyOwner;
        System.out.print("Enter policy owner: ");
        policyOwner = scanner.nextLine();

        Date expirationDate;
        while (true) {
            System.out.print("Enter expiration date (DD/MM/YYYY): ");
            String expirationDateString = scanner.nextLine();
            try {
                expirationDate = dateFormat.parse(expirationDateString);
                break; // Valid date format, exit the loop
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use DD/MM/YYYY format.");
            }
        }

        // Apply the card to the policyholder and dependents
        applyCardToFamily(customer, cardNumber, customers);
        InsuranceCard card = new InsuranceCard(cardNumber, customer.getId(), policyOwner, expirationDate);
        existingCards.add(card);
        saveToFile(card);
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

    private static boolean isUniqueCardNumber(String cardNumber, List<InsuranceCard> existingCards) {
        return existingCards.stream().noneMatch(card -> card.cardNumber.equals(cardNumber));
    }

    private static void saveToFile(InsuranceCard card) {
        try (FileWriter fw = new FileWriter(INSURANCE_CARD_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(card.cardNumber + "," + card.cardHolder + "," + card.policyOwner + "," + dateFormat.format(card.expirationDate));
            System.out.println("Insurance card saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the insurance card.");
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
