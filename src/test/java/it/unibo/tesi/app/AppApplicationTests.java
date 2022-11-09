package it.unibo.tesi.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppApplicationTests {

	@Test
	void testOrdineDAO()
	{
		System.out.println("Prova");

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();

		String numero_odp = "04/00005";

		//inserisco un odp che so essere terminato
		assertTrue(ordineDAO.isTerminato(numero_odp));

		OrdineDiProduzioneDTO ordine;
		ordine = ordineDAO.read(numero_odp);
		//System.out.println(ordine);

		ordine.incrementaScarti();
		ordineDAO.update(ordine);
		ordine = ordineDAO.read(numero_odp);
		//System.out.println(ordine);
		assertEquals(ordine.getScarti(), 1);

	}

	@Test
	void testSchedaDAO(){

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		
		assertEquals(schedaDAO.nextCode(),7);

		SchedaControlloDTO scheda = new SchedaControlloDTO(8, "TEST", "SCHEDA DI TEST", 124715008);
		scheda.setDataEsito(new Date());
	
		schedaDAO.create(scheda);

		//da controllare sul db
	
	}

	@Test
	void testControlloDAO()
	{
	
		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		ControlloDAO controlloDAO = daoFactoryInstance.getControlloDAO();
	
		ArrayList<ControlloDTO> controlli = new ArrayList<>();

		for (int i = 1; i < 5; i++) {
			controlli.add(new ControlloDTO(8, i, "OK", "OK_FLOW"));
		}

		for (ControlloDTO controllo : controlli) {
			controlloDAO.create(controllo);
		}

	}
	

}
