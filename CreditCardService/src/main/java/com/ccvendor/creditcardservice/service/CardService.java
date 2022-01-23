package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.controllers.CardDataController;
import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import com.ccvendor.creditcardservice.model.CardDetails;
import com.ccvendor.creditcardservice.util.MaskUtil;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(CardDataController.class.getName());

    private final CardDAO cardDAO;

    private final AppSyncService dbSyncService;
    private final Integer dbSyncThreadCount;
    private ExecutorService execService;

    @Autowired
    public CardService(final CardDAO cardDAO, final AppSyncService dbSyncService, @Value("${dbsync.threadcount}") final Integer dbSyncThreadCount) {
        this.cardDAO = cardDAO;
        this.dbSyncService = dbSyncService;
        this.dbSyncThreadCount = dbSyncThreadCount;
    }

    private static String generateRandomCVCNumber() {
        final int cvcNumber = ThreadLocalRandom.current().nextInt(050, 900 + 1);
        return cvcNumber < 100 ? "0" + String.valueOf(cvcNumber) : String.valueOf(cvcNumber);
    }

    private static List<CardResponse> transformCCToReponse(final List<CardDetails> cc) {
        return cc.stream().map(t -> CardResponse.builder().cardNumber(t.getCreditCardNumber()).applicantName(t.getApplicantName())
                .limit(t.getCardInitialLimit()).cvc(t.getCvc()).validFrom(t.getValidFrom()).validThru(t.getValidThru()).balance(t.getCurrentBalance()).build())
                .collect(Collectors.toList());
    }

    /**
     * Method composes all the credit card data and invoked the database save.
     *
     * @param cardRequest
     * @return
     */
    public CardResponse addCreditCard(final CardRequest cardRequest) {

        final Calendar cal = Calendar.getInstance();
        final Date validFrom = cal.getTime();
        cal.add(Calendar.YEAR, 4); // to get previous year add -1
        final Date validThru = cal.getTime();

        final String cvcNumber = generateRandomCVCNumber();
        // Supporting only single hard coded currency for simplicity.
        final String currency = "GBP";
        final double initialBalance = 0.00d;

        final CardDetails cc = this.cardDAO.addCreditCard(cardRequest.getCardNumber(), cardRequest.getApplicantName(),
                cardRequest.getCardLimit(), cvcNumber, validFrom, validThru, initialBalance, currency);

        if (cc == null) {
            log.info("Card details for card number : {} already present in DB", MaskUtil.maskCreditCardNumber(cardRequest.getCardNumber()));
            return CardResponse.builder().cardRequest(cardRequest).errorMessage("Card Details already exists").build();
        }

        log.info("Card details for card number : {} added to the database", MaskUtil.maskCreditCardNumber(cc.getCreditCardNumber()));

        this.broadcastCache(cc);

        return transformCCToReponse(ImmutableList.of(cc)).iterator().next();
    }

    /**
     * Method invokes dySyncService on a new card addition, to update all the available instances.
     * Is a async process and runs on a new thread.
     *
     * @param cc
     */
    private void broadcastCache(final CardDetails cc) {
        if (this.execService == null)
            this.execService = Executors.newFixedThreadPool(Integer.valueOf(this.dbSyncThreadCount));
        log.info("Invoking async instances DB sync operation");
        this.execService.submit(() -> this.dbSyncService.syncDatabaseData(ImmutableList.of(cc)));
    }

    /**
     * Fetch method to list all the valid card data.
     * Omits the sensitive information of a card
     *
     * @return
     */
    public List<CardResponse> getValidCreditCards() {
        final List<CardDetails> cards = this.cardDAO.fetchAllValidCreditCard();
        final List<CardResponse> cardResponse = transformCCToReponse(cards);

        cardResponse.forEach(CardResponse::omitSensitiveInformation);
        log.info("{} number of card details fetched from DB", cardResponse.size());
        return cardResponse;
    }

    /**
     * Method invoked from internal API to update the db on other available instances.
     *
     * @param cardRequest
     */
    public void syncCreditCardData(final List<CardDetails> cardRequest) {
        log.debug("Db sync storing {} card details", cardRequest.size());
        this.cardDAO.saveCardData(cardRequest);
    }

    /**
     * Method invoked from internal API to fetch the card data on application start.
     *
     * @return
     */
    public List<CardDetails> fetchAllCreditCardData() {
        log.debug("Db sync fetching card details");
        return this.cardDAO.fetchAllCreditCardData();
    }


}
