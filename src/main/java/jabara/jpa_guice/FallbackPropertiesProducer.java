/**
 * 
 */
package jabara.jpa_guice;

import jabara.general.ArgUtil;
import jabara.general.IProducer;

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
        final Map<String, String> ret = produceCore();
        return ret == null ? new HashMap<String, String>() : ret;
    }

    private Map<String, String> produceCore() {
        for (final IFallbackableProducer producer : this.producers) {
            if (producer.canProduce()) {
                return producer.produce();
            }
        }
        return null;
    }
}
