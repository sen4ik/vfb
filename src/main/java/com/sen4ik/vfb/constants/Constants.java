package com.sen4ik.vfb.constants;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public static final String WEBSITE_NAME = "www.VerseFromBible.com";
    public static final String SCHEMA = "verse_from_bible";
    public static final String reCaptchaFailedMessage = "reCaptcha failed on you! Are you a robot?";
    public static final String generalMessage = "Hi, I am " + WEBSITE_NAME + " bot. If you want to subscribe - go to www." + WEBSITE_NAME + ". If you want to stop your subscription - reply STOP";
    public static final String confirmSubscriptionMessage = "It looks like you have subscribed to " + WEBSITE_NAME + ". If that is correct, reply YES.";
    public static Map<String, List<String>> bookAbbrsAndDatFiles = new HashMap<>();

    {
        bookAbbrsAndDatFiles.put("Gen.",   Lists.newArrayList("Быт.", "01-genesis.dat"));
        bookAbbrsAndDatFiles.put("Ex.",    Lists.newArrayList("Исх.", "02-exodus.dat"));
        bookAbbrsAndDatFiles.put("Lev.",   Lists.newArrayList("Лев.", "03-leviticus.dat"));
        bookAbbrsAndDatFiles.put("Num.",   Lists.newArrayList("Чис.", "04-numbers.dat"));
        bookAbbrsAndDatFiles.put("Deut.",  Lists.newArrayList("Втор.", "05-deuteronomy.dat"));
        bookAbbrsAndDatFiles.put("Josh.",  Lists.newArrayList("И. Нав.", "06-joshua.dat"));
        bookAbbrsAndDatFiles.put("Judg.",  Lists.newArrayList("Суд.", "07-judges.dat"));
        bookAbbrsAndDatFiles.put("Ruth",   Lists.newArrayList("Руфь", "08-ruth.dat"));
        bookAbbrsAndDatFiles.put("1 Sam.", Lists.newArrayList("1 Цар.", "09-1samuel.dat"));
        bookAbbrsAndDatFiles.put("2 Sam.", Lists.newArrayList("2 Цар.", "10-2samuel.dat"));
        bookAbbrsAndDatFiles.put("1 Kgs.", Lists.newArrayList("3 Цар.", "11-1kings.dat"));
        bookAbbrsAndDatFiles.put("2 Kgs.", Lists.newArrayList("4 Цар.", "12-2kings.dat"));
        bookAbbrsAndDatFiles.put("1 Chr.", Lists.newArrayList("1 Пар.", "13-1chronicles.dat"));
        bookAbbrsAndDatFiles.put("2 Chr.", Lists.newArrayList("2 Пар.", "14-2chronicles.dat"));
        bookAbbrsAndDatFiles.put("Ezra",   Lists.newArrayList("Ездра", "16-ezra.dat"));
        bookAbbrsAndDatFiles.put("Neh.",   Lists.newArrayList("Неем.", "17-nehemiah.dat"));
        bookAbbrsAndDatFiles.put("Esth.",  Lists.newArrayList("Есф.", "21-esther.dat"));
        bookAbbrsAndDatFiles.put("Job",    Lists.newArrayList("Иов", "22-job.dat"));
        bookAbbrsAndDatFiles.put("Ps.",    Lists.newArrayList("Пс.", "23-psalms.dat"));
        bookAbbrsAndDatFiles.put("Prov.",  Lists.newArrayList("Прит.", "24-proverbs.dat"));
        bookAbbrsAndDatFiles.put("Eccles.", Lists.newArrayList("Еккл.", "25-ecclesiastes.dat"));
        bookAbbrsAndDatFiles.put("Song",   Lists.newArrayList("П. Песн.", "26-songofsolomon.dat"));
        bookAbbrsAndDatFiles.put("Isa.",   Lists.newArrayList("Ис.", "29-isaiah.dat"));
        bookAbbrsAndDatFiles.put("Jer.",   Lists.newArrayList("Иер.", "30-jeremiah.dat"));
        bookAbbrsAndDatFiles.put("Lam.",   Lists.newArrayList("Пл. Иер.", "31-lamentations.dat"));
        bookAbbrsAndDatFiles.put("Ezek.",  Lists.newArrayList("Иез.", "34-ezekiel.dat"));
        bookAbbrsAndDatFiles.put("Dan.",   Lists.newArrayList("Дан.", "35-daniel.dat"));
        bookAbbrsAndDatFiles.put("Hos.",   Lists.newArrayList("Ос.", "36-hosea.dat"));
        bookAbbrsAndDatFiles.put("Joel",   Lists.newArrayList("Иоиль", "37-joel.dat"));
        bookAbbrsAndDatFiles.put("Amos",   Lists.newArrayList("Амос", "38-amos.dat"));
        bookAbbrsAndDatFiles.put("Obad.",  Lists.newArrayList("Ав.", "39-obadiah.dat"));
        bookAbbrsAndDatFiles.put("Jonah",  Lists.newArrayList("Иона", "40-jonah.dat"));
        bookAbbrsAndDatFiles.put("Mic.",   Lists.newArrayList("Мих.", "41-micah.dat"));
        bookAbbrsAndDatFiles.put("Nah.",   Lists.newArrayList("Наум", "42-nahum.dat"));
        bookAbbrsAndDatFiles.put("Hab.",   Lists.newArrayList("Авв.", "43-habakkuk.dat"));
        bookAbbrsAndDatFiles.put("Zeph.",  Lists.newArrayList("Соф.", "44-zephaniah.dat"));
        bookAbbrsAndDatFiles.put("Hag.",   Lists.newArrayList("Аггей", "45-haggai.dat"));
        bookAbbrsAndDatFiles.put("Zech.",  Lists.newArrayList("Зах.", "46-zechariah.dat"));
        bookAbbrsAndDatFiles.put("Mal.",   Lists.newArrayList("Мал.", "47-malachi.dat"));
        bookAbbrsAndDatFiles.put("Matt.",  Lists.newArrayList("Матф.", "52-matthew.dat"));
        bookAbbrsAndDatFiles.put("Mark",   Lists.newArrayList("Марк", "53-mark.dat"));
        bookAbbrsAndDatFiles.put("Luke",   Lists.newArrayList("Луки", "54-luke.dat"));
        bookAbbrsAndDatFiles.put("John",   Lists.newArrayList("Иоан.", "55-john.dat"));
        bookAbbrsAndDatFiles.put("Acts",   Lists.newArrayList("Деян.", "56-acts.dat"));
        bookAbbrsAndDatFiles.put("Rom.",   Lists.newArrayList("Рим.", "64-romans.dat"));
        bookAbbrsAndDatFiles.put("1 Cor.", Lists.newArrayList("1 Кор.", "65-1corinthians.dat"));
        bookAbbrsAndDatFiles.put("2 Cor.", Lists.newArrayList("2 Кор.", "66-2corinthians.dat"));
        bookAbbrsAndDatFiles.put("Gal.",   Lists.newArrayList("Гал.", "67-galatians.dat"));
        bookAbbrsAndDatFiles.put("Eph.",   Lists.newArrayList("Еф.", "68-ephesians.dat"));
        bookAbbrsAndDatFiles.put("Phil.",  Lists.newArrayList("Филип.", "69-philippians.dat"));
        bookAbbrsAndDatFiles.put("Col.",   Lists.newArrayList("Кол.", "70-colossians.dat"));
        bookAbbrsAndDatFiles.put("1 Thess.", Lists.newArrayList("1 Фесс.", "71-1thessalonians.dat"));
        bookAbbrsAndDatFiles.put("2 Thess.", Lists.newArrayList("2 Фесс.", "72-2thessalonians.dat"));
        bookAbbrsAndDatFiles.put("1 Tim.", Lists.newArrayList("1 Тим.", "73-1timothy.dat"));
        bookAbbrsAndDatFiles.put("2 Tim.", Lists.newArrayList("2 Тим.", "74-2timothy.dat"));
        bookAbbrsAndDatFiles.put("Titus",  Lists.newArrayList("Титу", "75-titus.dat"));
        bookAbbrsAndDatFiles.put("Philem.", Lists.newArrayList("Филем.", "76-philemon.dat"));
        bookAbbrsAndDatFiles.put("Heb.",   Lists.newArrayList("Евр.", "77-hebrews.dat"));
        bookAbbrsAndDatFiles.put("James",  Lists.newArrayList("Иак.", "57-james.dat"));
        bookAbbrsAndDatFiles.put("1 Pet.", Lists.newArrayList("1 Пет.", "58-1peter.dat"));
        bookAbbrsAndDatFiles.put("2 Pet.", Lists.newArrayList("2 Пет.", "59-2peter.dat"));
        bookAbbrsAndDatFiles.put("1 John", Lists.newArrayList("1 Иоан.", "60-1john.dat"));
        bookAbbrsAndDatFiles.put("2 John", Lists.newArrayList("2 Иоан.", "61-2john.dat"));
        bookAbbrsAndDatFiles.put("3 John", Lists.newArrayList("3 Иоан.", "62-3john.dat"));
        bookAbbrsAndDatFiles.put("Jude",   Lists.newArrayList("Иуда", "63-jude.dat"));
        bookAbbrsAndDatFiles.put("Rev.",   Lists.newArrayList("Откр.", "78-revelation.dat"));
    }
}
