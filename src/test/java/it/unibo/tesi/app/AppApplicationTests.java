package it.unibo.tesi.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppApplicationTests {

	@Test
	void testDAO()
	{
		System.out.println("Prova");

		DAOFactory daoFactoryInstance = DAOFactory.getDAOFactory();
		SchedaControlloDAO schedaDAO = daoFactoryInstance.getSchedaControlloDAO();
		OrdineDiProduzioneDAO ordineDAO = daoFactoryInstance.getOrdineDiProduzioneDAO();
		ControlloDAO controlloDAO = daoFactoryInstance.getControlloDAO();

		String numero_odp = "04/00005";

		//inserisco un odp che so essere terminato
		assertTrue(ordineDAO.isTerminato(numero_odp));

		OrdineDiProduzioneDTO ordine;
		ordine = ordineDAO.read(numero_odp);
		System.out.println(ordine);

		// ordineDAO.update(null);

		// schedaDAO.create(null);

		assertEquals(schedaDAO.nextCode(),7);
		

		// controlloDAO.create(null);

	}

}
