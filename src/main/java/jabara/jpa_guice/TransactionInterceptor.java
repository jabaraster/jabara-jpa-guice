/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.ArgUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * @author jabaraster
 */
class TransactionInterceptor implements MethodInterceptor {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * @param pEntityManagerFactory -
     */
    public TransactionInterceptor(final EntityManagerFactory pEntityManagerFactory) {
        ArgUtil.checkNull(pEntityManagerFactory, "pEntityManagerFactory"); //$NON-NLS-1$
        this.entityManagerFactory = pEntityManagerFactory;
    }

    /**
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(final MethodInvocation pInvocation) throws Throwable {
        final EntityManager em = this.entityManagerFactory.createEntityManager();
        final boolean startTxInThisFrame = !em.getTransaction().isActive();
        try {
            if (startTxInThisFrame) {
                em.getTransaction().begin();
            }

            final Object ret = pInvocation.proceed();

            if (startTxInThisFrame) {
                em.getTransaction().commit();
            }

            return ret;

        } catch (final Throwable e) {
            if (startTxInThisFrame && em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
            throw e;

        } finally {
            if (startTxInThisFrame) {
                closeReality(em);
            }
        }
    }

    private static void closeReality(final EntityManager pEm) {
        try {
            final InvocationHandler handler = Proxy.getInvocationHandler(pEm);
            if (!(handler instanceof CloseBarrierEntityManagerHandler)) {
                return;
            }
            ((CloseBarrierEntityManagerHandler) handler).closeReality();

        } catch (final IllegalArgumentException e) {
            return;
        }
    }
}
