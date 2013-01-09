package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.ReflectionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author jabaraster
 */
public class ThreadLocalEntityManagerFactoryHandler implements InvocationHandler {

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

        em = ThreadLocalEntityManagerFactoryHandler.wrap(this.original.createEntityManager(), new Runnable() {
            @Override
            public void run() {
                ThreadLocalEntityManagerFactoryHandler.this.entityManagerHolder.set(null);
            }
        });
        this.entityManagerHolder.set(em);
        return em;
    }

    /**
     * スレッドローカルな{@link EntityManager}を返すような{@link EntityManagerFactory}にラップします.
     * 
     * @param pOriginal オリジナル.
     * @return ラップした結果.
     */
    public static EntityManagerFactory wrap(final EntityManagerFactory pOriginal) {
        ArgUtil.checkNull(pOriginal, "pOriginal"); //$NON-NLS-1$
        return (EntityManagerFactory) Proxy.newProxyInstance( //
                JpaModule.class.getClassLoader() //
                , new Class<?>[] { EntityManagerFactory.class } //
                , new ThreadLocalEntityManagerFactoryHandler(pOriginal) //
                );
    }

    private static EntityManager wrap(final EntityManager pOriginal, final Runnable pOperationAtClose) {
        return (EntityManager) Proxy.newProxyInstance( //
                ThreadLocalEntityManagerFactoryHandler.class.getClassLoader() //
                , new Class<?>[] { EntityManager.class } //
                , new CloseBarrierEntityManagerHandler(pOriginal, pOperationAtClose) //
                );
    }

}