CREATE OR REPLACE FUNCTION WSO2_TOKEN_CLEANUP_SP() RETURNS void AS $$

DECLARE
  batchSize int;
  cursorLimit int;
  backupTables int;
  sleepTime float;
  safePeriod int;
  rowCount int;
  enableLog boolean;
  logLevel VARCHAR(10);
  enableAudit int;
  deleteTillTime timestamp;
  count int;

BEGIN

-- ------------------------------------------
-- CONFIGURABLE ATTRIBUTES
-- ------------------------------------------
  batchSize := 10000; -- SET BATCH SIZE FOR AVOID TABLE LOCKS    [DEFAULT : 10000]
  backupTables := 1;    -- SET IF TOKEN TABLE NEEDS TO BACKUP BEFORE DELETE     [DEFAULT : TRUE]
  sleepTime := 2; -- SET SLEEP TIME FOR AVOID TABLE LOCKS     [DEFAULT : 2]
  safePeriod := 2; -- SET SAFE PERIOD OF HOURS FOR TOKEN DELETE, SINCE TOKENS COULD BE CASHED    [DEFAULT : 2]
  rowCount := 0;
  enableLog := true; -- ENABLE LOGGING [DEFAULT : FALSE]
  logLevel := 'TRACE'; -- SET LOG LEVELS : TRACE , DEBUG
  enableAudit := 1;  -- SET TRUE FOR  KEEP TRACK OF ALL THE DELETED TOKENS USING A TABLE    [DEFAULT : TRUE]
  deleteTillTime := timezone('UTC'::text, now()) - INTERVAL '1hour' * safePeriod;
  count := 0;

  RAISE NOTICE 'CLEANUP_OAUTH2_TOKENS() .... !';
  -- ------------------------------------------------------
-- BACKUP IDN_OAUTH2_ACCESS_TOKEN TABLE
-- ------------------------------------------------------

  IF (backupTables = 1)
  THEN
    RAISE NOTICE 'TABLE BACKUP STARTED ... !';
    DROP TABLE IF exists PUBLIC.IDN_OAUTH2_ACCESS_TOKEN_BAK;
    SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN;
    RAISE NOTICE 'BACKING UP IDN_OAUTH2_ACCESS_TOKEN AND NUMBER OF TOKENS: %',count;
    RAISE NOTICE 'BACKING UP IDN_OAUTH2_ACCESS_TOKEN TOKENS INTO IDN_OAUTH2_ACCESS_TOKEN_BAK TABLE ...';
    CREATE TABLE PUBLIC.IDN_OAUTH2_ACCESS_TOKEN_BAK as SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN;
    RAISE NOTICE 'BACKING UP IDN_OAUTH2_ACCESS_TOKEN_BAK COMPLETED';

    -- ------------------------------------------------------
-- BACKUP IDN_OAUTH2_AUTHORIZATION_CODE TABLE
-- ------------------------------------------------------
	RAISE NOTICE 'CLEANUP_OAUTH2_TOKENS() .... !';
  	DROP TABLE IF exists PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE_BAK;
    SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE;
	RAISE NOTICE 'BACKING UP IDN_OAUTH2_AUTHORIZATION_CODE AND NUMBER OF CODES: %',count;
	RAISE NOTICE 'BACKING UP IDN_OAUTH2_AUTHORIZATION_CODE INTO IDN_OAUTH2_AUTHORIZATION_CODE_BAK TABLE ...';
    CREATE TABLE PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE_BAK as SELECT * FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE;
    RAISE NOTICE 'BACKING UP IDN_OAUTH2_AUTHORIZATION_CODE_BAK COMPLETED';

  END IF;

-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- CREATING IDN_OAUTH2_ACCESS_TOKEN_CLEANUP_AUDITLOG a nd IDN_OAUTH2_AUTHORIZATION_CODE_CLEANUP_AUDITLOGFOR DELETING
--TOKENS and authorization codes
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

IF (enableAudit)
THEN
    CREATE TABLE IF NOT EXISTS PUBLIC.IDN_OAUTH2_ACCESS_TOKEN_CLEANUP_AUDITLOG as SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN WHERE 1 = 2;

    CREATE TABLE IF NOT EXISTS PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE_CLEANUP_AUDITLOG as SELECT * FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE WHERE 1 = 2;

END IF;

-- ------------------------------------------------------
-- BATCH DELETE IDN_OAUTH2_ACCESS_TOKEN
-- ------------------------------------------------------
RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_ACCESS_TOKEN STARTED .... !';

SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN;

RAISE NOTICE 'TOTAL TOKENS ON IDN_OAUTH2_ACCESS_TOKEN TABLE BEFORE DELETE: %',count;

SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_STATE IN ('EXPIRED','INACTIVE','REVOKED') OR (TOKEN_STATE='ACTIVE'
AND (deleteTillTime > TIME_CREATED + INTERVAL '1minute' * ((VALIDITY_PERIOD/1000)/60)) AND (deleteTillTime > REFRESH_TOKEN_TIME_CREATED + INTERVAL '1minute' * (
(REFRESH_TOKEN_VALIDITY_PERIOD/1000)/60)));

RAISE NOTICE 'TOTAL TOKENS SHOULD BE DELETED FROM IDN_OAUTH2_ACCESS_TOKEN: %',count;

SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_STATE='ACTIVE' AND ((deleteTillTime < TIME_CREATED + INTERVAL '1minute' * ((VALIDITY_PERIOD/1000)/60)) OR (deleteTillTime <  REFRESH_TOKEN_TIME_CREATED + INTERVAL '1minute' * (
(REFRESH_TOKEN_VALIDITY_PERIOD/1000)/60)));

RAISE NOTICE 'TOTAL TOKENS SHOULD BE RETAIN IN IDN_OAUTH2_ACCESS_TOKEN: %',count;

IF (enableAudit)
THEN
  INSERT INTO PUBLIC.IDN_OAUTH2_ACCESS_TOKEN_CLEANUP_AUDITLOG SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_STATE IN
  ('EXPIRED','INACTIVE','REVOKED') OR (TOKEN_STATE='ACTIVE' AND (deleteTillTime > TIME_CREATED + INTERVAL '1minute' *
   ((VALIDITY_PERIOD/1000)/60)) AND (deleteTillTime > REFRESH_TOKEN_TIME_CREATED + INTERVAL
    '1minute' * ((REFRESH_TOKEN_VALIDITY_PERIOD/1000)/60)));
END IF;

LOOP

IF rowCount > 0
THEN
    perform pg_sleep(sleepTime);
END IF;

DELETE FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN WHERE TOKEN_STATE IN ('EXPIRED','INACTIVE','REVOKED') OR (TOKEN_STATE='ACTIVE'
AND (deleteTillTime > TIME_CREATED + INTERVAL '1minute' * ((VALIDITY_PERIOD/1000)/60)) AND (deleteTillTime > REFRESH_TOKEN_TIME_CREATED + INTERVAL '1minute' * (
(REFRESH_TOKEN_VALIDITY_PERIOD/1000)/60)));

GET diagnostics rowCount := ROW_COUNT;

RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_ACCESS_TOKEN : %',rowCount;

exit WHEN rowCount=0;

RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_ACCESS_TOKEN COMPLETED .... !';

END loop;

-- ------------------------------------------------------
-- BATCH DELETE IDN_OAUTH2_AUTHORIZATION_CODE
-- ------------------------------------------------------

RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_AUTHORIZATION_CODE STARTED .... !';

SELECT count(*) INTO count FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE;

RAISE NOTICE 'TOTAL AUTHORIZATION CODES ON IDN_OAUTH2_AUTHORIZATION_CODE TABLE BEFORE DELETE: %',count;

SELECT COUNT(*) INTO count FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE WHERE CODE_ID IN ( SELECT * FROM ( SELECT CODE_ID FROM
    PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE code WHERE NOT EXISTS ( SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN token WHERE token
    .TOKEN_ID = code.TOKEN_ID AND token.TOKEN_STATE = 'ACTIVE') AND code.STATE NOT IN ( 'ACTIVE' ) ) as x) OR deleteTillTime > ( TIME_CREATED + INTERVAL '1minute' * (( VALIDITY_PERIOD / 1000 )/ 60 ));

RAISE NOTICE 'TOTAL AUTHORIZATION CODES SHOULD BE DELETED FROM IDN_OAUTH2_AUTHORIZATION_CODE: %', count;

IF (enableAudit)
THEN
  INSERT INTO PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE_CLEANUP_AUDITLOG  SELECT * FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE acode WHERE
   NOT EXISTS (SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN tok WHERE tok.TOKEN_ID = acode.TOKEN_ID) OR STATE NOT IN
   ('ACTIVE') OR deleteTillTime > (TIME_CREATED + INTERVAL '1minute' * ((VALIDITY_PERIOD/1000)/60)) OR TOKEN_ID IS
    NULL;
  INSERT INTO PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE_CLEANUP_AUDITLOG  SELECT * FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE WHERE
  CODE_ID IN ( SELECT * FROM ( SELECT CODE_ID FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE code WHERE NOT EXISTS ( SELECT *
  FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN token WHERE token.TOKEN_ID = code.TOKEN_ID AND token.TOKEN_STATE = 'ACTIVE') AND code
  .STATE NOT IN ( 'ACTIVE' ) ) as x) OR  deleteTillTime > ( TIME_CREATED + INTERVAL '1minute' * (( VALIDITY_PERIOD / 1000 )/ 60 ));

END IF;

LOOP
IF rowCount > 0
THEN
    perform pg_sleep(sleepTime);
END IF;
    DELETE FROM PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE WHERE CODE_ID in ( SELECT * FROM ( SELECT CODE_ID FROM
    PUBLIC.IDN_OAUTH2_AUTHORIZATION_CODE code WHERE NOT EXISTS ( SELECT * FROM PUBLIC.IDN_OAUTH2_ACCESS_TOKEN token WHERE token
    .TOKEN_ID = code.TOKEN_ID AND token.TOKEN_STATE = 'ACTIVE') AND code.STATE NOT IN ( 'ACTIVE' ) ) as x) OR deleteTillTime > ( TIME_CREATED + INTERVAL '1minute' * (( VALIDITY_PERIOD / 1000 )/ 60 ));
GET diagnostics rowCount := ROW_COUNT;
RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_AUTHORIZATION_CODE : %',rowCount;
exit WHEN rowCount=0;
RAISE NOTICE 'BATCH DELETE ON IDN_OAUTH2_AUTHORIZATION_CODE COMPLETED .... !';
END loop;

END;
$$
LANGUAGE 'plpgsql';
