package com.sen4ik.vfb;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

@Slf4j
public class TempTests {

    @Test
    public void t() {
        int currentHour = LocalTime.now().getHour();
        log.info(currentHour + "");
        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        log.info(df.format(currentDate));
    }

}
