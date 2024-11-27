package com.os.fasdoor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @GetMapping
    public ResponseEntity hello() {
        return ResponseEntity.ok("Secures EndPoint");
    }
}
