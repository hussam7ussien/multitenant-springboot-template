package com.multitenant.menu.controller;

import lombok.extern.slf4j.Slf4j;

/**
 * Base controller class for all API controllers.
 * Provides common functionality and logging for API endpoints.
 */
@Slf4j
public abstract class AbstractController {
    
    protected void logInfo(String message) {
        log.info(message);
    }
    
    protected void logError(String message, Exception e) {
        log.error(message, e);
    }
}
