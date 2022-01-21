package com.ccvendor.creditcardservice.data;

import com.ccvendor.creditcardservice.validator.CardLimit;

import javax.validation.constraints.NotEmpty;

public class CardRequest {

    private String cardNumber;

    @NotEmpty(message = "Credit Card request Applicant Name cannot be empty")
    private String applicantName;

    @CardLimit
    private Double cardLimit;

    public CardRequest() {
    }

    public CardRequest(final String cardNumber, final String applicantName, final Double cardLimit) {
        this.cardNumber = cardNumber;
        this.applicantName = applicantName;
        this.cardLimit = cardLimit;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getApplicantName() {
        return this.applicantName;
    }

    public void setApplicantName(final String applicantName) {
        this.applicantName = applicantName;
    }

    public Double getCardLimit() {
        return this.cardLimit;
    }

    public void setCardLimit(final Double cardLimit) {
        this.cardLimit = cardLimit;
    }
}
