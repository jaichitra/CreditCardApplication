package com.ccvendor.creditcardservice.data;

import java.util.List;
import java.util.Objects;

public final class AppUserCredentials {

    private final String username;
    private final String password;
    private final List<String> roles;

    public AppUserCredentials(final String username, final String password, final List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final AppUserCredentials that = (AppUserCredentials) o;
        return Objects.equals(this.username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username);
    }
}