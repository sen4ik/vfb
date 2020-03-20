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
    private String ruSynodalId;

    public Verse getBibleVerse(String bookName, int chapterNumber, int verseNumber) throws IOException {

        log.info("book: " + bookName);
        log.info("chapter: " + chapterNumber);
        log.info("verse: " + verseNumber);

        // List<String> bibleIDs = Arrays.asList(esvId, nivId, kjvId);
        Map<String, String> bibles = new HashMap<String, String>();
        bibles.put("esv", esvId);
        bibles.put("niv", nivId);
        bibles.put("kjv", kjvId);

        // JSONObject responseObj = new JSONObject();

        Verse verseResultObj = new Verse();

        OkHttpClient client = new OkHttpClient();

        for (Map.Entry<String, String> entry : bibles.entrySet()) {
            String currentBibleId = entry.getValue();
            String currentBibleAbr = entry.getKey();

            // get book ID
            Request booksRequest = new Request.Builder()
                    .url(url + "/bibles/" + currentBibleId + "/books")
                    .header("api-key", apiKey)
                    .build();

            Call call = client.newCall(booksRequest);
            Response response = call.execute();
            String booksJson = response.body().string();
            String bookNameField = "name";
            if(currentBibleId.equals(nivId)) bookNameField = "nameLong";
            net.minidev.json.JSONArray bookArr = JsonPath.parse(booksJson).read("$.data[?(@." + bookNameField + " == '" + bookName + "')].id");
            String bookId = bookArr.get(0).toString();
            if(currentBibleId.equals(esvId)){
                net.minidev.json.JSONArray abbrObj = JsonPath.parse(booksJson).read("$.data[?(@." + bookNameField + " == '" + bookName + "')].abbreviation");
                verseResultObj.setEnVerseLocation(abbrObj.get(0).toString() + " " + chapterNumber + ":" + verseNumber);
            }
            response.close();

            // get chapter ID
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host(host)
                    .addPathSegment("v1")
                    .addPathSegment("bibles")
                    .addPathSegment(currentBibleId)
                    .addPathSegment("verses")
                    .addPathSegment(bookId + "." + chapterNumber + "." + verseNumber)
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

            String verseJson = client.newCall(verseRequest).execute().body().string();

            String verse = JsonPath.parse(verseJson).read("$.data.content");
            verse = verse.replaceAll("\n", "").replaceAll("\r", "").trim();

            // responseObj.put(currentBibleAbr, verseStr);

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

        /*
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(responseObj.toString());
         */
    }

}
