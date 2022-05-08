package chat.server.repository;

import chat.commons.entities.Message;
import chat.commons.entities.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class MessagesRepo {

    private EntityManagerFactory emf;

    @Inject
    public MessagesRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public void save(Message message) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(message);
        em.getTransaction().commit();
        em.close();
    }

    public List<Message> readMessageByUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<Message> query =
                em.createQuery("SELECT m FROM Message AS m LEFT JOIN User AS u ON m.sender.user_id = u.user_id WHERE u.userName =: user", Message.class);
        em.setProperty("user", user.getUserName());
        List<Message> resultList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return resultList;
    }
}