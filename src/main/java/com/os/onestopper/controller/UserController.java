package com.os.onestopper.controller;

import com.os.onestopper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(path= "/signup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity signup(@RequestBody String object) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.signUp(result, object);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception exception) {
            result.put("error", "Some thing went wrong");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
