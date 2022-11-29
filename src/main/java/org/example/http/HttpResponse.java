package org.example.http;

import org.example.utils.StatusCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String protocol;
    private StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;
    private InputStream bodyIn = null;

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

    public void addBody(InputStream in) {
        bodyIn = in;
    }


    public void send(OutputStream outputStream) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol)
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(System.lineSeparator());

        for (Map.Entry<String, String> kvp : headers.entrySet()) {
            sb.append(kvp.getKey())
                    .append(": ")
                    .append(kvp.getValue())
                    .append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        try {
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            out.write(sb.toString().getBytes());
            if(bodyIn != null) {
                int b;

                while ((b = bodyIn.read()) != -1) {
                    out.write(b);
                }
            } else {
                out.write(body);
            }

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e); //todo
        }
    }
}
