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
import java.util.Set;
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
        String roomName = UUID.randomUUID().toString().substring(0, 8);
        room.setRoomName(roomName); //todo - new room's name by user
        em.persist(room);
        em.flush();
        System.out.println(room);

        List<User> userList = usersNameInvitedToChatList.stream().map(u -> usersRepo.findUserByName(u)).toList();
        userList.forEach(user -> room.addUser(user));

        em.merge(room);
        em.flush();
        em.getTransaction().commit();
        log.info("T:1 room, merge, flush : {}", room);
        log.info("T:2 roomName : {}", roomName);
//        new Scanner(System.in).nextLine();

        Room roomByRoomName = findRoomByRoomName(roomName);
        Long roomId = roomByRoomName.getRoomId();
        log.info("T:3 Room created and saved: {}", roomByRoomName);

//        em.getTransaction().commit();
        em.close();
        return roomId;
    }


    public long createRoomAndMoveUsersToThatRoom1(List<String> usersNameInvitedToChatList) {
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

//        TypedQuery<Room> query1 = em.createQuery("SELECT r FROM Room r", Room.class);
//        System.out.println(query1.getResultList().size());

        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomName=:roomName", Room.class);
        query.setParameter("roomName", roomName);
//        System.out.println(roomName);
        Room room = query.getSingleResult();
        System.out.println(room);

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

    public Set<User> findAllUsersInTheRoomByRoomId(Long roomId) {
        EntityManager em = emf.createEntityManager();
        log.info("T: roomId: {}", roomId);
        var resultSet = em.createQuery("SELECT r.usersInRoom FROM Room AS r WHERE r.roomId = :roomId", Object.class)
                .setParameter("roomId", roomId)
                .getResultStream()
                .peek(System.out::println)
                .map(o->(User) o)
                .collect(Collectors.toSet());

        //Type specified for TypedQuery [chat.commons.entities.User] is incompatible with query return type [interface java.util.Set]
        em.close();
        return resultSet;
    }
}