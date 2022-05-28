package rs.repository;

import lombok.extern.slf4j.Slf4j;
import rs.entities.Room;
import rs.entities.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

@Slf4j
public class UserRepoRS {

    @Inject
    private RoomRepoRS roomRepoRS;

    private EntityManagerFactory emf;

    @Inject
    public UserRepoRS(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public Long addUser(User user) {
//        String userName = user.getUserName();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Room waitingRoom = roomRepoRS.findRoomByRoomName("WaitingRoom");
        waitingRoom.addUser(user);
        log.info("T: pointer 2");
        em.persist(user);
        em.merge(user);

        em.getTransaction().commit();
        em.close();
        return user.getUserId();
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

    public User findUserByName(String userName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User as u WHERE u.userName = :name", User.class);
        query.setParameter("name", userName);

        User user = query.getSingleResult();
        em.getTransaction().commit();
        em.close();

        return user;
    }


    public void removeUserFromRoom(String userName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<User> query = em.createQuery("SELECT u FROM User as u WHERE u.userName = :name", User.class);
        query.setParameter("name", userName);
        User user = query.getSingleResult();

        em.createQuery("DELETE FROM User WHERE id = :id")
                .setParameter("id", user.getUserId())
                .executeUpdate();

        em.getTransaction().commit();
        em.close();
    }


    public void moveUserToRoom(User user, Long roomId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Room room = roomRepoRS.findRoomById(roomId);
        user.setRoom(room);
        room.addUser(user);
        log.info("T: pointer 5 {}, USER: {}", room, user);
        em.merge(user);
        em.merge(room);
//        em.persist(room);
//        em.persist(user);
        em.flush();

        em.getTransaction().commit();
        em.close();
    }

    public User findUserById(Long userId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<User> query = em.createQuery("SELECT u FROM User as u WHERE u.userId = :userId", User.class)
                .setParameter("userId", userId);

        User user = query.getSingleResult();
        em.getTransaction().commit();
        em.close();

        return user;
    }
}
