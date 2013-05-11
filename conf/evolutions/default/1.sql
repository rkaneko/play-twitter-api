# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table twitter_api_info (
  id                        bigint auto_increment not null,
  screen_name               varchar(255) not null,
  token                     varchar(255) not null,
  tokenSecret               varchar(255) not null,
  constraint uq_twitter_api_info_screen_name unique (screen_name),
  constraint pk_twitter_api_info primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table twitter_api_info;

SET FOREIGN_KEY_CHECKS=1;

