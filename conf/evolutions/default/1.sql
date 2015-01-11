# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `Addons` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`TITLE` VARCHAR(254) NOT NULL,`PRICE` INTEGER NOT NULL,`CONTENTURL` VARCHAR(254) NOT NULL);
create table `Currencys` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`FROM` VARCHAR(254) NOT NULL,`TO` VARCHAR(254) NOT NULL,`VALUE` DOUBLE NOT NULL);
create table `Feature` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`ICONURL` VARCHAR(254) NOT NULL,`TEXT` VARCHAR(254) NOT NULL);
create table `FeaturesHub` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`ROOM_ID` INTEGER,`FEATURE_ID` INTEGER);
create table `RoomListings` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`NAME` VARCHAR(254) NOT NULL,`PHOTOURL` VARCHAR(254) NOT NULL,`PHOTOALT` VARCHAR(254) NOT NULL,`ICONURL` VARCHAR(254) NOT NULL,`CTATEXT` VARCHAR(254) NOT NULL);
create table `Rooms` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`NAME` VARCHAR(254) NOT NULL,`PHOTOURL` VARCHAR(254) NOT NULL,`PHOTOALT` VARCHAR(254) NOT NULL,`TEXT` TEXT NOT NULL,`PRICE` INTEGER NOT NULL,`DISCOUNT` INTEGER,`SQUARE` INTEGER NOT NULL,`LISTING_ID` INTEGER);
alter table `FeaturesHub` add constraint `FEATURE_FK` foreign key(`FEATURE_ID`) references `Feature`(`ID`) on update NO ACTION on delete NO ACTION;
alter table `FeaturesHub` add constraint `ROOM_FK` foreign key(`ROOM_ID`) references `Rooms`(`ID`) on update NO ACTION on delete NO ACTION;
alter table `Rooms` add constraint `listing_fk` foreign key(`LISTING_ID`) references `RoomListings`(`ID`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE FeaturesHub DROP FOREIGN KEY FEATURE_FK;
ALTER TABLE FeaturesHub DROP FOREIGN KEY ROOM_FK;
ALTER TABLE Rooms DROP FOREIGN KEY listing_fk;
drop table `Addons`;
drop table `Currencys`;
drop table `Feature`;
drop table `FeaturesHub`;
drop table `RoomListings`;
drop table `Rooms`;

