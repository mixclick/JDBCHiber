sq.CreateUsersTable = CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), last_name VARCHAR(255), age SMALLINT)
sq.DropUsersTable = DROP TABLE IF EXISTS users
sq.GetAllUsersTable = FROM User
sq.CleanUsersTable = TRUNCATE TABLE users RESTART IDENTITY

jdbc.Insert = INSERT INTO users (name, last_name, age) VALUES (?, ?, ?)
jdbc.RemoveUsersById = DELETE FROM users WHERE id = ?
jdbc.GetAllUsers = SELECT * FROM users






