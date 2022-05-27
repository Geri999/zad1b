package rs.repository.old;

import rs.entities.MessageTxt;
import rs.entities.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class MessagesTxtRepo {

    private EntityManagerFactory emf;

    @Inject
    public MessagesTxtRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public void save(MessageTxt messagetxt) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(messagetxt);
        em.getTransaction().commit();
        em.close();
    }

    public List<MessageTxt> readMessageByUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<MessageTxt> query =
//                em.createQuery("SELECT m FROM MessageTxt AS m LEFT JOIN User AS u ON m..user_id = u.user_id WHERE u.userName =: user", MessageTxt.class);
                em.createQuery("SELECT m FROM MessageTxt AS m LEFT JOIN User AS u WHERE u.userName =: user", MessageTxt.class);
        em.setProperty("user", user.getUserName());
        List<MessageTxt> resultList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return resultList;
    }
}