import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class IndicatorEventListener {

    @EventListener
    public void handleCriticIndicator(IndicadorCriticoEvent event) {
        System.out.println("🚨 ALERTA CRÍTICO DETECTADO!");
        System.out.println("Índice de Tráfego: " + event.getIndiceTrafego());
        System.out.println("Índice de Segurança: " + event.getIndiceSeguranca());

       
    }
}
