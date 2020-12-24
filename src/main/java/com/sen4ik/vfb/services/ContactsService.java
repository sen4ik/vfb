package com.sen4ik.vfb.services;

import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public String maskPhoneNumber(String phoneNumber){
        phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 4) + "****";
        return phoneNumber;
    }

    public List<String> maskPhoneNumber(List<PhoneNumber> phoneNumbers){
        List<String> masked = new ArrayList<>();
        for(PhoneNumber pn : phoneNumbers){
            masked.add(maskPhoneNumber(pn.toString()));
        }
        return masked;
    }

}
