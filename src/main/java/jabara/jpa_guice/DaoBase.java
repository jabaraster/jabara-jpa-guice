/**
 * 
 */
package jabara.jpa_guice;

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
}
