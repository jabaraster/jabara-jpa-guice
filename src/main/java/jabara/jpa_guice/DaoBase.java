/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.NotFound;
import jabara.jpa.entity.IEntity;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import com.google.inject.Inject;

/**
 * @author jabaraster
 */
public class DaoBase implements Serializable {
    private static final long       serialVersionUID = -6876768771003819068L;

    /**
     * 
     */
    public static final Predicate[] EMPTY_PREDICATE  = {};

    /**
     * 
     */
    @Inject
    @javax.inject.Inject
    protected EntityManagerFactory  emf;

    /**
     * @return {@link EntityManager}オブジェクト.
     */
    public EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }

    /**
     * ID値が一致するエンティティを検索して返します.
     * 
     * @param pEntityType 結果のエンティティの型.
     * @param pId ID値.
     * @return エンティティオブジェクト.
     * @throws NotFound 該当エンティティがない場合.
     * @param <E> 結果エンティティオブジェクトの型.
     */
    protected <E extends IEntity> E findByIdCore(final Class<E> pEntityType, final long pId) throws NotFound {
        ArgUtil.checkNull(pEntityType, "pEntityType"); //$NON-NLS-1$
        final E ret = getEntityManager().find(pEntityType, Long.valueOf(pId));
        if (ret == null) {
            throw NotFound.GLOBAL;
        }
        return ret;
    }

    /**
     * 結果が高々１件のクエリを実行して結果を返します. <br>
     * 
     * @param pQuery クエリオブジェクト.
     * @return エンティティオブジェクト.
     * @throws NotFound 該当がない場合.
     * @param <E> 結果オブジェクトの型.
     */
    protected static <E> E getSingleResult(final TypedQuery<E> pQuery) throws NotFound {
        try {
            return pQuery.getSingleResult();
        } catch (final NoResultException e) {
            throw NotFound.GLOBAL;
        }
    }
}
