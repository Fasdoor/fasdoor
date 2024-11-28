package com.os.fasdoor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.os.fasdoor.exception.customException.UserAlredyPresentException;
import com.os.fasdoor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;

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
        return userService.authSuccess(principal);
    }

    @PostMapping("/verify/id")
    public ResponseEntity googleLogin(@RequestBody Map<String, String> object) {
        Map<String, Object> result = new HashMap<>();
        return userService.googleLogin(result, object);
    }
}