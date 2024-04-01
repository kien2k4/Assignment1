import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Claim implements ClaimProcessManager {
    private String claimId;
    private Date claimDate;
    private Customer insuredPerson;
    private String cardNumber;
    private Date examDate;
    private List<String> documents;
    private double claimAmount;
    private String status;
    private String receiverBankingInfo;
    private static final List<Claim> claims = new ArrayList<>();
    private static final String CLAIM_FILE = "claims_data.txt";

    public Claim(String id, Date claimDate, Customer insuredPerson, String cardNumber, Date examDate, List<String> documents, double claimAmount, String status, String receiverBankingInfo) {
        this.claimId = id;
        this.claimDate = claimDate;
        this.insuredPerson = insuredPerson;
        this.cardNumber = cardNumber;
        this.examDate = examDate;
        this.documents = documents;
        this.claimAmount = claimAmount;
        this.status = status;
        this.receiverBankingInfo = receiverBankingInfo;
    }
    public Claim() {
        // Initialize with default or empty values
        this.claimId = "";
        this.claimDate = new Date();
        this.insuredPerson = null;
        this.cardNumber = "";
        this.examDate = new Date();
        this.documents = new ArrayList<>();
        this.claimAmount = 0.0;
        this.status = "";
        this.receiverBankingInfo = "";
    }

    public static List<Claim> getClaims() {
        return claims;
    }

    // Getter and setter methods
    public String getId() {
        return claimId;
    }

    public void setId(String id) {
        this.claimId = id;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public Customer getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(Customer insuredPerson) {
        this.insuredPerson = insuredPerson;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverBankingInfo() {
        return receiverBankingInfo;
    }

    public void setReceiverBankingInfo(String receiverBankingInfo) {
        this.receiverBankingInfo = receiverBankingInfo;
    }

    // Display information method
    public static void manageClaims(Scanner scanner, List<Customer> customers) {
        int option;
        Claim claimManager = new Claim(); // Create an instance of Claim
        do {
            System.out.println("\n--- Claim Management ---");
            System.out.println("1. Add Claim");
            System.out.println("2. Update Claim");
            System.out.println("3. View Claim");
            System.out.println("4. Delete Claim");
            System.out.println("5. Return to Main Menu");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character left by nextInt()

            switch (option) {
                case 1:
                    claimManager.addClaim(scanner, customers); // Use the claimManager instance
                    break;
                case 2:
                    // Implement or call updateClaim method on claimManager
                    break;
                case 3:
                    // Implement or call viewClaims method
                    break;
                case 4:
                    // Implement or call deleteClaim method on claimManager
                    claimManager.deleteClaim(scanner);
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please select a valid option.");
                    break;
            }
        } while (option != 5);
    }

    public void addClaim(Scanner scanner, List<Customer> customers) {
        String claimId;
        while (true) {
            System.out.print("Enter claim ID (format 'f-' followed by 10 numbers): ");
            claimId = scanner.nextLine();
            if (!claimId.matches("f-\\d{10}")) {
                System.out.println("Invalid claim ID format. It should be 'f-' followed by 10 numbers.");
            } else if (findClaimById(claimId) != null) {
                System.out.println("Claim ID already exists. Please enter a unique claim ID.");
            } else {
                break;
            }
        }

        Date claimDate = null;
        while (claimDate == null) {
            System.out.print("Enter claim date (DD/MM/YYYY): ");
            String claimDateString = scanner.nextLine();
            try {
                claimDate = new SimpleDateFormat("dd/MM/yyyy").parse(claimDateString);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            }
        }

        Customer insuredPerson = null;
        while (insuredPerson == null) {
            System.out.print("Enter insured person's ID (format 'c-' followed by 7 numbers): ");
            String insuredId = scanner.nextLine();
            insuredPerson = customers.stream()
                    .filter(c -> c.getId().equals(insuredId))
                    .findFirst()
                    .orElse(null);
            if (insuredPerson == null) {
                System.out.println("No customer found with this ID.");
            } else if (insuredPerson.getInsuranceCardId() == null || insuredPerson.getInsuranceCardId().isEmpty()) {
                System.out.println("This customer does not have an insurance card. Cannot add a claim.");
                insuredPerson = null; // Reset to ensure we go through the loop again if needed
            }
        }

        // Use the card number from the insured person
        String cardNumber = insuredPerson.getInsuranceCardId();

        Date examDate = null;
        while (examDate == null) {
            System.out.print("Enter exam date (DD/MM/YYYY): ");
            String examDateString = scanner.nextLine();
            try {
                examDate = new SimpleDateFormat("dd/MM/yyyy").parse(examDateString);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            }
        }

        List<String> documents = new ArrayList<>();
        while (documents.isEmpty()) {
            System.out.print("Enter document names (separated by comma): ");
            String documentsString = scanner.nextLine();
            if (!documentsString.trim().isEmpty()) {
                String finalClaimId = claimId;
                documents = Arrays.stream(documentsString.split("\\s*,\\s*"))
                        .map(docName -> finalClaimId + "_" + cardNumber + "_" + docName.trim().replaceAll("\\s+", "") + ".pdf")
                        .collect(Collectors.toList());
            }
        }

        double claimAmount = 0.0;
        while (claimAmount <= 0) {
            System.out.print("Enter claim amount: ");
            try {
                claimAmount = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a number.");
            }
        }

        String status = "";
        while (status.isEmpty()) {
            System.out.print("Enter status (New, Processing, Done): ");
            status = scanner.nextLine().trim();
            if (status.isEmpty() || !(status.equals("New") || status.equals("Processing") || status.equals("Done"))) {
                System.out.println("Invalid status. Please enter 'New', 'Processing', or 'Done'.");
                status = "";
            }
        }

        String receiverBankingInfo = "";
        while (receiverBankingInfo.isEmpty()) {
            System.out.print("Enter receiver banking info (Bank – Name – Number): ");
            receiverBankingInfo = scanner.nextLine().trim();
            if (receiverBankingInfo.isEmpty()) {
                System.out.println("Receiver banking info cannot be empty.");
            }
        }

        Claim newClaim = new Claim(claimId, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo);
        insuredPerson.addClaimId(newClaim.getId());
        Claim.getClaims().add(newClaim);
        saveClaimsToFile();
        Customer.saveCustomersToFile();
        System.out.println("Claim added successfully.");
    }


    @Override
    public void updateClaim(Claim claim) {
        // Implementation of updating a claim
    }

    @Override
    public void deleteClaim(Scanner scanner) {
        System.out.print("Enter the claim ID of the claim to delete: ");
        String claimId = scanner.nextLine();
        Claim claimToDelete = findClaimById(claimId);
        if (claimToDelete != null) {
            // Remove the claim from the list of claims
            claims.remove(claimToDelete);
            System.out.println("Claim with ID " + claimId + " has been successfully deleted.");
            // Remove the claim ID from the associated customer
            Customer insuredPerson = claimToDelete.getInsuredPerson();
            if (insuredPerson != null) {
                insuredPerson.getClaimIds().remove(claimId);
                System.out.println("Claim ID removed from customer record.");
            }
            // Save the updated claims to the file
            saveClaimsToFile();
            // Save the updated customers to the file
            Customer.saveCustomersToFile();
        } else {
            System.out.println("No claim found with ID " + claimId + ".");
        }
    }

    @Override
    public Claim findClaimById(String id) {
        // Implementation of finding a claim by ID
        return claims.stream().filter(c -> c.claimId.equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Claim> listAllClaims() {
        return new ArrayList<>(claims); // Return a copy of the claims list
    }

    public static void saveClaimsToFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CLAIM_FILE))) {
            for (Claim claim : getClaims()) {
                String line = String.join(",",
                        claim.getId(),
                        dateFormat.format(claim.getClaimDate()),
                        claim.getInsuredPerson().getId(),
                        claim.getCardNumber(),
                        dateFormat.format(claim.getExamDate()),
                        String.join(";", claim.getDocuments()),
                        String.valueOf(claim.getClaimAmount()),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo()
                );
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Claims saved successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving claims to file: " + e.getMessage());
        }
    }

    public static void loadClaimsFromFile() {
        File file = new File(CLAIM_FILE);
        if (!file.exists()) {
            System.out.println("No existing claims file found. A new one will be created when needed.");
            return;  // Exit the method as there is no file to read from
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try (BufferedReader reader = new BufferedReader(new FileReader(CLAIM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) { // Ensure there are enough parts to construct a Claim
                    String id = parts[0];
                    Date claimDate = dateFormat.parse(parts[1]);
                    Customer insuredPerson = findCustomerById(parts[2]); // Implement this method to find a Customer by ID
                    String cardNumber = parts[3];
                    Date examDate = dateFormat.parse(parts[4]);
                    List<String> documents = Arrays.asList(parts[5].split(";"));
                    double claimAmount = Double.parseDouble(parts[6]);
                    String status = parts[7];
                    String receiverBankingInfo = parts[8];

                    Claim claim = new Claim(id, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo);
                    getClaims().add(claim);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("An error occurred while loading claims from file: " + e.getMessage());
        }
    }

    private static Customer findCustomerById(String id) {
        // Assume Customer.customers is the list where all customers are stored
        for (Customer customer : Customer.customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null; // or throw an exception if the customer must exist
    }

    public static void viewClaims() {
        if (claims.isEmpty()) {
            System.out.println("No claims to display.");
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.printf("%-15s %-12s %-15s %-12s %-14s %-15s %-10s %-23s %s\n",
                "Claim ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "Claim Amount", "Status", "Receiver Banking Info", "Documents");
        for (Claim claim : claims) {
            String documents = String.join("; ", claim.getDocuments());
            System.out.printf("%-15s %-12s %-15s %-12s %-14s %-15s %-10s %-23s %s\n",
                    claim.getId(),
                    dateFormat.format(claim.getClaimDate()),
                    claim.getInsuredPerson().getId(),
                    claim.getCardNumber(),
                    dateFormat.format(claim.getExamDate()),
                    claim.getClaimAmount(),
                    claim.getStatus(),
                    claim.getReceiverBankingInfo(),
                    documents);
        }
    }
}
