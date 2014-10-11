SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

CREATE TABLE IF NOT EXISTS `player_storage` (
  `player_uuid` varchar(64) NOT NULL,
  `player_inventory` LONGTEXT NOT NULL,
  `player_armor` LONGTEXT NOT NULL,
  `player_enderchest` LONGTEXT NOT NULL,
  `player_potioneffects` LONGTEXT NOT NULL,
  `player_explevel` int(16) DEFAULT 0,
  `player_health` int(16) DEFAULT 20,
  `player_maxhealth` int(16) DEFAULT 20,
  `player_food` int(16) DEFAULT 20,
  `player_location` varchar(255) NOT NULL,
  `player_balance` bigint DEFAULT 0,
  `player_homes` TEXT NOT NULL,
  `player_kit_cooldowns` TEXT NOT NULL,
  `is_online` varchar(10) NOT NULL,
  `online_server_address` varchar(100) NOT NULL,
  `arkhamcolorchat_datafile` LONGTEXT NOT NULL,
  `marriage_datafile` LONGTEXT NOT NULL,
  `redeemmcmmo_datafile` LONGTEXT NOT NULL,
  `magiccrates_datafile` LONGTEXT NOT NULL,
  PRIMARY KEY (`player_uuid`),
  UNIQUE KEY `player_uuid` (`player_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;