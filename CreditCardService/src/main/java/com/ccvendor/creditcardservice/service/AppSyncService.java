package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.model.CardDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Class fetches the registry from discovery client and broadcast the updated card details to all the
 * available service instances.
 */
@Component
public class AppSyncService extends AbstractSyncService {
    private static final Logger log = LoggerFactory.getLogger(AppSyncService.class);

    @Autowired
    public AppSyncService(final DiscoveryClient discoveryClient,
                          final RestTemplate restTemplate, final Environment environment) {
        super(discoveryClient, restTemplate, environment);
    }


    public void syncDatabaseData(final List<CardDetails> cardData) {
        final List<ServiceInstance> instances = this.discoveryClient.getInstances(this.creditServiceAppName);
        if (cardData.size() == 0 || (instances == null || instances.size() == 0)) return;

        for (final ServiceInstance instance : instances) {
            final String requestURL = instance.getUri() + "/v1/dbSync";
            if (this.isHostInstance(instance.getPort(), instance.getHost())) // Not to invoke dbsync on its own instance.
                continue;
            log.info("Invoking instance with URL : {} , update cache ", requestURL);

            this.executeRequest(requestURL, cardData);
        }

    }

    private void executeRequest(final String requestURL, final List<CardDetails> cardData) {
        try {
            final ResponseEntity<Void> responseEntity =
                    this.restTemplate.exchange(requestURL, HttpMethod.POST, new HttpEntity<>(cardData, httpHeaders), Void.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info("Cache updated successfully");
            } else {
                log.info("Error while broadcasting updating cache");
            }

        } catch (final Exception e) {
            // Possiblity of error, on Instances that are lost and waiting for eviction.
            log.error("Error while broadcasting the data , {}", e.getMessage());
        }
    }


}