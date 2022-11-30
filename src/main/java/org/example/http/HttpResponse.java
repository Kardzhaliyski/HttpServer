package org.example.http;

import org.example.Server;
import org.example.utils.CliOptions;
import org.example.utils.StatusCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public boolean serverAcceptGzip = false;
    public boolean compressed = false;

    public HttpResponse(String protocol, StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
        this.body = new byte[0];
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
        if (bodyFile != null) {
            addHeaders();
            if (serverAcceptGzip) {
                bodyFile = getCompressedVersion(bodyFile);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(protocol)
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(System.lineSeparator());

        headers.put("Date", LocalDateTime.now().toString());

        if (bodyFile != null) {
            long bodyLength = bodyFile.length();
            headers.put("Content-Length", String.valueOf(bodyLength));
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
        out.flush();

        if (bodyFile != null) {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(bodyFile));
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }

        } else {
            out.write(body);
        }

        out.flush();
    }

    private File getCompressedVersion(File bodyFile) {
        Server server = Server.getInstance();
        boolean rtCompression = server.options.contains(CliOptions.REAL_TIME_COMPRESSION);
        boolean cachedCompression = server.options.contains(CliOptions.CACHED_COMPRESSION);
        if (!(rtCompression || cachedCompression)) {
            return bodyFile;
        }

        String contentType = headers.get("Content-Type");
        if (!(contentType.startsWith("text") ||
                contentType.contains("json") ||
                contentType.contains("svg") ||
                contentType.contains("xml"))) {
            return bodyFile;
        }

        String fName = bodyFile.getName();
        Path cPath = bodyFile.toPath().getParent().resolve(fName + ".gz");
        File cFile = cPath.toFile();
        if (cFile.exists()) {
            if (cFile.lastModified() > bodyFile.lastModified()) {
                return cFile;
            }
        }

        if (!rtCompression) {
            return bodyFile;
        }

        try (GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(cFile)));
             InputStream in = new FileInputStream(bodyFile)) {
            byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) != -1) {
                out.write(buff, 0, n);
            }

            out.finish();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cFile;
    }

    private void addHeaders() throws IOException {
        Instant lastMod = Instant.ofEpochMilli(bodyFile.lastModified());
        headers.put("Last-Modified", String.valueOf(lastMod));

        String contentType = Files.probeContentType(bodyFile.toPath());
        headers.put("Content-Type", contentType);
        if (contentType.startsWith("image")) {
            serverAcceptGzip = false;
        }

        if (serverAcceptGzip) {
            headers.put("Content-Encoding", "gzip");
        }
    }
}
