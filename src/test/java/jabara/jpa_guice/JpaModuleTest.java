/**
 * 
 */
package jabara.jpa_guice;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import jabara.general.IProducer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.postgresql.Driver;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author jabaraster
 */
public class JpaModuleTest {

    /**
     * @throws InterruptedException -
     */
    @SuppressWarnings({ "static-method", "nls" })
    @Test
    public void _test() throws InterruptedException {
        final Injector injector = Guice.createInjector(new JpaModule("jabara-jpa-guice", new IProducer<Map<String, String>>() {
            @Override
            public Map<String, String> produce() {
                final Map<String, String> ret = new HashMap<String, String>();

                ret.put("javax.persistence.driver", Driver.class.getName());
                ret.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/postgres");
                ret.put("javax.persistence.jdbc.user", "postgres");
                ret.put("javax.persistence.jdbc.password", "postgres");

                return ret;
            }
        }));
        final EntityManagerFactory emf = injector.getInstance(EntityManagerFactory.class);

        assertSame(emf.createEntityManager(), emf.createEntityManager(new HashMap<String, String>()));
        final AtomicReference<EntityManager> emRef = new AtomicReference<EntityManager>();
        final Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                emRef.set(emf.createEntityManager());
            }
        });
        th.start();
        th.join();
        assertNotSame(emf.createEntityManager(), emRef.get());

        System.out.println(injector.getInstance(TestDao.class).test());
    }

    /**
     * @author jabaraster
     */
    public static class TestDao extends DaoBase {
        private static final long serialVersionUID = 2396934424504573698L;

        /**
         * @return -
         */
        @SuppressWarnings({ "nls" })
        public List<String> test() {
            final EntityManager em = this.emf.createEntityManager();
            return Arrays.asList("1", "2", "3", "4", String.valueOf(em));
        }
    }
}
