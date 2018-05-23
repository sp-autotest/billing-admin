create table "CARRIER" (
  "ID" NUMBER(19,0) NOT NULL ENABLE,
  "NAME" VARCHAR2(255 CHAR),
  "IATA_CODE" VARCHAR2(255 CHAR),
  "CREATED_AT" TIMESTAMP,
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

ALTER TABLE billing_file ADD fk_carrier_id NUMBER(19,0);
ALTER TABLE bo_file ADD fk_carrier_id NUMBER(19,0);
ALTER TABLE processing_record ADD fk_carrier_id NUMBER(19,0);
ALTER TABLE processing_record ADD utrnno VARCHAR2(20 CHAR);

create sequence bsp_fe_utrnno_seq start with 10000; --set start value