package chat.server.repository;

import chat.commons.Room2;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

@Singleton
public class Room2Repo {

    private EntityManagerFactory emf;

    @Inject
    public Room2Repo(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void createRoomWithUsers(Room2 room) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(room);
        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();
    }


    public long createEmptyRoomAndReturnRoomId(String roomName) {
        EntityManager entityManager = emf.createEntityManager();
        Room2 room = new Room2(roomName);
        //todo sprawdzenie czy nie ma już pokoju z taką samą nazwą
        entityManager.getTransaction().begin();
        entityManager.persist(room);
        entityManager.getTransaction().commit();
        entityManager.close();
        emf.close();

        Room2 roomByRoomName = findRoomByRoomName(roomName);
        return roomByRoomName.getRoomId();
    }


    public Room2 findRoomById(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Room2> query = em.createQuery("SELECT r FROM ROOM2 as r WHERE r.ROOM_ID = :id", Room2.class);
        query.setParameter("id", id);
        Room2 room = query.getSingleResult();
        em.getTransaction().commit();
        em.close();

        return room;
    }

    public Room2 findRoomByRoomName(String roomName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Room2> query = em.createQuery("SELECT r FROM ROOM2 as r WHERE r.ROOM_NAME = :roomName", Room2.class);
        query.setParameter("roomName", roomName);
        Room2 room2 = query.getSingleResult();
        em.getTransaction().commit();
        em.close();
        return room2;


    }
}