package com.sen4ik.vfb.services;

import com.jayway.jsonpath.JsonPath;
import com.sen4ik.vfb.constants.Constants;
import com.sen4ik.vfb.entities.Verse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Component
@Slf4j
public class BibleApiService {

    @Value("${bible.api.key}")
    private String apiKey;

    @Autowired
    ResourceLoader resourceLoader;

    // Docs: https://scripture.api.bible/docs#all-bibles
    private String host = "api.scripture.api.bible";
    private String url = "https://" + host + "/v1";
    private String esvId = "f421fe261da7624f-01";
    private String nivId = "78a9f6124f344018-01";
    private String kjvId = "de4e12af7f28f599-01";
    private String enBookAbbreviation;

    public Verse getBibleVerse(String bookName, int chapterNumber, int verseFrom, Integer verseTo) throws IOException {

        log.info("book: " + bookName);
        log.info("chapter: " + chapterNumber);
        log.info("verse: " + verseFrom);

        Map<String, String> bibles = new HashMap<String, String>();
        bibles.put("esv", esvId);
        bibles.put("niv", nivId);
        bibles.put("kjv", kjvId);

        Verse verseResultObj = new Verse();
        OkHttpClient client = new OkHttpClient();

        for (Map.Entry<String, String> entry : bibles.entrySet()) {

            String currentBookName = bookName;
            String currentBibleId = entry.getValue();
            String currentBibleAbr = entry.getKey();
            log.info("Working on following translation: " + currentBibleAbr);

            // get book ID
            Request booksRequest = new Request.Builder()
                    .url(url + "/bibles/" + currentBibleId + "/books")
                    .header("api-key", apiKey)
                    .build();

            Call call = client.newCall(booksRequest);
            Response response = call.execute();
            String booksJson = response.body().string();
            // log.info("booksJson: " + booksJson);

            // verify the response and throw an error if its not 200
            int sc = response.code();
            if(sc != 200){
                log.error("api.bible returned " + sc + " status code while getting the list of Bible books.\nResponse Body: " + booksJson);
                return null;
            }

            String bookNameField = "name";
            if(currentBibleId.equals(nivId)){
                // use nameLong field for lookup
                bookNameField = "nameLong";
            }

            if(currentBibleId.equals(nivId) || currentBibleId.equals(kjvId)){
                // in NIV and KJV it is Psalms
                if(currentBookName.equals("Psalm")){
                    currentBookName = "Psalms";
                }
            }

            net.minidev.json.JSONArray bookArr = JsonPath.parse(booksJson).read("$.data[?(@." + bookNameField + " == '" + currentBookName + "')].id");
            String bookId = bookArr.get(0).toString();

            // set verse location. use ESV to get the book abbreviation.
            if(currentBibleId.equals(esvId)) {

                net.minidev.json.JSONArray abbrObj = JsonPath.parse(booksJson).read("$.data[?(@." + bookNameField + " == '" + currentBookName + "')].abbreviation");
                enBookAbbreviation = abbrObj.get(0).toString();
                String ruBookAbbr = getRusBookAbbr(enBookAbbreviation);

                // TODO: tweak the chapter and verse numbers for russian translation for the book of Psalms
                // We might need to do tweaks for other books
                // https://www.ph4.org/btraduk_ruennum.php
                // https://mybible.zone/ruennum-eng.php

                String enVerseLocation;
                String ruVerseLocation = "";
                if (verseTo == null || verseTo == verseFrom) {
                    // single verse
                    // set verse location for EN version
                    enVerseLocation = enBookAbbreviation + " " + chapterNumber + ":" + verseFrom;

                    // set verse location for RU version
                    if(!currentBookName.equals("Psalm") || !currentBookName.equals("Psalms")){
                        ruVerseLocation = ruBookAbbr + " " + chapterNumber + ":" + verseFrom;
                    }
                }
                else {
                    // verse range
                    // set verse location for EN version
                    enVerseLocation = enBookAbbreviation + " " + chapterNumber + ":" + verseFrom + "-" + verseTo;

                    // set verse location for RU version
                    if(!currentBookName.equals("Psalm") || !currentBookName.equals("Psalms")){
                        ruVerseLocation = ruBookAbbr + " " + chapterNumber + ":" + verseFrom + "-" + verseTo;
                    }
                }
                verseResultObj.setEnVerseLocation(enVerseLocation);
                verseResultObj.setRuVerseLocation(ruVerseLocation);
            }
            response.close();

            String verse = "";
            if(verseTo == null || verseTo == verseFrom){
                // single verse
                // get EN verse
                String v = getEnVerse(currentBibleId, bookId, chapterNumber, verseFrom, client);
                verse = v;
            }
            else{
                // verse range
                for(int i = verseFrom; i <= verseTo; i++){
                    // get EN verses
                    String v = getEnVerse(currentBibleId, bookId, chapterNumber, i, client);
                    verse = (i == verseFrom) ? v : verse + " " + v;
                }
            }

            if(currentBibleId.equals(esvId)){
                verseResultObj.setEnEsv(verse);
            }
            else if(currentBibleId.equals(nivId)){
                verseResultObj.setEnNiv(verse);
            }
            else if(currentBibleId.equals(kjvId)){
                verseResultObj.setEnKjv(verse);
            }
        }

        // Get RU verse
        String ruVerse = getRuVerse(enBookAbbreviation, chapterNumber, verseFrom, verseTo);
        log.info("ruVerse: " + ruVerse);
        if(ruVerse == null || ruVerse.isEmpty()){
            return null;
        }
        verseResultObj.setRuSynodal(ruVerse);

        return verseResultObj;
    }

    private String getEnVerse(String currentBibleId,
                              String bookId,
                              int chapterNumber,
                              int verseFrom,
                              OkHttpClient client) throws IOException {

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("v1")
                .addPathSegment("bibles")
                .addPathSegment(currentBibleId)
                .addPathSegment("verses")
                .addPathSegment(bookId + "." + chapterNumber + "." + verseFrom)
                .addQueryParameter("include-titles", "false")
                .addQueryParameter("include-notes", "false")
                .addQueryParameter("include-verse-numbers", "false")
                .addQueryParameter("include-verse-spans", "false")
                .addQueryParameter("include-chapter-numbers", "false")
                .addQueryParameter("content-type", "text")
                .build();

        Request verseRequest = new Request.Builder()
                .url(httpUrl)
                .header("api-key", apiKey)
                .build();

        Response verseResponse = client.newCall(verseRequest).execute();
        String verseJson = verseResponse.body().string();
        int statusCode = verseResponse.code();
        if(statusCode != 200){
            log.error("api.bible returned " + statusCode + " status code while doing verse lookup.\nRequest: " + verseRequest.toString() + "\nResponse Body: " + verseJson);
            return null;
        }
        verseResponse.close();

        String verse = JsonPath.parse(verseJson).read("$.data.content");
        verse = verse
                .replaceAll("\n", "")
                .replaceAll("\r", "").trim()
                .replaceAll("\\s+", " ");

        return verse.trim();
    }

    public String getRuVerse(String enBookAbbreviation, int chapterNumber, int verseFrom, Integer verseTo) throws IOException {
        String verse = "";
        String fileName = Constants.bookAbbrsAndDatFiles.get(enBookAbbreviation).get(1);
        log.info("ru .dat file:  " + fileName);
        List<String> bookArr = readFileIntoArray("static/bible/rst/" + fileName);
        log.info("bookArr size: " + bookArr.size());

        if(verseTo == null || verseTo == verseFrom) {
            // single verse
            verse = findLineFromArrayThatStartsWithPrefix(bookArr, "#" + chapterNumber + ":" + verseFrom + "#");
        }
        else {
            // verse range
            for(int i = verseFrom; i <= verseTo; i++){
                String v = findLineFromArrayThatStartsWithPrefix(bookArr, "#" + chapterNumber + ":" + i + "#");
                verse = (i == verseFrom) ? v : verse + " " + v;
            }
        }
        return verse;
    }

    private String getRusBookAbbr(String enEsvBookAbbr){
        return Constants.bookAbbrsAndDatFiles.get(enEsvBookAbbr).get(0);
    }

    private List<String> readFileIntoArray(String filePath) throws IOException {
        log.info("readFileIntoArray(): filePath: " + filePath);
        List<String> arr = new ArrayList<>();

        InputStream is = new ClassPathResource(filePath).getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while(reader.ready()) {
            String line = reader.readLine();
            // log.info("is: " + line);
            arr.add(line);
        }

        return arr;
    }

    private String findLineFromArrayThatStartsWithPrefix(List<String> arr, String prefix){
        for(String str : arr){
            if(str.startsWith(prefix)){
                log.info("Line that matched " + prefix + ": " + str);
                return str
                        .replace(prefix, "")
                        .replaceAll("(\\r|\\n|\\t)", "")
                        .trim();
            }
        }
        return null;
    }

}
