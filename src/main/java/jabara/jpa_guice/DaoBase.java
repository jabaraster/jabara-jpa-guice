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

import com.google.inject.Inject;

/**
 * @author jabaraster
 */
public class DaoBase implements Serializable {
    private static final long      serialVersionUID = -6876768771003819068L;

    /**
     * 
     */
    @Inject
    @javax.inject.Inject
    protected EntityManagerFactory emf;

    /**
     * @return {@link EntityManager}オブジェクト.
     */
    public EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }

    /**
     * @param pEntityType
     * @param pId
     * @return
     * @throws NotFound
     */
    protected <E extends IEntity> E findByIdCore(final Class<E> pEntityType, final long pId) throws NotFound {
        ArgUtil.checkNull(pEntityType, "pEntityType"); //$NON-NLS-1$
        return getEntityManager().find(pEntityType, Long.valueOf(pId));
    }
}
