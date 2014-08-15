package jabara.jpa_guice.impl;

import jabara.general.ArgUtil;
import jabara.jpa.PersistenceXmlPropertyNames;
import jabara.jpa.db_kind.DbKind;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jabaraster
 */
public class HibernateEntityManagerProperitesBuilder {

    private String dialectClassName;
    private String driverClassName;
    private String jdbcUrl;
    private String jdbcUserName;
    private String jdbcPassword;

    /**
     * @return -
     */
    public Map<String, Object> build() {
        final Map<String, Object> ret = new HashMap<String, Object>();
        ret.put(PersistenceXmlPropertyNames.Hibernate.DIALECT, this.dialectClassName);
        ret.put(PersistenceXmlPropertyNames.DRIVER, this.driverClassName);
        ret.put(PersistenceXmlPropertyNames.JDBC_URL, this.jdbcUrl);
        ret.put(PersistenceXmlPropertyNames.JDBC_USER, this.jdbcUserName);
        ret.put(PersistenceXmlPropertyNames.JDBC_PASSWORD, this.jdbcPassword);
        return ret;
    }

    /**
     * @return the dialectClassName
     */
    public String getDialectClassName() {
        return this.dialectClassName;
    }

    /**
     * @return the driverClassName
     */
    public String getDriverClassName() {
        return this.driverClassName;
    }

    /**
     * @return the jdbcPassword
     */
    public String getJdbcPassword() {
        return this.jdbcPassword;
    }

    /**
     * @return the jdbcUrl
     */
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    /**
     * @return the jdbcUserName
     */
    public String getJdbcUserName() {
        return this.jdbcUserName;
    }

    /**
     * @param pDbKind
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setDbKind(final DbKind pDbKind) {
        ArgUtil.checkNull(pDbKind, "pDbKind"); //$NON-NLS-1$
        this.dialectClassName = pDbKind.getDialectClassName();
        this.driverClassName = pDbKind.getDriverClassName();
        return this;
    }

    /**
     * @param pDialectClassName the dialectClassName to set
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setDialectClassName(final String pDialectClassName) {
        this.dialectClassName = pDialectClassName;
        return this;
    }

    /**
     * @param pDriverClassName the driverClassName to set
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setDriverClassName(final String pDriverClassName) {
        this.driverClassName = pDriverClassName;
        return this;
    }

    /**
     * @param pJdbcPassword the jdbcPassword to set
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setJdbcPassword(final String pJdbcPassword) {
        this.jdbcPassword = pJdbcPassword;
        return this;
    }

    /**
     * @param pJdbcUrl the jdbcUrl to set
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setJdbcUrl(final String pJdbcUrl) {
        this.jdbcUrl = pJdbcUrl;
        return this;
    }

    /**
     * @param pJdbcUserName the jdbcUserName to set
     * @return thisを返します.
     */
    public HibernateEntityManagerProperitesBuilder setJdbcUserName(final String pJdbcUserName) {
        this.jdbcUserName = pJdbcUserName;
        return this;
    }
}
