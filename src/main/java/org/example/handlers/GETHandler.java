package org.example.handlers;

import org.example.Server;
import org.example.http.HttpRequest;
import org.example.http.HttpResponse;
import org.example.http.HttpResponseFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GETHandler {
    public static HttpResponse handle(Server server, HttpRequest request) {
        Path root = server.root;
        Path path = root.resolve(request.path);
        File dest = path.toFile();
        if (!dest.exists()) {
            return HttpResponseFactory.notFound(request.protocol);
        }

        if (dest.isDirectory()) {
            File[] files = dest.listFiles();
            for (File file : files) {
                try {
                    if (file.getName().equals("index.html")) {
                        return HttpResponseFactory.file(request.protocol, file);
                    }
                } catch (FileNotFoundException ignored) {
                }
            }

            if (server.showDirectoryContent) {
                String content = Arrays.stream(files)
                        .map(f -> String.format("<a href=%s/%s>%s</a>",
                               root.equals(path) ? "." : path.getFileName() , f.getName(), f.getName()))
                        .collect(Collectors.joining("<br/>"));
                return HttpResponseFactory.stringResponse(request.protocol, content);
            } else {
                return HttpResponseFactory.notFound(request.protocol);
            }
        }

        try {
            return HttpResponseFactory.file(request.protocol, dest);
        } catch (FileNotFoundException ignored) {
        }

        return null;
    }
}
