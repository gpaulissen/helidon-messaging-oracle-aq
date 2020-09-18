DECLARE
    output1 NUMBER;
    output2 VARCHAR2(100);
BEGIN
	dbms_aqadm.create_queue_table('KEC_OUT_QUEUE_TABLE', 'SYS.AQ$_JMS_TEXT_MESSAGE');
	dbms_aqadm.create_queue('KEC_OUT_QUEUE','KEC_OUT_QUEUE_TABLE');
	dbms_aqadm.start_queue('KEC_OUT_QUEUE');
END;
/