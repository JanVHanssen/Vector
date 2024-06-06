package org.example.model;

public enum RewinderOption {
    RW_1("RW 1"),
    RW_2("RW 2"),
    RW_4("RW 4"),
    RW_5("RW 5"),
    RW_9("RW 9"),
    RW_10("RW 10"),
    RW_11("RW 11"),
    RW_15("RW 15");

    private final String displayValue;

    RewinderOption(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}
