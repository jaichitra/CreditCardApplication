package com.ccvendor.creditcardservice.config;


import com.ccvendor.creditcardservice.data.AppUserCredentials;
import com.ccvendor.creditcardservice.data.SystemAdminUser;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FilePasswordManager {

    private static final Logger log = LoggerFactory.getLogger(FilePasswordManager.class);

    private Map<String, AppUserCredentials> credentials;

    public FilePasswordManager() {
        this.load();
    }

    public AppUserCredentials getCredentials(final String uName) {
        return this.credentials.get(uName);
    }

    /**
     * Method loads the credential from password.txt file.
     * Avoid any user from password.txt with ROLE -> ROLE_SYSTEM_ADMIN
     */
    public void load() {
        try {
            final List<String> lineContent = IOUtils.readLines(ClassLoader.getSystemResourceAsStream("passwords.txt"));
            this.credentials = new HashMap<>();

            for (final String content : lineContent) {
                final String[] credentialContent = content.split(" ");
                final String username = credentialContent[0];
                final String password = credentialContent[1];
                final String[] rolesList = credentialContent[2].split(",");
                if (!username.equalsIgnoreCase(SystemAdminUser.USERNAME))
                    this.credentials.put(username, new AppUserCredentials(username, password, Arrays.asList(rolesList).stream().filter(t -> !t.equalsIgnoreCase(SystemAdminUser.USER_ROLE)).collect(Collectors.toList())));
            }
            // Code doesnt supports loading of any username with role of SystemAdminUser, This user would be confined to do the syncing between instances and is initiated within the system.
            this.credentials.put(SystemAdminUser.USERNAME, new AppUserCredentials(SystemAdminUser.USERNAME, SystemAdminUser.PASSWORD, ImmutableList.of(SystemAdminUser.USER_ROLE)));
            log.info("Password Manager Loaded with total count of credentials : " + this.credentials.size());
        } catch (final Exception e) {
            log.error("Passwords File loading error", e.getMessage());
        }


    }
}