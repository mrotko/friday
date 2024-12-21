package pl.rotkom.friday.core.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyEpisode;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyShow;
import pl.rotkom.friday.core.repository.service.spotify.ISpotifyEpisodeService;
import pl.rotkom.friday.core.repository.service.spotify.ISpotifyShowService;
import pl.rotkom.friday.thirdparty.google.spreadsheet.api.service.ISpreadsheetApi;
import pl.rotkom.friday.thirdparty.spotify.api.dto.EpisodeDTO;
import pl.rotkom.friday.thirdparty.spotify.api.dto.ShowDTO;
import pl.rotkom.friday.thirdparty.spotify.api.service.ISpotifyApi;
import pl.rotkom.friday.thirdparty.todoist.api.dto.NewTaskDTO;
import pl.rotkom.friday.thirdparty.todoist.api.service.ITodoistApi;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckSpotifyPodcasts {

    private static final String SHEET_NAME = "spotify podcasty";

    private static final String TODOIST_PROJECT = "spotify";

    private final ITodoistApi todoistApi;

    private final ISpreadsheetApi spreadsheetApi;

    private final ISpotifyApi spotifyApi;

    private final ISpotifyEpisodeService spotifyEpisodeService;

    private final ISpotifyShowService spotifyShowService;

    @Scheduled(fixedRate = 30,initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void check() {
        // list show ids from spotify api
        var shows = spotifyApi.getShows();

        var currentShows = new HashMap<>(spotifyShowService.getExternalIdToIdMappingAll());


        for (var dto : shows) {
            if(currentShows.containsKey(dto.getId())) {
                continue;
            }

            var show = new SpotifyShow();

            show.setExternalId(dto.getId());
            show.setName(dto.getName());
            show.setUri(dto.getUri());

            spotifyShowService.save(show);
            currentShows.put(show.getExternalId(), show.getId());
        }


        var showsById = shows.stream().collect(Collectors.toMap(ShowDTO::getId, Function.identity()));
        log.info("Found {} shows", showsById.size());

        // get episodes for each show
        var episodes = spotifyApi.getEpisodesByShowIds(showsById.keySet());
        var episodeIds = episodes.stream().map(EpisodeDTO::getId).toList();

        log.info("Found {} episodes", episodes.size());

        // retain new episode ids
        var newIds = spreadsheetApi.retainNewIds(SHEET_NAME, episodeIds);
        log.info("Found {} new episodes", newIds.size());

        // add new episodes to todoist
        var project = todoistApi.getProjectByName(TODOIST_PROJECT);

        var currentEpisodes = spotifyEpisodeService.getExternalIdToIdMappingAll();
        for (EpisodeDTO dto : episodes) {
            if (currentEpisodes.containsKey(dto.getId())) {
                continue;
            }

            var episode = new SpotifyEpisode();

            episode.setExternalId(dto.getId());
            episode.setName(dto.getName());
            episode.setUri(dto.getUri());
            episode.setShowId(currentShows.get(dto.getShowId()));
            episode.setDurationDec(dto.getDuration().toSeconds());
            episode.setReleaseAt(dto.getReleaseAt());

            spotifyEpisodeService.save(episode);
        }


        var newEpisodes = episodes.stream()
                .filter(episode -> newIds.contains(episode.getId()))
                .toList();
        var tasks = newEpisodes.stream()
                .map(episode -> {
                    var task = new NewTaskDTO();
                    task.setContent(formatTaskContent(episode, showsById));
                    task.setProjectId(project.getId());
                    task.setDuration(TasksUtils.toMin(episode.getDuration()));
                    task.setDurationUnit("minute");
                    return task;
                })
                .toList();

        todoistApi.createTasks(tasks);
        log.info("Added {} new tasks", tasks.size());

        var values = newEpisodes.stream()
                .map(episode -> {
                    return List.of(
                            (Object) episode.getId(),
                            episode.getName(),
                            episode.getUri(),
                            TasksUtils.toMin(episode.getDuration()),
                            episode.getReleaseAt().toString(),
                            showsById.get(episode.getShowId()).getName()
                    );
                })
                .toList();


        var valueRange = new ValueRange();
        valueRange.setValues(values);
        spreadsheetApi.appendValues(SHEET_NAME, valueRange);
    }

    private String formatTaskContent(EpisodeDTO episode, Map<String, ShowDTO> showsById) {
        return String.format("[%s - %s (%d min)](%s)",
                episode.getName(),
                showsById.get(episode.getShowId()).getName(),
                TasksUtils.toMin(episode.getDuration()),
                episode.getUri());
    }
}
