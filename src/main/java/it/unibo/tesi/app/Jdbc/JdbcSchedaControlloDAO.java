package it.unibo.tesi.app.Jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.unibo.tesi.app.SchedaControlloDAO;
import it.unibo.tesi.app.SchedaControlloDTO;

public class JdbcSchedaControlloDAO implements SchedaControlloDAO{


    // === Costanti letterali per non sbagliarsi a scrivere !!! ============================

	static final String TABLE = "[dbo].[SCCL_SchedaControllo]";

	// -------------------------------------------------------------------------------------

	static final String CODICE = "Codice";
	static final String MODULOCONTROLLO = "ModuloControllo";
	static final String DATA = "DataEsito";
    static final String NOTE = "Nota";
	static final String STATOCONTROLLO = "EsitoControllo";
	
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
			MODULOCONTROLLO + ", " +
			DATA + ", " +
            NOTE + ", " +
			STATOCONTROLLO + ", " +
			TBCreated + ", " +
			TBModified + ", " +
			TBCreatedID + ", " +
			TBModifiedID +
			") " +
			"VALUES (?,?,?,?,?,?,?,?,?) ";
	
	// SELECT MAX(CODICE) FROM table;
	static String max_code = "SELECT MAX(" + CODICE + ") as CODICE " +
			"FROM " + TABLE;
	
	// === METODI DAO =========================================================================

	/**
	 * C
	 */
	public void create(SchedaControlloDTO scheda) {
		// --- 1. Dichiarazione della variabile per il risultato ---
		//Long result = new Long(-1);
		// --- 2. Controlli preliminari sui dati in ingresso ---
		if (scheda == null) {
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


			prep_stmt.setInt(1, scheda.getCodice());
			prep_stmt.setString(2, scheda.getModuloControllo());
			prep_stmt.setDate(3, new java.sql.Date(scheda.getDataEsito().getTime()));
            prep_stmt.setString(4, scheda.getNote());
			prep_stmt.setInt(5, scheda.getEsitoControllo());
			prep_stmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
			prep_stmt.setDate(7, new java.sql.Date(System.currentTimeMillis()));
			prep_stmt.setString(8, TBCreatedIDefault);
			prep_stmt.setString(9, TBModifiedIDDefault);


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


	@Override
	public int nextCode() {
		// --- 1. Dichiarazione della variabile per il risultato ---
		int result = -1;
		// --- 2. Controlli preliminari sui dati in ingresso ---
		
		// --- 3. Apertura della connessione ---
		Connection conn = JdbcDAOFactory.createConnection();
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			// --- a. Crea (se senza parametri) o prepara (se con parametri) lo statement
			PreparedStatement prep_stmt = conn.prepareStatement(max_code);
			// --- b. Pulisci e imposta i parametri (se ve ne sono)
			prep_stmt.clearParameters();
			// --- c. Esegui l'azione sul database ed estrai il risultato (se atteso)
			ResultSet rs = prep_stmt.executeQuery();
			// --- d. Cicla sul risultato (se presente) pe accedere ai valori di ogni sua tupla
			if (rs.next()) {
				int entry;
				entry = rs.getInt(CODICE);
				result = entry;
			}
			// --- e. Rilascia la struttura dati del risultato
			rs.close();
			// --- f. Rilascia la struttura dati dello statement
			prep_stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("nextcode(): " + max_code + " failed to retrieve max entry from: " + TABLE + e.getMessage());
			e.printStackTrace();
		}
		// --- 6. Rilascio, SEMPRE E COMUNQUE, la connessione prima di restituire il controllo al chiamante
		finally {
			JdbcDAOFactory.closeConnection(conn);
		}
		// --- 7. Restituzione del risultato (eventualmente di fallimento)
		return result;
	}


}
