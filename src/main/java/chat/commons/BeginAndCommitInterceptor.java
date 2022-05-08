package chat.commons;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@BeginAndCommit
@Interceptor
public class BeginAndCommitInterceptor {
    private EntityManagerFactory emf;

    @Inject
    public BeginAndCommitInterceptor(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ic) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Object[] parameters = ic.getParameters();
        try {
            parameters[1] = entityManager;
            ic.setParameters(parameters);
            return ic.proceed();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }
}
