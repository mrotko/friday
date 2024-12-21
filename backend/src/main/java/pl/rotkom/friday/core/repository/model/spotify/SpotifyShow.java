package pl.rotkom.friday.core.repository.model.spotify;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

@Entity
@Table(name = "fr_spotify_show")
@Data
public class SpotifyShow extends ExternalEntity {

    private String name;

    private String uri;
}
