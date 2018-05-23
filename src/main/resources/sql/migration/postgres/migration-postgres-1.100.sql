create table carrier (
  id bigint not null,
  name character varying(100),
  iata_code character varying(100),
  created_at timestamp without time zone,
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

ALTER TABLE billing_file ADD fk_carrier_id bigint;
ALTER TABLE bo_file ADD fk_carrier_id bigint;
ALTER TABLE processing_record ADD fk_carrier_id bigint;
ALTER TABLE processing_record ADD utrnno character varying(20);

create sequence bsp_fe_utrnno_seq start with 10000; --set start value

