package com.multitenant.template.repository;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class TenantAwareMongoTemplate {
    private final MongoTemplate mongoTemplate;

    public TenantAwareMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <T> void saveForTenant(String tenantId, String schema, String collection, T document){
        String collectionName = tenantId + "." + schema + "." + collection;
        mongoTemplate.save(document, collectionName);
    }

    public <T> List<T> findAllForTenant(String tenantId, String schema, String collection,Class<T> entityClass){
        String collectionName = tenantId + "." + schema + "." + collection;
        return  mongoTemplate.findAll(entityClass, collectionName);
    }
}
