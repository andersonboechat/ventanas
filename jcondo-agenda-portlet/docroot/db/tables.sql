-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ventanas
-- ------------------------------------------------------
-- Server version	5.6.22

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
-- Table structure for table `jco_booking`
--

DROP TABLE IF EXISTS `jco_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_booking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) NOT NULL,
  `personId` bigint(20) NOT NULL,
  `resourceId` bigint(20) NOT NULL,
  `beginDate` datetime NOT NULL,
  `endDate` datetime NOT NULL,
  `status` int(1) NOT NULL,
  `price` double DEFAULT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jco_booking`
--

LOCK TABLES `jco_booking` WRITE;
/*!40000 ALTER TABLE `jco_booking` DISABLE KEYS */;
INSERT INTO `jco_booking` VALUES (1,15,6,1,'2015-04-04 10:00:00','2015-04-04 22:00:00',2,0,'2015-04-02 11:35:01',10405),(3,5,6,4,'2015-04-17 10:00:00','2015-04-17 22:00:00',0,0,'2015-03-31 17:29:55',10405),(4,4,6,3,'2015-03-10 10:00:00','2015-03-10 12:00:00',0,0,'2015-04-01 11:25:21',10405),(5,10,6,5,'2015-04-24 10:00:00','2015-04-24 22:00:00',0,0,'2015-04-01 11:30:45',10405),(6,13,6,2,'2015-04-18 10:00:00','2015-04-18 22:00:00',0,0,'2015-04-01 13:09:06',10405);
/*!40000 ALTER TABLE `jco_booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jco_resource`
--

DROP TABLE IF EXISTS `jco_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `folderId` bigint(20) DEFAULT '0',
  `agreementId` bigint(20) DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `available` tinyint(1) NOT NULL DEFAULT '1',
  `price` double DEFAULT '0',
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jco_resource`
--

LOCK TABLES `jco_resource` WRITE;
/*!40000 ALTER TABLE `jco_resource` DISABLE KEYS */;
INSERT INTO `jco_resource` VALUES (1,17603,17904,'Churrasqueira sem forno','<span class=\"capacity\"><label>Capacidade:</label> 30 pessoas</span><span class=\"itens\"><label>Itens inclusos:</label> uma grelha super grill, uma grelha super grill (Asas), três espetos duplos, dois espetos simples, um puxador de carvão, um garfo trinchante, uma espátula, um pegador, um cesto de porta talheres, um freezer, uma lixeira grande, um cooler grande.</span>',1,78.8,'2015-03-25 12:30:00',10405),(2,0,0,'Churrasqueira com forno','Capacidade: 30 pessoas',1,78.8,'2015-03-25 12:30:00',10405),(3,0,0,'Espaço Cinema','Capacidade: 30 pessoas',1,39.4,'2015-03-25 12:30:00',10405),(4,0,0,'Espaço Gourmet','Capacidade: 30 pessoas',1,236.4,'2015-03-25 12:30:00',10405),(5,0,0,'Salão de Festa','Capacidade: 30 pessoas',1,236.4,'2015-03-25 12:30:00',10405),(6,0,0,'Espaço Som','Capacidade: 30 pessoas',0,236.4,'2015-03-25 12:30:00',10405);
/*!40000 ALTER TABLE `jco_resource` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-02 16:57:50
