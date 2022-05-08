package chat.client;

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

@Slf4j
public class AppClient {

    public static void main(String[] args) {

        log.info("GP: Client application started...");
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        container.select(Client.class).get().start();

        log.info("GP: Client application ending...");
        container.shutdown();
    }
}