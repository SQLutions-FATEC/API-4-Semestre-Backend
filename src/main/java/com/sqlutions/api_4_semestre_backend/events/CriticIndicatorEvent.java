import org.springframework.context.ApplicationEvent;

public class CriticIndicatorEvent extends ApplicationEvent {

    private final int indiceTrafego;
    private final int indiceSeguranca;

    public CriticIndicatorEvent(Object source, int indiceTrafego, int indiceSeguranca) {
        super(source);
        this.indiceTrafego = indiceTrafego;
        this.indiceSeguranca = indiceSeguranca;
    }

    public int getIndiceTrafego() {
        return indiceTrafego;
    }

    public int getIndiceSeguranca() {
        return indiceSeguranca;
    }
}
