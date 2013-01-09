/**
 * 
 */
package jabara.jpa_guice.util;

import jabara.general.IProducer;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

/**
 * @author jabaraster
 */
public class SingleJpaModule extends JpaModuleBase {

    /**
     * @param pPersistenceUnitName -
     */
    public SingleJpaModule(final String pPersistenceUnitName) {
        super(pPersistenceUnitName);
    }

    /**
     * @param pPersistenceUnitName -
     * @param pPropertiesProducer -
     */
    public SingleJpaModule(final String pPersistenceUnitName, final IProducer<Map<String, String>> pPropertiesProducer) {
        super(pPersistenceUnitName, pPropertiesProducer);
    }

    /**
     * @see jabara.jpa_guice.util.JpaModuleBase#getTransactionTargetClassMatcher()
     */
    @Override
    protected Matcher<? super Class<?>> getTransactionTargetClassMatcher() {
        return Matchers.any();
    }

    /**
     * @see jabara.jpa_guice.util.JpaModuleBase#getTransactionTargetMethodMatcher()
     */
    @Override
    protected Matcher<Method> getTransactionTargetMethodMatcher() {
        return PUBlIC_METHOD_MATCHER;
    }
}
