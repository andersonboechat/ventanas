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
-- Table structure for table `jco_access_log`
--

DROP TABLE IF EXISTS `jco_access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_access_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity` int(1) NOT NULL,
  `type` int(1) NOT NULL,
  `date` datetime NOT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_domain`
--

DROP TABLE IF EXISTS `jco_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `relatedId` varchar(45) NOT NULL,
  `domain` int(1) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_flat`
--

DROP TABLE IF EXISTS `jco_flat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_flat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) NOT NULL,
  `block` int(1) NOT NULL,
  `number` int(5) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `block_number_UNIQUE` (`block`,`number`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_membership`
--

DROP TABLE IF EXISTS `jco_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_membership` (
  `domainId` bigint(20) NOT NULL,
  `personId` bigint(20) NOT NULL,
  `type` int(2) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`domainId`,`personId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_parking`
--

DROP TABLE IF EXISTS `jco_parking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_parking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) NOT NULL,
  `code` varchar(10) NOT NULL,
  `type` int(1) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_parking_domain`
--

DROP TABLE IF EXISTS `jco_parking_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_parking_domain` (
  `id` bigint(20) NOT NULL,
  `renterDomainId` bigint(20) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`,`renterDomainId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_person`
--

DROP TABLE IF EXISTS `jco_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `identity` varchar(14) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `identity_UNIQUE` (`identity`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_person_flat`
--

DROP TABLE IF EXISTS `jco_person_flat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_person_flat` (
  `personId` bigint(20) NOT NULL,
  `flatId` bigint(20) NOT NULL,
  PRIMARY KEY (`personId`,`flatId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_vehicle`
--

DROP TABLE IF EXISTS `jco_vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) DEFAULT '0',
  `imageId` bigint(20) DEFAULT '0',
  `license` varchar(8) NOT NULL,
  `updateDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `license_UNIQUE` (`license`),
  KEY `domain_INDEX` (`domainId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_vehicle_access_log`
--

DROP TABLE IF EXISTS `jco_vehicle_access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_vehicle_access_log` (
  `id` bigint(20) NOT NULL,
  `vehicleId` bigint(20) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-02-12 16:32:49
