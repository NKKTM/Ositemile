# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  _id                       bigint not null,
  _comment                  varchar(255),
  constraint pk_comment primary key (_id))
;

create table goods (
  _id                       bigint not null,
  _goods_name               varchar(255),
  _image_url                varchar(255),
  _amazon_url               varchar(255),
  constraint pk_goods primary key (_id))
;

create table post (
  _id                       bigint not null,
  _post_comment             varchar(255),
  _post                     bigint,
  constraint pk_post primary key (_id))
;

create table user (
  _id                       bigint not null,
  _user_name                varchar(255),
  _passward                 varchar(255),
  _login_id                 varchar(255),
  constraint pk_user primary key (_id))
;

create sequence comment_seq;

create sequence goods_seq;

create sequence post_seq;

create sequence user_seq;

alter table comment add constraint fk_comment__user_1 foreign key (_comment) references user (_id) on delete restrict on update restrict;
create index ix_comment__user_1 on comment (_comment);
alter table comment add constraint fk_comment__post_2 foreign key (_comment) references post (_id) on delete restrict on update restrict;
create index ix_comment__post_2 on comment (_comment);
alter table post add constraint fk_post__goods_3 foreign key (_post) references goods (_id) on delete restrict on update restrict;
create index ix_post__goods_3 on post (_post);
alter table post add constraint fk_post__user_4 foreign key (_post) references user (_id) on delete restrict on update restrict;
create index ix_post__user_4 on post (_post);



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

