package acadflow.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHash {
    private final String password;
    private String storedHash;

    public PasswordHash(String password) {
        this.password = password;
    }

    public PasswordHash(String password, String storedHash) {
        this.password = password;
        this.storedHash = storedHash;
    }

    /**
     * *Create a  password hash
     * @return String - the generated hash or an empty string if there was an error
     */
    public String createHash() {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));

        if (matchesHash(hash)) {
            return hash;
        } else {
            System.out.println("\u001B[31mError creating password hash.\u001B[0m");
            return "";
        }
    }

    /**
     *Overloading - Parameterized Method - check if the provided password matches the hash
     * @return boolean - true if the password matches the hash, false otherwise
     */
    public boolean matchesHash(String hash) {
        return BCrypt.checkpw(password, hash);
    }

    /**
     * Overloading - Parameterless method - uses stored hash from constructor
     * Check if the entered password matches the stored hash
     * @return boolean - true if the password matches the stored hash, false otherwise
     */
    public boolean matchesHash() {
        if (password == null || storedHash == null) {
            throw new IllegalStateException("Password and stored hash must be set in constructor");
        }
        return BCrypt.checkpw(password, storedHash);
    }
}
