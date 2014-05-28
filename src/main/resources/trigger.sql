CREATE OR REPLACE TRIGGER ::TRIGGER_NAME::
AFTER INSERT OR UPDATE OR DELETE ON "::TABLE_NAME::"
REFERENCING NEW AS new
FOR EACH ROW 
  DECLARE
  BEGIN
    IF UPDATING THEN
      INSERT INTO DBR_RECORDER (DBR_ID, CLIENT_IDENTIFIER, SESSION_USER, SESSIONID, TABLE_NAME, ACTION, DATAROW) VALUES (
        DBR_RECORDER_ID_SEQ.nextval,
        sys_context('USERENV', 'CLIENT_IDENTIFIER'),
        sys_context('USERENV', 'SESSION_USER'),
        sys_context('USERENV', 'SESSIONID'),
        '::TABLE_NAME::',
        'UPDATE',
        ::UPDATE_DATAROW::
      );
    ELSIF DELETING THEN
      INSERT INTO DBR_RECORDER (DBR_ID, CLIENT_IDENTIFIER, SESSION_USER, SESSIONID, TABLE_NAME, ACTION, DATAROW) VALUES (
        DBR_RECORDER_ID_SEQ.nextval,
        sys_context('USERENV', 'CLIENT_IDENTIFIER'),
        sys_context('USERENV', 'SESSION_USER'),
        sys_context('USERENV', 'SESSIONID'),
        '::TABLE_NAME::',
        'DELETE',
        ::DELETE_DATAROW::
      );
    ELSIF INSERTING THEN
      INSERT INTO DBR_RECORDER (DBR_ID, CLIENT_IDENTIFIER, SESSION_USER, SESSIONID, TABLE_NAME, ACTION, DATAROW) VALUES (
        DBR_RECORDER_ID_SEQ.nextval,
        sys_context('USERENV', 'CLIENT_IDENTIFIER'),
        sys_context('USERENV', 'SESSION_USER'),
        sys_context('USERENV', 'SESSIONID'),
        '::TABLE_NAME::',
        'INSERT',
        ::INSERT_DATAROW::
      );
    END IF;
  END;



