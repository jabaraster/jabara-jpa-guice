/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.IProducer;
import jabara.jpa.JpaDaoBase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

/**
 * @author jabaraster
 */
public class MultiPersistenceUnitJpaModule extends JpaModuleBase {

    private final Class<? extends Annotation> daoAnnotationType;
    private final Class<? extends Annotation> transactionalAnnotationType;

    /**
     * @param pPersistenceUnitName PU名
     * @param pDaoAnnotationType このモジュールが使うPUの接続が必要なDaoを識別するアノテーションを指定します.
     * @param pTransactionalAnnotationType このモジュールが使うPUのトランザクション処理が必要なメソッドを識別するアノテーションを指定します.
     */
    public MultiPersistenceUnitJpaModule( //
            final String pPersistenceUnitName //
            , final Class<? extends Annotation> pDaoAnnotationType //
            , final Class<? extends Annotation> pTransactionalAnnotationType //
    ) {
        super(pPersistenceUnitName);
        this.daoAnnotationType = pDaoAnnotationType;
        this.transactionalAnnotationType = pTransactionalAnnotationType;
    }

    /**
     * @param pPersistenceUnitName PU名
     * @param pPropertiesProducer PUのプロパティを返すオブジェクト.
     * @param pDaoAnnotationType このモジュールが使うPUの接続が必要なDaoを識別するアノテーションを指定します.
     * @param pTransactionalAnnotationType このモジュールが使うPUのトランザクション処理が必要なメソッドを識別するアノテーションを指定します.
     */
    public MultiPersistenceUnitJpaModule( //
            final String pPersistenceUnitName //
            , final IProducer<Map<String, String>> pPropertiesProducer //
            , final Class<? extends Annotation> pDaoAnnotationType //
            , final Class<? extends Annotation> pTransactionalAnnotationType //
    ) {
        super(pPersistenceUnitName, pPropertiesProducer);
        this.daoAnnotationType = pDaoAnnotationType;
        this.transactionalAnnotationType = pTransactionalAnnotationType;
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
        return PUBlIC_METHOD_MATCHER.and(Matchers.annotatedWith(this.transactionalAnnotationType));
    }

    /**
     * @see jabara.jpa_guice.JpaModuleBase#preConfigure()
     */
    @Override
    protected void preConfigure() {
        this.bind(JpaDaoBase.class).annotatedWith(this.daoAnnotationType).toInstance(new SafeDao(this.entityManagerFactory));
    }

    private static class SafeDao extends JpaDaoBase {
        private static final long serialVersionUID = -3861066152394738948L;

        SafeDao(final EntityManagerFactory pEmf) {
            super(pEmf);
        }

        @Override
        public EntityManager getEntityManager() {
            final EntityManager ret = super.getEntityManager();
            if (!ret.getTransaction().isActive()) {
                throw new IllegalStateException("先にトランザクションを開始している必要があります."); //$NON-NLS-1$
            }
            return ret;
        }
    }
}
