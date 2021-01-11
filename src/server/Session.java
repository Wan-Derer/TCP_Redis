package server;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Session extends Thread {
    private final Socket socket;
    private final RedisHandler redisHandler;
    private final Queue<String> responseQueue = new ConcurrentLinkedQueue<>();

    public Session(Socket socket, RedisHandler redisHandler) {
        this.socket = socket;
        this.redisHandler = redisHandler;
    }

    @Override
    public void run() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            out.println("Session started. Server port: " + socket.getPort());

            String request = null;
            // TODO how to find out that the client is disconnected?
            while (true) {
                if (in.ready()) {
                    request = in.readLine();        // receive a request from client
                    System.out.println(request);
                    if (request.equalsIgnoreCase("exit")) break;

                    // send request to Redis handler
                    RequestObject requestObject = new RequestObject(this, request);
                    if (!redisHandler.addRequest(requestObject)) {
                        out.println("Unable to send request");
                    }
                }

                if (!responseQueue.isEmpty()) {  // send response to client
                    String response = responseQueue.poll();
                    out.println("Request:\t" + request + "\nResponse:\t" + response);

                }
            }

            System.out.println("Session has terminated: " + socket);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addResponse(String responseString) {
        responseQueue.add(responseString);
    }
}




