package com.ccvendor.creditcardservice.service;

import com.ccvendor.creditcardservice.data.SystemAdminUser;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class AbstractSyncService {
    private static final Logger log = LoggerFactory.getLogger(AbstractSyncService.class);
    protected static HttpHeaders httpHeaders;

    static {
        final String plainCreds = String.format("%s:%s", SystemAdminUser.USERNAME, SystemAdminUser.PASSWORD);
        final byte[] encodedAuth = Base64.encodeBase64(
                plainCreds.getBytes(Charset.forName("US-ASCII")));
        final String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", authHeader);
    }

    protected final DiscoveryClient discoveryClient;
    protected final String creditServiceAppName;
    protected final RestTemplate restTemplate;
    private final String localHostName;
    private final Environment env;

    public AbstractSyncService(final DiscoveryClient discoveryClient, final RestTemplate restTemplate, final Environment environment) {
        this.discoveryClient = discoveryClient;
        this.creditServiceAppName = environment.getProperty("spring.application.name");
        this.restTemplate = restTemplate;
        this.env = environment;
        this.localHostName = this.initializeHostname();
    }


    protected boolean isHostInstance(final int port, final String host) {
        return this.env.getProperty("local.server.port").equals(String.valueOf(port)) && host.equals(this.localHostName);
    }


    private String initializeHostname() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            log.error("Error While initializing local hostname ", e);
        }
        return "localhost";
    }
}
