package chat.client;

import chat.client.view.ClientGui;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

@Data
@Slf4j
public class Client {
    private int port;
    private String host;
    private String prompt;
    private Socket socket;

    private boolean isLogged;
    private String clientName;

    public void start() {
        isLogged = false;
        clientName = "guest";
        log.info("GP: Client created");

        new ClientGui(this).menu();
    }


    @Inject
    public void loadClientConfiguration(Properties properties) {
        port = Integer.parseInt(properties.getProperty("port"));
        host = properties.getProperty("host");
        prompt = properties.getProperty("prompt");
        log.info("GP: Config data for **CLIENT** loaded...");
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}