/*
Navicat MySQL Data Transfer

Source Server         : MyLeitner
Source Server Version : 50619
Source Host           : localhost:3306
Source Database       : myleitner

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2014-07-10 12:48:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for card
-- ----------------------------
DROP TABLE IF EXISTS `card`;
CREATE TABLE `card` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DEVICE_UDK` varchar(50) DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `TITLE` text,
  `FRONT` text,
  `BACK` text,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CHECK_TIME` timestamp NULL DEFAULT NULL,
  `UPDATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LIKE_COUNT` int(11) DEFAULT '0',
  `CATEGORY` int(11) DEFAULT '0',
  `BOX_INDEX` int(11) DEFAULT '0',
  `GROUP_INDEX` int(11) DEFAULT '0',
  `COUNT_CORRECT` int(11) DEFAULT '0',
  `COUNT_INCORRECT` int(11) DEFAULT '0',
  `REFERENCE` tinyint(1) DEFAULT '0',
  `DEPENDANT` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_DEVICE_CARD` (`DEVICE_UDK`),
  KEY `FK_USER_CARD` (`USER_ID`),
  CONSTRAINT `FK_DEVICE_CARD` FOREIGN KEY (`DEVICE_UDK`) REFERENCES `device` (`UDK`),
  CONSTRAINT `FK_USER_CARD` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for card_tag
-- ----------------------------
DROP TABLE IF EXISTS `card_tag`;
CREATE TABLE `card_tag` (
  `ID` int(11) NOT NULL,
  `TAG_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`TAG_ID`),
  KEY `FK_CARD_TAG_TAG` (`TAG_ID`),
  CONSTRAINT `FK_CARD_TAG_CARD` FOREIGN KEY (`ID`) REFERENCES `card` (`ID`),
  CONSTRAINT `FK_CARD_TAG_TAG` FOREIGN KEY (`TAG_ID`) REFERENCES `tag` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `CONTEXT` text,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LIKE_COUNT` int(11) DEFAULT NULL,
  `DISLIKE_COUNT` int(11) DEFAULT NULL,
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CAR_ID` int(11) NOT NULL,
  `USE_ID` int(11) NOT NULL,
  `LEI_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CARD_COMMENT` (`CAR_ID`),
  KEY `FK_LEITNER_COMMENT` (`LEI_ID`),
  KEY `FK_USER_COMMENT` (`USE_ID`),
  CONSTRAINT `FK_CARD_COMMENT` FOREIGN KEY (`CAR_ID`) REFERENCES `card` (`ID`),
  CONSTRAINT `FK_LEITNER_COMMENT` FOREIGN KEY (`LEI_ID`) REFERENCES `leitner` (`ID`),
  CONSTRAINT `FK_USER_COMMENT` FOREIGN KEY (`USE_ID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UDK` varchar(50) DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `DENSITY_DPI` int(11) DEFAULT NULL,
  `SIZE_INCHES` float DEFAULT NULL,
  `HEIGHT` int(11) DEFAULT NULL,
  `DENSITY` int(11) DEFAULT NULL,
  `WIDTH` int(11) DEFAULT NULL,
  `XDPI` float DEFAULT NULL,
  `YDPI` float DEFAULT NULL,
  `STORAGE_EXTERNAL` float DEFAULT NULL,
  `STORAGE_EXTERNAL_FREE` float DEFAULT NULL,
  `STORAGE_INTERNAL` float DEFAULT NULL,
  `RAM_SIZE` float DEFAULT NULL,
  `CPU_ABI` varchar(50) DEFAULT NULL,
  `CPU_ABI2` varchar(50) DEFAULT NULL,
  `MAX_FREQUENCY` int(11) DEFAULT NULL,
  `CORES` int(11) DEFAULT NULL,
  `ANDROID_ID` varchar(50) DEFAULT NULL,
  `BLUETOOTH_ADDRESS` varchar(50) DEFAULT NULL,
  `BOARD` varchar(100) DEFAULT NULL,
  `BRAND` varchar(100) DEFAULT NULL,
  `DEVICE_NAME` varchar(100) DEFAULT NULL,
  `DISPLAY_NAME` varchar(100) DEFAULT NULL,
  `LABEL` varchar(100) DEFAULT NULL,
  `IMEI` varchar(30) DEFAULT NULL,
  `MANUFACTURE` varchar(50) DEFAULT NULL,
  `MODEL` varchar(50) DEFAULT NULL,
  `PRODUCT` varchar(50) DEFAULT NULL,
  `WLAN_ADDRESS` varchar(50) DEFAULT NULL,
  `SDK_VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `UDK` (`UDK`),
  KEY `FK_DEVICE_USER` (`USER_ID`),
  CONSTRAINT `FK_DEVICE_USER` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for example
-- ----------------------------
DROP TABLE IF EXISTS `example`;
CREATE TABLE `example` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CAR_ID` int(11) NOT NULL,
  `CONTEXT` text,
  `SUGESTED` tinyint(1) DEFAULT NULL,
  `APPROVED` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CARD_EXAMPLE` (`CAR_ID`),
  CONSTRAINT `FK_CARD_EXAMPLE` FOREIGN KEY (`CAR_ID`) REFERENCES `card` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for leitner
-- ----------------------------
DROP TABLE IF EXISTS `leitner`;
CREATE TABLE `leitner` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `LIKE_COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for leitner_card
-- ----------------------------
DROP TABLE IF EXISTS `leitner_card`;
CREATE TABLE `leitner_card` (
  `LEI_ID` int(11) NOT NULL,
  `ID` int(11) NOT NULL,
  PRIMARY KEY (`LEI_ID`,`ID`),
  KEY `FK_LEITNER_CARD_CARD` (`ID`),
  CONSTRAINT `FK_LEITNER_CARD_CARD` FOREIGN KEY (`ID`) REFERENCES `card` (`ID`),
  CONSTRAINT `FK_LEITNER_CARD_LEITNER` FOREIGN KEY (`LEI_ID`) REFERENCES `leitner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for leitner_tag
-- ----------------------------
DROP TABLE IF EXISTS `leitner_tag`;
CREATE TABLE `leitner_tag` (
  `ID` int(11) NOT NULL,
  `TAG_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`TAG_ID`),
  KEY `FK_LEITNER_TAG_TAG` (`TAG_ID`),
  CONSTRAINT `FK_LEITNER_TAG_LEITNER` FOREIGN KEY (`ID`) REFERENCES `leitner` (`ID`),
  CONSTRAINT `FK_LEITNER_TAG_TAG` FOREIGN KEY (`TAG_ID`) REFERENCES `tag` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DEVICE_UDK` varchar(50) DEFAULT NULL,
  `FIRST_NAME` varchar(50) DEFAULT NULL,
  `LAST_NAME` varchar(50) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `DISPLAY_NAME` varchar(50) DEFAULT NULL,
  `EMAIL_ADDRESS` varchar(255) NOT NULL,
  `PICTURE` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNIQUE_EMAIL` (`EMAIL_ADDRESS`) USING BTREE,
  KEY `FK_USER_DEVICE` (`DEVICE_UDK`),
  CONSTRAINT `FK_USER_DEVICE` FOREIGN KEY (`DEVICE_UDK`) REFERENCES `device` (`UDK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
