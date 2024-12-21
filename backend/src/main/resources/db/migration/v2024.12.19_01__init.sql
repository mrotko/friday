CREATE SEQUENCE hibernate_sequence
  START WITH 1
  INCREMENT BY 1
  CACHE 50
  NO CYCLE;

create table fr_spotify_episode (
  id numeric primary key not null,
  name varchar(255) not null,
  external_id varchar(255) not null
)
