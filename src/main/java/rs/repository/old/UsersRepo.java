package rs.repository.old;

import rs.entities.Room;
import rs.entities.User;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Slf4j
public class UsersRepo {

    private EntityManagerFactory emf;
    @Inject
    RoomsRepo roomsRepo;

    Map<String, Socket> userSocket = new HashMap<>();

    @Inject
    public UsersRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void addUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
//        userSocket.put(user.getUserName(), user.getSocket());
        Room waitingRoom = roomsRepo.findRoomByRoomName("WaitingRoom");
//        user.setRoom(waitingRoom);  //????? "detached entity passed to persist:"
        waitingRoom.addUser(user);
        System.out.println(waitingRoom);
        System.out.println(user);

        em.persist(user);
        em.merge(user);
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

    public Map<String, Socket> getUserSocket() {
        return userSocket;
    }
}