package org.example.model;

public enum MLOption {
    ML_1("ML 1"),
    ML_2("ML 2"),
    ML_3("ML 3"),
    ML_4("ML 4"),
    ML_5("ML 5"),
    ML_6("ML 6"),
    ML_7("ML 7"),
    ML_8("ML 8"),
    ML_12("ML 12"),
    ML_13("ML 13");

    private final String displayValue;

    MLOption(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}
