package com.multitenant.menu.services;

import com.multitenant.menu.entity.mongo.LogEntry;
import com.multitenant.menu.repository.mongo.TenantAwareMongoTemplate;
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
