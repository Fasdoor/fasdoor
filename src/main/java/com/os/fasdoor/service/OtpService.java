package com.os.fasdoor.service;

import com.os.fasdoor.mailsender.MailSender;
import com.os.fasdoor.model.OtpDetails;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private SecureRandom secureRandom;
    Map<String, OtpDetails> otpDetailsMap = new ConcurrentHashMap<>();
    private final MailSender mailSender;
    private long otpLen = 4;
    private long otpExpirationTime = 5;

    @Autowired
    public OtpService(MailSender mailSender) throws NoSuchAlgorithmException {
        this.mailSender = mailSender;
        this.secureRandom = SecureRandom.getInstanceStrong();
    }

    public void generateOtp(Map<String, Object> result, String object) throws JSONException {
        JSONObject jsonObject = new JSONObject(object);
        String userName = jsonObject.getString("emailId");

        StringBuilder stringBuilder = getOtp();

        otpDetailsMap.put(userName, new OtpDetails(stringBuilder.toString(), Instant.now().plus(Duration.ofMinutes(otpExpirationTime))));
        mailSender.sendEmail(userName, stringBuilder.toString());
        result.put("success", "Otp is Send to ".concat(userName).concat(" Email Id"));
    }

    public void verifyOtp(Map<String, Object> result, String object) throws JSONException {
        JSONObject jsonObject = new JSONObject(object);
        String userName = jsonObject.has("emailId") ? jsonObject.getString("emailId") : "";
        String otp = jsonObject.getString("otp");
        OtpDetails otpDetails = otpDetailsMap.get(userName);

        if (Instant.now().isBefore(otpDetails.getExpirationTime())) {
            if (!otpDetails.getOtp().equals(otp)) result.put("error", "Wrong Otp Entered");
            else {
                otpDetailsMap.remove(userName);
                result.put("success", "User Verified Successfully");
            }
        } else result.put("error", "Otp Is Expired");
    }

    public StringBuilder getOtp() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < otpLen; i++) {
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder;
    }
}
