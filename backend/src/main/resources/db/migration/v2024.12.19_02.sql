delete from fr_spotify_episode;


create table fr_spotify_show
(
  id          numeric primary key not null,
  name        varchar(255)        not null,
  external_id varchar(255)        not null,
  uri         varchar(255)        not null
);

alter table fr_spotify_episode
  add column show_id      numeric      not null references fr_spotify_show (id),
  add column uri          varchar(255) not null,
  add column duration_s   numeric      not null,
  add column release_date date         not null;


