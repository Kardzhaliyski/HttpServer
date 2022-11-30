package org.example.http;

import org.example.utils.StatusCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
    public String protocol;
    public StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;
    private File bodyFile = null;
    public boolean shouldCompress = false;

    public HttpResponse(String protocol, StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addBody(String body) {
        addBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public void addBody(byte[] body) {
        this.body = body;
    }

    public void addBody(File file) {
        bodyFile = file;
    }


    public void send(OutputStream outputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol)
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(System.lineSeparator());

        headers.put("Date", LocalDateTime.now().toString());
        if (bodyFile != null) {
            addHeaders();
        }

        for (Map.Entry<String, String> kvp : headers.entrySet()) {
            sb.append(kvp.getKey())
                    .append(": ")
                    .append(kvp.getValue())
                    .append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        OutputStream out = new BufferedOutputStream(outputStream);
        out.write(sb.toString().getBytes());

        if (bodyFile != null) {
            if(shouldCompress) {
                out = new GZIPOutputStream(out);
            }

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(bodyFile));
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }

            if(shouldCompress) {
                ((GZIPOutputStream)out).finish();
            }
        } else {
            out.write(body);
        }


        out.flush();

    }

    private void addHeaders() throws IOException {
        long bodyLength = bodyFile.length();
        headers.put("Content-Length", String.valueOf(bodyLength));

        Instant lastMod = Instant.ofEpochMilli(bodyFile.lastModified());
        headers.put("Last-Modified", String.valueOf(lastMod));

        String contentType = Files.probeContentType(bodyFile.toPath());
        headers.put("Content-Type", contentType);
        if(contentType.startsWith("image")) {
            shouldCompress = false;
        }

        if(shouldCompress) {
            headers.put("Content-Encoding", "gzip");
        }
    }
}
