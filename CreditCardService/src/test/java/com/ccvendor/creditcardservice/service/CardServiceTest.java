package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.data.CardRequest;
import com.ccvendor.creditcardservice.data.CardResponse;
import com.ccvendor.creditcardservice.model.CardDetails;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CardServiceTest {

    @Mock
    private CardDAO cardDAO;

    @Mock
    private AppSyncService dbSyncService;

    private CardService cardService;

    @Before
    public void init() {
        this.cardService = new CardService(this.cardDAO, this.dbSyncService, 5);
    }


    @Test
    public void verifyFieldsToDao() {
        final CardRequest cardRequest = new CardRequest("4617019281263712", "Smith", 10000.00d);

        final Answer<CardDetails> ans = new Answer<CardDetails>() {
            @Override
            public CardDetails answer(final InvocationOnMock invocation) throws Throwable {
                final String cardNumber = invocation.getArgumentAt(0, String.class);
                final String applicantName = invocation.getArgumentAt(1, String.class);
                final Double cardLimit = invocation.getArgumentAt(2, Double.class);
                final String cvcNumber = invocation.getArgumentAt(3, String.class);
                final Date validFrom = invocation.getArgumentAt(4, Date.class);
                final Date validThru = invocation.getArgumentAt(5, Date.class);
                final Double initialBalance = invocation.getArgumentAt(6, Double.class);
                final String currency = invocation.getArgumentAt(7, String.class);

                Assert.assertTrue(validFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(
                        Date.from(Instant.now()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                Assert.assertTrue(validThru.toInstant().isAfter(validFrom.toInstant()));
                Assert.assertTrue(cvcNumber.length() == 3);
                Assert.assertEquals(cardRequest.getCardNumber(), cardNumber);
                Assert.assertEquals(cardRequest.getApplicantName(), applicantName);
                Assert.assertEquals(cardRequest.getCardLimit(), cardLimit);
                Assert.assertEquals(0.00d, initialBalance, 0.00);
                return new CardDetails(cardNumber, applicantName, cvcNumber, validFrom, validThru, cardLimit, initialBalance, currency);
            }
        };
        doNothing().when(this.dbSyncService).syncDatabaseData(anyList());
        doAnswer(ans).when(this.cardDAO).addCreditCard(anyString(), anyString(), anyDouble(), anyString(), any(Date.class), any(Date.class), anyDouble(), anyString());
        final CardResponse response = this.cardService.addCreditCard(cardRequest);
        verify(this.cardDAO, times(1)).addCreditCard(anyString(), anyString(), anyDouble(), anyString(), any(Date.class), any(Date.class), anyDouble(), anyString());

    }


    @Test
    public void verifyCallToAppSyncOnDaoSuccessCall() {
        final CardRequest cardRequest = new CardRequest("4617019281263712", "Smith", 10000.00d);
        final Environment env = Mockito.mock(Environment.class);
        when(env.getProperty("spring.application.name")).thenReturn("credit-card-service");

        this.cardService = new CardService(this.cardDAO, new AppSyncService(null, null, env) {
            @Override
            public void syncDatabaseData(final List<CardDetails> cardData) {
                assertEquals(cardRequest.getCardNumber(), cardData.iterator().next().getCreditCardNumber());
            }
        }, 5);


        final Answer<CardDetails> ans = new Answer<CardDetails>() {
            @Override
            public CardDetails answer(final InvocationOnMock invocation) throws Throwable {
                final String cardNumber = invocation.getArgumentAt(0, String.class);
                final String applicantName = invocation.getArgumentAt(1, String.class);
                final Double cardLimit = invocation.getArgumentAt(2, Double.class);
                final String cvcNumber = invocation.getArgumentAt(3, String.class);
                final Date validFrom = invocation.getArgumentAt(4, Date.class);
                final Date validThru = invocation.getArgumentAt(5, Date.class);
                final Double initialBalance = invocation.getArgumentAt(6, Double.class);
                final String currency = invocation.getArgumentAt(7, String.class);
                return new CardDetails(cardNumber, applicantName, cvcNumber, validFrom, validThru, cardLimit, initialBalance, currency);
            }
        };

        doAnswer(ans).when(this.cardDAO).addCreditCard(anyString(), anyString(), anyDouble(), anyString(), any(Date.class), any(Date.class), anyDouble(), anyString());
        final CardResponse response = this.cardService.addCreditCard(cardRequest);
    }


    @Test
    public void verifyNoCallToAppSyncOnDaoFailureCall() {
        final CardRequest cardRequest = new CardRequest("4617019281263712", "Smith", 10000.00d);
        final Environment env = Mockito.mock(Environment.class);
        when(env.getProperty("spring.application.name")).thenReturn("credit-card-service");

        this.cardService = new CardService(this.cardDAO, new AppSyncService(null, null, env) {
            @Override
            public void syncDatabaseData(final List<CardDetails> cardData) {
                fail("Not expecting call sync method");
            }
        }, 5);


        final Answer<CardDetails> ans = new Answer<CardDetails>() {
            @Override
            public CardDetails answer(final InvocationOnMock invocation) throws Throwable {
                return null;
            }
        };

        doAnswer(ans).when(this.cardDAO).addCreditCard(anyString(), anyString(), anyDouble(), anyString(), any(Date.class), any(Date.class), anyDouble(), anyString());
        final CardResponse response = this.cardService.addCreditCard(cardRequest);
        assertTrue(response.getBalance() == null);
        assertTrue(response.getErrorMessage() != null && response.getErrorMessage().equalsIgnoreCase("Card Details already exists"));
    }

    @Test
    public void verifyNoSensitiveInfoPresentInResponse() {

        when(this.cardDAO.fetchAllValidCreditCard()).thenReturn(ImmutableList.of(new CardDetails("4325321345234", "Jones", "213",
                Date.from(Instant.now()), Date.from(Instant.now()), 10000.00d, 0.00d, "GBP")));
        final List<CardResponse> cardResponses = this.cardService.getValidCreditCards();
        assertNull(cardResponses.iterator().next().getValidFrom());
        assertNull(cardResponses.iterator().next().getValidThru());
        assertNull(cardResponses.iterator().next().getCvc());
    }


}


