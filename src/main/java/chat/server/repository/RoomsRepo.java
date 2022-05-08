package chat.server.repository;

import chat.commons.BeginAndCommit;
import chat.commons.entities.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
@Singleton
public class RoomsRepo implements Serializable {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf;

    @Inject
    public RoomsRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public RoomsRepo() {
        System.out.println("GP: Room2Repo created");
    }

    //@Transactional  ?????????? todo: sprawdz to
    public void createRoomWithUsers(Room room) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(room);
        em.getTransaction().commit();
        em.close();
    }


    public long createEmptyRoomAndReturnRoomId(String roomName) {
        EntityManager em = emf.createEntityManager();
        Room room = new Room(roomName);
        //todo sprawdzenie czy nie ma już pokoju z taką samą nazwą
        em.getTransaction().begin();
        em.persist(room);
        em.getTransaction().commit();
        em.close();

        Room roomByRoomName = findRoomByRoomName(roomName);
        return roomByRoomName.getRoomId();
    }

    @Deprecated
    @BeginAndCommit
    public long createEmptyRoomAndReturnRoomId2(String roomName, EntityManager em) {
        Room room = new Room(roomName);
        em.persist(room);

        Room roomByRoomName = findRoomByRoomName(roomName);
        return roomByRoomName.getRoomId();
    }


    public Room findRoomById(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomId = :id", Room.class);
        query.setParameter("id", id);
        Room room = query.getSingleResult();
        em.getTransaction().commit();
        em.close();

        return room;
    }

    public Room findRoomByRoomName(String roomName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomName = :roomName", Room.class);
        query.setParameter("roomName", roomName);
        Room room = query.getSingleResult();

        em.getTransaction().commit();
        em.close();
        return room;
    }

    public List<Room> findAllRoomsByUserNameWhereIsUser(String userName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r left join User as u on u.room.roomId=r.roomId where u.userName = :userName", Room.class);
        query.setParameter("userName", userName);
        List<Room> resultList = query.getResultList();

        em.getTransaction().commit();
        em.close();
        return resultList;
    }





/*    public synchronized Room findRoomById(String id) {
        Room room = roomsList.stream().filter(s -> id.equals(s.getRoomId())).findFirst().get();
        log.info(room.toString());
        return room;
    }*/

/*    public synchronized List<Room> findRoomByUserName(String senderName) {
  *//*      List<Room> rooms = null;
        for (Room room : roomsList) {
            if (room.getUserListInRoom().stream().filter(s->s.getName().equals(senderName)).count()>0) rooms.add(room);
        }*//*

        List<Room> collect = roomsList.stream()
                .filter(s -> (s.getUserListInRoom()
                        .stream()
                        .filter(u -> u.getName().equals(senderName))
                        .count() > 0))
                .collect(Collectors.toList());

        log.info("List<Room> collect.size()={}",collect.size());
        return collect;
    }*/






}