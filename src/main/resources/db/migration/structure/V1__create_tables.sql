CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE topics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_id BIGINT NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at DATE,
    updated_at DATE,
    CONSTRAINT fk_topic_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    body TEXT NOT NULL,
    created_at DATE,
    updated_at DATE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comment_topic FOREIGN KEY (topic_id) REFERENCES topics (id)
);