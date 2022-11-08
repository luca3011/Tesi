package it.unibo.tesi.app.Jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;


import it.unibo.tesi.app.ControlloDAO;
import it.unibo.tesi.app.ControlloDTO;

public class JdbcControlloDAO implements ControlloDAO {
	// === Costanti letterali per non sbagliarsi a scrivere !!! ============================

	static final String TABLE = "[dbo].[SCCL_SchedaControlloControlli]";

	// -------------------------------------------------------------------------------------

	static final String CODICE = "Codice";
	static final String RIGA = "Riga";
	static final String ESITO = "Valore";
	
	static final String TBCreated = "TBCreated";
	static final String TBModified = "TBModified";
	static final String TBCreatedID = "TBCreatedID";
	static final String TBModifiedID = "TBModifiedID";

	// == STATEMENT SQL ====================================================================

	static final String TBCreatedIDefault = "0";
	static final String TBModifiedIDDefault = "0";

	// INSERT INTO table ( name,description, ...) VALUES ( ?,?, ... );
	static final String insert = "INSERT " +
			"INTO " + TABLE +
			" ( " +
			CODICE + ", " +
			RIGA + ", " +
			ESITO + ", " +
			TBCreated + ", " +
			TBModified + ", " +
			TBCreatedID + ", " +
			TBModifiedID +
			") " +
			"VALUES (?,?,?,?,?,?,?) ";

	
	// === METODI DAO =========================================================================

	/**
	 * C
	 */
	public void create(ControlloDTO controllo) {
		// --- 1. Dichiarazione della variabile per il risultato ---
		//Long result = new Long(-1);
		// --- 2. Controlli preliminari sui dati in ingresso ---
		if (controllo == null) {
			System.err.println("create(): failed to insert a null entry");
			return;
		}
		// --- 3. Apertura della connessione ---
		Connection conn = JdbcDAOFactory.createConnection();
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			// --- a. Crea (se senza parametri) o prepara (se con parametri) lo statement
			PreparedStatement prep_stmt = conn.prepareStatement(insert);
			// --- b. Pulisci e imposta i parametri (se ve ne sono)
			prep_stmt.clearParameters();
			prep_stmt.setInt(1, controllo.getCodiceScheda());
			prep_stmt.setInt(2, controllo.getNumeroRiga());
			prep_stmt.setString(3, controllo.getEsito());
			prep_stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
			prep_stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
			prep_stmt.setString(6, TBCreatedIDefault);
			prep_stmt.setString(7, TBModifiedIDDefault);
			// --- c. Esegui l'azione sul database ed estrai il risultato (se atteso)
			prep_stmt.executeUpdate();
			// --- d. Cicla sul risultato (se presente) pe accedere ai valori di ogni sua tupla
			// n.d.
			// --- e. Rilascia la struttura dati del risultato
			// n.d.
			// --- f. Rilascia la struttura dati dello statement
			prep_stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("create(): failed to insert entry: " + e.getMessage());
			e.printStackTrace();
			//result = new Long(-2);
		} finally {
			JdbcDAOFactory.closeConnection(conn);
		}

		// Nel caso della creazione di una nuova tupla eseguo un secondo accesso per sapere che code le e' stato assegnato
		// Chiaramente e' inutile farlo se gia'� il primo accesso ha prodotto errori
		// Devo inoltre preoccuparmi di rimuovere la chiusura dalla connessione dal blocco finally definito precedentemente in quanto riutilizzata
		// --- 6./7. Chiusura della connessione in caso di errori e restituizione prematura di un risultato di fallimento
		/*if ( result == -2 ) {
			MySqlDAOFactory.closeConnection(conn);
			return result;
		}
		
		// --- 1. Dichiarazione della variabile per il risultato ---
		// riutilizziamo quella di prima
		
		// --- 2. Controlli preliminari sui dati in ingresso ---
		// gia'� fatti
		
		// --- 3. Apertura della connessione ---
		// ce n'e' una gia'� aperta, se siamo qui
		
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(MySqlStudentDAO.lastInsert);
			if ( rs.next() ) {
				result = rs.getLong(1);
			}		
			rs.close();
			stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("create(): failed to retrieve id of last inserted entry: "+e.getMessage());
			e.printStackTrace();
		}
		// --- 6. Rilascio, SEMPRE E COMUNQUE, la connessione prima di restituire il controllo al chiamante
		finally {
			MySqlDAOFactory.closeConnection(conn);
		}
		// --- 7. Restituzione del risultato (eventualmente di fallimento)
		return result;*/
	}

}
