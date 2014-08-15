/**
 * 
 */
package jabara.jpa_guice.impl;

import jabara.general.EnvironmentUtil;
import jabara.jpa.db_kind.PostgreSQL9;
import jabara.jpa_guice.IFallbackableProducer;

import java.util.Map;

/**
 * @author jabaraster
 * 
 */
public class RdsPostgreSqlPropertiesProducer implements IFallbackableProducer {
    private static final long serialVersionUID = -5439334521092875506L;

    /**
     * @see jabara.jpa_guice.IFallbackableProducer#canProduce()
     */
    @Override
    public boolean canProduce() {
        return EnvironmentUtil.getString("RDS_DB_NAME", null) != null; //$NON-NLS-1$
    }

    /**
     * @see jabara.general.IProducer#produce()
     */
    @Override
    public Map<String, Object> produce() {
        final String dbName = EnvironmentUtil.getStringUnsafe("RDS_DB_NAME"); //$NON-NLS-1$
        final String port = EnvironmentUtil.getStringUnsafe("RDS_PORT"); //$NON-NLS-1$
        final String hostname = EnvironmentUtil.getStringUnsafe("RDS_HOSTNAME"); //$NON-NLS-1$
        final String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        final String userName = EnvironmentUtil.getStringUnsafe("RDS_USERNAME"); //$NON-NLS-1$
        final String password = EnvironmentUtil.getStringUnsafe("RDS_PASSWORD"); //$NON-NLS-1$

        return new HibernateEntityManagerProperitesBuilder() //
                .setDbKind(PostgreSQL9.INSTANCE) //
                .setJdbcPassword(password) //
                .setJdbcUrl(jdbcUrl) //
                .setJdbcUserName(userName) //
                .build();
    }
}
