package org.example.http;

import org.example.handlers.GETHandler;
import org.example.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpCommunication implements Runnable{
    Socket socket;
    Server server;

    public HttpCommunication(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = GETHandler.handle(server, request);

            OutputStream outputStream = socket.getOutputStream();
            response.send(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);//todo
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
