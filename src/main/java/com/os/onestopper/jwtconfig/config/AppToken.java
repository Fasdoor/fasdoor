package com.os.onestopper.jwtconfig.config;

import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class AppToken {
    private String userId;
    private Set<String> role;
    private Instant expiresDate;
}
