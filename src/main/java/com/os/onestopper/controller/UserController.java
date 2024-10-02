package com.os.onestopper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.os.onestopper.exception.customException.UserAlredyPresentException;
import com.os.onestopper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping(path= "/signup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity signup(@RequestBody String object) throws JsonProcessingException, UserAlredyPresentException {
        Map<String, Object> result = new HashMap<>();
        userService.signUp(result, object);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path= "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity login(@RequestBody String object) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.login(result, object);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception exception) {
            result.put("error", "Some thing went wrong");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path= "/change-password", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity changePassword(@RequestBody String object) throws JSONException, UsernameNotFoundException {
        Map<String, Object> result = new HashMap<>();
        userService.changePassword(result, object);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path= "/access-denied")
    public ResponseEntity accessDenied() throws JSONException, UsernameNotFoundException {
        Map<String, Object> result = new HashMap<>();
        result.put("error", "Access Denied");
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    @GetMapping(path= "/success")
    public ResponseEntity authSuccess(@AuthenticationPrincipal OidcUser principal) {
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
}