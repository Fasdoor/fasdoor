package com.os.fasdoor.utility;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommonUtil {
    public Date getCurrentTime() {
        long currentTimesInMillis = System.currentTimeMillis();
        return new Date(currentTimesInMillis);
    }
}
