/*
SQLyog Ultimate v8.32 
MySQL - 5.5.23 : Database - nettyserver
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`nettyserver` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `nettyserver`;

/*Table structure for table `achievementprocess` */

DROP TABLE IF EXISTS `achievementprocess`;

CREATE TABLE `achievementprocess` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `ifFinish` int(10) DEFAULT NULL,
  `achievementId` int(11) DEFAULT NULL,
  `processs` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=896 DEFAULT CHARSET=utf8;

/*Data for the table `achievementprocess` */

insert  into `achievementprocess`(`id`,`username`,`ifFinish`,`achievementId`,`processs`,`type`) values (751,'f',0,1,'4:0',1),(752,'f',1,2,'6',2),(753,'f',0,3,'0',3),(754,'f',0,4,'0',4),(755,'f',0,5,'0',5),(756,'f',0,6,'0',6),(757,'f',0,7,'0',7),(758,'f',0,8,'0',8),(759,'f',0,9,'0',9),(760,'f',1,10,'3:2',1),(761,'f',1,11,'0',10),(762,'f',0,12,'10',11),(763,'f',0,13,'0',12),(764,'f',0,14,'0',13),(765,'f',1,15,'2',3),(766,'f',1,16,'1',3),(767,'k',0,1,'4:1',1),(768,'k',1,2,'6',2),(769,'k',0,3,'0',3),(770,'k',0,4,'0',4),(771,'k',0,5,'0',5),(772,'k',1,6,'1',6),(773,'k',1,7,'1',7),(774,'k',1,8,'1',8),(775,'k',0,9,'0',9),(776,'k',1,10,'3:2',1),(777,'k',1,11,'0',10),(778,'k',0,12,'10',11),(779,'k',0,13,'0',12),(780,'k',0,14,'0',13),(781,'k',1,15,'2',3),(782,'k',1,16,'1',3),(783,'q',0,1,'4:0',1),(784,'q',1,2,'6',2),(785,'q',0,3,'0',3),(786,'q',0,4,'0',4),(787,'q',0,5,'0',5),(788,'q',0,6,'0',6),(789,'q',0,7,'0',7),(790,'q',0,8,'0',8),(791,'q',0,9,'0',9),(792,'q',1,10,'3:2',1),(793,'q',1,11,'0',10),(794,'q',0,12,'10',11),(795,'q',0,13,'0',12),(796,'q',0,14,'0',13),(797,'q',1,15,'2',3),(798,'q',1,16,'1',3),(799,'z',1,1,'4:3',1),(800,'z',1,2,'6',2),(801,'z',1,3,'4',3),(802,'z',0,4,'0',4),(803,'z',0,5,'0',5),(804,'z',1,6,'1',6),(805,'z',0,7,'0',7),(806,'z',1,8,'1',8),(807,'z',0,9,'0',9),(808,'z',1,10,'3:2',1),(809,'z',1,11,'0',10),(810,'z',1,12,'10',11),(811,'z',1,13,'0',12),(812,'z',1,14,'0',13),(813,'z',1,15,'2',3),(814,'z',4,16,'1',3),(815,'e',0,1,'4:0',1),(816,'e',0,2,'1',2),(817,'e',0,3,'0',3),(818,'e',0,4,'0',4),(819,'e',0,5,'0',5),(820,'e',0,6,'0',6),(821,'e',0,7,'0',7),(822,'e',0,8,'0',8),(823,'e',0,9,'0',9),(824,'e',1,10,'3:2',1),(825,'e',1,11,'0',10),(826,'e',0,12,'10',11),(827,'e',0,13,'0',12),(828,'e',0,14,'0',13),(829,'e',0,15,'0',3),(830,'e',0,16,'0',3),(831,'i',0,1,'4:0',1),(832,'i',0,2,'1',2),(833,'i',0,3,'0',3),(834,'i',0,4,'0',4),(835,'i',0,5,'0',5),(836,'i',0,6,'0',6),(837,'i',0,7,'0',7),(838,'i',0,8,'0',8),(839,'i',0,9,'0',9),(840,'i',0,10,'3:0',1),(841,'i',0,11,'0',10),(842,'i',0,12,'0',11),(843,'i',0,13,'0',12),(844,'i',0,14,'0',13),(845,'i',0,15,'0',3),(846,'i',0,16,'0',3),(847,'m',0,1,'4:0',1),(848,'m',1,2,'6',2),(849,'m',0,3,'0',3),(850,'m',0,4,'0',4),(851,'m',0,5,'0',5),(852,'m',0,6,'0',6),(853,'m',1,7,'1',7),(854,'m',0,8,'0',8),(855,'m',0,9,'0',9),(856,'m',1,10,'3:2',1),(857,'m',1,11,'0',10),(858,'m',0,12,'10',11),(859,'m',0,13,'0',12),(860,'m',0,14,'0',13),(861,'m',1,15,'2',3),(862,'m',1,16,'1',3),(863,'o',0,1,'4:2',1),(864,'o',1,2,'5',2),(865,'o',0,3,'0',3),(866,'o',0,4,'0',4),(867,'o',0,5,'0',5),(868,'o',0,6,'0',6),(869,'o',1,7,'1',7),(870,'o',0,8,'0',8),(871,'o',0,9,'0',9),(872,'o',0,10,'3:0',1),(873,'o',1,11,'0',10),(874,'o',0,12,'0',11),(875,'o',0,13,'0',12),(876,'o',0,14,'0',13),(877,'o',1,15,'2',3),(878,'o',0,16,'0',3),(879,'t',0,1,'4:0',1),(880,'t',1,2,'6',2),(881,'t',0,3,'0',3),(882,'t',0,4,'0',4),(883,'t',0,5,'0',5),(884,'t',0,6,'0',6),(885,'t',0,7,'0',7),(886,'t',0,8,'0',8),(887,'t',0,9,'0',9),(888,'t',1,11,'0',10),(889,'t',0,12,'10',11),(890,'t',0,13,'0',12),(891,'t',0,14,'0',13),(892,'t',4,16,'1',3),(893,'t',4,10,'3:2',1),(895,'t',4,15,'2',3);

/*Table structure for table `applyunioninfo` */

DROP TABLE IF EXISTS `applyunioninfo`;

CREATE TABLE `applyunioninfo` (
  `applyId` varchar(50) NOT NULL,
  `applyUser` varchar(50) DEFAULT NULL,
  `unionId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`applyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `applyunioninfo` */

/*Table structure for table `friendapplyinfo` */

DROP TABLE IF EXISTS `friendapplyinfo`;

CREATE TABLE `friendapplyinfo` (
  `id` varchar(70) NOT NULL,
  `fromUser` varchar(40) DEFAULT NULL,
  `applyStatus` int(11) DEFAULT NULL,
  `toUser` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `friendapplyinfo` */

insert  into `friendapplyinfo`(`id`,`fromUser`,`applyStatus`,`toUser`) values ('7cabacf3-f224-4cb1-8c3b-6fe699fb3a4a','k',1,'z'),('9c7983d3-03d9-4663-a5ad-2b218804b28d','z',1,'k'),('d8bd5075-3880-461e-9c5a-9af6c1e105fa','z',0,'q');

/*Table structure for table `friendinfo` */

DROP TABLE IF EXISTS `friendinfo`;

CREATE TABLE `friendinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `friendname` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `friendinfo` */

/*Table structure for table `team` */

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
  `teamId` varchar(255) NOT NULL,
  `teamName` varchar(255) DEFAULT NULL,
  `leaderId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `team` */

/*Table structure for table `teamapplyinfo` */

DROP TABLE IF EXISTS `teamapplyinfo`;

CREATE TABLE `teamapplyinfo` (
  `id` varchar(50) NOT NULL,
  `teamId` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `teamapplyinfo` */

insert  into `teamapplyinfo`(`id`,`teamId`,`username`) values ('2f2015b1-3ac6-4952-8f78-5306914ef0ae','9921591d-0168-4e8d-8a8d-b21ad2bb83f0','k');

/*Table structure for table `unioninfo` */

DROP TABLE IF EXISTS `unioninfo`;

CREATE TABLE `unioninfo` (
  `unionId` varchar(50) NOT NULL,
  `unionName` varchar(50) DEFAULT NULL,
  `unionWarehourseId` varchar(50) DEFAULT NULL,
  `unionMoney` int(11) DEFAULT NULL,
  PRIMARY KEY (`unionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unioninfo` */

insert  into `unioninfo`(`unionId`,`unionName`,`unionWarehourseId`,`unionMoney`) values ('38227765-9f4d-45d4-b7fd-0af5713bc3ac','第二回','6114deb8-9189-491e-ab98-79598415f127',0),('51a82c34-a88f-45d6-894e-a97651094705','武术会','27b9315a-474c-4cb4-befe-68ea6780b65b',500);

/*Table structure for table `unionwarehouse` */

DROP TABLE IF EXISTS `unionwarehouse`;

CREATE TABLE `unionwarehouse` (
  `unionWarehouseId` varchar(50) DEFAULT NULL,
  `userbagId` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unionwarehouse` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` varchar(2) DEFAULT NULL,
  `pos` varchar(10) DEFAULT NULL,
  `mp` varchar(20) DEFAULT NULL,
  `hp` varchar(20) DEFAULT NULL,
  `money` varchar(10) DEFAULT NULL,
  `roleId` int(10) DEFAULT NULL,
  `experience` int(10) DEFAULT NULL,
  `unionId` varchar(50) DEFAULT NULL,
  `unionLevel` int(10) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`status`,`pos`,`mp`,`hp`,`money`,`roleId`,`experience`,`unionId`,`unionLevel`) values ('e','ee','1','0','2000','2000','7901',1,161,NULL,NULL),('f','ff','1','0','6600','6600','27140',2,8651,NULL,NULL),('i','ii','1','0','1000','1000','10000',1,1,NULL,NULL),('k','kk','1','3','7699','7699','19022',3,6926,NULL,NULL),('m','mm','1','0','6600','6600','11080',4,4651,NULL,NULL),('o','oo','1','3','11000','11000','10180',1,4461,'38227765-9f4d-45d4-b7fd-0af5713bc3ac',1),('q','qq','1','0','6600','6600','10290',4,3146,NULL,NULL),('t','tt','1','1','6000','6000','10030',1,516,NULL,NULL),('z','zz','1','3','11000','61000','41655',1,19640,'51a82c34-a88f-45d6-894e-a97651094705',1);

/*Table structure for table `userbag` */

DROP TABLE IF EXISTS `userbag`;

CREATE TABLE `userbag` (
  `id` varchar(50) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `wid` int(10) DEFAULT NULL,
  `num` int(10) DEFAULT NULL,
  `typeOf` varchar(10) DEFAULT NULL COMMENT '物品所属的种类',
  `durability` int(11) DEFAULT NULL,
  `startLevel` int(11) DEFAULT NULL COMMENT '星级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userbag` */

insert  into `userbag`(`id`,`name`,`wid`,`num`,`typeOf`,`durability`,`startLevel`) values ('13a4ef14-1a8c-4e55-8513-ea713e195433','q',90000,11,'4',NULL,NULL),('14b0ca8e-a2bd-443d-ac17-13c5bb775736','k',1001,1,'1',NULL,NULL),('176a8da9-8d43-4b46-abb7-72a9778e3abc','k',1500,2,'2',NULL,NULL),('1c21acbb-5541-4588-bcc3-e19913ba2d95','k',3011,1,'3',10,3),('1cdde24a-58e9-4688-9f31-05bf412594cb','m',1500,2,'2',NULL,NULL),('26716038-4b15-4eea-b4b1-30c1bb17b753','k',1003,6,'1',NULL,NULL),('28c2e34c-3aa7-40b1-8dd4-8a2b68af571f','o',1001,1,'1',NULL,NULL),('38021f81-bc6c-48ef-b8cc-63591c3ee13c','z',90000,9,'4',NULL,NULL),('392a85b8-8ecd-4974-b327-396a6284f528','m',90000,10,'4',NULL,NULL),('3d6573c7-1450-4b76-8bad-f9c2b4c9f473','z',1500,3,'2',NULL,NULL),('403d463f-3698-429d-a680-2dca887069eb','f',3011,1,'3',10,1),('540f0d5b-1984-4b93-b274-024dac3dee7b','k',1002,1,'1',NULL,NULL),('54ada531-c974-4643-890e-9dfa1ff5efa0',NULL,90000,3,'4',NULL,NULL),('71ad2a00-8a60-4464-8848-2d67b458d7b0','z',90001,6,'4',NULL,NULL),('7bd5ccdf-d320-4dd7-ae4e-464839f828c6','e',90000,1,'4',NULL,NULL),('7cf569b0-4820-4d8e-9b0f-e78cf7a495a1','z',3153,1,'3',0,3),('7d9e6a5e-6cde-45a1-aa1d-6bfed2db4ac7',NULL,90000,3,'4',NULL,NULL),('8584232b-325a-4d40-a2d2-b22ff804f842','z',3011,1,'3',100,1),('86e65b55-1b29-49c5-bbb6-9d4ce03f7b1f',NULL,90000,3,'4',NULL,NULL),('9666430e-e266-48fa-b2b9-0f482e67aecd','f',90001,6,'4',NULL,NULL),('9ca19e4e-4316-4cda-93ed-4cd544854a51','m',90000,3,'4',NULL,NULL),('a55e0195-7801-41ac-b7a8-192b02fae2a2','z',3011,1,'3',100,3),('b7953386-41ec-4cd9-a42a-a9192520cd31','k',3011,1,'3',10,1),('c36a88e9-b77e-4e1b-ab7f-9deef79ec06c','m',3011,1,'3',10,1),('d121f7a5-2e46-4476-9be1-c5994b4604f6','f',90000,11,'4',NULL,NULL),('da4ca880-1cbd-4482-9887-902fc43ba2da','k',90000,15,'4',NULL,NULL),('db44d212-09f1-4d84-91ba-65a73ecf5b88','o',90000,1,'4',NULL,NULL),('e63ca2a7-4c30-4eed-b4f1-5d4f5aad58a2','m',3153,1,'3',0,1),('e6c80beb-a1c3-44a3-83d2-0b150ea1bb3d','z',1003,1,'1',NULL,NULL),('f25bc92a-982e-4db6-b1cc-a515397f1349','k',3153,1,'3',0,3),('f83703ea-1345-4114-aed8-3896f14b0307','f',3153,1,'3',0,1);

/*Table structure for table `userskillrelation` */

DROP TABLE IF EXISTS `userskillrelation`;

CREATE TABLE `userskillrelation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `skillId` int(11) DEFAULT NULL,
  `keypos` varchar(10) DEFAULT NULL,
  `skillCDS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;

/*Data for the table `userskillrelation` */

insert  into `userskillrelation`(`id`,`username`,`skillId`,`keypos`,`skillCDS`) values (44,'f',4,'1',1545991483544),(45,'f',5,'2',1545991483550),(46,'f',10,'3',1545991483555),(47,'k',6,'1',1545998238984),(48,'k',7,'2',1545998238991),(49,'q',8,'1',1545998787260),(50,'q',9,'2',1545998787266),(51,'z',1,'1',1545999072527),(52,'z',2,'2',1545999072533),(53,'z',3,'3',1545999072536),(54,'e',1,'1',1546053819172),(55,'e',2,'2',1546053819178),(56,'e',3,'3',1546053819181),(57,'z',11,'4',1546053819181),(58,'e',11,'4',1546053819181),(59,'i',1,'1',1547192547500),(60,'i',2,'2',1547192547506),(61,'i',3,'3',1547192547510),(62,'i',11,'4',1547192547514),(63,'m',8,'1',1548149257933),(64,'m',9,'2',1548149257939),(65,'o',1,'1',1548676966367),(66,'o',2,'2',1548676966375),(67,'o',3,'3',1548676966379),(68,'o',11,'4',1548676966383),(69,'t',1,'1',1548904675294),(70,'t',2,'2',1548904675300),(71,'t',3,'3',1548904675304),(72,'t',11,'4',1548904675308);

/*Table structure for table `weaponequipmentbar` */

DROP TABLE IF EXISTS `weaponequipmentbar`;

CREATE TABLE `weaponequipmentbar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `wid` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `typeOf` varchar(10) DEFAULT NULL,
  `startlevel` int(11) DEFAULT NULL,
  `wpos` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8;

/*Data for the table `weaponequipmentbar` */

insert  into `weaponequipmentbar`(`id`,`username`,`wid`,`durability`,`typeOf`,`startlevel`,`wpos`) values (228,'z',3153,0,'3',1,2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
