package com.multitenant.template.tenant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class PostgresConnection implements Serializable {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;
}
