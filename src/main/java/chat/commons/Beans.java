package chat.commons;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Beans {

    @Produces
    @Singleton
    public EntityManagerFactory getEntityManagerFactory() {
        System.out.println("GP: EntityManagerFactory was created");
        return Persistence.createEntityManagerFactory("defaultH2");
    }


}