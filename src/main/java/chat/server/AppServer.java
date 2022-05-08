package chat.server;

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

@Slf4j
public class AppServer {

    public static void main(String[] args) {
        log.info("GP: Application started...");
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        container.select(Server.class).get().start();

        log.info("GP: Application ending...");
        container.shutdown();
    }
}