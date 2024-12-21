package pl.rotkom.friday.core.repository.model.youtube;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import pl.rotkom.friday.core.repository.model.common.ExternalEntity;

@Entity
@Table(name = "fr_youtube_channel")
@Data
public class YouTubeChannel extends ExternalEntity {

    private String name;

}
