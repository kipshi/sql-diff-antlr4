This module is used for create upgrade.sql and revert.sql when db is change;

use **antlr4** to parse sql to ParseTree and rebuild;

execute command below
```yaml
java -jar diff-1.0-SNAPSHOT-jar-with-dependencies.jar origin.sql target.sql
```

then create **upgrade.sql** and **revert.sql**

for example:

***origin.sql*** is as below：
```mysql
CREATE DATABASE IF NOT EXISTS test;
USE test;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`           int(11)      NOT NULL AUTO_INCREMENT,
    `name`         varchar(256) NOT NULL COMMENT 'account name',
    `password`     varchar(64)  NOT NULL COMMENT 'password md5',
    `account_type` int(11)      NOT NULL DEFAULT '1' COMMENT 'account type, 0-manager 1-normal',
    `due_date`     datetime              DEFAULT NULL COMMENT 'due date for account',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time`  datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
    `create_by`    varchar(256) NOT NULL COMMENT 'create by sb.',
    `update_by`    varchar(256)          DEFAULT NULL COMMENT 'update by sb.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_user_name_idx` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='User table';
```

***target.sql*** as below:
```mysql
CREATE DATABASE IF NOT EXISTS test;
USE test;


CREATE TABLE IF NOT EXISTS `user`
(
    `id`              int(11)      NOT NULL AUTO_INCREMENT COMMENT 'Incremental primary key',
    `name`            varchar(256) NOT NULL COMMENT 'Username',
    `password`        varchar(64)  NOT NULL COMMENT 'Password md5',
    `secret_key`      varchar(256)          DEFAULT NULL COMMENT 'Auth key for public network access',
    `public_key`      text                  DEFAULT NULL COMMENT 'Public key for asymmetric data encryption',
    `private_key`     text                  DEFAULT NULL COMMENT 'Private key for asymmetric data encryption',
    `encrypt_version` int(11)               DEFAULT NULL COMMENT 'Encryption key version',
    `account_type`    int(11)      NOT NULL DEFAULT '1' COMMENT 'Account type, 0-manager 1-normal',
    `due_date`        datetime              DEFAULT NULL COMMENT 'Due date for user',
    `ext_params`      text COMMENT 'Json extension info',
    `status`          int(11)               DEFAULT '100' COMMENT 'Status',
    `is_deleted`      int(11)               DEFAULT '0' COMMENT 'Whether to delete, 0 is not deleted, if greater than 0, delete',
    `creator`         varchar(256) NOT NULL COMMENT 'Creator name',
    `modifier`        varchar(256)          DEFAULT NULL COMMENT 'Modifier name',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `modify_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify time',
    `version`         int(11)      NOT NULL DEFAULT '1' COMMENT 'Version number, which will be incremented by 1 after modification',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_user_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='User table';
```

then create ***upgrade.sql*** like this:

```mysql
USE test;
ALTER TABLE `user`
    DROP COLUMN  `update_time`,
    DROP COLUMN  `create_by`,
    DROP COLUMN  `update_by`,
    ADD COLUMN  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modify time' ,
    ADD COLUMN  `encrypt_version` int(11) DEFAULT NULL COMMENT 'Encryption key version' ,
    MODIFY COLUMN  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Incremental primary key' ,
    MODIFY COLUMN  `account_type` int(11) NOT NULL DEFAULT '1' COMMENT 'Account type, 0-manager 1-normal' ,
    ADD COLUMN  `public_key` text DEFAULT NULL COMMENT 'Public key for asymmetric data encryption' ,
    ADD COLUMN  `version` int(11) NOT NULL DEFAULT '1' COMMENT 'Version number, which will be incremented by 1 after modification' ,
    ADD COLUMN  `status` int(11) DEFAULT '100' COMMENT 'Status' ,
    ADD COLUMN  `modifier` varchar(256) DEFAULT NULL COMMENT 'Modifier name' ,
    ADD COLUMN  `secret_key` varchar(256) DEFAULT NULL COMMENT 'Auth key for public network access' ,
    ADD COLUMN  `private_key` text DEFAULT NULL COMMENT 'Private key for asymmetric data encryption' ,
    ADD COLUMN  `is_deleted` int(11) DEFAULT '0' COMMENT 'Whether to delete, 0 is not deleted, if greater than 0, delete' ,
    MODIFY COLUMN  `due_date` datetime DEFAULT NULL COMMENT 'Due date for user' ,
    ADD COLUMN  `ext_params` text COMMENT 'Json extension info' ,
    MODIFY COLUMN  `name` varchar(256) NOT NULL COMMENT 'Username' ,
    MODIFY COLUMN  `password` varchar(64) NOT NULL COMMENT 'Password md5' ,
    ADD COLUMN  `creator` varchar(256) NOT NULL COMMENT 'Creator name' ,
    DROP INDEX `unique_user_name_idx`,
    ADD CONSTRAINT `unique_user_name` UNIQUE (`name`);
```

create ***revert.sql*** like this
```mysql
USE test;
ALTER TABLE `user`
    DROP COLUMN  `modify_time`,
    DROP COLUMN  `encrypt_version`,
    DROP COLUMN  `public_key`,
    DROP COLUMN  `version`,
    DROP COLUMN  `status`,
    DROP COLUMN  `modifier`,
    DROP COLUMN  `secret_key`,
    DROP COLUMN  `private_key`,
    DROP COLUMN  `is_deleted`,
    DROP COLUMN  `ext_params`,
    DROP COLUMN  `creator`,
    ADD COLUMN  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time' ,
    ADD COLUMN  `create_by` varchar(256) NOT NULL COMMENT 'create by sb.' ,
    MODIFY COLUMN  `due_date` datetime DEFAULT NULL COMMENT 'due date for account' ,
    ADD COLUMN  `update_by` varchar(256) DEFAULT NULL COMMENT 'update by sb.' ,
    MODIFY COLUMN  `id` int(11) NOT NULL AUTO_INCREMENT ,
    MODIFY COLUMN  `name` varchar(256) NOT NULL COMMENT 'account name' ,
    MODIFY COLUMN  `account_type` int(11) NOT NULL DEFAULT '1' COMMENT 'account type, 0-manager 1-normal' ,
    MODIFY COLUMN  `password` varchar(64) NOT NULL COMMENT 'password md5' ,
    DROP INDEX `unique_user_name`,
    ADD CONSTRAINT `unique_user_name_idx` UNIQUE (`name`);
```

only used for MySQL !!