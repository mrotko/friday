package pl.rotkom.friday.library.files;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Component
@Log4j2
public class FileService {

    @Value("${friday.files.path}")
    private String filesDir;

    @SneakyThrows
    public Path getFilesDir() {
        var path = Path.of(filesDir);
        if (!Files.exists(path)) {
            log.info("Creating app directory: {}", path);
            Files.createDirectories(path);
        }
        return path;
    }
}
