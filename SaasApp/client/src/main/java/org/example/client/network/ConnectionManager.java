package org.example.client.network;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.util.JsonMapper;

import java.io.*;
import java.net.Socket;


@Slf4j
public class ConnectionManager implements AutoCloseable{
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public ConnectionManager(String host, int port, int timeoutMs) throws IOException {
        this.socket = new Socket(host,port);
        socket.setSoTimeout(timeoutMs);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        log.info("Connected to {} : {}", host, port);
    }

    public void send(Request request) throws IOException{
        String json = JsonMapper.INSTANCE.writeValueAsString(request);
        out.println(json);
        log.debug("Sent: {}" , json);
    }

    public Response receive() throws IOException{
        String json = in.readLine();
        if(json == null){
            throw new IOException("Server closed connection");
        }
        log.info("Received: {}", json);
        return JsonMapper.INSTANCE.readValue(json, Response.class);
    }

    public void close(){
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket!= null) socket.close();
        }catch (IOException ex){
            log.error("Error closing connection", ex);
        }

    }
}
