package server;

import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RedisHandler extends Thread {
    private Jedis jedis;
    private String responseString;
    private final Queue<RequestObject> requestQueue = new ConcurrentLinkedQueue<>();
    private final String BAD_REQUEST = "Incorrect request. Read the manual!\n";

    public RedisHandler(String redisHost, int redisPort) {
        jedis = new Jedis(redisHost, redisPort);
    }

    @Override
    public void run() {
        System.out.println("Redis Handler has started...");

        while (!this.isInterrupted()) {
            if (!requestQueue.isEmpty()) {
                RequestObject requestObject = requestQueue.poll();
                Session session = (Session) requestObject.getSession();
                String requestString = requestObject.getRequestString();

                String[] requestArray = requestString.toLowerCase().split(" ");

                jedis.connect();

                switch (requestArray[0]) {      // Sorry, Mr. Nemchinsky ^--^
                    case "set":
                        responseString = setHandler(requestArray);
                        break;
                    case "get":
                        responseString = getHandler(requestArray);
                        break;
                    case "del":
                        responseString = delHandler(requestArray);
                        break;
                    case "keys":
                        responseString = keysHandler(requestArray);
                        break;
                    default:
                        responseString = BAD_REQUEST;
                }

                jedis.close();

                session.addResponse(responseString);

            }
        }

        System.out.println("Redis Handler has stopped...");
    }

    private String setHandler(String[] requestArray) {
        if (requestArray.length < 3) return BAD_REQUEST;
        switch (requestArray.length) {
            case 3:
                return jedis.set(requestArray[1], requestArray[2]) + "\n";
            case 4:
                return jedis.set(requestArray[1], requestArray[2], requestArray[3]) + "\n";
            case 6:
                if (!requestArray[4].equals("ex")) return BAD_REQUEST;

                try {
                    int ttlInSeconds = Integer.parseInt(requestArray[5]);
                    return jedis.set(requestArray[1], requestArray[2], requestArray[3],
                            requestArray[4], ttlInSeconds) + "\n";
                } catch (NumberFormatException e) {
                    return BAD_REQUEST;
                }
            default:
                return BAD_REQUEST;
        }
    }

    private String getHandler(String[] requestArray) {
        if (requestArray.length != 2) return BAD_REQUEST;
        String response = jedis.get(requestArray[1]);
        return response != null ? response + "\n" : "No such key\n";
    }

    private String delHandler(String[] requestArray) {
        if (requestArray.length < 2) return BAD_REQUEST;
        return jedis.del(Arrays.copyOfRange(requestArray, 1, requestArray.length))
                + " keys were removed\n";
    }

    private String keysHandler(String[] requestArray) {
        if (requestArray.length != 2) return BAD_REQUEST;
        Set<String> keys = jedis.keys(requestArray[1]);
        if (keys.size() == 0) return "No keys were found\n";

        // TODO Can I use StringBuilder?
        StringBuffer keysString = new StringBuffer("Keys were found:\n\t\t\t\t");
        for (String key : keys) {
            keysString.append(key).append("\n\t\t\t\t");
        }
        return keysString.toString();
    }


    // Sessions add requests into requests queue
    public boolean addRequest(RequestObject request) {
        return requestQueue.add(request);
    }
}
