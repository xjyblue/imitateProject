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
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`status`,`pos`,`mp`,`hp`) values ('a','aa','1','1','10000','100000000'),('k','kk','1','2','10000','100000000'),('p','pp','1','1','10000','100000000'),('w','ww','1','0','10000','100000000'),('z','zz','1','1','10000','100000000');

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

insert  into `userskillrelation`(`id`,`username`,`skillId`,`keypos`,`skillCDS`) values (1,'a',1,'1',1541733339783),(2,'a',2,'0',1541733339783),(3,'k',1,'1',1541733339783),(4,'k',2,'0',1541733339783),(5,'z',1,'2',1541733339783),(6,'z',2,'3',1541733339783),(7,'w',1,'1',1541733339783),(8,'w',2,'0',1541733339783),(9,'p',1,'1',1541733339783),(10,'p',2,'0',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
