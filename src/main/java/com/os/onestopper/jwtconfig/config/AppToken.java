package com.os.onestopper.jwtconfig.config;

import lombok.*;

import java.time.Instant;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppToken {
    private String userId;
    private Set<String> role;
    private Instant expiresDate;
}
