package com.multitenant.menu.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class OtpLoggerService {

    @Value("${otp.log-file-path:logs/otp}")
    private String logFilePath;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logOtp(String phone, String otp, String tenantId) {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(logFilePath);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Create tenant-specific log file
            String fileName = String.format("%s/otp-%s.log", logFilePath, tenantId);
            File logFile = new File(fileName);

            // Append to log file
            try (FileWriter writer = new FileWriter(logFile, true)) {
                String logEntry = String.format("[%s] Phone: %s, OTP: %s, Tenant: %s%n",
                        LocalDateTime.now().format(TIMESTAMP_FORMATTER),
                        phone,
                        otp,
                        tenantId);
                writer.write(logEntry);
                writer.flush();
            }

            log.info("OTP logged for phone: {} (tenant: {})", phone, tenantId);
        } catch (IOException e) {
            log.error("Failed to log OTP to file for phone: {} (tenant: {})", phone, tenantId, e);
        }
    }
}

