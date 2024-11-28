package com.os.fasdoor.jwtconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.os.fasdoor.jwtconfig.config.AppToken;
import lombok.extern.slf4j.Slf4j;
import org.paseto4j.commons.PasetoException;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version3.Paseto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
public class TokenService {
    @Value("${app.token.secret}")
    public String secret;

    @Value("${app.token.footer}")
    public String footer;

    public Optional<String> encrypt(AppToken token) {
        String payload;
        try {
            payload = mapper().writeValueAsString(token);
            return Optional.of(Paseto.encrypt(key(), payload, footer));
        } catch (PasetoException | JsonProcessingException e) {
            log.error("Failed to encode token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<AppToken> decrypt(String token) {
        try {
            String payload = Paseto.decrypt(key(), token, footer);
            AppToken appToken = mapper().readValue(payload, AppToken.class);
            if (Instant.now().isAfter(appToken.getExpiresDate())) {
                return Optional.empty();
            }
            return Optional.of(appToken);
        } catch (PasetoException | JsonProcessingException e) {
            log.error("Failed to decode token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private SecretKey key() {
        return new SecretKey(Arrays.copyOf(this.secret.getBytes(StandardCharsets.UTF_8), 32), Version.V3);
    }

    private JsonMapper mapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
