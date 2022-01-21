package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class CreditCardService {


    public CardResponse addCreditCard(final CardRequest cardRequest) {
        return CardResponse.builder().cardRequest(cardRequest).cvc("232").validFrom(Date.from(Instant.now())).validThru(Date.from(Instant.now())).cardAdded(true).balance(0.00d).build();
    }


    public List<CardResponse> getCreditCards() {
        return null;
    }
}
