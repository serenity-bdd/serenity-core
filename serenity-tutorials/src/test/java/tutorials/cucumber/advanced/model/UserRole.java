package tutorials.cucumber.advanced.model;

/**
 * Enum representing user roles in the system.
 * Used by custom parameter type to transform role strings in Gherkin steps.
 */
public enum UserRole {
    ADMIN("Administrator", true, true, true),
    MANAGER("Manager", true, true, false),
    USER("Standard User", true, false, false),
    GUEST("Guest", false, false, false);

    private final String displayName;
    private final boolean canRead;
    private final boolean canWrite;
    private final boolean canDelete;

    UserRole(String displayName, boolean canRead, boolean canWrite, boolean canDelete) {
        this.displayName = displayName;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public boolean canDelete() {
        return canDelete;
    }
}
