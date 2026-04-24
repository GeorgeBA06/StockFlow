package org.example.client.network;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.example.client.config.ClientConfig;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.exception.ErrorCode;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NetworkService implements AutoCloseable{
    private final ClientConfig clientConfig;
    private final SessionManager sessionManager;
    private ConnectionManager connection;
    private final ExecutorService receiverExecutor = Executors.newSingleThreadExecutor();
    private final Map<String, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<ConnectionListener> listeners = new CopyOnWriteArrayList<>();

    public interface ConnectionListener{
        void onConnectionLost();
        void onConnectionReestablished();
    }
    private volatile AtomicBoolean running = new AtomicBoolean(true);

    public NetworkService(ClientConfig clientConfig, SessionManager sessionManager) throws IOException {
      this.clientConfig = clientConfig;
      this.sessionManager = sessionManager;
      connect();
      startReceiver();

    }

    private void connect() throws IOException{
        this.connection = new ConnectionManager(
                clientConfig.getServerHost(),
                clientConfig.getServerPort(),
                clientConfig.getSocketTimeoutMs()
        );
        log.info("Connected to server");
    }

    public void startReceiver(){
        receiverExecutor.submit(()-> {
            while (running.get()) {
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
                    if (running.get()) {
                        log.error("Connection lost", e);
                        notifyListeners();
                        tryReconnect();

                    }
                } catch (Exception e) {
                    log.error("Unexpected error in receiver", e);
                }

            }
        });
    }

    public CompletableFuture<Response> send(Request request){
        if(!running.get()){
            CompletableFuture<Response> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IOException("Connection is closed"));
            return failed;
        }
        String requestId = UUID.randomUUID().toString();
        request.setRequestId(requestId);

        if(request.getToken() == null || request.getToken().isBlank() &&
        sessionManager.isAuthenticated()){
            request.setToken(sessionManager.getToken());
        }

        CompletableFuture<Response> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        try{
            connection.send(request);
        }catch (IOException ex){
            pendingRequests.remove(requestId);
            future.completeExceptionally(ex);
        }
        return future.thenApply(response -> {
            if(response.getErrorCodeEnum() == ErrorCode.UNAUTHORIZED && sessionManager.isAuthenticated()){
                log.warn("Unauthorized response received. Clearing client session.");
                sessionManager.closeSession();
            }
            return response;
        });
    }

    private void notifyListeners(){
        listeners.forEach(l->{
            try {
                Platform.runLater(l::onConnectionLost);
            }catch (Exception e){
                log.error("Listener error", e);
            }
        });
    }

    private void tryReconnect(){
        int attempts = 0;
        while(attempts <= 5){
            attempts++;
            log.info("Reconnection attempt {}/5", attempts);
            try {
                Thread.sleep(3000L *attempts);
                connect();
                log.info("Reconnected successfully!");
                notifyListenersReestablished();
                return;
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                return;
            }catch (IOException ex){
                log.error("Reconnected failed", ex);
            }
        }
        log.error("Couldn't reconnected to server after 5 attempts");
        pendingRequests.values().forEach(f->f.completeExceptionally(new IOException("Connection permanently lost")));
        pendingRequests.clear();
    }

    private void notifyListenersReestablished(){
        listeners.forEach(l->{
            try{
                Platform.runLater(l::onConnectionReestablished);
            }catch (Exception e){
                log.error("Listener error", e);
            }
        });
    }

    public void addListener(ConnectionListener listener){
        listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener){
        listeners.remove(listener);
    }

    @Override
    public void close(){
        running.set(false);
        receiverExecutor.shutdownNow();
        try{
            connection.close();
        }catch (Exception ex){
            log.error("Error closing connection", ex);
        }
    }
}
