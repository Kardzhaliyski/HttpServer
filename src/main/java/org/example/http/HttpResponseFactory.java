package org.example.http;

import org.example.utils.StatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class HttpResponseFactory {
    public static HttpResponse notFound(String protocol) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.NOT_FOUND);
        File file = Path.of("src/main/resources/static/NotFound.html").toFile();
        r.addBody(file);
        return r;
    }

    public static HttpResponse file(String protocol, File file) throws IOException {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(file);
        return r;
    }

    public static HttpResponse stringResponse(String protocol, String response) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(response);
        r.addHeader("Content-Type", "text/html");
        return r;
    }
}
