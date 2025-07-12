package com.mutlitenant.template.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
public class TenantData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String tenantId;
}
