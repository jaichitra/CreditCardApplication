package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import com.ccvendor.creditcardservice.model.CreditCard;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class CreditCardService {

    private final CreditCardDAO creditCardDAO;

    @Autowired
    public CreditCardService(final CreditCardDAO creditCardDAO) {
        this.creditCardDAO = creditCardDAO;
    }

    private static String generateRandomCVCNumber() {
        final int cvcNumber = ThreadLocalRandom.current().nextInt(050, 900 + 1);
        return cvcNumber < 100 ? "0" + String.valueOf(cvcNumber) : String.valueOf(cvcNumber);
    }

    private static List<CardResponse> transformCCToReponse(final List<CreditCard> cc) {
        return cc.stream().map(t -> CardResponse.builder().cardNumber(t.getCreditCardNumber()).applicantName(t.getApplicantName())
                .limit(t.getCardInitialLimit()).cvc(t.getCvc()).validFrom(t.getValidFrom()).validThru(t.getValidThru()).balance(t.getCurrentBalance()).build())
                .collect(Collectors.toList());
    }

    public CardResponse addCreditCard(final CardRequest cardRequest) {

        final Calendar cal = Calendar.getInstance();
        final Date validFrom = cal.getTime();
        cal.add(Calendar.YEAR, 4); // to get previous year add -1
        final Date validThru = cal.getTime();

        final String cvcNumber = generateRandomCVCNumber();
        // Supporting only single hard coded currency for simplicity.
        final String currency = "GBP";
        final double initialBalance = 0.00d;

        final CreditCard cc = this.creditCardDAO.addCreditCard(cardRequest.getCardNumber(), cardRequest.getApplicantName(),
                cardRequest.getCardLimit(), cvcNumber, validFrom, validThru, initialBalance, currency);

        if (cc == null) {
            return CardResponse.builder().cardRequest(cardRequest).errorMessage("Card Details already exists").build();
        }

        return transformCCToReponse(ImmutableList.of(cc)).iterator().next();
    }

    public List<CardResponse> getCreditCards() {
        final List<CreditCard> cards = this.creditCardDAO.fetchAllCreditCard();
        final List<CardResponse> cardResponse = transformCCToReponse(cards);

        cardResponse.forEach(CardResponse::omitSensitiveInformation);
        return cardResponse;
    }

}
