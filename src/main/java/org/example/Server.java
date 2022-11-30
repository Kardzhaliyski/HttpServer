package org.example;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.example.http.HttpTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Server instance = null;
    private int port;
    private ExecutorService executorService;
    public boolean showDirectoryContent;
    public Path root;
    public Set<Option> options;

    public Server(String root, int port, int threadCount, boolean showDirectoryContent, Set<Option> options) {
        this.root = Path.of(root);
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.showDirectoryContent = showDirectoryContent;
        this.options = options;
        instance = this;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(new HttpTask(this, socket));
            }
        }
    }

    public static Server getInstance() {
        return instance;
    }
}
