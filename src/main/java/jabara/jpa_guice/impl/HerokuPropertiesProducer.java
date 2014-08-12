/**
 * 
 */
package jabara.jpa_guice.impl;

import jabara.general.EnvironmentUtil;
import jabara.jpa.db_kind.PostgreSQL9;
import jabara.jpa_guice.IFallbackableProducer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author jabaraster
 * 
 */
public class HerokuPropertiesProducer implements IFallbackableProducer {
    private static final long serialVersionUID = -8642791780945413253L;

    /**
     * @see jabara.jpa_guice.IFallbackableProducer#canProduce()
     */
    @Override
    public boolean canProduce() {
        return EnvironmentUtil.getString("DATABASE_URL", null) != null; //$NON-NLS-1$
    }

    /**
     * @see jabara.general.IProducer#produce()
     */
    @Override
    public Map<String, String> produce() {
        try {
            final URI dbUri = new URI(EnvironmentUtil.getStringUnsafe("DATABASE_URL")); //$NON-NLS-1$

            final String[] userInfoTokens = dbUri.getUserInfo().split(":"); //$NON-NLS-1$
            final String username = userInfoTokens[0];
            final String password = userInfoTokens[1];
            final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath() + ":" + dbUri.getPort(); //$NON-NLS-1$ //$NON-NLS-2$

            return new HibernateEntityManagerProperitesBuilder() //
                    .setDbKind(PostgreSQL9.INSTANCE) //
                    .setJdbcPassword(password) //
                    .setJdbcUrl(dbUrl) //
                    .setJdbcUserName(username) //
                    .build();

        } catch (final URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
