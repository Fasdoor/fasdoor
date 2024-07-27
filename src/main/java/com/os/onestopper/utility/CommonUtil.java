package com.os.onestopper.utility;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommonUtil {
    public Date getCurrentTime() {
        long currentTimesInMillis = System.currentTimeMillis();
        return new Date(currentTimesInMillis);
    }
}
