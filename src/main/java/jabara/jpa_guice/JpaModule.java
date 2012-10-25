/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.IProducer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

/**
 * @author jabaraster
 */
public class JpaModule extends AbstractModule {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * @param pPersistenceUnitName -
     */
    public JpaModule(final String pPersistenceUnitName) {
        this(pPersistenceUnitName, new IProducer<Map<String, String>>() {
            @Override
            public Map<String, String> produce() {
                return new HashMap<String, String>();
            }
        });
    }

    /**
     * @param pPersistenceUnitName -
     * @param pPropertiesProducer -
     */
    public JpaModule(final String pPersistenceUnitName, final IProducer<Map<String, String>> pPropertiesProducer) {
        ArgUtil.checkNullOrEmpty(pPersistenceUnitName, "pPersistenceUnitName"); //$NON-NLS-1$
        ArgUtil.checkNull(pPropertiesProducer, "pPropertiesProducer"); //$NON-NLS-1$

        final Map<String, String> properties = pPropertiesProducer.produce();
        this.entityManagerFactory = wrap(Persistence.createEntityManagerFactory( //
                pPersistenceUnitName //
                , properties == null ? new HashMap<String, String>() : properties));
    }

    /**
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        this.bind(EntityManagerFactory.class).toInstance(this.entityManagerFactory);
        configureTransactionInterceptor();
    }

    private void configureTransactionInterceptor() {
        final Matcher<? super Class<?>> classMatcher = Matchers.subclassesOf(DaoBase.class);
        final Matcher<? super Method> methodMatcher = new AbstractMatcher<Method>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public boolean matches(final Method pT) {
                if (isToStringMethod(pT)) {
                    return false;
                }
                return Modifier.isPublic(pT.getModifiers());
            }
        };
        final TransactionInterceptor ti = new TransactionInterceptor(this.entityManagerFactory);
        bindInterceptor(classMatcher, methodMatcher, ti);
    }

    static EntityManager wrap(final EntityManager pOriginal, final Runnable pOperationAtClose) {
        return (EntityManager) Proxy.newProxyInstance( //
                JpaModule.class.getClassLoader() //
                , new Class<?>[] { EntityManager.class } //
                , new CloseBarrierEntityManagerHandler(pOriginal, pOperationAtClose) //
                );
    }

    private static boolean isToStringMethod(final Method pMethod) {
        return "toString".equals(pMethod.getName()) && pMethod.getParameterTypes().length == 0; //$NON-NLS-1$
    }

    private static EntityManagerFactory wrap(final EntityManagerFactory pOriginal) {
        return (EntityManagerFactory) Proxy.newProxyInstance( //
                JpaModule.class.getClassLoader() //
                , new Class<?>[] { EntityManagerFactory.class } //
                , new ThreadLocalEntityManagerFactoryHandler(pOriginal) //
                );
    }
}
