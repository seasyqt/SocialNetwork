CREATE TABLE language
(
    id   bigserial primary key not null,
    name varchar(255)          not null
);

CREATE TABLE country
(
    id   bigserial primary key not null,
    name varchar(255)          not null
);

CREATE TABLE town
(
    id         bigserial primary key not null,
    name       varchar(255)          not null,
    country_id integer               not null
);

CREATE TYPE user_type as ENUM ('USER', 'MODERATOR','ADMIN');
CREATE TYPE user_messages as ENUM ('ALL','FRIENDS');

CREATE TABLE "user"
(
    id                  bigserial primary key not null,
    type                user_type             not null,
    first_name          varchar(255)          not null,
    last_name           varchar(255)          not null,
    reg_date            date                  not null,
    birth_date          date,
    email               varchar(255)          not null,
    phone               varchar(30),
    password            varchar(255)          not null,
    confirmation_code   varchar(255),
    is_approved         smallint              not null,
    photo               TEXT,
    about               TEXT,
    town_id             integer,
    messages_permission user_messages         not null,
    last_online_time    date                  not null,
    is_blocked          smallint              not null,
    is_online           smallint              not null
);

CREATE TABLE file_info
(
    id                 bigserial primary key not null,
    hash_file          varchar(255)          not null,
    owner_id           integer               not null,
    file_name          varchar(255),
    relative_file_path varchar(255),
    raw_file_url       varchar(255),
    file_format        varchar(255),
    bytes              integer               not null,
    file_type          varchar(255),
    created_at         date
);

CREATE TYPE friendship_status as enum ('REQUEST', 'FRIEND', 'SUBSCRIBED', 'DECLINED', 'BLOCKED');

CREATE TABLE friendship
(
    id          bigserial primary key not null,
    src_user_id integer               not null,
    dst_user_id integer               not null,
    status      friendship_status     not null,
    time        date                  not null
);

CREATE TYPE message_read_status as enum ('SENT','READ');

CREATE TABLE message
(
    id           bigserial primary key not null,
    time         date                  not null,
    author_id    integer               not null,
    message_text TEXT,
    read_status  message_read_status   not null,
    dialog_id    integer               not null
);

CREATE TABLE dialog
(
    id           bigserial primary key not null,
    owner_id     integer               not null,
    recipient_id integer               not null,
    is_deleted   boolean               not null,
    invite_url   text                  not null,
    last_message integer
);

CREATE TABLE post
(
    id         bigserial primary key not null,
    time       date                  not null,
    author_id  integer               not null,
    title      varchar(255)          not null,
    post_text  TEXT                  not null,
    is_blocked smallint              not null,
    is_deleted smallint Default 0    not null
);

CREATE TABLE tag
(
    id   bigserial primary key not null,
    name varchar(255)          not null
);

CREATE TABLE post2tag
(
    id      bigserial primary key not null,
    post_id integer               not null,
    tag_id  integer               not null
);

CREATE TABLE post_like
(
    id         bigserial primary key not null,
    time       date                  not null,
    post_id    integer,
    comment_id integer,
    user_id    integer               not null
);

CREATE TABLE post_file
(
    id      bigserial primary key not null,
    post_id integer               not null,
    name    varchar(255)          not null,
    path    TEXT                  not null
);

CREATE TABLE post_comment
(
    id           bigserial primary key not null,
    time         date                  not null,
    post_id      integer               not null,
    parent_id    integer,
    author_id    integer               not null,
    comment_text TEXT                  not null,
    is_blocked   smallint              not null,
    is_deleted   smallint DEFAULT 0    not null
);

CREATE TABLE report
(
    id         bigserial primary key not null,
    time       date                  not null,
    post_id    integer               not null,
    comment_id integer,
    author_id  integer               not null
);

CREATE TYPE notification_type as enum ('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY');

CREATE TABLE notification
(
    id              bigserial primary key not null,
    type            notification_type     not null,
    sent_time       date                  not null,
    user_id         integer               not null,
    entity_id       integer               not null,
    email           varchar(255),
    sent_email_time date,
    phone           varchar(30)
);

CREATE TYPE block_history_action as enum ('BLOCK','UNBLOCK');

CREATE TABLE block_history
(
    id         bigserial primary key not null,
    time       date                  not null,
    user_id    integer               not null,
    post_id    integer,
    comment_id integer,
    action     block_history_action  not null
);

CREATE TABLE token2user
(
    id      bigserial primary key not null,
    token   varchar(255)          not null,
    time    date                  not null,
    user_id integer               not null
);

CREATE TYPE notification_setting_type as enum ('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'FRIEND_BIRTHDAY');

CREATE TABLE notification_setting
(
    id        bigserial primary key     not null,
    type      notification_setting_type not null,
    user_id   integer                   not null,
    is_enable smallint                  not null
);

CREATE TABLE logs
(
    USER_ID VARCHAR(20)   NOT NULL,
    DATED   date          NOT NULL,
    LOGGER  VARCHAR(50)   NOT NULL,
    LEVEL   VARCHAR(10)   NOT NULL,
    MESSAGE VARCHAR(1000) NOT NULL
);

CREATE TYPE user_print_status_enum as enum ('ACTIVE', 'PRINTS');

CREATE TABLE user_print_status
(
    id        bigserial primary key  not null,
    status    user_print_status_enum not null,
    user_id   integer                not null,
    dialog_id integer                not null,
    time      date                   not null
);

create table hibernate_sequence
(
    next_val bigint
);

insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
insert into hibernate_sequence
values (1);
