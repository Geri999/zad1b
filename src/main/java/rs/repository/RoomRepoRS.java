package rs.repository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.entities.Room;
import rs.entities.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

@Slf4j
@NoArgsConstructor
public class RoomRepoRS {

    private EntityManagerFactory emf;

    @Inject
    public RoomRepoRS(EntityManagerFactory emf) {
        this.emf = emf;
    }


    public long createWaitingRoomWithAdmin() {
        String waitingRoom = "WaitingRoom";
        String admin = "Admin";
        EntityManager em = /*ChatServicesRS.*/emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Room room = new Room(waitingRoom);
            em.persist(room);
            User user = new User(admin);
            em.persist(user);
            user.setRoom(room);
            log.info("T: pointer");
            room.addUser(user);
            //todo sprawdzenie czy nie ma już pokoju z taką samą nazwą
            transaction.commit();
        } finally {
            if (em.getTransaction().isActive()) transaction.rollback();
        }
        em.close();

        Room roomByRoomName = findRoomByRoomName(waitingRoom);
        return roomByRoomName.getRoomId();
    }

    public Room findRoomByRoomName(String roomName) {
        EntityManager em = /*ChatServicesRS.*/emf.createEntityManager();
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room as r WHERE r.roomName=:roomName", Room.class);
        query.setParameter("roomName", roomName);
        Room room = query.getSingleResult();

        log.info("GP: finded Room By RoomName: {}", room);

        em.close();
        return room;
    }


}
