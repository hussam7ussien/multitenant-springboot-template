package com.multitenant.menu.tenant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class DatabaseConnection implements Serializable {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;
}
