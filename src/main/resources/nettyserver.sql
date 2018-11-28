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
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`status`,`pos`,`mp`,`hp`,`money`) values ('k','kk','1','1','8720','100000000','100000'),('z','zz','1','1','6230','9770000','1000000');

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

insert  into `userbag`(`id`,`name`,`wid`,`num`,`typeOf`,`durability`) values ('07b9f9e0-f0ce-49fd-a82a-f3a53e12766d','z',1003,5,'1',NULL),('7663daaa-7cad-48ee-a718-18f092e8285d','k',1003,5,'1',NULL),('a30f1af7-a107-423b-b9d1-e3fa55bedb47','z',1002,5,'1',NULL),('ad3d5ae2-0f83-4ce7-848c-613e9fc99c11','z',1001,10,'1',NULL),('c3b52008-6ada-4e61-921b-47757095417d','k',1001,10,'1',NULL),('eaa3da8f-183b-41b2-be1c-8544f1509eaa','z',3004,1,'3',10),('f36514b5-5601-44e1-ad21-deba2cb642e1','k',1002,5,'1',NULL);

/*Table structure for table `userskillrelation` */

DROP TABLE IF EXISTS `userskillrelation`;

CREATE TABLE `userskillrelation` (
  `id` int(11) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `skillId` int(11) DEFAULT NULL,
  `keypos` varchar(10) DEFAULT NULL,
  `skillCDS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userskillrelation` */

insert  into `userskillrelation`(`id`,`username`,`skillId`,`keypos`,`skillCDS`) values (3,'k',1,'1',1541733339783),(4,'k',2,'0',1541733339783),(5,'z',1,'2',1541733339783),(6,'z',2,'3',1541733339783),(7,'z',3,'1',1541733339783),(8,'k',3,'2',1541733339783);

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

insert  into `weaponequipmentbar`(`id`,`username`,`wid`,`durability`,`typeOf`) values (0,'z',3004,10,'3'),(1,'k',3004,10,'3');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
