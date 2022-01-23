package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.model.CardDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class fetches the registry from discovery client and fetches the current state of database data from all the other available instances.
 */
@Component
public class AppStartSyncService extends AbstractSyncService {
    private static final Logger log = LoggerFactory.getLogger(AppStartSyncService.class);

    private final CardService cardService;

    @Autowired
    public AppStartSyncService(final DiscoveryClient discoveryClient,
                               final RestTemplate restTemplate, final Environment environment, final CardService cardService) {
        super(discoveryClient, restTemplate, environment);
        this.cardService = cardService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void afterPropertiesSet() throws Exception {
        final List<ServiceInstance> instances = this.discoveryClient.getInstances(this.creditServiceAppName);
        if (instances == null || instances.size() == 0) return;
        final List<List<CardDetails>> cacheDataList = new ArrayList<>();

        for (final ServiceInstance instance : instances) {
            final String requestURL = instance.getUri() + "/v1/dbSync";
            if (this.isHostInstance(instance.getPort(), instance.getHost())) // Not to invoke dbsync on its own instance.
                continue;
            log.info("Invoking instance with URL : {} , fetching cache ", requestURL);
            this.executeRequest(requestURL, cacheDataList);
        }

        final List<CardDetails> cacheData = cacheDataList.stream().flatMap(List::stream).collect(Collectors.groupingBy(t ->
                t.getCreditCardNumber(), Collectors.reducing((k, v) -> k))).values().stream().map(t -> t.get()).collect(Collectors.toList());

        if (cacheData.size() > 0)
            this.cardService.syncCreditCardData(cacheData);
    }

    private void executeRequest(final String requestURL, final List<List<CardDetails>> cacheDataList) {
        try {
            final ResponseEntity<List<CardDetails>> responseEntity =
                    this.restTemplate.exchange(requestURL, HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<List<CardDetails>>() {
                    });

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("Cache updated successfully");
                cacheDataList.add(responseEntity.getBody());
            } else {
                log.info("Error while updating cache");
            }
        } catch (final Exception e) {
            // Possiblity of error, on Instances that are lost and waiting for eviction.
            log.error("Error while executing request on URL : {}", requestURL);
        }
    }
}
