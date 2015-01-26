CREATE DATABASE  IF NOT EXISTS `ventanas` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `ventanas`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: ventanas
-- ------------------------------------------------------
-- Server version	5.6.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `rb_room`
--

DROP TABLE IF EXISTS `rb_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rb_room` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imageFolderId` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `detail` varchar(1000) DEFAULT NULL,
  `agreement` varchar(200) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT '1',
  `price` double DEFAULT NULL,
  `agentId` bigint(20) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rb_room`
--

LOCK TABLES `rb_room` WRITE;
/*!40000 ALTER TABLE `rb_room` DISABLE KEYS */;
INSERT INTO `rb_room` VALUES (1,11620,'Churrasqueira sem forno','30 pessoas','/documents/10179/16746/Termo+de+Responsabilidade+-+Churrasqueira?targetExtension=pdf',1,78.8,10195,'2015-01-03 20:18:33','2013-04-21 06:00:00'),(2,11622,'Churrasqueira com forno','','/documents/10179/14205/Termo+de+Responsabilidade+-+Churrasqueira+Gourmet?targetExtension=pdf',1,72.4,10195,'2015-01-03 22:25:41','2013-04-21 06:00:00'),(3,11616,'Espaço Cinema','','/documents/10179/14205/Termo+de+Responsabilidade+-+Espaco+Cinema?targetExtension=pdf',1,38,10195,'2015-01-01 21:37:01','2013-04-21 06:00:00'),(4,11614,'Espaço Gourmet','','/documents/10179/14205/Termo+de+Responsabilidade+-+Espaco+Gourmet?targetExtension=pdf',1,114,10195,'2015-01-01 21:37:01','2013-04-21 06:00:00'),(5,11618,'Salão de Festa','','/documents/10179/14205/Termo+de+Responsabilidade+-+Espaco+Infantil?targetExtension=pdf',1,114,10195,'2015-01-01 21:37:01','2013-04-21 06:00:00'),(6,0,'Espaço Som',NULL,'/documents/10179/14205/Termo+de+Responsabilidade+-+Espaco+Som.docx?targetExtension=pdf',1,56,10195,'2015-01-01 21:37:01','2014-12-22 12:41:31');
/*!40000 ALTER TABLE `rb_room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-01-07 19:13:22
