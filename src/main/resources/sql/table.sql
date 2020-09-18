CREATE TABLE kec.test_log (uuid VARCHAR2(255) PRIMARY KEY, message VARCHAR2(255) NOT NULL, insert_date DATE DEFAULT (sysdate));

INSERT INTO kec.test_log (uuid, message) VALUES ('A1', 'HELLO!');

DELETE FROM kec.test_log ;

DROP TABLE kec.test_log ;

SELECT * FROM kec.test_log;