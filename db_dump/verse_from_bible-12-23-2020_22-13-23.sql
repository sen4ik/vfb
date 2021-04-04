-- MySQL dump 10.13  Distrib 8.0.17, for macos10.14 (x86_64)
--
-- Host: localhost    Database: verse_from_bible
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `action_log`
--

DROP TABLE IF EXISTS `action_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `action_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `log_user_id` int(11) DEFAULT NULL,
  `log_verse_id` int(11) DEFAULT NULL,
  `log_message_body` longtext COLLATE utf8mb4_unicode_ci,
  `log_to` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `log_from` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `log_timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `log_action` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `log_notes` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `action_log`
--

LOCK TABLES `action_log` WRITE;
/*!40000 ALTER TABLE `action_log` DISABLE KEYS */;
INSERT INTO `action_log` VALUES (4,NULL,NULL,'asd','2017314557',NULL,'2020-03-27 13:32:09',NULL,NULL),(5,NULL,NULL,'asd',NULL,'2017314557','2020-03-27 13:32:09',NULL,NULL);
/*!40000 ALTER TABLE `action_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `bible_translation` varchar(100) DEFAULT NULL,
  `selected_send_time` double(3,1) DEFAULT '8.0',
  `selected_time_zone` varchar(45) DEFAULT NULL,
  `selected_send_time_pacific` double(3,1) DEFAULT NULL,
  `added_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `subscription_confirmed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (167,'John Green','1112223333','esv',8.0,'pacific',8.0,'2020-04-07 13:23:55',0),(168,'John Brown','1112223334','kjv',8.0,'pacific',8.0,'2020-04-07 13:24:34',1);
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `phone_number` varchar(50) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `role` varchar(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `last_modified_by` varchar(255) DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `confirmation_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1009 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'jwhite@test.com','$2a$10$llQRAdcYCeAulqJfH6kfaOUolX4uNkihDwcXlOBC9N8/ClYKM5dZS','John','White','7778889999',1,'ADMIN','1','2020-03-15 21:56:35','jwhite@test.com','2020-12-17 22:30:12','c5ea6e32-192f-410a-983f-8254b9e8b85a');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verses`
--

DROP TABLE IF EXISTS `verses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ru_synodal` text,
  `en_esv` text,
  `en_kjv` text,
  `en_niv` text,
  `ru_verse_location` varchar(45) DEFAULT NULL,
  `en_verse_location` varchar(45) DEFAULT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verses`
--

LOCK TABLES `verses` WRITE;
/*!40000 ALTER TABLE `verses` DISABLE KEYS */;
INSERT INTO `verses` VALUES (1,'А тем, которые приняли Его, верующим во имя Его, дал власть быть чадами Божиими.','But to all who did receive him, who believed in his name, he gave the right to become children of God.','But as many as received him, to them gave he power to become the sons of God, even to them that believe on his name.','Yet to all who did receive him, to those who believed in his name, he gave the right to become children of God.','Иоан. 1:12','John 1:12','2020-03-19'),(2,'Иисус сказал ему в ответ: истинно, истинно говорю тебе: если кто не родится свыше, не может увидеть Царствия Божия.','Jesus answered him, “Truly, truly, I say to you, unless one is born again he cannot see the kingdom of God.”','Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.','Jesus replied, “Very truly I tell you, no one can see the kingdom of God unless they are born again.','Иоан. 3:3','John 3:3','2020-03-20'),(23,'Будем любить Его, потому что Он прежде возлюбил нас.','We love because he first loved us.','We love him, because he first loved us.','We love because he first loved us.','1 Иоанна 4:19','1 John 4:19','2020-03-21'),(24,'А кто соблюдает слово Его, в том истинно любовь Божия совершилась: из сего узнаём, что мы в Нем.','but whoever keeps his word, in him truly the love of God is perfected. By this we may know that we are in him:','But whoso keepeth his word, in him verily is the love of God perfected: hereby know we that we are in him.','But if anyone obeys his word, love for God[a] is truly made complete in them. This is how we know we are in him:','1 Иоанна 2:5','1 John 2:5','2020-03-22'),(25,'И мир Божий, который превыше всякого ума, соблюдет сердца ваши и помышления ваши во Христе Иисусе.','And the peace of God, which surpasses all understanding, will guard your hearts and your minds in Christ Jesus.','And the peace of God, which passeth all understanding, shall keep your hearts and minds through Christ Jesus.','And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.','Фил. 4:7','Philippians 4:7','2020-03-23'),(26,'А все, что писано было прежде, написано нам в наставление, чтобы мы терпением и утешением из Писаний сохраняли надежду.','For whatever was written in former days was written for our instruction, that through endurance and through the encouragement of the Scriptures we might have hope.','For whatsoever things were written aforetime were written for our learning, that we through patience and comfort of the scriptures might have hope.','For everything that was written in the past was written to teach us, so that through the endurance taught in the Scriptures and the encouragement they provide we might have hope.','Римлянам 15:4','Romans 15:4','2020-03-24'),(27,'Бог же надежды да исполнит вас всякой радости и мира в вере, дабы вы, силою Духа Святаго, обогатились надеждою.','May the God of hope fill you with all joy and peace in believing, so that by the power of the Holy Spirit you may abound in hope.','Now the God of hope fill you with all joy and peace in believing, that ye may abound in hope, through the power of the Holy Ghost.','May the God of hope fill you with all joy and peace as you trust in him, so that you may overflow with hope by the power of the Holy Spirit.','Римлянам 15:13','Romans 15:13','2020-03-25'),(28,'Плод же духа: любовь, радость, мир, долготерпение, благость, милосердие, вера.','But the fruit of the Spirit is love, joy, peace, patience, kindness, goodness, faithfulness','But the fruit of the Spirit is love, joy, peace, longsuffering, gentleness, goodness, faith','But the fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness','Галатам 5:22','Galatians 5:22','2020-03-26'),(29,'Откровения Твои несомненно верны. Дому Твоему, Господи, принадлежит святость на долгие дни. ','How great are your works, O Lord! Your thoughts are very deep!','O Lord, how great are thy works! and thy thoughts are very deep.','How great are your works, Lord, how profound your thoughts!','Пс. 92:5','Psalm 92:5','2020-03-27'),(30,'Признавайтесь друг пред другом в проступках и молитесь друг за друга, чтобы исцелиться: много может усиленная молитва праведного.','Therefore, confess your sins to one another and pray for one another, that you may be healed. The prayer of a righteous person has great power as it is working.','Confess your faults one to another, and pray one for another, that ye may be healed. The effectual fervent prayer of a righteous man availeth much.','Therefore confess your sins to each other and pray for each other so that you may be healed. The prayer of a righteous person is powerful and effective.','Иак. 5:16','James 5:16','2020-03-28'),(31,'Но слово Господне пребывает вовек; а это есть то слово, которое вам проповедано.','but the word of the Lord remains forever.” And this word is the good news that was preached to you.','But the word of the Lord endureth for ever. And this is the word which by the gospel is preached unto you.','but the word of the Lord endures forever.” And this is the word that was preached to you.','1 Пет. 1:25','1 Peter 1:25','2020-03-29'),(32,'Вседержитель! мы не постигаем Его. Он велик силою, судом и полнотою правосудия. Он [никого] не угнетает.','The Almighty—we cannot find him; he is great in power; justice and abundant righteousness he will not violate.','Touching the Almighty, we cannot find him out: he is excellent in power, and in judgment, and in plenty of justice: he will not afflict.','The Almighty is beyond our reach and exalted in power; in his justice and great righteousness, he does not oppress.','Иов. 37:23','Job 37:23','2020-03-30'),(33,'Ищите Господа и силы Его, ищите лица Его всегда.','Seek the Lord and his strength; seek his presence continually!','Seek the Lord, and his strength: seek his face evermore.','Look to the Lord and his strength; seek his face always.','Псалом 104:4','Psalm 105:4','2020-03-31'),(34,'Побеждающему дам сесть со Мною на престоле Моем, как и Я победил и сел с Отцем Моим на престоле Его.','The one who conquers, I will grant him to sit with me on my throne, as I also conquered and sat down with my Father on his throne.','To him that overcometh will I grant to sit with me in my throne, even as I also overcame, and am set down with my Father in his throne.','To the one who is victorious, I will give the right to sit with me on my throne, just as I was victorious and sat down with my Father on his throne.','Откр. 3:21','Revelation 3:21','2020-04-01'),(35,'Не забывайте также благотворения и общительности, ибо таковые жертвы благоугодны Богу.','Do not neglect to do good and to share what you have, for such sacrifices are pleasing to God.','But to do good and to communicate forget not: for with such sacrifices God is well pleased.','And do not forget to do good and to share with others, for with such sacrifices God is pleased.','Евр. 13:16','Hebrews 13:16','2020-04-02'),(36,'Цель же увещания есть любовь от чистого сердца и доброй совести и нелицемерной веры.','The aim of our charge is love that issues from a pure heart and a good conscience and a sincere faith.','Now the end of the commandment is charity out of a pure heart, and of a good conscience, and of faith unfeigned:','The goal of this command is love, which comes from a pure heart and a good conscience and a sincere faith.','1 Тим. 1:5','1 Timothy 1:5','2020-04-03'),(37,'Сам же Господь мира да даст вам мир всегда во всем. Господь со всеми вами!','Now may the Lord of peace himself give you peace at all times in every way. The Lord be with you all.','Now the Lord of peace himself give you peace always by all means. The Lord be with you all.','Now may the Lord of peace himself give you peace at all times and in every way. The Lord be with all of you.','2 Фесс. 3:16','2 Thess. 3:16','2020-04-04'),(38,'Умоляем также вас, братия, вразумляйте бесчинных, утешайте малодушных, поддерживайте слабых, будьте долготерпеливы ко всем.','And we urge you, brothers, admonish the idle,[a] encourage the fainthearted, help the weak, be patient with them all.','Now we exhort you, brethren, warn them that are unruly, comfort the feebleminded, support the weak, be patient toward all men.','And we urge you, brothers and sisters, warn those who are idle and disruptive, encourage the disheartened, help the weak, be patient with everyone.','1 Фесс. 5:14','1 Thess. 5:14','2020-04-05'),(39,'Более же всего облекитесь в любовь, которая есть совокупность совершенства.','And above all these put on love, which binds everything together in perfect harmony.','And above all these things put on charity, which is the bond of perfectness.','And over all these virtues put on love, which binds them all together in perfect unity.','Кол. 3:14','Colossians 3:14','2020-04-06'),(40,'Ибо в вас должны быть те же чувствования, какие и во Христе Иисусе','Have this mind among yourselves, which is yours in Christ Jesus,','Let this mind be in you, which was also in Christ Jesus:','In your relationships with one another, have the same mindset as Christ Jesus:','Фил. 2:5','Philippians 2:5','2020-04-07'),(41,'Ибо не знавшего греха Он сделал для нас [жертвою за] грех, чтобы мы в Нем сделались праведными пред Богом.','For our sake he made him to be sin who knew no sin, so that in him we might become the righteousness of God.','For he hath made him to be sin for us, who knew no sin; that we might be made the righteousness of God in him.','God made him who had no sin to be sin[a] for us, so that in him we might become the righteousness of God.','2 Кор. 5:21','2 Cor 5:21','2020-04-08');
/*!40000 ALTER TABLE `verses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-23 22:13:24
