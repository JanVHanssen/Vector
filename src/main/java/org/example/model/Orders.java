package org.example.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seven_number")
    private String sevenNumber;

    @Column(name = "four_number")
    private String fourNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "description_id")
    private Description description;

    @ManyToOne
    @JoinColumn(name = "oldDescription_id")
    private OldDescription oldDescription;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSevenNumber() {
        return sevenNumber;
    }

    public void setSevenNumber(String sevenNumber) {
        this.sevenNumber = sevenNumber;
    }

    public String getFourNumber() {
        return fourNumber;
    }

    public void setFourNumber(String fourNumber) {
        this.fourNumber = fourNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public OldDescription getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(OldDescription oldDescription) {
        this.oldDescription = oldDescription;
    }
}