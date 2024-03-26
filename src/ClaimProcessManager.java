import java.util.List;

public interface ClaimProcessManager {
    void addClaim(Claim claim);
    void update(Claim claim);
    void delete(Claim claim);
    Claim getOne(String id);
    List<Claim> getAll();
}

