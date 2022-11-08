package it.unibo.tesi.app;

public interface SchedaControlloDAO {

    public void create(SchedaControlloDTO scheda);

    public int nextCode();
    
}
