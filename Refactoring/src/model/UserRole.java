package model;

public enum UserRole {
    GENERAL_MANAGER,
    WAREHOUSE_MANAGER,
    DELIVERYMAN,
    USER,
    NON_USER,
    CUSTOMER;


    // Role Checker
    public boolean isGeneralManager() {
        return this == GENERAL_MANAGER;
    }

    public boolean isWarehouseManager() {
        return this == WAREHOUSE_MANAGER;
    }

    public boolean isDeliverMan() {
        return this == DELIVERYMAN;
    }

    public boolean isUser() {
        return this == USER;
    }

    public boolean isNonUser() {
        return this == NON_USER;
    }

    public boolean isCUSTOMER() {
        return this == NON_USER;
    }


}
