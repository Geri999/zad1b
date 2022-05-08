package chat.commons;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class Beans {

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("defaultH2");
    }
}