package com.sen4ik.vfb.services;

import com.sen4ik.vfb.entities.ActionLog;
import com.sen4ik.vfb.repositories.ActionLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
public class ActionsLogService {

    @Autowired
    ActionLogRepository actionLogRepository;

    public enum Actions {

        confirmed("confirmed"),
        message_sent("message_sent"),
        received("received"),
        deleted("deleted");

        public final String value;

        Actions(String value) {
            this.value = value;
        }

        final String value() {
            return value;
        }
    }

    public void log(Integer userId, Integer verseId, String messageBody, String to, String from, String action, String notes){
        ActionLog al = new ActionLog();
        if(userId != null) al.setUserId(userId);
        if(verseId != null) al.setVerseId(verseId);
        if(messageBody != null) al.setMessageBody(messageBody);
        if(to != null) al.setTo(to);
        if(from != null) al.setFrom(from);
        if(action != null) al.setAction(action);
        if(notes != null) al.setNotes(notes);
        actionLogRepository.save(al);
    }

    public void messageSent(String to, String from, String messageBody, String notes){
        ActionLog al = new ActionLog();
        if(to != null) al.setTo(to);
        if(from != null) al.setFrom(from);
        if(messageBody != null) al.setMessageBody(messageBody);
        if(notes != null) al.setNotes(notes);
        al.setAction(Actions.message_sent.value);
        actionLogRepository.save(al);
    }

}
