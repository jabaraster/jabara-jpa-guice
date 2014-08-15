/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.ExceptionUtil;
import jabara.general.IProducer;
import jabara.jpa.PersistenceXmlPropertyNames;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jabaraster
 * 
 */
public class FallbackPropertiesProducer implements IProducer<Map<String, String>> {
    private static final long                 serialVersionUID = -9073817090793524093L;

    private final List<IFallbackableProducer> producers;

    /**
     * @param pProducers -
     */
    public FallbackPropertiesProducer(final IFallbackableProducer... pProducers) {
        ArgUtil.checkNull(pProducers, "pProducers"); //$NON-NLS-1$
        this.producers = Collections.unmodifiableList(Arrays.asList(pProducers));
    }

    /**
     * @see jabara.general.IProducer#produce()
     */
    @Override
    public Map<String, String> produce() {
        final Map<String, String> ret = cnv(produceCore());
        loadDriverClass(ret); // 環境によってはJDBCドライバの読み込みが必要. ほとんどコストがかからないので、ここで読み込んでおく.
        return ret;
    }

    /**
     * {@link JpaModuleBase}に渡す前にプロパティを加工したい場合はこのメソッドをオーバーライドし、加工後の{@link Map}を返してください. <br>
     * デフォルトの実装では、引数のMapを何も触らず、そのままreturnしています.
     * 
     * @param pProperties -
     * @return -
     */
    @SuppressWarnings("static-method")
    protected Map<String, Object> processProperties(final Map<String, Object> pProperties) {
        // デフォルト処理なし
        return pProperties;
    }

    private Map<String, Object> produceCore() {
        Map<String, Object> ret = visitProducers();
        if (ret == null) {
            ret = new HashMap<String, Object>();
        }
        ret = this.processProperties(ret);

        return ret == null ? new HashMap<String, Object>() : ret;
    }

    private Map<String, Object> visitProducers() {
        for (final IFallbackableProducer producer : this.producers) {
            if (producer.canProduce()) {
                return producer.produce();
            }
        }
        return new HashMap<String, Object>();
    }

    private static Map<String, String> cnv(final Map<String, Object> pMap) {
        final Map<String, String> ret = new HashMap<String, String>();
        for (final Map.Entry<String, Object> entry : pMap.entrySet()) {
            final Object value = entry.getValue();
            ret.put(entry.getKey(), value == null ? null : value.toString());
        }
        return ret;
    }

    private static void loadDriverClass(final Map<String, String> pProperties) {
        final String driverName = pProperties.get(PersistenceXmlPropertyNames.DRIVER);
        if (driverName == null) {
            return;
        }
        try {
            Class.forName(driverName);
        } catch (final ClassNotFoundException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}
