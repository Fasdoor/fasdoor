package com.os.onestopper.logger;

import com.os.onestopper.model.ApplicationUser;
import com.os.onestopper.model.Logs;
import com.os.onestopper.repository.LogsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class OneStopLogger {
    @Autowired
    LogsRepo repo;

    public String info(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = Objects.isNull(authentication) ? null : String.valueOf(((ApplicationUser)authentication.getPrincipal()).getId());
        repo.save(Logs.builder()
                        .date(new Date())
                        .level("INFO")
                        .message(message)
                        .userName(userName)
                .build());
        return message;
    }

    public String debug(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = Objects.isNull(authentication) ? null : String.valueOf(((ApplicationUser)authentication.getPrincipal()).getId());
        repo.save(Logs.builder()
                .date(new Date())
                .level("DEBUG")
                .message(message)
                .userName(userName)
                .build());
        return message;
    }
}
