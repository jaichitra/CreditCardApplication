package com.ccvendor.creditcardservice.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "carddetails")
public class CardDetails {

    @Id
    private String creditCardNumber;
    @Column
    private String applicantName;
    @Column
    private String cvc;
    @Column
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date validFrom;
    @Column
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date validThru;
    @Column
    private Double cardInitialLimit;
    @Column
    private Double currentBalance;
    @Column
    private String currency;

    public CardDetails() {

    }

    public CardDetails(final String creditCardNumber, final String applicantName, final String cvc, final Date validFrom, final Date validThru, final Double cardInitialLimit, final Double currentBalance, final String currency) {
        this.creditCardNumber = creditCardNumber;
        this.applicantName = applicantName;
        this.cvc = cvc;
        this.validFrom = validFrom;
        this.validThru = validThru;
        this.cardInitialLimit = cardInitialLimit;
        this.currentBalance = currentBalance;
        this.currency = currency;
    }

    public String getCreditCardNumber() {
        return this.creditCardNumber;
    }

    public void setCreditCardNumber(final String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getApplicantName() {
        return this.applicantName;
    }

    public void setApplicantName(final String applicantName) {
        this.applicantName = applicantName;
    }

    public String getCvc() {
        return this.cvc;
    }

    public void setCvc(final String cvc) {
        this.cvc = cvc;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidThru() {
        return this.validThru;
    }

    public void setValidThru(final Date validThru) {
        this.validThru = validThru;
    }

    public Double getCardInitialLimit() {
        return this.cardInitialLimit;
    }

    public void setCardInitialLimit(final Double cardInitialLimit) {
        this.cardInitialLimit = cardInitialLimit;
    }

    public Double getCurrentBalance() {
        return this.currentBalance;
    }

    public void setCurrentBalance(final Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

}
