package com.service;



import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.model.User;
import com.repository.UserRepository;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // Save OTP to user record
    public void saveOtpToUser(User user, String otp) {
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
    }

    public boolean verifyOtp(User user, String otp) {
        if (user.getOtp() == null) return false;

        if (!user.getOtp().equals(otp)) return false;

        return LocalDateTime.now().isBefore(user.getOtpExpiry());
    }
}

