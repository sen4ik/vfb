package com.sen4ik.vfb.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
public class ContactsService {

    public String sanitizePhoneNumber(String pn){
        String phoneNumber = pn.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("-", "").replaceAll(" ", "").trim();
        if(phoneNumber.startsWith("+1")){
            phoneNumber = phoneNumber.substring(2, phoneNumber.length());
        }
        log.debug(phoneNumber);
        return phoneNumber;
    }

}
