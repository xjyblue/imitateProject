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
  `ifFinish` tinyint(1) DEFAULT NULL,
  `achievementId` int(11) DEFAULT NULL,
  `processs` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8;

/*Data for the table `achievementprocess` */

insert  into `achievementprocess`(`id`,`username`,`ifFinish`,`achievementId`,`processs`,`type`) values (77,'z',0,1,'4:0',1),(78,'z',1,2,'7',2),(79,'z',0,3,'0',3),(80,'z',0,4,'0',4),(81,'z',0,5,'0',5),(82,'z',0,6,'0',6),(83,'z',0,7,'0',7),(84,'z',1,8,'1',8),(85,'k',0,1,'4:0',1),(86,'k',0,2,'1',2),(87,'k',0,3,'0',3),(88,'k',0,4,'0',4),(89,'k',0,5,'0',5),(90,'k',0,6,'0',6),(91,'k',0,7,'0',7),(92,'k',1,8,'1',8);

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

insert  into `friendapplyinfo`(`id`,`fromUser`,`applyStatus`,`toUser`) values ('2035a0c2-974a-4929-919f-996d9ed1056e','z',1,'k');

/*Table structure for table `friendinfo` */

DROP TABLE IF EXISTS `friendinfo`;

CREATE TABLE `friendinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `friendname` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `friendinfo` */

insert  into `friendinfo`(`id`,`username`,`friendname`) values (9,'z','k'),(10,'k','z');

/*Table structure for table `unioninfo` */

DROP TABLE IF EXISTS `unioninfo`;

CREATE TABLE `unioninfo` (
  `unionId` varchar(50) NOT NULL,
  `unionName` varchar(50) DEFAULT NULL,
  `unionWarehourseId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`unionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unioninfo` */

insert  into `unioninfo`(`unionId`,`unionName`,`unionWarehourseId`) values ('388f708e-dfe0-445d-8af3-64a053783248','带带','42d7a98e-9b1f-451c-8c25-da56aea6155d');

/*Table structure for table `unionwarehouse` */

DROP TABLE IF EXISTS `unionwarehouse`;

CREATE TABLE `unionwarehouse` (
  `unionWarehouseId` varchar(50) DEFAULT NULL,
  `userbagId` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unionwarehouse` */

insert  into `unionwarehouse`(`unionWarehouseId`,`userbagId`) values ('42d7a98e-9b1f-451c-8c25-da56aea6155d','f36514b5-5601-44e1-ad21-deba2cb642e6'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','f36514b5-5601-44e1-ad21-deba2cb642e7'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','eaa3da8f-183b-41b2-be1c-8544f1509eaa'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','46f79697-a0b6-499e-a0b8-e0cbf1a552ea'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','8075baeb-0420-42e7-9667-f7481a180364'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','b7937a6b-02a2-47cd-82fb-591fc97b154e');

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

insert  into `user`(`username`,`password`,`status`,`pos`,`mp`,`hp`,`money`,`roleId`,`experience`,`unionId`,`unionLevel`) values ('e','ee','1','1','10000','0','141000000',3,301,NULL,NULL),('f','ff','1','0','10000','10000','10000',2,1,NULL,NULL),('k','kk','1','1','8890','10000','100000',1,9,'388f708e-dfe0-445d-8af3-64a053783248',2),('q','qq','1','0','10000','10000','1000000',4,1,'388f708e-dfe0-445d-8af3-64a053783248',3),('w','ww','1','1','450','54500','3210000',1,101,NULL,NULL),('z','zz','1','1','3260','547771900','147200000',1,4561,'388f708e-dfe0-445d-8af3-64a053783248',1);

/*Table structure for table `userbag` */

DROP TABLE IF EXISTS `userbag`;

CREATE TABLE `userbag` (
  `id` varchar(50) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `wid` int(10) DEFAULT NULL,
  `num` int(10) DEFAULT NULL,
  `typeOf` varchar(10) DEFAULT NULL COMMENT '物品所属的种类',
  `durability` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userbag` */

insert  into `userbag`(`id`,`name`,`wid`,`num`,`typeOf`,`durability`) values ('07b9f9e0-f0ce-49fd-a82a-f3a53e12766d',NULL,1003,4,'1',NULL),('46f79697-a0b6-499e-a0b8-e0cbf1a552ea',NULL,3005,1,'3',10),('7663daaa-7cad-48ee-a718-18f092e8285d','k',1003,8,'1',NULL),('8075baeb-0420-42e7-9667-f7481a180364',NULL,3005,1,'3',10),('9098a09b-6c16-4246-96d7-6de222a8de55','z',3006,1,'3',20),('a30f1af7-a107-423b-b9d1-e3fa55bedb47','z',1002,17,'1',NULL),('ad3d5ae2-0f83-4ce7-848c-613e9fc99c11',NULL,1001,12,'1',NULL),('b7937a6b-02a2-47cd-82fb-591fc97b154e',NULL,1003,5,'1',NULL),('c3b52008-6ada-4e61-921b-47757095417d','k',1001,16,'1',NULL),('e158e742-f636-4553-8c0d-0dbaf54462c0','k',3006,1,'3',20),('eaa3da8f-183b-41b2-be1c-8544f1509eaa',NULL,3004,1,'3',10),('f36514b5-5601-44e1-ad21-deba2cb642e1',NULL,1002,2,'1',NULL),('f36514b5-5601-44e1-ad21-deba2cb642e2','w',1002,10,'1',NULL),('f36514b5-5601-44e1-ad21-deba2cb642e3','w',1001,10,'1',NULL),('f36514b5-5601-44e1-ad21-deba2cb642e5','w',1003,10,'1',NULL),('f36514b5-5601-44e1-ad21-deba2cb642e6',NULL,1001,96,'1',NULL),('f36514b5-5601-44e1-ad21-deba2cb642e7',NULL,1002,101,'1',NULL),('fca80ddb-840a-4167-b8ad-b194df39815f','z',1001,4,'1',NULL);

/*Table structure for table `userskillrelation` */

DROP TABLE IF EXISTS `userskillrelation`;

CREATE TABLE `userskillrelation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `skillId` int(11) DEFAULT NULL,
  `keypos` varchar(10) DEFAULT NULL,
  `skillCDS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

/*Data for the table `userskillrelation` */

insert  into `userskillrelation`(`id`,`username`,`skillId`,`keypos`,`skillCDS`) values (3,'k',1,'1',1541733339783),(4,'k',2,'3',1541733339783),(5,'z',1,'2',1541733339783),(6,'z',2,'3',1541733339783),(7,'z',3,'1',1541733339783),(8,'k',3,'2',1541733339783),(9,'w',1,'2',1541733339783),(10,'w',2,'3',1541733339783),(11,'w',3,'1',1541733339783),(12,'f',4,'1',1543994302073),(13,'f',5,'2',1543994302084),(14,'e',6,'1',1544061686017),(15,'e',7,'2',1544061686024),(16,'q',8,'1',1544097693866),(17,'q',9,'2',1544097693872);

/*Table structure for table `weaponequipmentbar` */

DROP TABLE IF EXISTS `weaponequipmentbar`;

CREATE TABLE `weaponequipmentbar` (
  `id` int(11) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `wid` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `typeOf` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `weaponequipmentbar` */

insert  into `weaponequipmentbar`(`id`,`username`,`wid`,`durability`,`typeOf`) values (0,'z',3004,10,'3'),(1,'k',3004,10,'3'),(2,'w',3005,10,'3');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
