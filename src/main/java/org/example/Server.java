package org.example;

import org.example.http.HttpTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private ExecutorService executorService;
    public boolean showDirectoryContent;
    public Path root;

    public Server(String root, int port, int threadCount, boolean showDirectoryContent) {
        this.root = Path.of(root);
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.showDirectoryContent = showDirectoryContent;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(new HttpTask(this, socket));
            }
        }
    }
}
