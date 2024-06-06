package org.example.model;

public enum BoxOption {
    D_205("D 205"),
    D_255("D 255"),
    D_300("D 300"),
    D_350("D 350"),
    D_390("D 390"),
    D_490("D 490"),
    GP_01("GP 01");

    private final String displayValue;

    BoxOption(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}
