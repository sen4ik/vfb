package com.sen4ik.vfb.base;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JobScheduler {

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Value("${telerivet.enabled}")
    private Boolean telerivetEnabled;

    // https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
    // @Scheduled(cron = "0 0 5-11 * * *")
    @Scheduled(cron = "0 */2 * * * *") // every two minutes
    public void sendOutVerses() throws Exception {
        log.info("CALLED: sendOutVerses()");

        // make sure we are in the hour
        Thread.sleep(5000);

        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(currentDate);
        log.info("Current date: " + formattedDate);

        Optional<VersesEntity> verseForToday = versesRepository.findByDate(currentDate);
        if (!verseForToday.isPresent()){
            log.error("No verses found for " + formattedDate);
        }

        int currentHour = LocalTime.now().getHour();
        log.info("Current hour: " + currentHour);

        List<ContactsEntity> contactsForCurrentHour = contactsRepository.findBySelectedSendTimePacific(Double.valueOf(currentHour));
        if (contactsForCurrentHour.size() > 0){



        }
        else{
            log.info("No contacts for hour " + currentHour);
        }

    }

    // @Scheduled(cron = "0 */2 * * * *")
    public void checkVersesForTomorrow() throws Exception {

    }

}
