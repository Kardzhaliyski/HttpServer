package org.example.http;

import org.example.utils.StatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class HttpResponseFactory {
    public static HttpResponse notFound(String protocol) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.NOT_FOUND);
        r.addHeader("Date", LocalDateTime.now().toString());

        r.addHeader("Content-Type", "html");
        File file = Path.of("src/main/resources/static/NotFound.html").toFile();
        try {
            r.addBody(new FileInputStream(file));
        } catch (FileNotFoundException ignored) {}
        return r;
    }

    public static HttpResponse file(String protocol, File file) throws FileNotFoundException {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(new FileInputStream(file));
        return r;
    }

    public static HttpResponse stringResponse(String protocol, String response) {
        HttpResponse r = new HttpResponse(protocol, StatusCode.OK);
        r.addBody(response);
        return r;
    }
}
