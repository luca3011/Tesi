package it.unibo.tesi.app;

import it.unibo.tesi.app.Jdbc.JdbcDAOFactory;

public abstract class DAOFactory {
	
	
	// --- Actual factory method ---
	
	public static DAOFactory getDAOFactory() {
		return new JdbcDAOFactory();
	}
	
	// --- Factory specification: concrete factories implementing this spec must provide this methods! ---
	
	public abstract OrdineDiProduzioneDAO getOrdineDiProduzioneDAO();
	
	public abstract SchedaControlloDAO getSchedaControlloDAO();

	public abstract ControlloDAO getControlloDAO();
	
	
}
