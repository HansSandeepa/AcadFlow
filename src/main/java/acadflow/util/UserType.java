package acadflow.util;

import java.util.regex.Pattern;

/**
 * Enum representing different user types with their corresponding ID patterns.
 * Provides validation and identification of users based on their registration numbers.
 */
public enum UserType {
    STUDENT("undergraduate", "^tg[0-9]{4}$"),
    LECTURER("lecturer", "^lec[0-9]{4}$"),
    ADMIN("admin", "^admin[0-9]{4}$"),
    TECHNICAL_OFFICER("tec_officer", "^to[0-9]{4}$");

    private final String tableName;
    private final Pattern pattern;

    /**
     * Constructor for UserType enum
     */
    UserType(String tableName, String patternString) {
        this.tableName = tableName;
        this.pattern = Pattern.compile(patternString);
    }

    /**
     * Get the table name for this user type
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Get the compiled regex pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Check if the given registration number matches this user type's pattern
     */
    public boolean matches(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            return false;
        }
        return pattern.matcher(regNo).matches();
    }

    /**
     * Find the user type by registration number
     */
    public static UserType findUserTypeFromRegNo(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            return null;
        }

        for (UserType type : UserType.values()) {
            if (type.matches(regNo)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Check if the given registration number is valid for any user type
     */
    public static boolean isValidRegistrationNumber(String regNo) {
        return findUserTypeFromRegNo(regNo) != null;
    }
}
