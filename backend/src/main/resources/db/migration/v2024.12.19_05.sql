alter table fr_spotify_episode rename duration_s to duration_sec;

create table fr_youtube_channel (
  id numeric primary key not null,
  name varchar(255) not null
);

create table fr_youtube_video (
  id numeric primary key not null,
  channel_id numeric not null,
  name varchar(255) not null,
  published_at timestamp not null,
  duration_sec integer not null,
  foreign key (channel_id) references fr_youtube_channel(id)
);
