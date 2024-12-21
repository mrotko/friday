package pl.rotkom.friday.core.tasks;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeChannel;
import pl.rotkom.friday.core.repository.model.youtube.YouTubeVideo;
import pl.rotkom.friday.core.repository.service.youtube.IYouTubeChannelService;
import pl.rotkom.friday.core.repository.service.youtube.IYouTubeVideoService;
import pl.rotkom.friday.thirdparty.google.spreadsheet.api.service.ISpreadsheetApi;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.ChannelDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.VideoDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.dto.VideoTypeDTO;
import pl.rotkom.friday.thirdparty.google.youtube.api.service.IYouTubeApi;
import pl.rotkom.friday.thirdparty.todoist.api.dto.NewTaskDTO;
import pl.rotkom.friday.thirdparty.todoist.api.service.ITodoistApi;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckNewYouTubeVideos {

    private final String SHEET_NAME = "youtube subskrypcje";

    private final String TODOIST_PROJECT_NAME = "youtube";

    private final ITodoistApi todoistApi;

    private final ISpreadsheetApi spreadsheetApi;

    private final IYouTubeApi youTubeApi;

    private final IYouTubeVideoService youTubeVideoService;

    private final IYouTubeChannelService youTubeChannelService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate = 30,initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void check() {
        // get subscribed channels
        var channels = youTubeApi.getSubscribedChannels();

        var currentChannels = youTubeChannelService.getExternalIdToIdMappingAll();
        for (var dto : channels) {
            if(currentChannels.containsKey(dto.getId())) {
                continue;
            }
            var channel = new YouTubeChannel();

            channel.setExternalId(dto.getId());
            channel.setName(dto.getTitle());

            youTubeChannelService.save(channel);
            currentChannels.put(dto.getId(), channel.getId());
        }


        var channelIds = channels.stream().map(ChannelDTO::getId).toList();
        log.info("Found {} subscribed channels", channelIds.size());

        // get new videos
        var latestVideoIds = youTubeApi.getLatestVideoIds(channelIds);

        log.info("Found {} latest videos", latestVideoIds.size());

        // retain new video ids
        var newIds = spreadsheetApi.retainNewIds(SHEET_NAME, latestVideoIds);
        log.info("Found {} new videos", newIds.size());

        // get video details
        var newVideos = youTubeApi.getVideos(newIds);

        var currentVideos = youTubeVideoService.getExternalIdToIdMappingAll();
        for (var dto : youTubeApi.getVideos(latestVideoIds)) {
            if (currentVideos.containsKey(dto.getId())) {
                continue;
            }
            var video = new YouTubeVideo();

            video.setName(dto.getTitle());
            video.setUrl(dto.getUrl());
            video.setChannelId(currentChannels.get(dto.getChannelId()));
            video.setPublishedAt(dto.getPublishedAt());
            video.setLanguage(dto.getLanguage());
            video.setDurationSec(dto.getDuration().toSeconds());
            video.setExternalId(dto.getId());

            youTubeVideoService.save(video);
        }

        // add new videos to todoist
        var project = todoistApi.getProjectByName(TODOIST_PROJECT_NAME);
        var tasks = newVideos.stream()
                .filter(video -> video.getType() == VideoTypeDTO.VIDEO)
                .map(video -> {
                    var task = new NewTaskDTO();
                    task.setContent(formatContent(video));
                    task.setProjectId(project.getId());
//                    if (video.getType() == VideoTypeDTO.VIDEO) {
//                        task.setDuration(TasksUtils.toMin(video.getDuration()));
//                        task.setDurationUnit("minute");
//                    }
                    task.setLabels(List.of("yt-" + video.getType().name().toLowerCase()));
                    return task;
                })
                .toList();
        todoistApi.createTasks(tasks);
        log.info("Added {} new videos to todoist", tasks.size());

        var values = newVideos.stream()
                .map(video -> {
                    // id, title, url, publishedAt (yyyy-mm-dd hh:mm:ss), channelName, duration
                    return List.of(
                            (Object) video.getId(),
                            video.getTitle(),
                            video.getUrl(),
                            formatInstant(video.getPublishedAt()),
                            video.getChannelTitle(),
                            video.getDuration() != null ? TasksUtils.toMin(video.getDuration()) : ""
                    );

                })
                .toList();

        // append new video ids to spreadsheet
        var valueRange = new ValueRange();
        valueRange.setValues(values);
        spreadsheetApi.appendValues(SHEET_NAME, valueRange);
    }

    private String formatInstant(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    private String formatContent(VideoDTO video) {
        return String.format("[%s - %s (%s)](%s)",
                video.getTitle(),
                video.getChannelTitle(),
                TasksUtils.toMin(video.getDuration()) + " min",
                video.getUrl()
        );
    }

}
