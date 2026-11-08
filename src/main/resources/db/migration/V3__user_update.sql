ALTER TABLE `wedding-booking-system`.`users`
ADD COLUMN `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP AFTER `phone_number`,
ADD COLUMN `wedding_date` DATE NULL AFTER `created_at`,
ADD COLUMN `budget` DOUBLE NULL DEFAULT 0.0 AFTER `wedding_date`,
ADD COLUMN `estimated_guests` INT NULL DEFAULT 0 AFTER `budget`,
ADD COLUMN `department` VARCHAR(150) NULL AFTER `estimated_guests`,
ADD COLUMN `access_level` VARCHAR(45) NULL AFTER `department`;
