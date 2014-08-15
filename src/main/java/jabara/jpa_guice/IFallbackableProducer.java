package jabara.jpa_guice;

import jabara.general.IProducer;

import java.util.Map;

/**
 * @author jabaraster
 */
public interface IFallbackableProducer extends IProducer<Map<String, Object>> {
    /**
     * @return -
     */
    boolean canProduce();
}