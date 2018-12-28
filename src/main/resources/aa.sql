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
) ENGINE=InnoDB AUTO_INCREMENT=628 DEFAULT CHARSET=utf8;

/*Data for the table `achievementprocess` */

insert  into `achievementprocess`(`id`,`username`,`ifFinish`,`achievementId`,`processs`,`type`) values (530,'z',1,1,'4:3',1),(531,'z',1,2,'7',2),(532,'z',0,3,'0',3),(533,'z',0,4,'3006',4),(534,'z',0,5,'0',5),(535,'z',1,6,'1',6),(536,'z',0,7,'0',7),(537,'z',1,8,'1',8),(538,'z',1,9,'100000',9),(539,'z',1,10,'3:2',1),(540,'z',0,11,'0',10),(541,'z',1,12,'1',11),(542,'z',0,13,'0',12),(543,'z',0,14,'0',13),(544,'e',0,1,'4:0',1),(545,'e',1,2,'7',2),(546,'e',0,3,'0',3),(547,'e',0,4,'0',4),(548,'e',0,5,'0',5),(549,'e',0,6,'0',6),(550,'e',0,7,'0',7),(551,'e',0,8,'0',8),(552,'e',0,9,'0',9),(553,'e',0,10,'3:0',1),(554,'e',0,11,'0',10),(555,'e',0,12,'0',11),(556,'e',0,13,'0',12),(557,'e',0,14,'0',13),(558,'f',0,1,'4:0',1),(559,'f',1,2,'7',2),(560,'f',0,3,'0',3),(561,'f',0,4,'0',4),(562,'f',0,5,'0',5),(563,'f',0,6,'0',6),(564,'f',0,7,'0',7),(565,'f',0,8,'0',8),(566,'f',0,9,'0',9),(567,'f',0,10,'3:0',1),(568,'f',0,11,'0',10),(569,'f',0,12,'0',11),(570,'f',0,13,'0',12),(571,'f',0,14,'0',13),(572,'q',0,1,'4:0',1),(573,'q',1,2,'7',2),(574,'q',0,3,'0',3),(575,'q',0,4,'0',4),(576,'q',0,5,'0',5),(577,'q',0,6,'0',6),(578,'q',0,7,'0',7),(579,'q',0,8,'0',8),(580,'q',0,9,'0',9),(581,'q',0,10,'3:0',1),(582,'q',0,11,'0',10),(583,'q',0,12,'0',11),(584,'q',0,13,'0',12),(585,'q',0,14,'0',13),(586,'w',0,1,'4:0',1),(587,'w',1,2,'5',2),(588,'w',0,3,'0',3),(589,'w',0,4,'0',4),(590,'w',0,5,'0',5),(591,'w',0,6,'0',6),(592,'w',0,7,'0',7),(593,'w',0,8,'0',8),(594,'w',0,9,'0',9),(595,'w',0,10,'3:0',1),(596,'w',0,11,'0',10),(597,'w',0,12,'0',11),(598,'w',0,13,'0',12),(599,'w',0,14,'0',13),(600,'k',0,1,'4:0',1),(601,'k',0,2,'3',2),(602,'k',0,3,'0',3),(603,'k',0,4,'0',4),(604,'k',0,5,'0',5),(605,'k',0,6,'0',6),(606,'k',1,7,'1',7),(607,'k',1,8,'1',8),(608,'k',1,9,'100000',9),(609,'k',0,10,'3:0',1),(610,'k',1,11,'0',10),(611,'k',0,12,'0',11),(612,'k',0,13,'0',12),(613,'k',0,14,'0',13),(614,'y',1,1,'4:3',1),(615,'y',1,2,'5',2),(616,'y',1,3,'4',3),(617,'y',1,4,'3006-3008-3009-3010',4),(618,'y',0,5,'0',5),(619,'y',1,6,'1',6),(620,'y',1,7,'1',7),(621,'y',1,8,'1',8),(622,'y',1,9,'100000',9),(623,'y',1,10,'3:2',1),(624,'y',1,11,'0',10),(625,'y',1,12,'1',11),(626,'y',1,13,'0',12),(627,'y',1,14,'0',13);

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

insert  into `friendapplyinfo`(`id`,`fromUser`,`applyStatus`,`toUser`) values ('11032e2b-ac1d-45b1-8aef-83300172caf3','y',1,'z'),('2035a0c2-974a-4929-919f-996d9ed1056e','z',1,'k'),('713aea89-39f7-41c6-a1e0-28897f4c3888','z',0,'k');

/*Table structure for table `friendinfo` */

DROP TABLE IF EXISTS `friendinfo`;

CREATE TABLE `friendinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `friendname` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

/*Data for the table `friendinfo` */

insert  into `friendinfo`(`id`,`username`,`friendname`) values (9,'z','k'),(10,'k','z'),(11,'y','z'),(12,'z','y');

/*Table structure for table `teamapplyinfo` */

DROP TABLE IF EXISTS `teamapplyinfo`;

CREATE TABLE `teamapplyinfo` (
  `id` varchar(50) NOT NULL,
  `teamId` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `teamapplyinfo` */

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

insert  into `unioninfo`(`unionId`,`unionName`,`unionWarehourseId`,`unionMoney`) values ('388f708e-dfe0-445d-8af3-64a053783248','带带','42d7a98e-9b1f-451c-8c25-da56aea6155d',2000100),('e02fbccb-8900-41a5-a25f-1470c822d789','世界第二回','a574c630-4706-4be8-8480-9f893aa01562',0);

/*Table structure for table `unionwarehouse` */

DROP TABLE IF EXISTS `unionwarehouse`;

CREATE TABLE `unionwarehouse` (
  `unionWarehouseId` varchar(50) DEFAULT NULL,
  `userbagId` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `unionwarehouse` */

insert  into `unionwarehouse`(`unionWarehouseId`,`userbagId`) values ('42d7a98e-9b1f-451c-8c25-da56aea6155d','8340638c-7293-4248-b2d5-1f2a64c46dd2'),('42d7a98e-9b1f-451c-8c25-da56aea6155d','031d8227-841b-4017-9044-18bf60dcf21f');

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

insert  into `user`(`username`,`password`,`status`,`pos`,`mp`,`hp`,`money`,`roleId`,`experience`,`unionId`,`unionLevel`) values ('e','ee','1','0','8690','586250','418450600',3,6601,NULL,NULL),('f','ff','1','0','10000','700000','40100000',2,901,NULL,NULL),('k','kk','1','1','8630','9800','119998',1,59,NULL,NULL),('q','qq','1','0','10000','700000','60949900',4,901,'388f708e-dfe0-445d-8af3-64a053783248',2),('w','ww','1','1','4500','54500','3210000',1,101,NULL,NULL),('y','yy','1','1','8640','26400','22757200',1,551,'388f708e-dfe0-445d-8af3-64a053783248',4),('z','zz','1','0','1450','499170','533503102',1,19161,'388f708e-dfe0-445d-8af3-64a053783248',1);

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

insert  into `userbag`(`id`,`name`,`wid`,`num`,`typeOf`,`durability`,`startLevel`) values ('031d8227-841b-4017-9044-18bf60dcf21f',NULL,1001,1,'1',NULL,NULL),('14b1c8e7-41f0-47a4-b81a-0773c2c5688b','k',1500,2,'2',NULL,NULL),('1fa24fa3-21f5-49a2-9733-b10faf83bd16','y',3009,1,'3',50,1),('24c417dd-4aed-4d48-bea6-6adebc202543','e',3007,1,'3',10,1),('2a053d10-ec48-4b21-bae7-8f9825d4e75c','y',3006,1,'3',20,2),('314eb818-0fd6-49e7-9482-a6a61c048ecb','y',3008,1,'3',50,1),('3de18db9-021b-45a6-a9d1-8fdce4e96380','e',1500,2,'2',0,1),('483ef3f6-4f4b-4466-9555-30bdb8402a59','e',3100,1,'3',0,4),('62fdec11-7f6a-46f5-9764-2c50b4bd6ec1',NULL,1001,5,'1',NULL,NULL),('8340638c-7293-4248-b2d5-1f2a64c46dd2',NULL,3006,1,'3',20,7),('9007418b-9bb4-489d-8a7e-cc3a59298b2e','y',3010,1,'3',50,4),('aa72866b-43ac-4baf-be9b-a566539eb8d8','z',3007,1,'3',10,3),('b0a25d3c-889f-45eb-bd7f-8f0dc34e0f57','z',1001,16,'1',NULL,NULL),('d5df41af-8f3b-403e-8960-5fe38091cd1d','z',1501,1,'2',NULL,NULL),('ebaf1306-bd04-48bb-8ead-d575a94e6df3','y',1001,1,'1',NULL,NULL),('ee4797bc-df79-484d-b06d-a9065964b235','z',1500,10,'2',0,1),('fbc4e909-3dcd-4501-81c0-325f027b4ec6','z',3006,1,'3',20,1);

/*Table structure for table `userskillrelation` */

DROP TABLE IF EXISTS `userskillrelation`;

CREATE TABLE `userskillrelation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `skillId` int(11) DEFAULT NULL,
  `keypos` varchar(10) DEFAULT NULL,
  `skillCDS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

/*Data for the table `userskillrelation` */

insert  into `userskillrelation`(`id`,`username`,`skillId`,`keypos`,`skillCDS`) values (3,'k',1,'1',1541733339783),(4,'k',2,'3',1541733339783),(5,'z',1,'2',1541733339783),(6,'z',2,'3',1541733339783),(7,'z',3,'1',1541733339783),(8,'k',3,'2',1541733339783),(9,'w',1,'2',1541733339783),(10,'w',2,'3',1541733339783),(11,'w',3,'1',1541733339783),(12,'f',4,'1',1543994302073),(13,'f',5,'2',1543994302084),(14,'e',6,'1',1544061686017),(15,'e',7,'2',1544061686024),(16,'q',8,'1',1544097693866),(17,'q',9,'2',1544097693872),(18,'y',1,'1',1545305938566),(19,'y',2,'2',1545305938572),(20,'y',3,'3',1545305938576);

/*Table structure for table `weaponequipmentbar` */

DROP TABLE IF EXISTS `weaponequipmentbar`;

CREATE TABLE `weaponequipmentbar` (
  `id` int(11) NOT NULL,
  `username` varchar(20) DEFAULT NULL,
  `wid` int(11) DEFAULT NULL,
  `durability` int(11) DEFAULT NULL,
  `typeOf` varchar(10) DEFAULT NULL,
  `startlevel` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `weaponequipmentbar` */

insert  into `weaponequipmentbar`(`id`,`username`,`wid`,`durability`,`typeOf`,`startlevel`) values (0,'z',3004,10,'3',1),(1,'k',3004,10,'3',1),(2,'w',3005,10,'3',1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
