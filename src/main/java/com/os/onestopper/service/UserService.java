package com.os.onestopper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.os.onestopper.Enum.Role;
import com.os.onestopper.exception.customException.UserAlredyPresentException;
import com.os.onestopper.exception.customException.UserNotVerifiedException;
import com.os.onestopper.jwtconfig.TokenService;
import com.os.onestopper.jwtconfig.config.AppToken;
import com.os.onestopper.logger.OneStopLogger;
import com.os.onestopper.mailsender.MailSender;
import com.os.onestopper.model.ApplicationUser;
import com.os.onestopper.repository.UserRepository;
import dev.paseto.jpaseto.PasetoKeyException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Random random = new Random();
    private final UserRepository userRepository;
    private final MailSender mailSender;
    private final OneStopLogger oneStopLogger;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleCliendid;

    @Autowired
    public UserService(UserRepository userRepository, MailSender mailSender, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService, OneStopLogger oneStopLogger, OAuth2AuthorizedClientService authorizedClientService) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.oneStopLogger = oneStopLogger;
        this.authorizedClientService = authorizedClientService;
    }

    private final Logger logger = LoggerFactory.getLogger(OneStopLogger.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    public void signUp(Map<String, Object> result, String object) throws JsonProcessingException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ApplicationUser applicationUser = objectMapper.readValue(object, ApplicationUser.class);
        userRepository.findByEmailId(applicationUser.getEmailId()).ifPresent(user -> {
            throw new UserAlredyPresentException("Email Already Register Kindly Login");
        });
        userRepository.findByPhoneNumber(applicationUser.getPhoneNumber()).ifPresent(user -> {
            throw new UserAlredyPresentException("Mobile Number Already Register Kindly Login");
        });
        String encodedPassword = passwordEncoder.encode(applicationUser.getPassword());
        applicationUser.setPassword(encodedPassword);
        applicationUser.setRole(Role.USER);
        userRepository.save(applicationUser);
        result.put("success", "Signup successful try to login");
    }

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
        try {
            JSONObject jsonObject = new JSONObject(object);
            String userName = jsonObject.getString("userName");
            String password = jsonObject.getString("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
            AppToken token = generateToken(userName);
            String pasetoToken = tokenService.encrypt(token).orElseThrow(() -> new PasetoKeyException("Unable to Signin"));
            result.put("success", pasetoToken);
        } catch (BadCredentialsException exception) {
            result.put("error", "Username Or Password Wrong");
        }
    }

    private AppToken generateToken(String userName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());

        Instant now = Instant.now();
        Instant expirationTime = now.plus(Duration.ofHours(12));

        AppToken token = new AppToken();
        token.setUserId(userName);
        token.setRole(roles);
        token.setExpiresDate(expirationTime);
        return token;
    }

    public void changePassword(Map<String, Object> result, String object) throws JSONException {
        JSONObject jsonObject = new JSONObject(object);
        String userName = jsonObject.getString("userName");
        String password = jsonObject.getString("password");
        ApplicationUser applicationUser = null;
        if (userName.contains("@")) {
            applicationUser = userRepository.findByEmailId(userName).orElseThrow(() -> new UsernameNotFoundException("Invalid Email Id"));
        } else {
            applicationUser = userRepository.findByPhoneNumber(userName).orElseThrow(() -> new UsernameNotFoundException("Invalid Mobile Number"));
        }

        String encodedPassword = passwordEncoder.encode(password);
        applicationUser.setPassword(encodedPassword);
        userRepository.save(applicationUser);
        logger.info(oneStopLogger.info("Password Changed"));
        result.put("success", "Password Changed Successfully");
    }

    public ResponseEntity authSuccess(OidcUser principal) {
        // Retrieve the OAuth2AuthorizedClient for Google
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                "google",  // The registration ID for Google in application.properties
                principal.getName());

        String message;
        String accessToken = null;
        if (authorizedClient != null) {
            // Extract the access token from the authorized client
            accessToken = authorizedClient.getAccessToken().getTokenValue();
            message = "Welcome, " + principal.getFullName() + "! Your access token is: " + accessToken;
        }

        message = "Welcome, " + principal.getFullName() + "! No access token available.";
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("token", accessToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity googleLogin(Map<String, Object> result, Map<String, String> object) {
        String token = object.get("token");

        if (StringUtils.isBlank(token)) throw new UserNotVerifiedException("Token Verification Failed");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singleton(googleCliendid))
                .build();

        try {
            GoogleIdToken googleIdToken = verifier.verify(token);

            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                // Use payload to get user info (e.g., email, name)
                String userId = payload.getSubject();
                String email = payload.getEmail();

                // Authenticate the user in your system and generate paseto or session
                AppToken pasetoToken = generateToken(email);  // your paseto generation logic
                return ResponseEntity.ok(Map.of("token", pasetoToken));
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
        }
    }
}
