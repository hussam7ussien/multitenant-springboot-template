package com.multitenant.menu.controller.user;

import com.multitenant.menu.api.UsersApi;
import com.multitenant.menu.model.*;
import com.multitenant.menu.controller.AbstractController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class UsersController extends AbstractController implements UsersApi {

    @Override
    public ResponseEntity<UserResponse> signin(String xTenantID, SigninRequest signinRequest) {
        logInfo("User signin attempt");
        return ResponseEntity.ok(new UserResponse());
    }

    @Override
    public ResponseEntity<UserResponse> signup(String xTenantID, SignupRequest signupRequest) {
        logInfo("User signup attempt");
        return ResponseEntity.ok(new UserResponse());
    }

    @Override
    public ResponseEntity<UserResponse> updateOtp(String xTenantID, UpdateOtpRequest updateOtpRequest) {
        logInfo("Updating OTP for user");
        return ResponseEntity.ok(new UserResponse());
    }

    @Override
    public ResponseEntity<UserResponse> updateUserInformation(String xTenantID, UpdateUserRequest updateUserRequest) {
        logInfo("Updating user information");
        return ResponseEntity.ok(new UserResponse());
    }

    @Override
    public ResponseEntity<UploadResponse> uploadFiles(String xTenantID, MultipartFile file) {
        logInfo("Uploading file for user");
        return ResponseEntity.ok(new UploadResponse());
    }

    @Override
    public ResponseEntity<UserResponse> verify(String xTenantID, VerifyRequest verifyRequest) {
        logInfo("Verifying user account");
        return ResponseEntity.ok(new UserResponse());
    }
}
