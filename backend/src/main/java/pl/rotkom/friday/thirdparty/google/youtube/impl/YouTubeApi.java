package pl.rotkom.friday.thirdparty.google.youtube.impl;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;

import lombok.extern.slf4j.Slf4j;


import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.common.GoogleCommons;
import pl.rotkom.friday.thirdparty.google.oauth2.GoogleOauth2Manager;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.ChannelDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.VideoDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.VideoTypeDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.service.IYouTubeApi;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
class YouTubeApi implements IYouTubeApi {

    private final YouTube youtube;

    private final HttpClient httpClient = HttpClient.create();

    public YouTubeApi(GoogleOauth2Manager oauth2Manager, GoogleConfig config) {
        this.youtube = new YouTube.Builder(GoogleCommons.HTTP_TRANSPORT, GoogleCommons.JSON_FACTORY, oauth2Manager.getCredentials())
                .setApplicationName(config.getProjectId())
                .build();
    }

    @Override
    public List<ChannelDTO> getSubscribedChannels() {
        List<ChannelDTO> channels = new ArrayList<>();
        String nextPageToken = null;
        do {
            try {
                var response = youtube.subscriptions().list(List.of("snippet"))
                        .setMine(Boolean.TRUE)
                        .setPageToken(nextPageToken)
                        .setMaxResults(50L)
                        .execute();

                response.getItems().forEach(item -> {
                    var channel = new ChannelDTO();
                    channel.setId(item.getSnippet().getResourceId().getChannelId());
                    channel.setTitle(item.getSnippet().getTitle());
                    channels.add(channel);
                });
                nextPageToken = response.getNextPageToken();
            } catch (Exception e) {
                log.error("Error fetching subscribed channels", e);
            }

        } while (nextPageToken != null);

        return channels;
    }

    @Override
    public List<String> getLatestVideoIds(List<String> channelIds) {
        return Flux.fromIterable(channelIds)
                .flatMap(channelId -> {
                    var req = httpClient.get().uri("https://www.youtube.com/feeds/videos.xml?channel_id=" + channelId);
                    return req.responseSingle((resp, buff) -> buff.asString(StandardCharsets.UTF_8));
                }, 10)
                .flatMap(xml -> {
                    List<String> ids = new ArrayList<>();

                    try {
                        var syndFeedInput = new SyndFeedInput();
                        var build = syndFeedInput.build(new StringReader(xml));

                        for (Object entry : build.getEntries().subList(0, Math.min(5, build.getEntries().size()))) {
                            ids.add(((SyndEntry) entry).getUri().replace("yt:video:", ""));
                        }
                    } catch (Exception e) {
                        log.error("Error parsing xml content", e);
                        return Flux.empty();
                    }
                    return Flux.fromIterable(ids);
                })
                .collectList()
                .blockOptional().orElse(Collections.emptyList());
    }

    @Override
    public List<VideoDTO> getVideos(List<String> videoIds) {
        return Lists.partition(videoIds, 50).parallelStream()
                .flatMap(batchIds -> {
                    try {
                        return youtube.videos().list(List.of("id", "snippet", "contentDetails"))
                                .setId(batchIds)
                                .execute()
                                .getItems().stream()
                                .filter(item -> item.getContentDetails().getDuration() != null)
                                .map(item -> {
                                    VideoDTO dto = new VideoDTO();
                                    dto.setId(item.getId());
                                    dto.setChannelId(item.getSnippet().getChannelId());
                                    dto.setChannelTitle(item.getSnippet().getChannelTitle());
                                    dto.setPublishedAt(
                                            Instant.ofEpochMilli(item.getSnippet().getPublishedAt().getValue()));
                                    dto.setTitle(item.getSnippet().getTitle());
                                    dto.setUrl("https://youtube.com/watch?v=" + item.getId());

                                    var duration = Duration.parse(item.getContentDetails().getDuration());
                                    dto.setDuration(duration);
                                    dto.setType(duration.getSeconds() <= 120 ? VideoTypeDTO.SHORT : VideoTypeDTO.VIDEO);

                                    if (item.getSnippet().getDefaultLanguage() != null) {
                                        dto.setLanguage(Locale.forLanguageTag(item.getSnippet().getDefaultLanguage()));
                                    }

                                    return dto;
                                });
                    } catch (Exception e) {
                        log.error("Error fetching new videos", e);
                        return Stream.empty();
                    }
                })
                .toList();
    }
}


//def get_transcription(self, video_id):
//url = f"https://youtubetranscript.com/?server_vid2={video_id}"
//        logger.info(f'Getting transcription from: {url}')
//
//        try:
//root = ET.fromstring(requests.get(url).text)
//error_element = root.find("error")
//            if error_element is not None:
//        logger.warning(f"Transcription error: {error_element.text}")
//                return None
//            return "\n".join([child.text for child in root])
//except Exception as e:
//        logger.error(f'Error occurred while getting transcription: {e}')
//            return None
//
//def get_channel_videos(self, channel_id):
//rss_url = 'https://www.youtube.com/feeds/videos.xml?channel_id=' + channel_id
//        response = requests.get(rss_url)
//        if response.status_code == 200:
//data = feedparser.parse(response.text)
//            return [Video(
//        item['id'],
//        item['link'],
//        item['title'],
//        datetime.datetime.strptime(item['published'], "%Y-%m-%dT%H:%M:%S%z").replace(tzinfo=pytz.utc),
//channel_id,
//item['author']
//        ) for item in data.entries]
//        else:
//        logger.error(f'Failed to retrieve videos from channel. Error: {response}')
//
//def get_new_videos(self, channel, limit):
//videos = self.get_channel_videos(channel.id)
//        return videos[:limit]
