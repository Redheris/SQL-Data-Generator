package dev.redheris.sqldatagenerator.cli;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Component
public class FileImporter {
    public Path validateFilepath(Path path) throws NoSuchFileException {
        path = path.toAbsolutePath();

        if (!Files.exists(path)) {
            throw new NoSuchFileException("No such file found: %s".formatted(path));
        }

        return path;
    }

    public Path validateFilepath(String path) throws NoSuchFileException {
        return validateFilepath(Path.of(path));
    }
}
