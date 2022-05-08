package chat.server.repository;

import chat.commons.entities.Room;
import chat.commons.entities.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

@Singleton
public class UsersRepo {

    private EntityManagerFactory emf;
    @Inject
    RoomsRepo roomsRepo;

    @Inject
    public UsersRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void addUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Room waitingRoom = roomsRepo.findRoomByRoomName("WaitingRoom");
        user.setRoom(waitingRoom);
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }

    public void removeUserFromRoom(String userName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(findUserByName(userName));
        em.getTransaction().commit();
        em.close();
    }

    public User findUserByName(String name) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User as u WHERE u.userName = :name", User.class);
        query.setParameter("name", name);
        User user = query.getSingleResult();
        em.getTransaction().commit();
        em.close();

        return user;
    }






    public List<User> findAllCurrentUsers() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User as u", User.class);
        List<User> resultList = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return resultList;
    }



}