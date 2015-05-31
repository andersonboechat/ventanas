-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: localhost    Database: jcondo
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
-- Table structure for table `jco_administration`
--

DROP TABLE IF EXISTS `jco_administration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_administration` (
  `id` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_booking`
--

DROP TABLE IF EXISTS `jco_booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_booking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) NOT NULL,
  `personId` bigint(20) DEFAULT '0',
  `resourceId` bigint(20) NOT NULL,
  `beginDate` datetime NOT NULL,
  `endDate` datetime NOT NULL,
  `status` int(1) NOT NULL,
  `price` double DEFAULT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=365 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_booking_guest`
--

DROP TABLE IF EXISTS `jco_booking_guest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_booking_guest` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bookingId` bigint(20) NOT NULL,
  `personId` bigint(20) DEFAULT NULL,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
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
  `parentId` bigint(20) DEFAULT NULL,
  `relatedId` bigint(20) NOT NULL,
  `folderId` bigint(20) DEFAULT '0',
  `domain` int(1) DEFAULT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1332 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_event`
--

DROP TABLE IF EXISTS `jco_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `detail` varchar(200) DEFAULT NULL,
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
  `id` bigint(20) NOT NULL,
  `block` int(1) NOT NULL,
  `number` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `block_number_UNIQUE` (`block`,`number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_kinship`
--

DROP TABLE IF EXISTS `jco_kinship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_kinship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `personId` bigint(20) NOT NULL,
  `relativeId` bigint(20) NOT NULL,
  `type` int(2) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_membership`
--

DROP TABLE IF EXISTS `jco_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_membership` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainId` bigint(20) NOT NULL,
  `personId` bigint(20) NOT NULL,
  `type` int(2) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_occurrence`
--

DROP TABLE IF EXISTS `jco_occurrence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_occurrence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `personId` bigint(20) NOT NULL,
  `answerId` bigint(20) DEFAULT NULL,
  `code` varchar(15) NOT NULL,
  `text` varchar(1000) NOT NULL,
  `date` datetime NOT NULL,
  `type` int(1) NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_occurrence_answer`
--

DROP TABLE IF EXISTS `jco_occurrence_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_occurrence_answer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `personId` bigint(20) NOT NULL,
  `text` varchar(1000) NOT NULL,
  `date` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_parking`
--

DROP TABLE IF EXISTS `jco_parking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_parking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ownerDomainId` bigint(20) DEFAULT NULL,
  `renterDomainId` bigint(20) DEFAULT NULL,
  `vehicleId` bigint(20) DEFAULT NULL,
  `code` varchar(10) NOT NULL,
  `type` int(1) NOT NULL,
  `updateDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1491 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jco_passage_event`
--

DROP TABLE IF EXISTS `jco_passage_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_passage_event` (
  `id` bigint(20) NOT NULL,
  `type` int(1) NOT NULL,
  `personId` bigint(20) NOT NULL,
  `vehicleId` bigint(20) DEFAULT NULL,
  `domainId` bigint(20) DEFAULT NULL,
  `authorizerId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=274 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `jco_supplier`
--

DROP TABLE IF EXISTS `jco_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jco_supplier` (
  `id` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `status` int(2) NOT NULL DEFAULT '0',
  `identity` varchar(18) DEFAULT NULL,
  PRIMARY KEY (`id`)
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
  `type` int(1) NOT NULL,
  `license` varchar(15) NOT NULL,
  `updateDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateUser` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `license_UNIQUE` (`license`),
  KEY `domain_INDEX` (`domainId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
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

--
-- Dumping routines for database 'jcondo'
--
/*!50003 DROP PROCEDURE IF EXISTS `parkings_generate` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `parkings_generate`(amount INT)
BEGIN
	SET @i=0;
	insert into jcondo.jco_parking select 0, f.id, null, null, lpad(@i:=@i+1, 3, '0'), 0, current_timestamp, 10405 from jcondo.jco_flat f, jcondo.jco_parking_tmp p where f.number = p.number and f.block = p.block;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-05-31 20:25:47
