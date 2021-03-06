/*
Navicat MySQL Data Transfer

Source Server         : test-local
Source Server Version : 50505
Source Host           : 192.168.203.7:3306
Source Database       : token_bak

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-04-03 19:11:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `account`
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(1) NOT NULL DEFAULT '1',
  `password` varchar(128) NOT NULL,
  `transaction_password` varchar(128) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `order_num` int(11) DEFAULT '0',
  `address_eth` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of account
-- ----------------------------

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `status` int(1) NOT NULL DEFAULT '1',
  `head_image` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'mvc-admin', '$2a$08$0y/jCDiPLDaX8S4k81hd0OOQW5UIQJTNoUc2dAICiBZvYO65wkbQi', '1', null, '2018-03-17 04:20:38', '2018-03-22 11:56:06');

-- ----------------------------
-- Table structure for `capital`
-- ----------------------------
DROP TABLE IF EXISTS `capital`;
CREATE TABLE `capital` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `token_id` bigint(20) NOT NULL,
  `balance` decimal(20,5) NOT NULL DEFAULT '0.00000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_token_id_user_id` (`user_id`,`token_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of capital
-- ----------------------------

-- ----------------------------
-- Table structure for `config`
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recharge_status` int(1) NOT NULL DEFAULT '0',
  `withdraw_status` int(1) NOT NULL DEFAULT '0',
  `min` float NOT NULL DEFAULT '0',
  `max` float NOT NULL DEFAULT '0',
  `poundage` decimal(10,5) NOT NULL DEFAULT '0.00000',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `token_name` varchar(32) NOT NULL,
  `need_show` int(1) NOT NULL DEFAULT '0',
  `contract_address` varchar(61) DEFAULT NULL,
  `decimals` int(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_token_name` (`token_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES ('0', '1', '1', '0.05', '2', '0.00000', '2018-03-17 04:21:14', '2018-04-03 11:08:47', 'ETH', '1', null, '0');

-- ----------------------------
-- Table structure for `orders`
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `eth_number` decimal(10,5) NOT NULL DEFAULT '0.00000',
  `token_number` decimal(10,5) NOT NULL DEFAULT '0.00000',
  `order_status` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) DEFAULT NULL,
  `token_name` varchar(32) DEFAULT NULL,
  `contract_address` varchar(64) DEFAULT NULL,
  `eth_number` decimal(10,5) DEFAULT NULL,
  `ratio` float DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `stop_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `homepage` varchar(255) DEFAULT NULL,
  `white_paper_address` varchar(255) DEFAULT NULL,
  `white_paper_name` varchar(64) DEFAULT NULL,
  `project_image_address` varchar(255) DEFAULT NULL,
  `project_image_name` varchar(64) DEFAULT NULL,
  `project_cover_address` varchar(255) DEFAULT NULL,
  `project_cover_name` varchar(64) DEFAULT NULL,
  `leader_image_address` varchar(255) DEFAULT NULL,
  `leader_image_name` varchar(64) DEFAULT NULL,
  `leader_name` varchar(64) DEFAULT NULL,
  `position` varchar(64) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(1) NOT NULL DEFAULT '0',
  `decimals` int(2) DEFAULT NULL,
  `need_show` int(1) NOT NULL DEFAULT '0',
  `send_token` int(1) NOT NULL DEFAULT '0',
  `retire` int(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of project
-- ----------------------------

-- ----------------------------
-- Table structure for `project_sold`
-- ----------------------------
DROP TABLE IF EXISTS `project_sold`;
CREATE TABLE `project_sold` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `buyer_num` int(11) NOT NULL DEFAULT '0',
  `sold_eth` decimal(10,5) NOT NULL DEFAULT '0.00000',
  `send_token` decimal(10,5) NOT NULL DEFAULT '0.00000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of project_sold
-- ----------------------------

-- ----------------------------
-- Table structure for `transaction`
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `order_id` varchar(64) DEFAULT NULL,
  `poundage` float DEFAULT NULL,
  `start_at` timestamp NULL DEFAULT NULL,
  `finish_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `number` decimal(10,5) DEFAULT NULL,
  `real_number` decimal(10,5) DEFAULT NULL,
  `token_id` bigint(20) DEFAULT NULL,
  `from_address` varchar(64) DEFAULT NULL,
  `to_address` varchar(64) DEFAULT NULL,
  `hash` varchar(128) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `type` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of transaction
-- ----------------------------

update config set id = 0;