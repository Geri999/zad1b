package chat.server.repository;

import chat.commons.BeginAndCommit;
import chat.commons.entities.Room;
import chat.commons.entities.User;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

//@ApplicationScoped
@Singleton
@Slf4j
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
    public long createRoomWithUsers(Room room) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(room);
        em.flush();
        Long roomId = room.getRoomId();
        log.info("GP: Room ID during room creation: {}", roomId);

        em.getTransaction().commit();
        em.close();
        return  roomId;
    }


    public long createEmptyRoomAndReturnRoomId(String roomName) {
        EntityManager em = emf.createEntityManager();
        Room room = new Room(roomName);
//        room.
        //todo sprawdzenie czy nie ma już pokoju z taką samą nazwą
        em.getTransaction().begin();
        em.persist(room);
        em.getTransaction().commit();
        em.close();

        Room roomByRoomName = findRoomByRoomName(roomName);
        findRoomByRoomName(roomName);
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
        log.info("GP: znaleziony po ID:{}",room);
        em.getTransaction().commit();
        em.close();

        return room;
    }

    public Room findRoomByRoomName(String roomName) {
        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();

        TypedQuery<Room> query1 = em.createQuery("SELECT r FROM Room r", Room.class);
        System.out.println(query1.getResultList().size());


        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomName=:roomName", Room.class);
        query.setParameter("roomName", roomName);
        System.out.println(roomName);
        Room room = query.getSingleResult();
        System.out.println(room);

//        em.getTransaction().commit();
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

    public List<User> findAllUsersInTheRoom(Long roomId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        TypedQuery<User> query = em.createQuery("SELECT r FROM Room as r left join User as u  WHERE r.roomId = :roomId", User.class);
        query.setParameter("roomId", roomId);
        List<User> resultList = query.getResultList();

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