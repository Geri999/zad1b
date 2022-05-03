package chat.client;

import chat.commons.MessageMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ReaderFromServer {
    Client client;

    public ReaderFromServer(Client client) {
        this.client = client;
    }

    public void readFromServerAndPrintOnConsole() {
        log.info("Thread: Reading from server...");
        String message;
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));

            while ((message = input.readLine()) != null) {
                log.info("inside while, input.readLine()={}", message);
                String[] split = message.split("\\|");
                if (split.length<2) continue;
                if (!split[1].equals(client.getClientName())) MessageMapper.printFormattedMessageOnConsole(split[1], split[3]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}