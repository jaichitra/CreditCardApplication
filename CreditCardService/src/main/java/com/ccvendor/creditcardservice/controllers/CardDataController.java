package com.ccvendor.creditcardservice.controllers;

import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import com.ccvendor.creditcardservice.data.SystemAdminUser;
import com.ccvendor.creditcardservice.model.CardDetails;
import com.ccvendor.creditcardservice.service.CardService;
import com.ccvendor.creditcardservice.util.MaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class CardDataController {
    private static final Logger log = LoggerFactory.getLogger(CardDataController.class.getName());

    private final CardService cardService;

    @Autowired
    public CardDataController(final CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping(value = "/creditcard", produces = "application/json")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<CardResponse> addCreditCard(@Valid @RequestBody final CardRequest cardRequest, final BindingResult bindingResult) {
        log.info("API invoked to add CreditCard {} and Applicant name {}", MaskUtil.maskCreditCardNumber(cardRequest.getCardNumber()), cardRequest.getApplicantName());
        if (bindingResult.hasErrors()) {
            log.error("Error validating the request");
            return new ResponseEntity<>(CardResponse.builder().cardNumber(cardRequest.getCardNumber()).applicantName(cardRequest.getApplicantName()).errorMessage(bindingResult.getAllErrors().iterator().next().getDefaultMessage()).build(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(this.cardService.addCreditCard(cardRequest), HttpStatus.OK);
    }

    @GetMapping(value = "/creditcards", produces = "application/json")
    @RolesAllowed("ROLE_ADMIN")
    public List<CardResponse> getCreditCard() {
        log.info("API invoked to Get Card data");
        return this.cardService.getValidCreditCards();
    }

    @PostMapping(value = "/dbSync")
    @RolesAllowed(SystemAdminUser.USER_ROLE)
    public ResponseEntity<Void> databaseSync(@RequestBody final List<CardDetails> cardRequest) {
        log.debug("Database Sync POST invoked for card data count : {} ", cardRequest.size());
        this.cardService.syncCreditCardData(cardRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/dbSync", produces = "application/json")
    @RolesAllowed(SystemAdminUser.USER_ROLE)
    public List<CardDetails> fetchCardCaches() {
        log.debug("Database Sync GET invoked, Fetching card data");
        return this.cardService.fetchAllCreditCardData();
    }
}
