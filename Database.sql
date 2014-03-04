SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`User`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`User` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(45) NULL ,
  `mail` VARCHAR(45) NOT NULL ,
  `passwd` VARCHAR(45) NULL ,
  `dateOfBirth` DATE NULL ,
  `location` VARCHAR(45) NULL ,
  `isMailVisible` TINYINT(1) NULL ,
  `isAdmin` TINYINT(1) NULL ,
  `firstName` VARCHAR(45) NULL ,
  `lastName` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `mail_UNIQUE` (`mail` ASC) ,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`News`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`News` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `title` VARCHAR(45) NULL ,
  `content` TEXT NULL ,
  `date` DATETIME NULL ,
  `User_id` INT NOT NULL ,
  PRIMARY KEY (`id`, `User_id`) ,
  INDEX `fk_News_User1` (`User_id` ASC) ,
  CONSTRAINT `fk_News_User1`
    FOREIGN KEY (`User_id` )
    REFERENCES `mydb`.`User` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Game`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`Game` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL ,
  `description` TEXT NULL ,
  `link` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`User_has_Game`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`User_has_Game` (
  `User_id` INT NOT NULL ,
  `Game_id` INT NOT NULL ,
  `score` INT NULL ,
  PRIMARY KEY (`User_id`, `Game_id`) ,
  INDEX `fk_User_has_Game_Game1` (`Game_id` ASC) ,
  INDEX `fk_User_has_Game_User` (`User_id` ASC) ,
  CONSTRAINT `fk_User_has_Game_User`
    FOREIGN KEY (`User_id` )
    REFERENCES `mydb`.`User` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Game_Game1`
    FOREIGN KEY (`Game_id` )
    REFERENCES `mydb`.`Game` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
