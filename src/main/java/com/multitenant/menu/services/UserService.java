package com.multitenant.menu.services;

import com.multitenant.menu.entity.sql.UserEntity;
import com.multitenant.menu.repository.sql.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity signup(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Generate OTP for verification
        user.setOtp(generateOtp());
        user.setVerified(false);
        return userRepository.save(user);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    public boolean verifyOtp(Long userId, String otp) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (otp.equals(user.getOtp())) {
                user.setVerified(true);
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}

