CREATE TABLE `wedding-booking-system`.`users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role` VARCHAR(45) NOT NULL,
  `full_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(45) NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `wedding_date` DATE NULL,
  `budget` DOUBLE NULL,
  `estimated_guests` INT NULL,
  `department` VARCHAR(150) NULL,
  `access_level` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
