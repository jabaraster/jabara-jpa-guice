/**
 * 
 */
package jabara.jpa_guice.util;

import jabara.general.ArgUtil;
import jabara.general.IProducer;
import jabara.general.ReflectionUtil;
import jabara.jpa_guice.ThreadLocalEntityManagerFactoryHandler;
import jabara.jpa_guice.TransactionInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

/**
 * @author jabaraster
 */
public abstract class JpaModuleBase extends AbstractModule {

    /**
     * publicメソッドとマッチするマッチャー.
     */
    public static final Matcher<Method>  PUBlIC_METHOD_MATCHER = new AbstractMatcher<Method>() {
                                                                   @Override
                                                                   public boolean matches(final Method pT) {
                                                                       if (ReflectionUtil.isObjectMethod(pT)) {
                                                                           return false;
                                                                       }
                                                                       return Modifier.isPublic(pT.getModifiers());
                                                                   }
                                                               };

    /**
     * 
     */
    protected final String               persistenceUnitName;

    /**
     * 
     */
    protected final EntityManagerFactory entityManagerFactory;

    /**
     * @param pPersistenceUnitName -
     */
    public JpaModuleBase(final String pPersistenceUnitName) {
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
    public JpaModuleBase(final String pPersistenceUnitName, final IProducer<Map<String, String>> pPropertiesProducer) {
        ArgUtil.checkNullOrEmpty(pPersistenceUnitName, "pPersistenceUnitName"); //$NON-NLS-1$
        ArgUtil.checkNull(pPropertiesProducer, "pPropertiesProducer"); //$NON-NLS-1$

        this.persistenceUnitName = pPersistenceUnitName;

        final Map<String, String> properties = pPropertiesProducer.produce();
        this.entityManagerFactory = ThreadLocalEntityManagerFactoryHandler.wrap(Persistence.createEntityManagerFactory( //
                pPersistenceUnitName //
                , properties == null ? new HashMap<String, String>() : properties));
    }

    /**
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected final void configure() {
        preConfigure();
        configureTransactionInterceptor();
        postConfigure();
    }

    /**
     * 
     */
    protected void configureTransactionInterceptor() {
        final Matcher<? super Class<?>> classMatcher = getTransactionTargetClassMatcher();
        final Matcher<? super Method> methodMatcher = getTransactionTargetMethodMatcher();
        final TransactionInterceptor ti = new TransactionInterceptor(this.entityManagerFactory);
        bindInterceptor(classMatcher, methodMatcher, ti);
    }

    /**
     * @return トランザクションをかけるメソッドを持つクラスの{@link Matcher}を返して下さい.
     */
    protected abstract Matcher<? super Class<?>> getTransactionTargetClassMatcher();

    /**
     * @return トランザクションをかけるメソッドの{@link Matcher}を返して下さい.
     */
    protected abstract Matcher<Method> getTransactionTargetMethodMatcher();

    /**
     * 
     */
    protected void postConfigure() {
        //
    }

    /**
     * 
     */
    protected void preConfigure() {
        //
    }

}
