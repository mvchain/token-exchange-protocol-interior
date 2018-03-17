/*
Navicat MySQL Data Transfer

Source Server         : test-local
Source Server Version : 50505
Source Host           : 192.168.203.7:3306
Source Database       : mvc_token_sell

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-03-17 12:21:37
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
  `status` int(1) NOT NULL,
  `password` varchar(128) NOT NULL,
  `transaction_password` varchar(128) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'user', 'admin', '1', null, '2018-03-17 04:20:38', '2018-03-17 04:20:38');

-- ----------------------------
-- Table structure for `capital`
-- ----------------------------
DROP TABLE IF EXISTS `capital`;
CREATE TABLE `capital` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `token_id` bigint(20) NOT NULL,
  `balance` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of capital
-- ----------------------------

-- ----------------------------
-- Table structure for `config`
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recharge_status` int(1) DEFAULT '0',
  `withdraw_status` int(1) DEFAULT '0',
  `min` float DEFAULT NULL,
  `max` float DEFAULT NULL,
  `poundage` decimal(10,0) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `project_id` bigint(20) DEFAULT NULL,
  `token_name` varchar(32) DEFAULT NULL,
  `need_show` int(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES ('0', '0', '0', '0', '0', '0', '2018-03-17 04:21:14', '2018-03-17 04:21:17', '0', 'ETH', '1');

-- ----------------------------
-- Table structure for `orders`
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `eth_number` decimal(10,0) DEFAULT NULL,
  `token_number` decimal(10,0) DEFAULT NULL,
  `order_status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `eth_number` decimal(10,0) DEFAULT NULL,
  `ratio` float DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `stop_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `white_paper_address` varchar(255) DEFAULT NULL,
  `white_paper_name` varchar(64) DEFAULT NULL,
  `project_image_address` varchar(255) DEFAULT NULL,
  `project_image_name` varchar(64) DEFAULT NULL,
  `leader_image_address` varchar(255) DEFAULT NULL,
  `leader_image_name` varchar(64) DEFAULT NULL,
  `leader_name` varchar(64) DEFAULT NULL,
  `position` varchar(64) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of project
-- ----------------------------

-- ----------------------------
-- Table structure for `project_sold`
-- ----------------------------
DROP TABLE IF EXISTS `project_sold`;
CREATE TABLE `project_sold` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `buyer_num` int(11) DEFAULT '0',
  `sold_eth` decimal(10,0) DEFAULT NULL,
  `send_token` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `number` decimal(10,0) DEFAULT NULL,
  `real_number` decimal(10,0) DEFAULT NULL,
  `token_id` bigint(20) DEFAULT NULL,
  `from_address` varchar(64) DEFAULT NULL,
  `to_address` varchar(64) DEFAULT NULL,
  `hash` varchar(64) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `type` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of transaction
-- ----------------------------
