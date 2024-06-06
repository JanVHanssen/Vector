package org.example.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("RewindOrder")
public class RewindOrder extends Orders {

    @Column(name = "rack")
    private String rack;

    @Enumerated(EnumType.STRING)
    @Column(name = "box")
    private BoxOption box;

    @Enumerated(EnumType.STRING)
    @Column(name = "rewinder")
    private RewinderOption rewinder;

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public BoxOption getBox() {
        return box;
    }

    public void setBox(BoxOption box) {
        this.box = box;
    }

    public RewinderOption getRewinder() {
        return rewinder;
    }

    public void setRewinder(RewinderOption rewinder) {
        this.rewinder = rewinder;
    }
}
