package com.multitenant.menu.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Slf4j
@Component
public class QrCodeGenerator {
    
    private static final int QR_CODE_SIZE = 300;
    private static final String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * Generate a QR code as a base64-encoded PNG image
     * @param data The data to encode (e.g., order code)
     * @return Base64-encoded PNG data URL
     */
    public String generateQrCodeAsBase64(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] qrCodeBytes = outputStream.toByteArray();
            String base64Encoded = Base64.getEncoder().encodeToString(qrCodeBytes);
            
            return BASE64_PREFIX + base64Encoded;
        } catch (Exception e) {
            log.error("Error generating QR code for data: {}", data, e);
            return null;
        }
    }
}
