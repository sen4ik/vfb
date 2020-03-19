package com.sen4ik.vfb.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
public class BibleApiService {

    @Value("${bible.api.key}")
    private String apiKey;

    // Docs: https://scripture.api.bible/docs#all-bibles
    private String host = "https://api.scripture.api.bible/v1";
    private String esvId;
    private String nivId;
    private String kjvId;
    private String ruSynodalId;

}
