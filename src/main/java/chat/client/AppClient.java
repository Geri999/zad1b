package chat.client;

import chat.client.view.ClientGui;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppClient {

    public static void main(String[] args) {
        Client newClient = new Client();
        new ClientGui(newClient).menu();
    }
}