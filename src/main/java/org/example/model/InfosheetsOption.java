package org.example.model;

public enum InfosheetsOption {
    YES("Yes"),
    NO("No");

    private final String displayValue;

    InfosheetsOption(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}

