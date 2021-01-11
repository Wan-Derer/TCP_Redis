package server;

// this object is for Request Queue for Redis requests
public class RequestObject {
    private final Thread session;          // link to Session who send request
    private final String requestString;     // request to Redis

    public RequestObject(Thread session, String requestString) {
        this.session = session;
        this.requestString = requestString;
    }

    public Thread getSession() {
        return session;
    }

    public String getRequestString() {
        return requestString;
    }
}



