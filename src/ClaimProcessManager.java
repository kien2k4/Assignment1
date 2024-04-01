import java.util.List;
import java.util.Scanner;

public interface ClaimProcessManager {
    void addClaim(Scanner scanner, List<Customer> customers);
    void updateClaim(Claim claim);
    void deleteClaim(Scanner scanner); // Updated to accept Scanner
    Claim findClaimById(String id);
    List<Claim> listAllClaims();
}