DECLARE
    enqueue_options    DBMS_AQ.ENQUEUE_OPTIONS_T;
    message_properties DBMS_AQ.MESSAGE_PROPERTIES_T;
    message_handle     RAW(16);
    msg                SYS.AQ$_JMS_TEXT_MESSAGE;
BEGIN
    msg := SYS.AQ$_JMS_TEXT_MESSAGE.construct;
    msg.set_text('HELLO PLSQL WORLD ! ' || TO_CHAR(sysdate, 'DD-MM-YY HH24:MI:SS'));
    DBMS_AQ.ENQUEUE(
            queue_name => 'KEC_IN_QUEUE',
            enqueue_options => enqueue_options,
            message_properties => message_properties,
            payload => msg,
            msgid => message_handle);
    COMMIT;
END;
/