package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class server {
    private static final int TCP_PORT = 23;
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;


    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedReader keybReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Server has started...");

        // RedisHandler extends Thread
        RedisHandler redisHandler = new RedisHandler(REDIS_HOST, REDIS_PORT);
        redisHandler.start();

        ServerSocket serverSocket = new ServerSocket(TCP_PORT);

        while (true) {
            if (keybReader.ready() && keybReader.readLine().equals("exit")) {
                redisHandler.interrupt();
                Thread.sleep(2000);
                System.out.println("Server has stopped...");
                break;
            }

            serverSocket.setSoTimeout(100);
            try {
                Socket socket = serverSocket.accept();
                Thread sessionThread = new Thread(new Session(socket, redisHandler));
                sessionThread.setDaemon(true);
                sessionThread.start();

                System.out.println(socket);
            } catch (SocketTimeoutException ignore) {
            }

        }


    }
}
