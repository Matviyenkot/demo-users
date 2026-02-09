CREATE TABLE users (
  user_id VARCHAR(64) PRIMARY KEY,
  login VARCHAR(64) NOT NULL,
  first_name VARCHAR(64) NOT NULL,
  last_name VARCHAR(64) NOT NULL
);