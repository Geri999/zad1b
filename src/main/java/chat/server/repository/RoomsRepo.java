package chat.server.repository;

import chat.commons.BeginAndCommit;
import chat.commons.entities.Room;
import chat.commons.entities.User;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
//@Singleton
@Slf4j
public class RoomsRepo implements Serializable {
    private static final long serialVersionUID = 1L;
    private EntityManagerFactory emf;
    @Inject
    private UsersRepo usersRepo;

    @Inject
    public RoomsRepo(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public RoomsRepo() {
        System.out.println("GP: Room2Repo created");
    }

    // todo:  @Transactional  ????
    public long createRoomAndMoveUsersToThatRoom(List<String> usersNameInvitedToChatList) {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        Room room = new Room();
        em.persist(room);
        room.setRoomName(UUID.randomUUID().toString().substring(0, 8)); //todo - new room named by user
        Long roomId = room.getRoomId();
        log.info("T: roomId just created {}", roomId);

        List<User> userList = usersNameInvitedToChatList.stream().map(u -> usersRepo.findUserByName(u)).toList();
        log.info("T: user to add to new room: {}", userList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        log.info("T:1 Room: {}", room);
        userList.forEach(user -> em.merge(user));
//        userList.forEach(user -> user.se setRoomId(roomId));

        userList.forEach(user -> user.setRoom(room));
        userList.forEach(user -> room.addUser(user));
        em.flush();
        log.info("T:2 Room: {}", room);
//        userList.forEach(user -> em.persist(user));

//        em.persist(room);
        em.flush();
        log.info("T:3 Room: {}", room);

        log.info("GP: Room ID during room creation: {}", roomId);

        em.getTransaction().commit();
        em.close();
        return roomId;
    }


    public long createWaitingRoomAndAdmin(String roomName) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Room room = new Room(roomName);
            em.persist(room);
            User user = new User("Admin");
            em.persist(user);
            user.setRoom(room);
            room.addUser(user);
            //todo sprawdzenie czy nie ma już pokoju z taką samą nazwą
            transaction.commit();
        } finally {
            if (transaction.isActive()) transaction.rollback();
        }
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
        em.createNativeQuery("SELECT * FROM Room as r WHERE r.ROOM_ID = :id", Room.class);
        query.setParameter("id", id);
        Room room = query.getSingleResult();
        log.info("GP: znaleziony po ID:{}", room);
        em.getTransaction().commit();
        em.close();

        return room;
    }

    public Room findRoomByRoomName(String roomName) {
        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();

//        TypedQuery<Room> query1 = em.createQuery("SELECT r FROM Room r", Room.class);
//        System.out.println(query1.getResultList().size());


        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomName=:roomName", Room.class);
        query.setParameter("roomName", roomName);
//        System.out.println(roomName);
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

    public List<User> findAllUsersInTheRoomByRoomId(Long roomId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

//        TypedQuery<User> query = em.createQuery("SELECT r.usersInRoom FROM Room as r left join User as u  WHERE r.roomId = :roomId", User.class);
        TypedQuery<User> query = em.createQuery("SELECT r.usersInRoom FROM Room AS r WHERE r.roomId = :roomId", User.class);
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