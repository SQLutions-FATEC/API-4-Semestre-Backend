import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IndicatorScheduler {

    @Autowired
    private IndicatorRepository indicadorRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 30000)
    public void checkindicator() {
        List<Map<String, Object>> results = indicatorRepository.searchIndicators();

        for (Map<String, Object> row : results) {
            int indiceTrafego = ((Number) row.get("indice_trafego")).intValue();
            int indiceSeguranca = ((Number) row.get("indice_seguranca")).intValue();

            if (indiceTrafego > 4 || indiceSeguranca > 4) {
                eventPublisher.publishEvent(new IndicadorCriticoEvent(this, indiceTrafego, indiceSeguranca));
            }
        }
    }
}
