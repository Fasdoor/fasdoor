package com.os.onestopper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.onestopper.jwtconfig.AuthenticationService;
import com.os.onestopper.logger.OneStopLogger;
import com.os.onestopper.mailsender.MailSender;
import com.os.onestopper.model.Address;
import com.os.onestopper.model.ApplicationUser;
import com.os.onestopper.repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {
    private final Random random = new Random();
    @Autowired
    UserRepository userRepository;
    @Autowired
    MailSender mailSender;
    @Autowired
    OneStopLogger oneStopLogger;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AuthenticationService authenticationService;

    private final Logger logger = Logger.getLogger(OneStopLogger.class.getName());

    private ObjectMapper objectMapper = new ObjectMapper();
    public void signUp(Map<String, Object> result, String object) throws JSONException, JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ApplicationUser applicationUser = objectMapper.readValue(object, ApplicationUser.class);
        Optional<ApplicationUser> optionalApplicationUser = userRepository.findByEmailId(applicationUser.getEmailId());
        if (optionalApplicationUser.isPresent()) {
            logger.info(oneStopLogger.info("Email Id Exits"));
            throw new RuntimeException("Email Already Register Kindly Login");
        }
//        String encodedPassword = hashPassword(applicationUser.getPassword());
        String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
        applicationUser.setPassword(encodedPassword);
        applicationUser.setVerified(false);
        String otp = generateOtp();
        applicationUser.setOtp(otp);
        userRepository.save(applicationUser);
        mailSender.sendEmail(applicationUser.getEmailId(), otp);
        result.put("success", "Otp is Send to ".concat(applicationUser.getEmailId()).concat(" Email Id"));
    }

//    private String hashPassword(String newPassword) {
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        return bCryptPasswordEncoder.encode(newPassword);
//    }
    private String generateOtp() {
        int length = 4;
        // Possible characters in the OTP
        String numbers = "0123456789";
        // StringBuilder to store generated OTP
        StringBuilder sb = new StringBuilder();

        // Generate OTP of specified length
        for (int i = 0; i < length; i++) {
            // Generate a random index between 0 and length of numbers string
            int index = random.nextInt(numbers.length());
            // Append the character at the randomly generated index to the OTP
            sb.append(numbers.charAt(index));
        }

        return sb.toString();
    }

    public void login(Map<String, Object> result, String object) throws JSONException {
        JSONObject jsonObject = new JSONObject(object);
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        String token = authenticationService.generateToken(userName);
        result.put("success", token);
    }
}
