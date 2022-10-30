CREATE TABLE `sw_character`
(
    `character_id` varchar(255) NOT NULL,
    `height`       varchar(50)  DEFAULT NULL,
    `mass`         varchar(50)  DEFAULT NULL,
    `name`         varchar(255) DEFAULT NULL,
    PRIMARY KEY (`character_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
