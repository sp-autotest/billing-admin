drop table if exists billing_file cascade;
drop table if exists billing_terminal cascade;
drop table if exists bo_file cascade;
drop table if exists report_file cascade;
drop table if exists country_currency cascade;
drop table if exists file_record cascade;
drop table if exists posting_file cascade;
drop table if exists processing_file cascade;
drop table if exists processing_record cascade;
drop table if exists system_settings cascade;
--drop TABLE if EXISTS "users" CASCADE ;
--drop table if exists user_history CASCADE ;
--drop table if exists carrier CASCADE ;
--drop table if exists terminal CASCADE ;
--drop table if exists terminal_countrycurrency CASCADE ;
drop sequence SEQ_COUNTRY_CURRENCY;
drop sequence SEQ_OPTIONS;
drop sequence SEQ_PROCESSING_FILE;
drop sequence SEQ_PROCESSING_RECORD;
--drop sequence SEQ_TERMINAL;
--drop sequence SEQ_CARRIER;
--drop sequence SEQ_USER;
--drop sequence SEQ_USER_HISTORY;


CREATE TABLE country_currency
(
  id bigint NOT NULL,
  country_code character varying(255) NOT NULL,
  currency_numeric_code integer NOT NULL,
  CONSTRAINT country_currency_pkey PRIMARY KEY (id),
  CONSTRAINT country_currency_country_code_currency_numeric_code_key UNIQUE (country_code, currency_numeric_code)
);
CREATE TABLE billing_terminal
(
  country_code character varying(255) NOT NULL,
  terminal_id character varying(255) NOT NULL,
  CONSTRAINT billing_terminal_pkey PRIMARY KEY (country_code)
);
CREATE TABLE system_settings
(
  id bigint NOT NULL,
  encoder character varying(255),
  modifier character varying(255),
  modify_date timestamp without time zone,
  name character varying(255) NOT NULL,
  value character varying(255),
  visibility character varying(255),
  CONSTRAINT system_settings_pkey PRIMARY KEY (id),
  CONSTRAINT system_settings_name_key UNIQUE (name)
);
CREATE TABLE processing_file
(
  id bigint NOT NULL,
  business_date date,
  created_date timestamp without time zone,
  file_type character varying(255),
  name character varying(255),
  original_file_name character varying(255),
  parent_id bigint,
  CONSTRAINT processing_file_pkey PRIMARY KEY (id)
);
CREATE TABLE billing_file
(
  count_lines integer,
  format character varying(255) NOT NULL,
  processing_date timestamp without time zone,
  id bigint NOT NULL,
  fk_carrier_id bigint,
  CONSTRAINT billing_file_pkey PRIMARY KEY (id)
);
CREATE TABLE bo_file
(
  format character varying(255) NOT NULL,
  id bigint NOT NULL,
  fk_carrier_id bigint,
  CONSTRAINT bo_file_pkey PRIMARY KEY (id)
);
CREATE TABLE report_file
(
  id bigint NOT NULL,
  CONSTRAINT report_file_pkey PRIMARY KEY (id)
);
CREATE TABLE posting_file
(
  format character varying(255) NOT NULL,
  id bigint NOT NULL,
  CONSTRAINT posting_file_pkey PRIMARY KEY (id)
);
CREATE TABLE file_record
(
  file_id bigint NOT NULL,
  record_id bigint NOT NULL,
  CONSTRAINT file_record_pkey PRIMARY KEY (file_id, record_id)
);
CREATE TABLE processing_record
(
  id bigint NOT NULL,
  amount integer,
  amount_mps integer,
  amount_rub integer,
  approval_code character varying(255),
  country_code character varying(255),
  create_date timestamp without time zone,
  currency character varying(255),
  document_date character varying(255),
  document_number character varying(255),
  error_message character varying(255),
  expiry character varying(255),
  invoice_date character varying(255),
  invoice_number character varying(255),
  pan character varying(255),
  rate_cb character varying(255),
  rate_mps character varying(255),
  rbs_id character varying(255),
  ref_num character varying(255),
  status character varying(255),
  transaction_type character varying(255),
  parent_id bigint,
  fk_carrier_id bigint,
  utrnno character varying(20),
  CONSTRAINT processing_record_pkey PRIMARY KEY (id),
  CONSTRAINT processing_record_rbs_id_key UNIQUE (rbs_id)
);
create index approval_code_index on processing_record (approval_code);
create index document_number_index on processing_record (document_number);
create index pan_index on processing_record (pan);

create table users (
  id bigint not null,
  username character varying (100),
  password character varying (100),
  updated_at timestamp without time zone,
  credentials_expired_at timestamp without time zone,
  is_locked boolean default false,
  is_enabled boolean default false,
  is_account_expired boolean default false,
  password_history character varying(1000),
  roles character varying(1000),
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username UNIQUE (username)
);

create table user_history (
  id bigint not null,
  user_id bigint,
  created_at timestamp without time zone,
  action character varying(100),
  old_value character varying(255),
  new_value character varying(255),
  message character varying(255),
  status boolean default null,
  CONSTRAINT user_history_pkey PRIMARY KEY (id)
);

create sequence SEQ_COUNTRY_CURRENCY;
create sequence SEQ_OPTIONS;
create sequence SEQ_PROCESSING_FILE;
create sequence SEQ_PROCESSING_RECORD;
create sequence SEQ_USER;
create sequence SEQ_USER_HISTORY;

CREATE TABLE billing_system
(
  id           BIGINT NOT NULL
    CONSTRAINT billing_system_pkey
    PRIMARY KEY,
  created_date TIMESTAMP,
  enabled      BOOLEAN,
  host_address VARCHAR(255),
  login        VARCHAR(255),
  mask_regexp  VARCHAR(255),
  name         VARCHAR(255)
    CONSTRAINT billing_system_name_key
    UNIQUE,
  password     VARCHAR(255),
  path         VARCHAR(255),
  sftp_port    INTEGER,
  carrier_id   BIGINT
    CONSTRAINT fk79be9c7389ba607
    REFERENCES carrier
);

CREATE TABLE billing_systems_emails
(
  id     BIGINT NOT NULL
    CONSTRAINT fk283ec716a66b4f32
    REFERENCES billing_system,
  emails VARCHAR(200)
);

--admin/pivot43\very
insert into users (updated_at, credentials_expired_at, is_account_expired, is_enabled, is_locked, password, password_history, username, id, roles) values (now(), null, '0', '1', '0', '95d05639c6dca65c9d68cdd55415af8b11631206ba97e1713415942e64e1006ad414fcb289e95889', '95d05639c6dca65c9d68cdd55415af8b11631206ba97e1713415942e64e1006ad414fcb289e95889;', 'admin', nextval('SEQ_USER'), null);

insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AU','036');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AZ','944');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AL','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BA','977');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GB','826');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'HU','348');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'DE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'HK','344');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GR','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'DK','208');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'EG','818');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'JO','400');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'IE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'ES','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'IT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KZ','398');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CA','124');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CY','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'QZ','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LV','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LB','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LT','440');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'IL','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LU','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MK','807');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MY','458');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'NI','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'NZ','554');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'NO','578');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AE','784');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PA','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PL','985');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'RO','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SA','682');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'RS','941');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SK','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SI','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'US','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'TH','764');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'TW','901');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'TR','949');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'UA','980');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'FI','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'FR','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'HR','191');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'ME','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CZ','203');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CH','756');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SE','752');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'EE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KR','410');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'JP','392');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CN','156');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'IN','356');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LK','144');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MN','496');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BG','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BG','975');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GL','208');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'IS','352');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'AD','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PM','124');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BM','060');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'OM','512');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'QA','634');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'BH','048');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MD','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KH','116');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KH','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GF','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MC','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CK','554');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'VN','704');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'VN','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MO','446');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'CR','188');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KI','036');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'FJ','242');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'WS','882');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'TO','776');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GI','826');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'KG','417');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SM','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GP','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'MQ','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'RE','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'LI','756');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'ID','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'YT','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'NL','978');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'SG','702');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PH','608');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'GE','981');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'PH','840');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'RU', '643');
insert into country_currency (id, country_code, currency_numeric_code) values (nextval('SEQ_COUNTRY_CURRENCY'),'RU', '810');

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

insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'billing.converter.count_available_reject_record','3');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'report.type','STANDARD');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'automate.enabled','false');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'automate.ascKeyPath','/usr/local/jetty-services2/config/billing-admin/secret.asc');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'automate.keySecret','123');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'is_refund_as_reversal','true');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'mail.alfacard','NOT_USED_NOW');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'mail.esupport','myworktech@gmail.com');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'mail.raschet','NOT_USED_NOW');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'scheduling.cron.bo','0 30 23 * * *');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'scheduling.cron.bsp','30 44 13 * * *');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'server.params.bo','{"address":"www.bo_server.com","port":1,"path":"/some/path/to/bo/files/","login":"bo_login","password":"bo_password"}');
insert into system_settings (id,name,value) values (nextval('SEQ_OPTIONS'),'server.params.posting','{"address":"10.77.5.93","port":8022,"path":"/chroot/postbofile/local/","login":"postbofile","password":"PostBOFile"}');

create table carrier (
  id bigint not null,
  name character varying(100),
  iata_code character varying(100),
  created_at timestamp without time zone,
  mcc CHARACTER VARYING(20),
  CONSTRAINT carrier_pkey PRIMARY KEY (id),
  CONSTRAINT carrier_name_code_unique UNIQUE (name),
  CONSTRAINT carrier_iata_code_unique UNIQUE (iata_code)
);

create sequence SEQ_CARRIER;

create table terminal (
  id bigint not null,
  name character varying(100),
  agrn character varying(100),
  terminal character varying(100),
  carrier_id bigint not null,
  CONSTRAINT country_pkey PRIMARY KEY (id),
  CONSTRAINT terminal_name_unique UNIQUE (name),
  CONSTRAINT terminal_agrn_unique UNIQUE (agrn),
  CONSTRAINT terminal_terminal_unique UNIQUE (terminal)
);

create sequence SEQ_TERMINAL;

create table terminal_countrycurrency (
  terminal bigint not null,
  countrycurrency bigint not null
);

create sequence bsp_fe_utrnno_seq start with 10000; --set start value

---
--drop table processing_file cascade;
--drop table billing_file cascade;
--drop table bo_file;
--drop table posting_file;
--drop table report_file;
--drop table file_record cascade;
--drop table processing_record cascade;

--truncate processing_file cascade;
--truncate billing_file cascade;
--truncate bo_file cascade;
--truncate posting_file cascade;
--truncate report_file cascade;
--truncate file_record cascade;
--truncate processing_record cascade;





