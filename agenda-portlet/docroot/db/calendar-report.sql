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
-- Temporary table structure for view `calendar`
--

DROP TABLE IF EXISTS `calendar`;
/*!50001 DROP VIEW IF EXISTS `calendar`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `calendar` (
  `month` tinyint NOT NULL,
  `date` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `calendar`
--

/*!50001 DROP TABLE IF EXISTS `calendar`*/;
/*!50001 DROP VIEW IF EXISTS `calendar`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `calendar` AS select 'Janeiro' AS `month`,concat(year(curdate()),'-01-20') AS `date` union select 'Fevereiro' AS `month`,concat(year(curdate()),'-02-20') AS `date` union select 'Março' AS `month`,concat(year(curdate()),'-03-20') AS `date` union select 'Abril' AS `month`,concat(year(curdate()),'-04-20') AS `date` union select 'Maio' AS `month`,concat(year(curdate()),'-05-20') AS `date` union select 'Junho' AS `month`,concat(year(curdate()),'-06-20') AS `date` union select 'Julho' AS `month`,concat(year(curdate()),'-07-20') AS `date` union select 'Agosto' AS `month`,concat(year(curdate()),'-08-20') AS `date` union select 'Setembro' AS `month`,concat(year(curdate()),'-09-20') AS `date` union select 'Outubro' AS `month`,concat(year(curdate()),'-10-20') AS `date` union select 'Novembro' AS `month`,concat(year(curdate()),'-11-20') AS `date` union select 'Dezembro' AS `month`,concat(year(curdate()),'-12-20') AS `date` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-01-07 19:16:42
