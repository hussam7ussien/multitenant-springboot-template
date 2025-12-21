package com.multitenant.menu.services;

import com.multitenant.menu.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private final OtpLoggerService otpLoggerService;
    private final JwtService jwtService;

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.expiration-seconds:600}")
    private long otpExpirationSeconds;

    @Value("${otp.redis-key-prefix:otp}")
    private String redisKeyPrefix;

    /**
     * Generate OTP for phone number and return temporary session token
     */
    public String generateOtp(String phone) {
        String tenantId = TenantContext.getTenant().getTenantId();
        
        // Generate 6-digit OTP
        String otp = generateOtpCode();
        
        // Store OTP in Redis with tenant-scoped key
        String redisKey = buildRedisKey(tenantId, phone);
        redisTemplate.opsForValue().set(redisKey, otp, otpExpirationSeconds, TimeUnit.SECONDS);
        
        // Log OTP to file
        otpLoggerService.logOtp(phone, otp, tenantId);
        
        // Generate temporary session JWT token (10 min expiration)
        String sessionToken = jwtService.generateTempSessionToken(phone, tenantId);
        
        log.info("OTP generated for phone: {} (tenant: {})", phone, tenantId);
        return sessionToken;
    }

    /**
     * Verify OTP using session token
     * Returns phone number if valid, null otherwise
     */
    public String verifyOtp(String sessionToken, String otp) {
        try {
            // Extract phone and tenant from session token
            String phone = jwtService.extractPhoneFromTempToken(sessionToken);
            String tenantId = jwtService.extractTenantIdFromTempToken(sessionToken);
            
            if (phone == null || tenantId == null) {
                log.warn("Invalid session token: missing phone or tenant");
                return null;
            }
            
            // Retrieve OTP from Redis
            String redisKey = buildRedisKey(tenantId, phone);
            String storedOtp = redisTemplate.opsForValue().get(redisKey);
            
            if (storedOtp == null) {
                log.warn("OTP not found or expired for phone: {} (tenant: {})", phone, tenantId);
                return null;
            }
            
            // Compare OTPs
            if (otp.equals(storedOtp)) {
                // Delete OTP from Redis (one-time use)
                redisTemplate.delete(redisKey);
                log.info("OTP verified successfully for phone: {} (tenant: {})", phone, tenantId);
                return phone;
            } else {
                log.warn("OTP mismatch for phone: {} (tenant: {})", phone, tenantId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error verifying OTP", e);
            return null;
        }
    }

    /**
     * Generate 6-digit OTP code
     */
    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 100000 to 999999
        return String.valueOf(otp);
    }

    /**
     * Build Redis key: {tenantId}:otp:{phone}
     */
    private String buildRedisKey(String tenantId, String phone) {
        return String.format("%s:%s:%s", tenantId, redisKeyPrefix, phone);
    }
}

