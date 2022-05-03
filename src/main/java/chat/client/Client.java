package chat.client;

import chat.commons.IOTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

@Data
@Slf4j
public class Client {
    private int port;
    private String host;
    private String prompt;
    private Socket socket;

    private boolean isLogged;
    private String clientName;

    public Client() {
        loadClientPreferences();
        isLogged = false;
        clientName = "guest";
        log.info("Client created");
    }

    public void loadClientPreferences() {
        HashMap<String, String> configMap = IOTools.loadConfigFile();
        port = Integer.parseInt(configMap.get("port"));
        host = configMap.get("host");
        prompt = configMap.get("prompt");
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Config data for **CLIENT** loaded...");
    }

/*    private boolean checkLoggingStatus() {
        if (this.isLogged()) return true;
        System.out.println("You are not logged!");
        return false;
    }*/
}