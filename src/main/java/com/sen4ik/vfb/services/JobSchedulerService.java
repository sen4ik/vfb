package com.sen4ik.vfb.services;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
public class JobSchedulerService {

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Value("${telerivet.enabled}")
    private Boolean telerivetEnabled;

    @Autowired
    TelerivetService telerivetService;

    public void sendVersesForCurrentHour() throws Exception {
        log.info("CALLED: sendVersesForCurrentHour()");

        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(currentDate);
        log.info("Current date: " + formattedDate);

        Optional<VersesEntity> verseForTodayOptional = versesRepository.findByDate(currentDate);
        if (!verseForTodayOptional.isPresent()){
            String errMsg = "No verses found for " + formattedDate;
            log.error(errMsg);
            throw new Exception(errMsg);
        }
        VersesEntity verseForToday = verseForTodayOptional.get();

        int currentHour = LocalTime.now().getHour();
        log.info("Current hour: " + currentHour);

        List<ContactsEntity> allContacts = contactsRepository.findAll();
        Set<String> bibleTranslations = allContacts.stream().map(ContactsEntity::getBibleTranslation).collect(Collectors.toSet());
        // Set<String> bibleTranslations = allContacts.stream().map(ContactsEntity::getBibleTranslation).collect(Collectors.toSet());

        for(String bibleTranslation : bibleTranslations){

            log.info("Bible Translation: " + bibleTranslation);

            List<String> phoneNumbers = new ArrayList<>();

            List<ContactsEntity> contactsForCurrentTranslation = contactsRepository.findBySelectedSendTimePacificAndBibleTranslationAndSubscriptionConfirmed(Double.valueOf(currentHour), bibleTranslation, (byte) 1);
            if (contactsForCurrentTranslation.size() > 0){

                for(ContactsEntity contact : contactsForCurrentTranslation){
                    String phoneNumber = contact.getPhoneNumber();
                    if(!phoneNumber.isEmpty() && phoneNumber != null){
                        phoneNumbers.add(phoneNumber);
                    }
                }

                log.info("Phone Numbers: " + phoneNumbers.toString());

                String verseToSend = "";
                String verseLocation = "";
                if(bibleTranslation.equals("en_esv")){
                    verseToSend = verseForToday.getEnEsv();
                    verseLocation = verseForToday.getEnVerseLocation();
                }
                else if(bibleTranslation.equals("en_niv")){
                    verseToSend = verseForToday.getEnNiv();
                    verseLocation = verseForToday.getEnVerseLocation();
                }
                else if(bibleTranslation.equals("en_kjv")){
                    verseToSend = verseForToday.getEnKjv();
                    verseLocation = verseForToday.getEnVerseLocation();
                }
                else if(bibleTranslation.equals("ru_synodal")){
                    verseToSend = verseForToday.getRuSynodal();
                    verseLocation = verseForToday.getRuVerseLocation();
                }
                else{
                    log.error("Unexpected bible translation present " + bibleTranslation);
                }

                if(telerivetEnabled && !verseToSend.isEmpty() && verseToSend != null){
                    telerivetService.sendMessageToGroup(verseToSend + " " + verseLocation, phoneNumbers);
                }
                else {
                    log.warn("Telerivet is disabled!");
                }

            }
            else{
                log.warn("No contacts for hour " + currentHour + " and bible translation " + bibleTranslation + "!");
            }

        }
    }

    public void checkIfVerseForTomorrowExists(){
        log.info("CALLED: checkIfVerseForTomorrowExists()");
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedTomorrowDate = df.format(dt);

        Optional<VersesEntity> verseForTomorrowOptional = versesRepository.findByDate(tomorrow);
        if (!verseForTomorrowOptional.isPresent()){
            String errMsg = "No verses found for " + formattedTomorrowDate;
            log.error(errMsg);

            emailServiceImpl.sendEmail("info@sen4ik.info", "VerseFromBible.com  - no verse present for tomorrow", "Hi Artur, There is no bible verse present for tomorrow. VerseFromBible.");
        }
        else{
            log.info("Verse for tomorrow is present.");
        }
    }

}