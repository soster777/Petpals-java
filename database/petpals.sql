-- -----------------------------------------------------
-- Base de datos PetPals
-- Sistema de gestion para una veterinaria
-- -----------------------------------------------------

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

DROP SCHEMA IF EXISTS `PetPals`;
CREATE SCHEMA IF NOT EXISTS `PetPals` DEFAULT CHARACTER SET utf8mb4;
USE `PetPals`;

-- -----------------------------------------------------
-- Usuarios
-- -----------------------------------------------------
CREATE TABLE `Usuarios` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido` VARCHAR(45) NOT NULL,
  `dni` INT NOT NULL,
  `tel` VARCHAR(20) NOT NULL,
  `mail` VARCHAR(100) NOT NULL,
  `contrasenia` VARCHAR(100) NOT NULL,
  `rol` ENUM('duenio', 'veterinario', 'empleado') NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `dni_UNIQUE` (`dni`),
  UNIQUE INDEX `mail_UNIQUE` (`mail`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Mascotas
-- -----------------------------------------------------
CREATE TABLE `Mascotas` (
  `id_mascota` INT NOT NULL AUTO_INCREMENT,
  `nombre_masc` VARCHAR(45) NOT NULL,
  `especie` VARCHAR(45) NOT NULL,
  `raza` VARCHAR(45) NOT NULL,
  `fecha_nac` DATE NOT NULL,
  `sexo` VARCHAR(45) NOT NULL,
  `peso` DECIMAL(5,2) NOT NULL,
  `observaciones` TINYTEXT NULL,
  `fk_usuario_duenio` INT NOT NULL,
  PRIMARY KEY (`id_mascota`),
  CONSTRAINT `fk_mascota_duenio` FOREIGN KEY (`fk_usuario_duenio`)
    REFERENCES `Usuarios` (`id_usuario`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Historiales
-- -----------------------------------------------------
CREATE TABLE `Historiales` (
  `id_historial` INT NOT NULL AUTO_INCREMENT,
  `fecha_regis` DATE NOT NULL,
  `tipo_regis` VARCHAR(45) NOT NULL,
  `detalle` TINYTEXT NOT NULL,
  `fk_usuario` INT NOT NULL,
  `fk_mascota` INT NOT NULL,
  PRIMARY KEY (`id_historial`),
  CONSTRAINT `fk_historial_usuario` FOREIGN KEY (`fk_usuario`)
    REFERENCES `Usuarios` (`id_usuario`),
  CONSTRAINT `fk_historial_mascota` FOREIGN KEY (`fk_mascota`)
    REFERENCES `Mascotas` (`id_mascota`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Turnos
-- el veterinario queda NULL hasta que el empleado confirma el turno
-- -----------------------------------------------------
CREATE TABLE `Turnos` (
  `id_turno` INT NOT NULL AUTO_INCREMENT,
  `fecha_turno` DATE NOT NULL,
  `hora_turno` TIME NOT NULL,
  `estado` ENUM('pendiente', 'confirmada', 'cancelada', 'realizada') NOT NULL DEFAULT 'pendiente',
  `tipo_turno` ENUM('vacunacion', 'guarderia', 'consulta') NOT NULL,
  `observaciones` TINYTEXT NULL,
  `fk_usuario_veterinario` INT NULL,
  `fk_mascota` INT NOT NULL,
  PRIMARY KEY (`id_turno`),
  CONSTRAINT `fk_turno_veterinario` FOREIGN KEY (`fk_usuario_veterinario`)
    REFERENCES `Usuarios` (`id_usuario`) ON DELETE SET NULL,
  CONSTRAINT `fk_turno_mascota` FOREIGN KEY (`fk_mascota`)
    REFERENCES `Mascotas` (`id_mascota`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Consultas
-- -----------------------------------------------------
CREATE TABLE `Consultas` (
  `id_consulta` INT NOT NULL AUTO_INCREMENT,
  `descripcion` TINYTEXT NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `motivo` TINYTEXT NOT NULL,
  `diagnostico` TINYTEXT NOT NULL,
  `tratamiento` TINYTEXT NULL,
  `observaciones` TINYTEXT NULL,
  `fk_turno` INT NOT NULL,
  PRIMARY KEY (`id_consulta`),
  CONSTRAINT `fk_consulta_turno` FOREIGN KEY (`fk_turno`)
    REFERENCES `Turnos` (`id_turno`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Vacunaciones
-- -----------------------------------------------------
CREATE TABLE `Vacunaciones` (
  `id_vacunacion` INT NOT NULL AUTO_INCREMENT,
  `nombre_vac` VARCHAR(45) NOT NULL,
  `descripcion` TINYTEXT NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `dosis` VARCHAR(45) NOT NULL,
  `fecha_aplic` DATE NOT NULL,
  `prox_dosis` DATE NULL,
  `fk_turno` INT NOT NULL,
  PRIMARY KEY (`id_vacunacion`),
  CONSTRAINT `fk_vacunacion_turno` FOREIGN KEY (`fk_turno`)
    REFERENCES `Turnos` (`id_turno`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Guarderias
-- -----------------------------------------------------
CREATE TABLE `Guarderias` (
  `id_guarderia` INT NOT NULL AUTO_INCREMENT,
  `descripcion` TINYTEXT NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `fecha_ing` DATE NOT NULL,
  `fecha_sal` DATE NOT NULL,
  `alimentacion` VARCHAR(100) NOT NULL,
  `medicacion` VARCHAR(100) NULL,
  `observaciones` TINYTEXT NULL,
  `fk_recepcionista` INT NOT NULL,
  `fk_mascota` INT NOT NULL,
  PRIMARY KEY (`id_guarderia`),
  CONSTRAINT `fk_guarderia_empleado` FOREIGN KEY (`fk_recepcionista`)
    REFERENCES `Usuarios` (`id_usuario`),
  CONSTRAINT `fk_guarderia_mascota` FOREIGN KEY (`fk_mascota`)
    REFERENCES `Mascotas` (`id_mascota`) ON DELETE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Notificaciones
-- -----------------------------------------------------
CREATE TABLE `Notificaciones` (
  `id_notificacion` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(45) NOT NULL,
  `mensaje` TINYTEXT NOT NULL,
  `fecha_env` DATETIME NOT NULL,
  `tipo_noti` ENUM('turno', 'vacunacion', 'guarderia', 'suscripcion', 'general') NOT NULL,
  `fk_usuario_duenio` INT NOT NULL,
  `fk_turno` INT NULL,
  `fk_guarderia` INT NULL,
  PRIMARY KEY (`id_notificacion`),
  CONSTRAINT `fk_notificacion_duenio` FOREIGN KEY (`fk_usuario_duenio`)
    REFERENCES `Usuarios` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_notificacion_turno` FOREIGN KEY (`fk_turno`)
    REFERENCES `Turnos` (`id_turno`) ON DELETE SET NULL,
  CONSTRAINT `fk_notificacion_guarderia` FOREIGN KEY (`fk_guarderia`)
    REFERENCES `Guarderias` (`id_guarderia`) ON DELETE SET NULL)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Planes
-- -----------------------------------------------------
CREATE TABLE `Planes` (
  `id_plan` INT NOT NULL AUTO_INCREMENT,
  `tipo_plan` ENUM('premium', 'vip') NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `descripcion` TINYTEXT NOT NULL,
  PRIMARY KEY (`id_plan`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Suscripciones
-- -----------------------------------------------------
CREATE TABLE `Suscripciones` (
  `id_suscripcion` INT NOT NULL AUTO_INCREMENT,
  `fecha_inicio` DATE NOT NULL,
  `fecha_fin` DATE NOT NULL,
  `estado` ENUM('activa', 'cancelada') NOT NULL DEFAULT 'activa',
  `fk_usuario_duenio` INT NOT NULL,
  `fk_plan` INT NOT NULL,
  PRIMARY KEY (`id_suscripcion`),
  CONSTRAINT `fk_suscripcion_duenio` FOREIGN KEY (`fk_usuario_duenio`)
    REFERENCES `Usuarios` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `fk_suscripcion_plan` FOREIGN KEY (`fk_plan`)
    REFERENCES `Planes` (`id_plan`))
ENGINE = InnoDB;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Datos de prueba (la contraseña de todos los usuarios es 1234)
-- -----------------------------------------------------
INSERT INTO `Usuarios` (`nombre`, `apellido`, `dni`, `tel`, `mail`, `contrasenia`, `rol`) VALUES
('Laura', 'Gomez', 30111222, '1144445555', 'duenio@petpals.com', '$2a$10$fr00yiIR2Xm4Z0bHdlUltuFq7jiHud19k3XKk9H7ahAvK8sQA/1lK', 'duenio'),
('Martin', 'Perez', 28333444, '1155556666', 'vet@petpals.com', '$2a$10$9iEUZpFY9c7sJIrWmJaYheItWD34SKbbgo6PNe0WVGbRuJq3BWqca', 'veterinario'),
('Sofia', 'Diaz', 35666777, '2215557777', 'empleado@petpals.com', '$2a$10$diBmho5ZVzTqNf4VG8FKTOehVB.7kEqYiJXl3p1FQWX9JQn/NUwSy', 'empleado');

INSERT INTO `Mascotas` (`nombre_masc`, `especie`, `raza`, `fecha_nac`, `sexo`, `peso`, `observaciones`, `fk_usuario_duenio`) VALUES
('Toby', 'Perro', 'Caniche', '2020-03-15', 'Macho', 8.50, 'Alergico al pollo', 1),
('Mishi', 'Gato', 'Siames', '2022-07-01', 'Hembra', 3.80, NULL, 1);

INSERT INTO `Historiales` (`fecha_regis`, `tipo_regis`, `detalle`, `fk_usuario`, `fk_mascota`) VALUES
('2026-05-10', 'Vacunación', 'Vacuna antirrabica anual', 2, 1);

INSERT INTO `Turnos` (`fecha_turno`, `hora_turno`, `estado`, `tipo_turno`, `observaciones`, `fk_usuario_veterinario`, `fk_mascota`) VALUES
(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:30:00', 'pendiente', 'consulta', 'Esta comiendo poco', NULL, 2),
(DATE_ADD(CURDATE(), INTERVAL 5 DAY), '16:00:00', 'confirmada', 'vacunacion', NULL, 2, 1);

INSERT INTO `Planes` (`tipo_plan`, `precio`, `descripcion`) VALUES
('premium', 15000.00, '10% de descuento en consultas y recordatorio de vacunas'),
('vip', 25000.00, '20% de descuento en consultas y vacunas, y un dia de guarderia gratis por mes');
