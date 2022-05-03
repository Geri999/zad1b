package chat.server.repository;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

public class User2Repo {

    private EntityManagerFactory emf;

    @Inject
    public User2Repo(EntityManagerFactory emf) {
        this.emf = emf;
    }

    void createUser(){
    emf.createEntityManager();
}

//    findRoomById(String id)
//    public synchronized List<Room> findRoomByUserName(String senderName)

}
