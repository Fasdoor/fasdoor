package com.os.fasdoor.logger;

import com.os.fasdoor.model.ApplicationUser;
import com.os.fasdoor.model.Logs;
import com.os.fasdoor.repository.LogsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class FasdoorLogger {
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

    public String error(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = Objects.isNull(authentication) ? null : String.valueOf(((ApplicationUser)authentication.getPrincipal()).getId());
        repo.save(Logs.builder()
                .date(new Date())
                .level("ERROR")
                .message(message)
                .userName(userName)
                .build());
        return message;
    }
}
