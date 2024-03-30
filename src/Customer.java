import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class Customer implements Serializable {
    public String id;
    public String fullName;
    public String insuranceCardId; // This can be null initially and set later
    private List<String> claimIds;
    private List<Customer> dependents;
    public String role; // Policy holder (PH) or Dependent (D)
    private static final String CUSTOMER_FILE = "customer_data.txt";
    static final List<Customer> customers = new ArrayList<>();

    public Customer(String id, String fullName, String role) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.insuranceCardId = null; // Initialize as null
        this.claimIds = new ArrayList<>();
        this.dependents = new ArrayList<>(); // Instantiate a new list for each Customer object
    }

    // Getters and setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getInsuranceCardId() {
        return insuranceCardId;
    }

    public void setInsuranceCardId(String insuranceCardId) {
        this.insuranceCardId = insuranceCardId;
    }

    public List<String> getClaimIds() {
        return claimIds;
    }

    public List<Customer> getDependents() {
        return dependents;
    }

    public void addDependent(Customer dependent) {
        if (this.dependents == null) {
            this.dependents = new ArrayList<>();
        }
        this.dependents.add(dependent);
    }
    public void addClaimId(String claimId) {
        if (this.claimIds == null) {
            this.claimIds = new ArrayList<>();
        }
        this.claimIds.add(claimId);
    }

    public static void handleCustomerOperations(Scanner scanner) {
        System.out.println("\n1. Add customer\n2. Delete customer\n3. Find a customer\n4. Main page\nPlease select an option: ");
        int customerOption = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over
        switch (customerOption) {
            case 1:
                addCustomer(scanner);
                break;
            case 2:
                // Placeholder for delete customer
                break;
            case 3:
                // Placeholder for find customer
                break;
            default:
                System.out.println("Returning to main menu.");
                break;
        }
    }

    public static void addCustomer(Scanner scanner) {
        String id;
        while (true) {
            System.out.print("Enter ID (format 'c-' followed by 7 numbers): ");
            id = scanner.nextLine();
            if (!id.matches("c-\\d{7}")) {
                System.out.println("Invalid ID format. The ID must start with 'c-' followed by 7 digits.");
            } else if (isValidCustomer(id, customers)) {
                System.out.println("A customer with this ID already exists. Please enter a unique ID.");
            } else {
                break; // ID is valid and unique, break the loop
            }
        }

        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Is this a 1. Policy holder or 2. Dependent: ");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over
        String role = roleChoice == 1 ? "PH" : "D";

        Customer customer = new Customer(id, name, role);
        customers.add(customer);
        if ("D".equals(role)) {
            while (true) {
                System.out.print("Enter policy holder ID ('c-' followed by 7 numbers format): ");
                String phId = scanner.nextLine();
                Customer policyHolder = customers.stream()
                        .filter(c -> c.getId().equals(phId) && "PH".equals(c.getRole()))
                        .findFirst()
                        .orElse(null);
                if (policyHolder == null) {
                    System.out.println("No policy holder found with this ID. Please try again.");
                } else {
                    customer.setInsuranceCardId(policyHolder.getInsuranceCardId()); // Assign the same card ID to the dependent
                    policyHolder.addDependent(customer);
                    break; // Valid policyholder found, break the loop
                }
            }
        }
        System.out.println("Customer added successfully.");
        // Save the updated customer data
        saveCustomersToFile();
    }

    public static boolean isValidCustomer(String cardHolderId, List<Customer> customers) {
        return customers.stream().anyMatch(c -> c.getId().equals(cardHolderId));
    }



    public static void loadCustomerFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            List<Customer> loadedCustomers = new ArrayList<>();
            Map<String, List<String>> dependentsMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    Customer customer = new Customer(parts[0], parts[1], parts[2]);
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        customer.setInsuranceCardId(parts[3]);
                    }
                    loadedCustomers.add(customer);
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        String[] dependentIds = parts[4].split(",");
                        dependentsMap.put(parts[0], new ArrayList<>(List.of(dependentIds)));
                    }
                    if (parts.length > 5 && !parts[5].isEmpty()) {
                        List<String> loadedClaimIds = Arrays.asList(parts[5].split(","));
                        for (String claimId : loadedClaimIds) {
                            customer.addClaimId(claimId);
                        }
                    }
                    customers.add(customer);
                }
            }
            // Link dependents to their policyholders
            for (Customer customer : loadedCustomers) {
                List<String> dependentIds = dependentsMap.get(customer.getId());
                if (dependentIds != null) {
                    for (String dependentId : dependentIds) {
                        loadedCustomers.stream()
                                       .filter(c -> c.getId().equals(dependentId))
                                       .findFirst()
                                       .ifPresent(customer::addDependent);
                    }
                }
            }
            customers.clear();
            customers.addAll(loadedCustomers);
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }

    public static void saveCustomersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMER_FILE, false))) {
            for (Customer customer : customers) {
                String dependentIds = customer.getDependents().stream()
                        .map(Customer::getId)
                        .collect(Collectors.joining(","));

                String claimIds = customer.getClaimIds().stream().collect(Collectors.joining(","));

                writer.write(customer.getId() + "\t" + customer.getFullName() + "\t" +
                        customer.getRole() + "\t" + customer.getInsuranceCardId() + "\t" +
                        dependentIds + "\t" + claimIds + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


    public static void viewCustomers() {
        System.out.printf("%-14s %-12s %-16s %-20s %-30s %s\n", "ID", "Role", "Name", "InsuranceCardID", "Dependents", "Claims");
        for (Customer customer : customers) {
            String claimIdsDisplay = customer.getClaimIds().isEmpty() ? "null" : String.join("; ", customer.getClaimIds());
            System.out.printf("%-14s %-12s %-16s %-20s %-30s %s%n",
                    customer.getId(), customer.getRole(), customer.getFullName(), customer.getInsuranceCardId(),
                    customer.getDependents().stream().map(Customer::getId).collect(Collectors.joining("; ")),
                    claimIdsDisplay);
        }
    }

    @Override
    public String toString() {
        String insuranceIdDisplay = insuranceCardId != null ? insuranceCardId : "null";
        String dependentIds = dependents.isEmpty() ? "null" : dependents.stream()
                                                                        .map(Customer::getId)
                                                                        .collect(Collectors.joining("; "));
        return String.format("%-14s %-12s %-16s %-20s %-10s", id, role, fullName, insuranceIdDisplay, dependentIds);
    }
}
