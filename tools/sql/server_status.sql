CREATE TABLE server_status (
    server_type ENUM('LOGIN','GAME') NOT NULL,
    status ENUM('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE',
    last_start DATETIME NULL,
    last_stop DATETIME NULL,
    last_heartbeat DATETIME NULL,
    PRIMARY KEY (server_type)
);
INSERT INTO server_status (server_type, status) VALUES ('LOGIN', 'OFFLINE'), ('GAME',  'OFFLINE') ON DUPLICATE KEY UPDATE server_type = server_type;
