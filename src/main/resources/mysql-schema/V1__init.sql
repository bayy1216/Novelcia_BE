-- 테이블 DDL 생성, UNIQUE, INDEX 생성

CREATE TABLE users
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    email                  VARCHAR(100),
    encoded_password       VARCHAR(255),
    nickname               VARCHAR(255) NOT NULL,
    role                   ENUM('USER', 'ADMIN'),
    point                  INT          NOT NULL,
    member_ship_expired_at TIMESTAMP(6) NOT NULL,
    is_deleted             BIT          NOT NULL,
    created_at             TIMESTAMP(6) NOT NULL,
    modified_at            TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq__email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE novel
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    author_id           BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         VARCHAR(255) NOT NULL,
    thumbnail_image_url VARCHAR(255),
    read_authority      ENUM('FREE', 'MEMBERSHIP', 'PAYMENT'),
    alarm_count         BIGINT       NOT NULL,
    episode_count       BIGINT       NOT NULL,
    favorite_count      BIGINT       NOT NULL,
    like_count          BIGINT       NOT NULL,
    view_count          BIGINT       NOT NULL,
    version             INT          NOT NULL,
    created_at          TIMESTAMP(6) NOT NULL,
    modified_at         TIMESTAMP(6) NOT NULL,
    is_deleted          BIT          NOT NULL,
    PRIMARY KEY (id),
--     FOREIGN KEY (author_id) REFERENCES users (id)
    INDEX               idx__author_id (author_id)
) ENGINE=InnoDB;



CREATE TABLE novel_episode
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    novel_id       BIGINT         NOT NULL,
    episode_number INT            NOT NULL,
    title          VARCHAR(255)   NOT NULL,
    content        VARCHAR(12000) NOT NULL,
    author_comment VARCHAR(255)   NOT NULL,
    read_authority ENUM('FREE', 'MEMBERSHIP', 'PAYMENT'),
    is_deleted     BIT            NOT NULL,
    version        INT            NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    modified_at    TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__novel_id__episode_number UNIQUE (novel_id, episode_number),
--     FOREIGN KEY (novel_id) REFERENCES novel (id)
    INDEX          idx__novel_id__is_deleted__episode_number (novel_id, is_deleted, episode_number)
) ENGINE=InnoDB;

CREATE TABLE episode_comment
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    episode_id  BIGINT       NOT NULL,
    parent_id   BIGINT,
    user_id     BIGINT       NOT NULL,
    content     VARCHAR(255) NOT NULL,
    is_deleted  BIT          NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    modified_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
--     FOREIGN KEY (episode_id) REFERENCES novel_episode (id),
--     FOREIGN KEY (parent_id) REFERENCES episode_comment (id),
--     FOREIGN KEY (user_id) REFERENCES users (id)
    INDEX       idx__episode_id (episode_id),
    INDEX       idx__parent_id (parent_id),
    INDEX       idx__user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE episode_like
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    episode_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__episode_id__user_id UNIQUE (episode_id, user_id),
--     FOREIGN KEY (episode_id) REFERENCES novel_episode (id),
--     FOREIGN KEY (user_id) REFERENCES users (id)
    INDEX      idx__episode_id (episode_id),
    INDEX      idx__user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE episode_view
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    novel_id   BIGINT NOT NULL,
    episode_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
--     FOREIGN KEY (episode_id) REFERENCES novel_episode (id),
--     FOREIGN KEY (novel_id) REFERENCES novel (id),
--     FOREIGN KEY (user_id) REFERENCES users (id)
    INDEX      idx__episode_id__user_id (episode_id, user_id),
    INDEX      idx__novel_id (novel_id),
    INDEX      idx__user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE idempotency_event
(
    created_at TIMESTAMP(6),
    id         VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;



CREATE TABLE novel_species
(
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    name           VARCHAR(50) NOT NULL,
    color_hex_code CHAR(7)     NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    modified_at    TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE novel_tag
(
    name           VARCHAR(255) NOT NULL,
    color_hex_code CHAR(7)      NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    modified_at    TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (name)
) ENGINE=InnoDB;

CREATE TABLE novel_alarm
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    novel_id   BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__novel_id__user_id UNIQUE (novel_id, user_id),
--     FOREIGN KEY (novel_id) REFERENCES novel (id),
--     FOREIGN KEY (user_id) REFERENCES users (id)
    INDEX      idx__user_id (user_id)
) ENGINE=InnoDB;

CREATE TABLE novel_and_species
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    novel_id         BIGINT NOT NULL,
    novel_species_id BIGINT NOT NULL,
    created_at       TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__novel_id__novel_species_id UNIQUE (novel_id, novel_species_id),
--     FOREIGN KEY (novel_id) REFERENCES novel (id),
--     FOREIGN KEY (novel_species_id) REFERENCES novel_species (id)
    INDEX            idx__novel_species_id (novel_species_id)
) ENGINE=InnoDB;

CREATE TABLE novel_and_tag
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    novel_id   BIGINT       NOT NULL,
    tag_id     VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__novel_id__tag_id UNIQUE (novel_id, tag_id),
--     FOREIGN KEY (novel_id) REFERENCES novel (id),
--     FOREIGN KEY (tag_id) REFERENCES novel_tag (name)
    INDEX      idx__tag_id (tag_id)
) ENGINE=InnoDB;

CREATE TABLE novel_favorite
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    novel_id   BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk__novel_id__user_id UNIQUE (novel_id, user_id),
--     FOREIGN KEY (novel_id) REFERENCES novel (id),
--     FOREIGN KEY (user_id) REFERENCES users (id)
    INDEX      idx__user_id (user_id)
) ENGINE=InnoDB;
