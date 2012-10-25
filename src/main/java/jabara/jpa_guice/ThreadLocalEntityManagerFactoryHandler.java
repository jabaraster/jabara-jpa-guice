package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.ReflectionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author jabaraster
 */
class ThreadLocalEntityManagerFactoryHandler implements InvocationHandler {

    private final ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<EntityManager>();

    private final EntityManagerFactory       original;

    /**
     * @param pOriginal -
     */
    public ThreadLocalEntityManagerFactoryHandler(final EntityManagerFactory pOriginal) {
        ArgUtil.checkNull(pOriginal, "pOriginal"); //$NON-NLS-1$
        this.original = pOriginal;
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(@SuppressWarnings("unused") final Object pProxy, final Method pMethod, final Object[] pArgs) {
        if ("createEntityManager".equals(pMethod.getName())) { //$NON-NLS-1$
            return createEntityManagerCore();
        }
        return ReflectionUtil.invoke(this.original, pMethod, pArgs);
    }

    @SuppressWarnings("synthetic-access")
    private EntityManager createEntityManagerCore() {
        EntityManager em = this.entityManagerHolder.get();
        if (em != null) {
            return em;
        }

        em = JpaModule.wrap(this.original.createEntityManager(), new Runnable() {
            @Override
            public void run() {
                ThreadLocalEntityManagerFactoryHandler.this.entityManagerHolder.set(null);
            }
        });
        this.entityManagerHolder.set(em);
        return em;
    }

}