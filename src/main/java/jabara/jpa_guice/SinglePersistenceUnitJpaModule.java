/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.IProducer;

import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

/**
 * @author jabaraster
 */
public class SinglePersistenceUnitJpaModule extends JpaModuleBase {

    /**
     * @param pPersistenceUnitName -
     */
    public SinglePersistenceUnitJpaModule(final String pPersistenceUnitName) {
        super(pPersistenceUnitName);
    }

    /**
     * @param pPersistenceUnitName -
     * @param pPropertiesProducer -
     */
    public SinglePersistenceUnitJpaModule(final String pPersistenceUnitName, final IProducer<Map<String, String>> pPropertiesProducer) {
        super(pPersistenceUnitName, pPropertiesProducer);
    }

    /**
     * @see jabara.jpa_guice.JpaModuleBase#getTransactionTargetClassMatcher()
     */
    @Override
    protected Matcher<? super Class<?>> getTransactionTargetClassMatcher() {
        return Matchers.any();
    }

    /**
     * @see jabara.jpa_guice.JpaModuleBase#getTransactionTargetMethodMatcher()
     */
    @Override
    protected Matcher<Method> getTransactionTargetMethodMatcher() {
        return PUBlIC_METHOD_MATCHER;
    }

    /**
     * @see jabara.jpa_guice.JpaModuleBase#preConfigure()
     */
    @Override
    protected void preConfigure() {
        this.bind(EntityManagerFactory.class).toInstance(this.entityManagerFactory);
    }
}
