package org.example.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;



@Entity
@DiscriminatorValue("ProductionOrder")
public class ProductionOrder extends Orders {

    @Column(name = "sequence")
    private String sequence;

    @Column(name = "sales_order")
    private String salesOrder;
    @Column(name = "so_line")
    private String soLine;
    @Column(name = "requested_date")
    private LocalDate requestedDate;
    @Column(name = "promised_date")
    private LocalDate promisedDate;
    @Column(name = "ml")
    @Enumerated(EnumType.STRING)
    private MLOption ml;
    @Column(name = "reel_length")
    private String reelLength;
    @Column(name = "conversion_date")
    private LocalDate conversionDate;
    @Column(name = "customer_date")
    private LocalDate customerDate;
    @Column(name = "infosheets")
    @Enumerated(EnumType.STRING)
    private InfosheetsOption infosheets;
    @Column(name = "catalog")
    private String catalog;


    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(String salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getSoLine() {
        return soLine;
    }

    public void setSoLine(String soLine) {
        this.soLine = soLine;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDate getPromisedDate() {
        return promisedDate;
    }

    public void setPromisedDate(LocalDate promisedDate) {
        this.promisedDate = promisedDate;
    }

    public MLOption getMl() {
        return ml;
    }

    public void setMl(MLOption ml) {
        this.ml = ml;
    }

    public String getReelLength() {
        return reelLength;
    }

    public void setReelLength(String reelLength) {
        this.reelLength = reelLength;
    }

    public LocalDate getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(LocalDate conversionDate) {
        this.conversionDate = conversionDate;
    }

    public LocalDate getCustomerDate() {
        return customerDate;
    }

    public void setCustomerDate(LocalDate customerDate) {
        this.customerDate = customerDate;
    }

    public InfosheetsOption getInfosheets() {
        return infosheets;
    }

    public void setInfosheets(InfosheetsOption infosheets) {
        this.infosheets = infosheets;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}