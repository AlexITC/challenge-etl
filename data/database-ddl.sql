DROP SCHEMA IF EXISTS hackathon;
CREATE SCHEMA IF NOT EXISTS hackathon;

DROP TABLE IF EXISTS `hackathon`.`person`;
CREATE TABLE IF NOT EXISTS `hackathon`.`person` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `age` INT NULL,
  `gender` CHAR(1) NULL,
  `time_create` TIMESTAMP NULL,
  `time_update` TIMESTAMP NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;

DROP TABLE IF  EXISTS `hackathon`.`term`;
CREATE TABLE IF NOT EXISTS `hackathon`.`term` (
  `id` INT NOT NULL,
  `year` INT NULL,
  `period` CHAR(1) NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;

DROP TABLE IF  EXISTS `hackathon`.`class`;
CREATE TABLE IF NOT EXISTS `hackathon`.`class` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  `credits` int NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;

DROP TABLE IF  EXISTS `hackathon`.`building`;
CREATE TABLE IF NOT EXISTS `hackathon`.`building` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  `rooms` int(45) NULL,
  `coordinates` TEXT NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;


DROP TABLE IF  EXISTS `hackathon`.`class_x_term`;
CREATE TABLE IF NOT EXISTS `hackathon`.`class_x_term` (
  `id` INT NOT NULL,
  `term_id` INT NOT NULL,
  `class_id` INT NOT NULL,
  `building_id` INT NOT NULL,
  `weekday` INT NOT NULL,
  `offering` INT NOT NULL,
  `room` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;

DROP TABLE IF  EXISTS `hackathon`.`term_enrolment`;
CREATE TABLE IF NOT EXISTS `hackathon`.`term_enrolment` (
  `id` INT NOT NULL,
  `person_id` int NULL,
  `term_id` int NULL,
  `grade` int NULL,
  `time_create` TIMESTAMP NULL,
  `time_update` TIMESTAMP NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM;

DROP TABLE IF  EXISTS `hackathon`.`class_enrolment`;
CREATE TABLE IF NOT EXISTS `hackathon`.`class_enrolment` (
  `id` INT NOT NULL,
  `term_enrolment_id` int NULL,
  `person_id` int NULL,
  `class_id` int NULL,
  `grade` int NULL
--  PRIMARY KEY (`id`)
)
ENGINE = MyISAM;
