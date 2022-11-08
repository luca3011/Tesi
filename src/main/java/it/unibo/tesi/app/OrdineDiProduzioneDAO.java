package it.unibo.tesi.app;

public interface OrdineDiProduzioneDAO {

    public OrdineDiProduzioneDTO read(String numeroOdP);

    public boolean update(OrdineDiProduzioneDTO OdpUpdated);
    
}
