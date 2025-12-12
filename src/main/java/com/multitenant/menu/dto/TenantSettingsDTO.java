package com.multitenant.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettingsDTO {
    private Long id;
    private String key;
    private String value;
    private String dataType;
}
