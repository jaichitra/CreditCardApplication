package com.ccvendor.creditcardservice.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponse {

    private final String cardNumber;
    private final String applicantName;
    private final String cvc;
    private final Date validFrom;
    private final Date validThru;
    private final Double limit;
    private final Double balance;
    private final String errorMessage;
    private final Boolean cardAdded;

    private CardResponse(final CardResponseBuilder cardResponseBuilder) {
        this.cardNumber = cardResponseBuilder.cardNumber;
        this.applicantName = cardResponseBuilder.applicantName;
        this.cvc = cardResponseBuilder.cvc;
        this.validFrom = cardResponseBuilder.validFrom;
        this.validThru = cardResponseBuilder.validThru;
        this.limit = cardResponseBuilder.limit;
        this.balance = cardResponseBuilder.balance;
        this.errorMessage = cardResponseBuilder.errorMessage;
        this.cardAdded = cardResponseBuilder.cardAdded;
    }

    public static CardResponseBuilder builder() {
        return new CardResponseBuilder();
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getApplicantName() {
        return this.applicantName;
    }

    public String getCvc() {
        return this.cvc;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public Date getValidThru() {
        return this.validThru;
    }

    public Double getLimit() {
        return this.limit;
    }

    public Double getBalance() {
        return this.balance;
    }

    public static class CardResponseBuilder {

        private String cardNumber;
        private String applicantName;
        private String cvc;
        private Date validFrom;
        private Date validThru;
        private Double limit;
        private Double balance;
        private String errorMessage;
        private Boolean cardAdded;

        public CardResponseBuilder cardRequest(final CardRequest cardRequest) {
            this.cardNumber = cardRequest.getCardNumber();
            this.applicantName = cardRequest.getApplicantName();
            this.limit = cardRequest.getCardLimit();
            return this;
        }

        public CardResponseBuilder cardNumber(final String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public CardResponseBuilder applicantName(final String applicantName) {
            this.applicantName = applicantName;
            return this;
        }

        public CardResponseBuilder cvc(final String cvc) {
            this.cvc = cvc;
            return this;
        }

        public CardResponseBuilder validFrom(final Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public CardResponseBuilder validThru(final Date validThru) {
            this.validThru = validThru;
            return this;
        }

        public CardResponseBuilder limit(final Double limit) {
            this.limit = limit;
            return this;
        }

        public CardResponseBuilder balance(final Double balance) {
            this.balance = balance;
            return this;
        }

        public CardResponseBuilder errorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }


        public CardResponseBuilder cardAdded(final Boolean cardAdded) {
            this.cardAdded = cardAdded;
            return this;
        }

        public CardResponse build() {
            return new CardResponse(this);
        }
    }
}
