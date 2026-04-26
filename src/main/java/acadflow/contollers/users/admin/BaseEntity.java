package acadflow.contollers.users.admin;


public interface BaseEntity {

    // Every entity must be able to return a unique String identifier.
    // DisplayUser returns its userId as a String.
    String getEntityId();
}

