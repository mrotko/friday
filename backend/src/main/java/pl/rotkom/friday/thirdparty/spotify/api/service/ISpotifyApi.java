package pl.rotkom.friday.thirdparty.spotify.api.service;

import java.util.Collection;
import java.util.List;

import pl.rotkom.friday.thirdparty.spotify.api.dto.EpisodeDTO;
import pl.rotkom.friday.thirdparty.spotify.api.dto.ShowDTO;


public interface ISpotifyApi {

    List<EpisodeDTO> getEpisodesByShowIds(Collection<String> showIds);

    List<ShowDTO> getShows();
}
