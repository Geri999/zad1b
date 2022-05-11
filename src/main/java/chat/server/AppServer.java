package chat.server;

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

@Slf4j
public class AppServer {
    static WeldContainer container;

    public static void main(String[] args) {
        log.info("GP: Server App started...");
        Weld weld = new Weld();
        container = weld.initialize();
        container.select(Server.class).get().startTheServer();

        log.info("GP: Application ending...");
        container.shutdown();
    }
}