package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.model.CardDetails;
import com.ccvendor.creditcardservice.repository.CardDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardDAO {

    private final CardDataRepository repository;

    @Autowired
    public CardDAO(final CardDataRepository repo) {
        this.repository = repo;
    }


    public CardDetails addCreditCard(final String cardNumber, final String applicantName, final Double cardLimit, final String cvcNumber,
                                     final Date validFrom, final Date validThru, final double initialBalance, final String currency) {
        if (this.repository.existsById(cardNumber)) {
            return null;
        }

        final CardDetails cc = this.repository.save(new CardDetails(cardNumber, applicantName, cvcNumber, validFrom, validThru, cardLimit, initialBalance, currency));
        return cc;
    }

    public void saveCardData(final List<CardDetails> cardData) {
        this.repository.saveAll(cardData);
    }


    public List<CardDetails> fetchAllValidCreditCard() {
        final List<CardDetails> cardData = this.repository.findAll();
        return cardData.stream().filter(t -> t.getValidThru().after(Date.from(Instant.now()))).collect(Collectors.toList());
    }


    public List<CardDetails> fetchAllCreditCardData() {
        return this.repository.findAll();
    }
}
