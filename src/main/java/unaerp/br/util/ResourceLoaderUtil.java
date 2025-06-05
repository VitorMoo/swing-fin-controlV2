package unaerp.br.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoaderUtil {

    public static String loadTextFromClasspath(String pathInClasspath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = ResourceLoaderUtil.class.getClassLoader().getResourceAsStream(pathInClasspath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                System.err.println("Não foi possível encontrar o recurso no classpath: " + pathInClasspath);
                return null;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Erro ao ler o recurso do classpath '" + pathInClasspath + "': " + e.getMessage());
            return null;
        }
        return contentBuilder.toString();
    }

    public static List<String> listResourceFiles(String directoryPathInResources) throws IOException {
        List<String> filenames = new ArrayList<>();
        URI uri;
        Path dirPath;

        try {
            uri = ResourceLoaderUtil.class.getClassLoader().getResource(directoryPathInResources).toURI();
        } catch (URISyntaxException | NullPointerException e) {
            System.err.println("Erro ao obter URI para o caminho do recurso: " + directoryPathInResources + " - " + e.getMessage());
            return Collections.emptyList();
        }

        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = null;
            try {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                }
                dirPath = fileSystem.getPath(directoryPathInResources);
            } catch (IOException e) {
                System.err.println("Erro ao acessar o sistema de arquivos JAR para o caminho: " + directoryPathInResources + " - " + e.getMessage());
                return Collections.emptyList();
            }
        } else {
            dirPath = Paths.get(uri);
        }

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            System.err.println("Caminho do recurso não é um diretório válido ou não existe: " + dirPath);
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(dirPath)) {
            filenames = stream
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao listar arquivos no diretório do recurso '" + directoryPathInResources + "': " + e.getMessage());
            throw e;
        }
        return filenames;
    }
}