package com.ccvendor.creditcardservice.controllers;

import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import com.ccvendor.creditcardservice.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class CreditCardController {

    private final CreditCardService cardService;

    @Autowired
    public CreditCardController(final CreditCardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping(value = "/creditcard", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardResponse> addCreditCard(@Valid @RequestBody final CardRequest cardRequest, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(CardResponse.builder().cardNumber(cardRequest.getCardNumber()).applicantName(cardRequest.getApplicantName()).errorMessage("Error").cardAdded(false).build(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(this.cardService.addCreditCard(cardRequest), HttpStatus.OK);
    }


    @GetMapping(value = "/creditcard", produces = MediaType.APPLICATION_JSON_VALUE)
    public CardResponse[] getCreditCard() {
        return this.cardService.getCreditCards();
    }


}
