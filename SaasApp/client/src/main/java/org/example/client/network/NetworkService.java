package org.example.client.network;

import lombok.extern.slf4j.Slf4j;
import org.example.client.config.ClientConfig;
import org.example.dto.request.Request;
import org.example.dto.response.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NetworkService implements AutoCloseable{
    private final ConnectionManager connection;
    private final ExecutorService receiverExecutor = Executors.newSingleThreadExecutor();
    private final Map<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();

    private volatile boolean running = true;

    public NetworkService(ClientConfig clientConfig) throws IOException {
        this.connection = new ConnectionManager(
                clientConfig.getServerHost(),
                clientConfig.getServerPort(),
                clientConfig.getSocketTimeoutMs()
        );
        startReceiver();

    }

    public void startReceiver(){
        receiverExecutor.submit(()-> {
            while (running) {
                try {
                    Response response = connection.receive();
                    String requestId = response.getRequestId();
                    if (requestId != null) {
                        CompletableFuture<Response> future = pendingRequests.remove(requestId);
                        if (future != null) {
                            future.complete(response);
                        } else {
                            log.warn("Received response for unknown requestId: {}", requestId);
                        }
                    } else {
                        log.debug("Received broadcast response: {}", response);
                    }
                }catch (SocketTimeoutException ex){
                    log.debug("Socket timeout, waiting for next message!");
                }
                catch (IOException e) {
                    if (running) {
                        log.error("Network error while receiving", e);
                        pendingRequests.values().forEach(f -> f.completeExceptionally(e));
                        pendingRequests.clear();
                        running = false;
                    }
                } catch (Exception e) {
                    log.error("Unexpected error in receiver", e);
                }

            }
        });
    }

    public CompletableFuture<Response> send(Request request){
        if(!running){
            CompletableFuture<Response> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IOException("Connection is closed"));
            return failed;
        }
        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);
        CompletableFuture<Response> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        try{
            connection.send(request);
        }catch (IOException ex){
            pendingRequests.remove(requestId);
            future.completeExceptionally(ex);
        }
        return future;
    }

    @Override
    public void close(){
        running = false;
        receiverExecutor.shutdownNow();
        try{
            connection.close();
        }catch (Exception ex){
            log.error("Error closing connection", ex);
        }
    }
}
