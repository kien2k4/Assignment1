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
    private static final String INSURANCE_CARD = "insurance_cards.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public InsuranceCard(String cardNumber, String cardHolder, String policyOwner, Date expirationDate) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.policyOwner = policyOwner;
        this.expirationDate = expirationDate;
    }

    public static void addInsuranceCard(Scanner scanner, List<Customer> customers, List<InsuranceCard> existingCards) {
        System.out.print("Enter card number (10 digits): ");
        String cardNumber = scanner.nextLine();
        // Check for card number uniqueness
        if (!cardNumber.matches("\\d{10}") || !isUniqueCardNumber(cardNumber, existingCards)) {
            System.out.println("Invalid or duplicate card number. Please enter a unique 10-digit card number.");
            return;
        }
        System.out.print("Enter card holder ID (c-7numbers format): ");
        String cardHolderId = scanner.nextLine();
        // Find the customer who will hold the card
        Customer cardHolder = customers.stream()
                .filter(c -> c.getId().equals(cardHolderId))
                .findFirst()
                .orElse(null);
        if (cardHolder == null) {
            System.out.println("No customer found with this ID. Please try again.");
            return;
        }
        System.out.print("Enter policy owner: ");
        String policyOwner = scanner.nextLine();
        System.out.print("Enter expiration date (DD/MM/YYYY): ");
        String expirationDateString = scanner.nextLine();
        Date expirationDate;
        try {
            expirationDate = dateFormat.parse(expirationDateString);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use DD/MM/YYYY format.");
            return;
        }
        InsuranceCard card = new InsuranceCard(cardNumber, cardHolder.getId(), policyOwner, expirationDate);
        existingCards.add(card);  // Add the new card to the existing cards list
        // Update customer's insurance card ID in memory
        cardHolder.setInsuranceCardId(cardNumber);
        // Save the updated customer information to the file
        Customer.saveCustomersToFile();  // Ensure this method correctly updates the existing records
        saveToFile(card);  // Save the new insurance card information
        System.out.println("Insurance card added successfully.");
    }

    private static boolean isUniqueCardNumber(String cardNumber, List<InsuranceCard> existingCards) {
        return existingCards.stream().noneMatch(card -> card.cardNumber.equals(cardNumber));
    }

    private static void saveToFile(InsuranceCard card) {
        try (FileWriter fw = new FileWriter(INSURANCE_CARD, true);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(INSURANCE_CARD))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(INSURANCE_CARD))) {
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
