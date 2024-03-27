import java.util.List;
import java.util.Scanner;

public interface ClaimProcessManager {
    void addClaim(Scanner scanner, List<Customer> customers); // Corresponds to adding a claim
    void updateClaim(Claim claim); // Corresponds to updating a claim
    void deleteClaim(Claim claim); // Corresponds to deleting a claim
    Claim findClaimById(String id); // Gets a single claim by its ID
    List<Claim> listAllClaims(); // Lists all claims
}
