package acadflow.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    private final String password;

    public PasswordHash(String password) {
        this.password = password;
    }

    public String createHash() {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));

        if (matchesHash(hash)) {
            return hash;
        } else {
            System.out.println("\u001B[31mError creating password hash.\u001B[0m");
            return "";
        }
    }

    public boolean matchesHash(String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
