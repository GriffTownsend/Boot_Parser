-- log_parser is the db name I used for sample.
create database log_parser_test;
GRANT ALL PRIVILEGES ON log_parser_test.* to 'logparser'@'localhost';
USE log_parser_test;
CREATE TABLE log_record(id BIGINT NOT NULL PRIMARY KEY, log_date DATETIME, ip_address varchar(15), request varchar(1024), status smallint, user_agent varchar(1024));
exit;
