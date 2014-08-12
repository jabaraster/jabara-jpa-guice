/**
 * 
 */
package jabara.jpa_guice.impl;

import jabara.jpa.db_kind.H2;
import jabara.jpa_guice.IFallbackableProducer;

import java.util.Map;

/**
 * @author jabaraster
 * 
 */
public class H2PropertiesProducer implements IFallbackableProducer {
    private static final long serialVersionUID = -5705472889037639936L;

    private final String      dataFilePath;

    /**
     * @param pDataFilePath -
     */
    public H2PropertiesProducer(final String pDataFilePath) {
        this.dataFilePath = pDataFilePath;
    }

    /**
     * @see jabara.jpa_guice.IFallbackableProducer#canProduce()
     */
    public boolean canProduce() {
        return true;
    }

    /**
     * @see jabara.general.IProducer#produce()
     */
    @Override
    public Map<String, String> produce() {
        return new HibernateEntityManagerProperitesBuilder() //
                .setDbKind(H2.INSTANCE) //
                .setJdbcPassword("") // //$NON-NLS-1$
                .setJdbcUrl("jdbc:h2:" + this.dataFilePath) // //$NON-NLS-1$
                .setJdbcUserName("sa") // //$NON-NLS-1$
                .build();
    }
}
