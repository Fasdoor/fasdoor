package com.os.fasdoor.controller;

import com.os.fasdoor.service.OtpService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/otp")
public class OtpController {
    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @RequestMapping(path= "/generate-otp", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity generateOtp(@RequestBody String object) throws JSONException {
        Map<String, Object> result = new HashMap<>();
        otpService.generateOtp(result, object);
        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path= "/verify-otp", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity verifyOtp(@RequestBody String object) throws JSONException {
        Map<String, Object> result = new HashMap<>();
        otpService.verifyOtp(result, object);
        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
