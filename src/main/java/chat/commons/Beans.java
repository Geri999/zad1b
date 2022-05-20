package chat.commons;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Slf4j
public class Beans {

    @Produces
    @Singleton
    public EntityManagerFactory getEntityManagerFactory() {
        log.info("GP: EntityManagerFactory was created");
        return Persistence.createEntityManagerFactory("defaultH2");
    }


}