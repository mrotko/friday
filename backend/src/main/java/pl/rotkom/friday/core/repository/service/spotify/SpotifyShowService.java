package pl.rotkom.friday.core.repository.service.spotify;

import org.springframework.stereotype.Service;

import pl.rotkom.friday.core.repository.common.ExternalEntityService;
import pl.rotkom.friday.core.repository.dao.spotify.ISpotifyShowDao;
import pl.rotkom.friday.core.repository.model.spotify.SpotifyShow;

@Service
class SpotifyShowService extends ExternalEntityService<SpotifyShow> implements ISpotifyShowService {

    private final ISpotifyShowDao dao;

    public SpotifyShowService(ISpotifyShowDao dao) {
        super(dao);
        this.dao = dao;
    }
}
