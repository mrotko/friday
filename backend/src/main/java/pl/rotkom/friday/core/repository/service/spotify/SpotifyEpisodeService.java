package pl.rotkom.friday.core.repository.service.spotify;

import org.springframework.stereotype.Service;

import pl.rotkom.friday.core.repository.common.ExternalEntityService;
import pl.rotkom.friday.core.repository.dao.spotify.ISpotifyEpisodeDao;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyEpisode;

@Service
class SpotifyEpisodeService extends ExternalEntityService<SpotifyEpisode> implements ISpotifyEpisodeService {

    private final ISpotifyEpisodeDao dao;

    public SpotifyEpisodeService(ISpotifyEpisodeDao dao) {
        super(dao);
        this.dao = dao;
    }
}
