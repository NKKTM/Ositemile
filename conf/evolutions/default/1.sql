# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigint not null,
  comment                   varchar(255),
  date                      timestamp,
  constraint pk_comment primary key (id))
;

create table goods (
  id                        bigint not null,
  goods_name                varchar(255),
  image_url                 varchar(255),
  amazon_url                varchar(255),
  constraint pk_goods primary key (id))
;

create table post (
  id                        bigint not null,
  post_title                varchar(255),
  post_comment              varchar(255),
  post                      bigint,
  date                      timestamp,
  constraint pk_post primary key (id))
;

create table user (
  id                        bigint not null,
  user_name                 varchar(255),
  password                  varchar(255),
  login_id                  varchar(255),
  constraint pk_user primary key (id))
;

create sequence comment_seq;

create sequence goods_seq;

create sequence post_seq;

create sequence user_seq;

alter table comment add constraint fk_comment_user_1 foreign key (comment) references user (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (comment);
alter table comment add constraint fk_comment_post_2 foreign key (comment) references post (id) on delete restrict on update restrict;
create index ix_comment_post_2 on comment (comment);
alter table post add constraint fk_post_goods_3 foreign key (post) references goods (id) on delete restrict on update restrict;
create index ix_post_goods_3 on post (post);
alter table post add constraint fk_post_user_4 foreign key (post) references user (id) on delete restrict on update restrict;
create index ix_post_user_4 on post (post);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists comment;

drop table if exists goods;

drop table if exists post;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists comment_seq;

drop sequence if exists goods_seq;

drop sequence if exists post_seq;

drop sequence if exists user_seq;
