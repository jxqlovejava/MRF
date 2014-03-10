/*
SQLyog Ultimate v8.53 
MySQL - 5.6.12-log : Database - mrf
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mrf` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mrf`;

/*Table structure for table `client_config` */

DROP TABLE IF EXISTS `client_config`;

CREATE TABLE `client_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '����\n',
  `host` varchar(50) NOT NULL COMMENT '������ַ',
  `port` int(11) NOT NULL COMMENT '�˿�',
  `username` varchar(255) NOT NULL COMMENT '�û���',
  `password` varchar(255) NOT NULL COMMENT '����',
  `retry_queue_name` varchar(255) NOT NULL COMMENT '���Զ�����',
  `client_service_name` varchar(255) NOT NULL COMMENT '�ͻ���ע���Retry��������\n',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_unique_index` (`retry_queue_name`,`client_service_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Table structure for table `interval_retry` */

DROP TABLE IF EXISTS `interval_retry`;

CREATE TABLE `interval_retry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����\n',
  `msg_id` bigint(20) NOT NULL COMMENT '��ϢID',
  `retry_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-toRetry��1-inRetry',
  `retry_queue_name` varchar(255) NOT NULL COMMENT '���Զ�����',
  `business_msg` text NOT NULL COMMENT 'ҵ����Ϣ�ַ��\n�',
  `retry_interval` int(11) NOT NULL COMMENT '����ʱ����\n',
  `retried_times` int(11) NOT NULL COMMENT '�����Դ���',
  `max_retry_times` int(11) NOT NULL COMMENT '������Դ���',
  `next_exec_at` datetime DEFAULT NULL COMMENT '�´�����ʱ��',
  `checkout_at` datetime DEFAULT NULL COMMENT 'ȡ����Ϣʱ��',
  `updated_at` datetime DEFAULT NULL COMMENT '��¼����ʱ��',
  `created_at` datetime DEFAULT NULL COMMENT '��¼����ʱ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_msg_id` (`msg_id`),
  KEY `index_next_exec_at` (`next_exec_at`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;

/*Table structure for table `recover` */

DROP TABLE IF EXISTS `recover`;

CREATE TABLE `recover` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����\n',
  `msg_id` bigint(20) NOT NULL COMMENT '��ϢID',
  `recover_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-toRecover��1-inRecover',
  `retry_queue_name` varchar(255) NOT NULL COMMENT '���Զ�����',
  `business_msg` text NOT NULL COMMENT 'ҵ����Ϣ�ַ��\n�',
  `retry_interval` int(11) NOT NULL COMMENT '����ʱ����\n',
  `retried_times` int(11) NOT NULL COMMENT '�����Դ���',
  `max_retry_times` int(11) NOT NULL COMMENT '������Դ���',
  `checkout_at` datetime DEFAULT NULL COMMENT 'ȡ����Ϣʱ��',
  `updated_at` datetime DEFAULT NULL COMMENT '��¼����ʱ��',
  `created_at` datetime DEFAULT NULL COMMENT '��¼����ʱ��',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_msg_id` (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
