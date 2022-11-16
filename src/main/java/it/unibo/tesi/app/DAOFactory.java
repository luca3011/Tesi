package it.unibo.tesi.app;

import it.unibo.tesi.app.Jdbc.MsqlDAOFactory;

public abstract class DAOFactory {
	
	
	// --- Actual factory method ---
	
	public static DAOFactory getDAOFactory() {
		return new MsqlDAOFactory();
	}
	
	// --- Factory specification: concrete factories implementing this spec must provide this methods! ---
	
	public abstract OrdineDiProduzioneDAO getOrdineDiProduzioneDAO();
	
	public abstract SchedaControlloDAO getSchedaControlloDAO();

	public abstract ControlloDAO getControlloDAO();
	
	
}