package com.sen4ik.vfb.services;

import com.jayway.jsonpath.JsonPath;
import com.sen4ik.vfb.entities.Verse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Component
@Slf4j
public class BibleApiService {

    @Value("${bible.api.key}")
    private String apiKey;

    // Docs: https://scripture.api.bible/docs#all-bibles
    private String host = "api.scripture.api.bible";
    private String url = "https://" + host + "/v1";
    private String esvId = "f421fe261da7624f-01";
    private String nivId = "78a9f6124f344018-01";
    private String kjvId = "de4e12af7f28f599-01";

    public Verse getBibleVerse(String bookName, int chapterNumber, int verseFrom, Integer verseTo) throws IOException {

        log.info("book: " + bookName);
        log.info("chapter: " + chapterNumber);
        log.info("verse: " + verseFrom);

        // List<String> bibleIDs = Arrays.asList(esvId, nivId, kjvId);
        Map<String, String> bibles = new HashMap<String, String>();
        bibles.put("esv", esvId);
        bibles.put("niv", nivId);
        bibles.put("kjv", kjvId);

        // JSONObject responseObj = new JSONObject();

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
                String enBookAbbreviation = abbrObj.get(0).toString();
                String ruBookAbbr = getRusBookAbbr(enBookAbbreviation);

                // TODO: tweak the chapter and verse numbers for russian translation for the book of Psalms
                // We might need to do tweaks for other books
                // https://www.ph4.org/btraduk_ruennum.php
                // https://mybible.zone/ruennum-eng.php

                String enVerseLocation = "";
                String ruVerseLocation = "";
                if (verseTo == null || verseTo == verseFrom) {
                    // single verse
                    // set verse location for EN version
                    enVerseLocation = enBookAbbreviation + " " + chapterNumber + ":" + verseFrom;

                    // set verse location for RU version
                    if(!currentBookName.equals("Psalm") || !currentBookName.equals("Psalms")){
                        ruVerseLocation = ruBookAbbr + " " + chapterNumber + ":" + verseFrom;
                    }
                } else {
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
                String v = getVerse(currentBibleId, bookId, chapterNumber, verseFrom, client);
                if(v == null || v.isEmpty()){
                    return null;
                }
                verse = v;
            }
            else{
                // verse range
                for(int i = verseFrom; i <= verseTo; i++){
                    String v = getVerse(currentBibleId, bookId, chapterNumber, i, client);
                    if(v == null || v.isEmpty()){
                        return null;
                    }
                    if(i == verseFrom)
                    {
                        verse = v;
                    }
                    else{
                        verse = verse + " " + v;
                    }
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

        return verseResultObj;
    }

    private String getVerse(String currentBibleId,
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

    private String getRusBookAbbr(String enEsvBookAbbr){
        Map<String,String> bookAbbreviationsMap = new HashMap<String,String>();
        bookAbbreviationsMap.put("Gen.", "Быт.");
        bookAbbreviationsMap.put("Ex.", "Исх.");
        bookAbbreviationsMap.put("Lev.", "Лев.");
        bookAbbreviationsMap.put("Num.", "Чис.");
        bookAbbreviationsMap.put("Deut.", "Втор.");
        bookAbbreviationsMap.put("Josh.", "И. Нав.");
        bookAbbreviationsMap.put("Judg.", "Суд.");
        bookAbbreviationsMap.put("Ruth", "Руфь");
        bookAbbreviationsMap.put("1 Sam.", "1 Цар.");
        bookAbbreviationsMap.put("2 Sam.", "2 Цар.");
        bookAbbreviationsMap.put("1 Kgs.", "3 Цар.");
        bookAbbreviationsMap.put("2 Kgs.", "4 Цар.");
        bookAbbreviationsMap.put("1 Chr.", "1 Пар.");
        bookAbbreviationsMap.put("2 Chr.", "2 Пар.");
        bookAbbreviationsMap.put("Ezra", "Ездра");
        bookAbbreviationsMap.put("Neh.", "Неем.");
        bookAbbreviationsMap.put("Esth.", "Есф.");
        bookAbbreviationsMap.put("Job", "Иов");
        bookAbbreviationsMap.put("Ps.", "Пс.");
        bookAbbreviationsMap.put("Prov.", "Прит.");
        bookAbbreviationsMap.put("Eccles.", "Еккл.");
        bookAbbreviationsMap.put("Song", "П. Песн.");
        bookAbbreviationsMap.put("Isa.", "Ис.");
        bookAbbreviationsMap.put("Jer.", "Иер.");
        bookAbbreviationsMap.put("Lam.", "Пл. Иер.");
        bookAbbreviationsMap.put("Ezek.", "Иез.");
        bookAbbreviationsMap.put("Dan.", "Дан.");
        bookAbbreviationsMap.put("Hos.", "Ос.");
        bookAbbreviationsMap.put("Joel", "Иоиль");
        bookAbbreviationsMap.put("Amos", "Амос");
        bookAbbreviationsMap.put("Obad.", "Ав.");
        bookAbbreviationsMap.put("Jonah", "Иона");
        bookAbbreviationsMap.put("Mic.", "Мих.");
        bookAbbreviationsMap.put("Nah.", "Наум");
        bookAbbreviationsMap.put("Hab.", "Авв.");
        bookAbbreviationsMap.put("Zeph.", "Соф.");
        bookAbbreviationsMap.put("Hag.", "Аггей");
        bookAbbreviationsMap.put("Zech.", "Зах.");
        bookAbbreviationsMap.put("Mal.", "Мал.");
        bookAbbreviationsMap.put("Matt.", "Матф.");
        bookAbbreviationsMap.put("Mark", "Марк");
        bookAbbreviationsMap.put("Luke", "Луки");
        bookAbbreviationsMap.put("John", "Иоан.");
        bookAbbreviationsMap.put("Acts", "Деян.");
        bookAbbreviationsMap.put("Rom.", "Рим.");
        bookAbbreviationsMap.put("1 Cor.", "1 Кор.");
        bookAbbreviationsMap.put("2 Cor.", "2 Кор.");
        bookAbbreviationsMap.put("Gal.", "Гал.");
        bookAbbreviationsMap.put("Eph.", "Еф.");
        bookAbbreviationsMap.put("Phil.", "Филип.");
        bookAbbreviationsMap.put("Col.", "Кол.");
        bookAbbreviationsMap.put("1 Thess.", "1 Фесс.");
        bookAbbreviationsMap.put("2 Thess.", "2 Фесс.");
        bookAbbreviationsMap.put("1 Tim.", "1 Тим.");
        bookAbbreviationsMap.put("2 Tim.", "2 Тим.");
        bookAbbreviationsMap.put("Titus", "Титу");
        bookAbbreviationsMap.put("Philem.", "Филем.");
        bookAbbreviationsMap.put("Heb.", "Евр.");
        bookAbbreviationsMap.put("James", "Иак.");
        bookAbbreviationsMap.put("1 Pet.", "1 Пет.");
        bookAbbreviationsMap.put("2 Pet.", "2 Пет.");
        bookAbbreviationsMap.put("1 John", "1 Иоан.");
        bookAbbreviationsMap.put("2 John", "2 Иоан.");
        bookAbbreviationsMap.put("3 John", "3 Иоан.");
        bookAbbreviationsMap.put("Jude", "Иуда");
        bookAbbreviationsMap.put("Rev.", "Откр.");
        return bookAbbreviationsMap.get(enEsvBookAbbr);
    }

}
