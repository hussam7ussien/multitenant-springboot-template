package com.multitenant.template.services;

import com.multitenant.template.entity.mongo.LogEntry;
import com.multitenant.template.repository.TenantAwareMongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    private final TenantAwareMongoTemplate tenantMongo;


    public LogService(TenantAwareMongoTemplate tenantMongo) {
        this.tenantMongo = tenantMongo;
    }

    public void logEvent(String tenantId, String message){
        LogEntry log = LogEntry.builder()
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        tenantMongo.saveForTenant(tenantId, "public", "logs", log);
    }

    public List<LogEntry> getLogs(String tenantId) {
        return tenantMongo.findAllForTenant(tenantId, "public", "logs", LogEntry.class);
    }

}
