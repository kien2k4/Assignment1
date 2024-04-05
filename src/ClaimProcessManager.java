/**
 * @author <Dang Trung Kien - s3979510>
 */
import java.util.List;
import java.util.Scanner;

public interface ClaimProcessManager {
    void addClaim(Scanner scanner, List<Customer> customers);
    void updateClaim(Scanner scanner);
    void deleteClaim(Scanner scanner); // Updated to accept Scanner
    Claim findClaimById(String id);
    void viewClaims();
}