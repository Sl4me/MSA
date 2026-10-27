INSERT INTO users (username, email) VALUES ('alice', 'alice@example.com');
INSERT INTO users (username, email) VALUES ('bob', 'bob@example.com');

INSERT INTO topics (author_id, slug, title, body)
VALUES (1, 'first-topic', 'Первая тема', 'Привет, это тема из Flyway!');