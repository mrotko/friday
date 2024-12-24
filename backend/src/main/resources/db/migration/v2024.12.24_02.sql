ALTER TABLE fr_order ADD UNIQUE (external_id);
ALTER TABLE fr_spotify_episode ADD UNIQUE (external_id);
ALTER TABLE fr_spotify_show ADD UNIQUE (external_id);
ALTER TABLE fr_youtube_video ADD UNIQUE (external_id);
ALTER TABLE fr_youtube_channel ADD UNIQUE (external_id);
