import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Claim {
    private String claimId; // format: f-0000000000
    private Date claimDate;
    private Customer insuredPerson;
    private String cardNumber;
    private Date examDate;
    private List<String> documents; // format: ClaimId_CardNumber_DocumentName.pdf
    private double claimAmount;
    private String status; // New, Processing, Done
    private String receiverBankingInfo; // Bank – Name – Number
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
                    addClaim(scanner, customers);
                    break;
                case 2:
                    // updateClaim(scanner, customers); // Implement this method for updating claims
                    break;
                case 3:
                    // viewClaims(customers); // Implement this method for viewing claims
                    break;
                case 4:
                    // deleteClaim(scanner, customers); // Implement this method for deleting claims
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

    public static void addClaim(Scanner scanner, List<Customer> customers) {
        System.out.print("Enter claim ID (format 'f-' followed by 10 numbers): ");
        String claimId = scanner.nextLine();
        if (!claimId.matches("f-\\d{10}")) {
            System.out.println("Invalid claim ID format. It should be 'f-' followed by 10 numbers.");
            return;
        }
        System.out.print("Enter claim date (DD/MM/YYYY): ");
        String claimDateString = scanner.nextLine();
        Date claimDate;
        try {
            claimDate = new SimpleDateFormat("dd/MM/yyyy").parse(claimDateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            return;
        }
        Customer insuredPerson = null;
        while (insuredPerson == null) {
            System.out.print("Enter insured person's ID (format 'c-' followed by 7 numbers): ");
            String insuredId = scanner.nextLine();
            for (Customer customer : customers) {
                if (customer.getId().equals(insuredId)) {
                    insuredPerson = customer;
                    break;
                }
            }
            if (insuredPerson == null) {
                System.out.println("No customer found with this ID.");
            }
        }
        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine();

        System.out.print("Enter exam date (DD/MM/YYYY): ");
        String examDateString = scanner.nextLine();
        Date examDate;
        try {
            examDate = new SimpleDateFormat("dd/MM/yyyy").parse(examDateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            return;
        }
        // Assuming documents are entered as a list of filenames, separated by commas
        System.out.print("Enter documents (separated by comma): ");
        String documentsString = scanner.nextLine();
        List<String> documents = new ArrayList<>(Arrays.asList(documentsString.split("\\s*,\\s*")));
        System.out.print("Enter claim amount: ");
        double claimAmount;
        try {
            claimAmount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return;
        }
        System.out.print("Enter status (New, Processing, Done): ");
        String status = scanner.nextLine();
        System.out.print("Enter receiver banking info (Bank – Name – Number): ");
        String receiverBankingInfo = scanner.nextLine();

        Claim newClaim = new Claim(claimId, claimDate, insuredPerson, cardNumber, examDate, documents, claimAmount, status, receiverBankingInfo);

        // Now add the claim ID to the customer's list of claim IDs
        insuredPerson.addClaimId(newClaim.getId());  // Assuming 'insuredPerson' is the Customer object

        Claim.getClaims().add(newClaim);  // Add the new claim to the list
        saveClaimsToFile();
        Customer.saveCustomersToFile();  // Make sure this line is after you've updated the claim list
        System.out.println("Claim added successfully.");
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
            System.out.println("Claims loaded successfully.");
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
        System.out.printf("%-15s %-12s %-15s %-12s %-15s %-20s %-12s %-20s %s\n",
                "Claim ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "Documents", "Claim Amount", "Status", "Receiver Banking Info");

        for (Claim claim : claims) {
            String documents = String.join("; ", claim.getDocuments());
            System.out.printf("%-15s %-12s %-15s %-12s %-15s %-20s %-12s %-20s %s\n",
                    claim.getId(),
                    dateFormat.format(claim.getClaimDate()),
                    claim.getInsuredPerson().getId(),
                    claim.getCardNumber(),
                    dateFormat.format(claim.getExamDate()),
                    documents,
                    claim.getClaimAmount(),
                    claim.getStatus(),
                    claim.getReceiverBankingInfo());
        }
    }
}
