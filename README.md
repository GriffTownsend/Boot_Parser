# Parser
Implementation of com.ef.Parser that uses Spring Boot and MySQL as db backend.

### Notes

Since this version does not use Spring Boot, `java -cp` commands will not work on invocation due to the modified classpath created by Spring Boot fat jars. The following invocation will work as expected, however.

`java -jar parser.jar --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100`

If no access log is provided, a sample file included in the project will be used. 

A simplified date parser for variations on the ISO provided date is included. Date pattern recognition would be much more thorough in a production environment.


### Build Instructions
Sample uses Maven for component management and build. To compile use:

`mvn clean install`

or

`mvn clean install -DskipTests=true`

If you've already verified that all tests pass.

### MySQL Schema Used

There is a db_create.sh file in resources that will create a log_parser Database, assuming the presence of a parseruser account.

```SQL
USE log_parser;
CREATE TABLE log_record(id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, log_date DATETIME, ip_address varchar(15), request varchar(1024), status smallint, user_agent varchar(1024));

--Pull IP Addresses exceeding a given threshold (500 in this case) for a one hour time period
SELECT ip_address FROM log_record where log_date >= '2017-01-01 00:00:00.000' and log_date <= '2017-01-01 01:00:00.000' GROUP BY ip_address HAVING COUNT(ip_address) > 10;

-- This will check for all IP Addresses exceeding a threshold of 500 using the DATE_ADD method, and include the number of attempts.
SELECT ip_address, COUNT(ip_address) as count FROM log_record where log_date >= '2017-01-01 00:00:00.000' and log_date < DATE_ADD('2017-01-01 00:00:00.000', INTERVAL 1 DAY) GROUP BY ip_address HAVING COUNT(ip_address) > 500;

```
