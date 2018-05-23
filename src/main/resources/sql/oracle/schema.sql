--truncate table processing_file;
--truncate table billing_file ;
--truncate table bo_file ;
--truncate table posting_file ;
--truncate table processing_record;
--truncate table file_record;
--truncate table billing_terminal;


drop table processing_file cascade constraints;
drop table billing_file cascade constraints;
drop table bo_file cascade constraints;
drop table report_file cascade constraints;
drop table posting_file cascade constraints;
drop table processing_record cascade constraints;
drop table file_record cascade constraints;
drop table billing_terminal cascade constraints;
drop table country_currency cascade constraints;
drop table system_settings cascade constraints;
drop sequence SEQ_COUNTRY_CURRENCY;
drop sequence SEQ_OPTIONS;
drop sequence SEQ_PROCESSING_FILE;
drop sequence SEQ_PROCESSING_RECORD;

CREATE TABLE "COUNTRY_CURRENCY" (
  "ID" NUMBER(19,0) NOT NULL ENABLE,
	"COUNTRY_CODE" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"CURRENCY_NUMERIC_CODE" NUMBER(10,0) NOT NULL ENABLE,
	 PRIMARY KEY ("ID"),
	 UNIQUE ("COUNTRY_CODE", "CURRENCY_NUMERIC_CODE")
);
CREATE TABLE "BILLING_TERMINAL" (
  "COUNTRY_CODE" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"TERMINAL_ID" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	 PRIMARY KEY ("COUNTRY_CODE")
);
CREATE TABLE "SYSTEM_SETTINGS" (
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"ENCODER" VARCHAR2(255 CHAR),
	"MODIFIER" VARCHAR2(255 CHAR),
	"MODIFY_DATE" TIMESTAMP (6),
	"NAME" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"VALUE" VARCHAR2(255 CHAR),
	"VISIBILITY" VARCHAR2(255 CHAR),
	 PRIMARY KEY ("ID")
);
CREATE TABLE "PROCESSING_FILE" (
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"BUSINESS_DATE" DATE,
	"CREATED_DATE" TIMESTAMP (6),
	"FILE_TYPE" VARCHAR2(255 CHAR),
	"NAME" VARCHAR2(255 CHAR),
	"ORIGINAL_FILE_NAME" VARCHAR2(255 CHAR),
	"PARENT_ID" NUMBER(19,0),
	 PRIMARY KEY ("ID")
);
CREATE TABLE "BILLING_FILE" (
  "COUNT_LINES" NUMBER(10,0),
	"FORMAT" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"PROCESSING_DATE" TIMESTAMP,
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"fk_carrier_id" NUMBER(19,0),
	PRIMARY KEY ("ID")
);
CREATE TABLE "BO_FILE" (
	"FORMAT" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"fk_carrier_id" NUMBER(19,0),
	 PRIMARY KEY ("ID")
);
CREATE TABLE "REPORT_FILE" (
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	 PRIMARY KEY ("ID")
);
CREATE TABLE "POSTING_FILE" (
  "FORMAT" VARCHAR2(255 CHAR) NOT NULL ENABLE,
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	 PRIMARY KEY ("ID")
);
CREATE TABLE "FILE_RECORD" (
	"FILE_ID" NUMBER(19,0),
	"RECORD_ID" NUMBER(19,0),
	 PRIMARY KEY ("FILE_ID", "RECORD_ID")
);
CREATE TABLE "PROCESSING_RECORD" (
	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"AMOUNT" NUMBER(10,0),
	"AMOUNT_MPS" NUMBER(10,0),
	"AMOUNT_RUB" NUMBER(10,0),
	"APPROVAL_CODE" VARCHAR2(255 CHAR),
	"COUNTRY_CODE" VARCHAR2(255 CHAR),
	"CREATE_DATE" TIMESTAMP (6),
	"CURRENCY" VARCHAR2(255 CHAR),
	"DOCUMENT_DATE" VARCHAR2(255 CHAR),  -------------------------
	"DOCUMENT_NUMBER" VARCHAR2(255 CHAR),--------------------------
	"ERROR_MESSAGE" VARCHAR2(255 CHAR),
	"EXPIRY" VARCHAR2(255 CHAR),
	"INVOICE_DATE" VARCHAR2(255 CHAR),
	"INVOICE_NUMBER" VARCHAR2(255 CHAR),
	"PAN" VARCHAR2(255 CHAR),
	"RATE_CB" VARCHAR2(255 CHAR),
	"RATE_MPS" VARCHAR2(255 CHAR),
	"RBS_ID" VARCHAR2(255 CHAR),    ------------------------------------
	"REF_NUM" VARCHAR2(255 CHAR),
	"STATUS" VARCHAR2(255 CHAR),
	"TRANSACTION_TYPE" VARCHAR2(255 CHAR), ----------------------------
	"PARENT_ID" NUMBER(19,0),
	"fk_carrier_id" NUMBER(19,0),
	"utrnno" VARCHAR2(20 CHAR),
	 PRIMARY KEY ("ID"),
	 UNIQUE ("RBS_ID")
);
create index approval_code_index on processing_record (approval_code);
create index document_number_index on processing_record (document_number);
create index pan_index on processing_record (pan);

create table users (
  id NUMBER(19,0) NOT NULL ENABLE,
  username VARCHAR2(100 CHAR),
  password VARCHAR2(100 CHAR),
  updated_at TIMESTAMP,
  credentials_expired_at TIMESTAMP,
  is_locked decimal(1) default 0 not null,
  is_enabled decimal(1) default 0 not null,
  is_account_expired decimal(1) default 0 not null,
  password_history VARCHAR2(1000),
  roles VARCHAR2(1000),
  UNIQUE (username)
);

create table user_history (
  id NUMBER(19,0) NOT NULL ENABLE,
  user_id NUMBER(19,0),
  created_at timestamp,
  action VARCHAR2(100 CHAR),
  old_value VARCHAR2(255 CHAR),
  new_value VARCHAR2(255 CHAR),
  message VARCHAR2(255 CHAR),
  status decimal(1) null
);

create sequence SEQ_COUNTRY_CURRENCY;
create sequence SEQ_OPTIONS;
create sequence SEQ_PROCESSING_FILE;
create sequence SEQ_PROCESSING_RECORD;
create sequence SEQ_USER;
create sequence SEQ_USER_HISTORY;

insert into users (updated_at, credentials_expired_at, is_account_expired, is_enabled, is_locked, password, password_history, username, id, roles) values (CURRENT_DATE, null, '0', '1', '0', '32bd066c4220173140a91f61702f0194e51ffc50878c416a5c1a75c713688ae404d28e648f95f6fd', '32bd066c4220173140a91f61702f0194e51ffc50878c416a5c1a75c713688ae404d28e648f95f6fd;', 'admin', SEQ_USER.nextval, null);
insert into users (updated_at, credentials_expired_at, is_account_expired, is_enabled, is_locked, password, password_history, username, id, roles) values (CURRENT_DATE, null, '0', '1', '0', '193539e3fa8a396de544a3640ecfcd4ba29f7f62b9cf8f267e9a0ea11f87b8c521107a4cc010b9a4', '193539e3fa8a396de544a3640ecfcd4ba29f7f62b9cf8f267e9a0ea11f87b8c521107a4cc010b9a4;', 'esupport', SEQ_USER.nextval, null);


insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AU','036');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AZ','944');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AL','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BA','977');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GB','826');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'HU','348');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'DE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'HK','344');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GR','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'DK','208');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'EG','818');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'JO','400');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'IE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'ES','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'IT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KZ','398');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CA','124');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CY','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'QZ','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LV','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LB','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LT','440');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'IL','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LU','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MK','807');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MY','458');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'NI','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'NZ','554');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'NO','578');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AE','784');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PA','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PL','985');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'RO','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SA','682');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'RS','941');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SK','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SI','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'US','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'TH','764');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'TW','901');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'TR','949');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'UA','980');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'FI','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'FR','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'HR','191');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'ME','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CZ','203');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CH','756');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SE','752');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'EE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KR','410');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'JP','392');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CN','156');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'IN','356');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LK','144');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MN','496');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BG','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BG','975');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GL','208');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'IS','352');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'AD','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PM','124');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BM','060');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'OM','512');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'QA','634');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'BH','048');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MD','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KH','116');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KH','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GF','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MC','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CK','554');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'VN','704');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'VN','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MO','446');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'CR','188');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KI','036');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'FJ','242');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'WS','882');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'TO','776');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GI','826');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'KG','417');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SM','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GP','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'MQ','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'RE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'LI','756');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'ID','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'YT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'NL','978');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'SG','702');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PH','608');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'GE','981');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'PH','840');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'RU', '643');
insert into country_currency (id, country_code, currency_numeric_code) values (SEQ_COUNTRY_CURRENCY.nextval,'RU', '810');

INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AB-terminal', 'AB');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AU-terminal', 'AU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AT-terminal', 'AT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AZ-terminal', 'AZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AL-terminal', 'AL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DZ-terminal', 'DZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AS-terminal', 'AS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AI-terminal', 'AI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AO-terminal', 'AO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AD-terminal', 'AD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AQ-terminal', 'AQ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AG-terminal', 'AG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AR-terminal', 'AR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AM-terminal', 'AM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AW-terminal', 'AW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AF-terminal', 'AF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BS-terminal', 'BS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BD-terminal', 'BD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BB-terminal', 'BB');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BH-terminal', 'BH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BY-terminal', 'BY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BZ-terminal', 'BZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BE-terminal', 'BE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BJ-terminal', 'BJ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BM-terminal', 'BM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BG-terminal', 'BG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BO-terminal', 'BO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BQ-terminal', 'BQ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BA-terminal', 'BA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BW-terminal', 'BW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BR-terminal', 'BR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IO-terminal', 'IO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BN-terminal', 'BN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BF-terminal', 'BF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BI-terminal', 'BI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BT-terminal', 'BT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VU-terminal', 'VU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HU-terminal', 'HU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VE-terminal', 'VE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VG-terminal', 'VG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VI-terminal', 'VI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VN-terminal', 'VN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GA-terminal', 'GA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HT-terminal', 'HT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GY-terminal', 'GY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GM-terminal', 'GM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GH-terminal', 'GH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GP-terminal', 'GP');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GT-terminal', 'GT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GN-terminal', 'GN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GW-terminal', 'GW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DE-terminal', 'DE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GG-terminal', 'GG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GI-terminal', 'GI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HN-terminal', 'HN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HK-terminal', 'HK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GD-terminal', 'GD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GL-terminal', 'GL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GR-terminal', 'GR');
--INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GE-terminal', 'GE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GU-terminal', 'GU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DK-terminal', 'DK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('JE-terminal', 'JE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DJ-terminal', 'DJ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DM-terminal', 'DM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('DO-terminal', 'DO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('EG-terminal', 'EG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ZM-terminal', 'ZM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('EH-terminal', 'EH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ZW-terminal', 'ZW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IL-terminal', 'IL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IN-terminal', 'IN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ID-terminal', 'ID');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('JO-terminal', 'JO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IQ-terminal', 'IQ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IR-terminal', 'IR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IE-terminal', 'IE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IS-terminal', 'IS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ES-terminal', 'ES');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IT-terminal', 'IT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('YE-terminal', 'YE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CV-terminal', 'CV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KZ-terminal', 'KZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KH-terminal', 'KH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CM-terminal', 'CM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CA-terminal', 'CA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('QA-terminal', 'QA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KE-terminal', 'KE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CY-terminal', 'CY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KG-terminal', 'KG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KI-terminal', 'KI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CN-terminal', 'CN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CC-terminal', 'CC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CO-terminal', 'CO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KM-terminal', 'KM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CG-terminal', 'CG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CD-terminal', 'CD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KP-terminal', 'KP');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KR-terminal', 'KR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CR-terminal', 'CR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CI-terminal', 'CI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CU-terminal', 'CU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KW-terminal', 'KW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CW-terminal', 'CW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LA-terminal', 'LA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LV-terminal', 'LV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LS-terminal', 'LS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LB-terminal', 'LB');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LY-terminal', 'LY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LR-terminal', 'LR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LI-terminal', 'LI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LT-terminal', 'LT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LU-terminal', 'LU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MU-terminal', 'MU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MR-terminal', 'MR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MG-terminal', 'MG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('YT-terminal', 'YT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MO-terminal', 'MO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MW-terminal', 'MW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MY-terminal', 'MY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ML-terminal', 'ML');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('UM-terminal', 'UM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MV-terminal', 'MV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MT-terminal', 'MT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MA-terminal', 'MA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MQ-terminal', 'MQ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MH-terminal', 'MH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MX-terminal', 'MX');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FM-terminal', 'FM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MZ-terminal', 'MZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MD-terminal', 'MD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MC-terminal', 'MC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MN-terminal', 'MN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MS-terminal', 'MS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MM-terminal', 'MM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NA-terminal', 'NA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NR-terminal', 'NR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NP-terminal', 'NP');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NE-terminal', 'NE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NG-terminal', 'NG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NL-terminal', 'NL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NI-terminal', 'NI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NU-terminal', 'NU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NZ-terminal', 'NZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NC-terminal', 'NC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NO-terminal', 'NO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AE-terminal', 'AE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('OM-terminal', 'OM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BV-terminal', 'BV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('IM-terminal', 'IM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('NF-terminal', 'NF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CX-terminal', 'CX');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HM-terminal', 'HM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KY-terminal', 'KY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CK-terminal', 'CK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TC-terminal', 'TC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PK-terminal', 'PK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PW-terminal', 'PW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PS-terminal', 'PS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PA-terminal', 'PA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VA-terminal', 'VA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PG-terminal', 'PG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PY-terminal', 'PY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PE-terminal', 'PE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PN-terminal', 'PN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PL-terminal', 'PL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PT-terminal', 'PT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PR-terminal', 'PR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MK-terminal', 'MK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('RE-terminal', 'RE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('RU-terminal', 'RU');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('RW-terminal', 'RW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('RO-terminal', 'RO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('WS-terminal', 'WS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SM-terminal', 'SM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ST-terminal', 'ST');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SA-terminal', 'SA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SZ-terminal', 'SZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SH-terminal', 'SH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MP-terminal', 'MP');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('BL-terminal', 'BL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('MF-terminal', 'MF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SN-terminal', 'SN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('VC-terminal', 'VC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('KN-terminal', 'KN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LC-terminal', 'LC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PM-terminal', 'PM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('RS-terminal', 'RS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SC-terminal', 'SC');
--INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SG-terminal', 'SG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SX-terminal', 'SX');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SY-terminal', 'SY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SK-terminal', 'SK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SI-terminal', 'SI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GB-terminal', 'GB');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('US-terminal', 'US');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SB-terminal', 'SB');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SO-terminal', 'SO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SD-terminal', 'SD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SR-terminal', 'SR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SL-terminal', 'SL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TJ-terminal', 'TJ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TH-terminal', 'TH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TW-terminal', 'TW');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TZ-terminal', 'TZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TL-terminal', 'TL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TG-terminal', 'TG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TK-terminal', 'TK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TO-terminal', 'TO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TT-terminal', 'TT');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TV-terminal', 'TV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TN-terminal', 'TN');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TM-terminal', 'TM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TR-terminal', 'TR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('UG-terminal', 'UG');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('UZ-terminal', 'UZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('UA-terminal', 'UA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('WF-terminal', 'WF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('UY-terminal', 'UY');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FO-terminal', 'FO');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FJ-terminal', 'FJ');
--INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PH-terminal', 'PH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FI-terminal', 'FI');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FK-terminal', 'FK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('FR-terminal', 'FR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GF-terminal', 'GF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('PF-terminal', 'PF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TF-terminal', 'TF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('HR-terminal', 'HR');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CF-terminal', 'CF');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('TD-terminal', 'TD');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ME-terminal', 'ME');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CZ-terminal', 'CZ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CL-terminal', 'CL');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('CH-terminal', 'CH');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SE-terminal', 'SE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SJ-terminal', 'SJ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('LK-terminal', 'LK');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('EC-terminal', 'EC');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GQ-terminal', 'GQ');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('AX-terminal', 'AX');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SV-terminal', 'SV');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ER-terminal', 'ER');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('EE-terminal', 'EE');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ET-terminal', 'ET');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('ZA-terminal', 'ZA');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('GS-terminal', 'GS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('OS-terminal', 'OS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('SS-terminal', 'SS');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('JM-terminal', 'JM');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('JP-terminal', 'JP');
INSERT INTO billing_terminal (terminal_id, country_code) VALUES ('EU-terminal', 'EU');
insert into billing_terminal (terminal_id, country_code) values ('666186','SG');
insert into billing_terminal (terminal_id, country_code) values ('666188','PH');
insert into billing_terminal (terminal_id, country_code) values ('666190','GE');

insert into system_settings (id,name,value) values (SEQ_OPTIONS.nextval,'billing.converter.count_available_reject_record','3');
insert into system_settings (id,name,value) values (SEQ_OPTIONS.nextval,'report.type','STANDARD');

create table "CARRIER" (
  "ID" NUMBER(19,0) NOT NULL ENABLE,
  "NAME" VARCHAR2(255 CHAR),
  "IATA_CODE" VARCHAR2(255 CHAR),
  "CREATED_AT" TIMESTAMP,
  "MCC" VARCHAR2(20 CHAR),
  PRIMARY KEY ("ID"),
  CONSTRAINT carrier_name_code_unique UNIQUE ("NAME"),
  CONSTRAINT carrier_iata_code_unique UNIQUE ("IATA_CODE")
);

create sequence SEQ_CARRIER;

create table "TERMINAL" (
  "ID" NUMBER(19,0) NOT NULL ENABLE,
  "NAME" VARCHAR2(255 CHAR),
  "AGRN" VARCHAR2(255 CHAR),
  "TERMINAL" VARCHAR2(255 CHAR),
  "CARRIER_ID" NUMBER(19,0) NOT NULL ENABLE,
  PRIMARY KEY ("ID"),
  CONSTRAINT terminal_name_unique UNIQUE ("NAME"),
  CONSTRAINT terminal_agrn_unique UNIQUE ("AGRN"),
  CONSTRAINT terminal_terminal_unique UNIQUE ("TERMINAL")
);

create sequence SEQ_TERMINAL;

create table "TERMINAL_COUNTRYCURRENCY" (
  "TERMINAL" NUMBER(19,0) NOT NULL ENABLE,
  "COUNTRYCURRENCY" NUMBER(19,0) NOT NULL ENABLE
);

create sequence bsp_fe_utrnno_seq start with 10000; --set start value
