package it.unibo.tesi.app.Jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.unibo.tesi.app.OrdineDiProduzioneDAO;
import it.unibo.tesi.app.OrdineDiProduzioneDTO;


public class MsqlOrdineDiProduzioneDAO implements OrdineDiProduzioneDAO {
	// === Costanti letterali per non sbagliarsi a scrivere !!! ============================

	static final String TABLE = "[dbo].[MA_MO]";

	// -------------------------------------------------------------------------------------

	static final String IDODP = "MOId";
	static final String NUMEROODP = "MONo";
	static final String CODICEARTICOLO = "BOM";
	static final String STATO = "MOStatus";
	static final String SCARTI = "ScrapQuantity";
	static final String SCHEDACONTROLLO = "SCCL_SchedaCollaudo";

	// == STATEMENT SQL ====================================================================

	// SELECT * FROM table WHERE idcolumn = ?;
	static String read_by_code = "SELECT * " +
			"FROM " + TABLE + " " +
			"WHERE " + NUMEROODP + " = ? ";

	// UPDATE table SET xxxcolumn = ?, ... WHERE idcolumn = ?;
	static String update = "UPDATE " + TABLE + " " +
			"SET " +
			SCARTI + " = ?, " +
			SCHEDACONTROLLO + " = ? " +
			"WHERE " + NUMEROODP + " = ? ";

	static String get_stato = "SELECT " + STATO + " AS " + STATO + " " +
			"FROM " + TABLE + " " +
			"WHERE " + NUMEROODP + " = ? ";

	// === METODI DAO =========================================================================

	public OrdineDiProduzioneDTO read(String numeroOdP) {
		// --- 1. Dichiarazione della variabile per il risultato ---
		OrdineDiProduzioneDTO result = null;
		// --- 2. Controlli preliminari sui dati in ingresso ---
		if (numeroOdP.isBlank()) {
			System.err.println("read(): cannot read an entry with empty OdP number");
			return result;
		}
		// --- 3. Apertura della connessione ---
		Connection conn = MsqlDAOFactory.createConnection();
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			// --- a. Crea (se senza parametri) o prepara (se con parametri) lo statement
			PreparedStatement prep_stmt = conn.prepareStatement(read_by_code);
			// --- b. Pulisci e imposta i parametri (se ve ne sono)
			prep_stmt.clearParameters();
			prep_stmt.setString(1, numeroOdP);
			// --- c. Esegui l'azione sul database ed estrai il risultato (se atteso)
			ResultSet rs = prep_stmt.executeQuery();
			// --- d. Cicla sul risultato (se presente) pe accedere ai valori di ogni sua tupla
			if (rs.next()) {
				OrdineDiProduzioneDTO entry = new OrdineDiProduzioneDTO();

				entry.setIdOdp(rs.getInt(IDODP));
				entry.setNumeroOdP(numeroOdP);
				entry.setCodiceArticolo(rs.getString(CODICEARTICOLO));
				entry.setStato(rs.getInt(STATO));

				result = entry;
			}
			// --- e. Rilascia la struttura dati del risultato
			rs.close();
			// --- f. Rilascia la struttura dati dello statement
			prep_stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("read(): failed to retrieve entry with id = " + numeroOdP + ": " + e.getMessage());
			e.printStackTrace();
		}
		// --- 6. Rilascio, SEMPRE E COMUNQUE, la connessione prima di restituire il controllo al chiamante
		finally {
			MsqlDAOFactory.closeConnection(conn);
		}
		// --- 7. Restituzione del risultato (eventualmente di fallimento)
		return result;
	}

	public boolean update(OrdineDiProduzioneDTO OdpUpdated) {
		// --- 1. Dichiarazione della variabile per il risultato ---
		boolean result = false;
		// --- 2. Controlli preliminari sui dati in ingresso ---
		if (OdpUpdated == null) {
			System.err.println("update(): failed to update a null entry");
			return result;
		}
		// --- 3. Apertura della connessione ---
		Connection conn = MsqlDAOFactory.createConnection();
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			// --- a. Crea (se senza parametri) o prepara (se con parametri) lo statement
			PreparedStatement prep_stmt = conn.prepareStatement(update);
			// --- b. Pulisci e imposta i parametri (se ve ne sono)
			prep_stmt.clearParameters();

			prep_stmt.setLong(1, OdpUpdated.getScarti());
			prep_stmt.setInt(2, OdpUpdated.getSchedaControllo().getCodice());
			prep_stmt.setString(3, OdpUpdated.getNumeroOdP());

			// --- c. Esegui l'azione sul database ed estrai il risultato (se atteso)
			prep_stmt.executeUpdate();
			// --- d. Cicla sul risultato (se presente) pe accedere ai valori di ogni sua tupla
			// n.d. qui devo solo dire al chiamante che e' andato tutto liscio
			result = true;
			// --- e. Rilascia la struttura dati del risultato
			// n.d.
			// --- f. Rilascia la struttura dati dello statement
			prep_stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("insert(): failed to update entry: " + e.getMessage());
			e.printStackTrace();
		}
		// --- 6. Rilascio, SEMPRE E COMUNQUE, la connessione prima di restituire il controllo al chiamante
		finally {
			MsqlDAOFactory.closeConnection(conn);
		}
		// --- 7. Restituzione del risultato (eventualmente di fallimento)
		return result;
	}

	public boolean isTerminato(String numeroOdP) {
		// --- 1. Dichiarazione della variabile per il risultato ---
		boolean result = false;
		// --- 2. Controlli preliminari sui dati in ingresso ---
		if (numeroOdP.isBlank()) {
			System.err.println("read(): cannot read an entry with empty OdP number");
			return false;
		}
		// --- 3. Apertura della connessione ---
		Connection conn = MsqlDAOFactory.createConnection();
		// --- 4. Tentativo di accesso al db e impostazione del risultato ---
		try {
			// --- a. Crea (se senza parametri) o prepara (se con parametri) lo statement
			PreparedStatement prep_stmt = conn.prepareStatement(get_stato);
			// --- b. Pulisci e imposta i parametri (se ve ne sono)
			prep_stmt.clearParameters();
			prep_stmt.setString(1, numeroOdP);
			// --- c. Esegui l'azione sul database ed estrai il risultato (se atteso)
			ResultSet rs = prep_stmt.executeQuery();
			// --- d. Cicla sul risultato (se presente) pe accedere ai valori di ogni sua tupla
			if (rs.next()) {
				int entry;
				entry = rs.getInt(STATO);
				
				if(entry==20578306)
					result = true;
			}
			// --- e. Rilascia la struttura dati del risultato
			rs.close();
			// --- f. Rilascia la struttura dati dello statement
			prep_stmt.close();
		}
		// --- 5. Gestione di eventuali eccezioni ---
		catch (Exception e) {
			System.err.println("read(): failed to retrieve entry with id = " + numeroOdP + ": " + e.getMessage());
			e.printStackTrace();
		}
		// --- 6. Rilascio, SEMPRE E COMUNQUE, la connessione prima di restituire il controllo al chiamante
		finally {
			MsqlDAOFactory.closeConnection(conn);
		}
		// --- 7. Restituzione del risultato (eventualmente di fallimento)

		return result;
	}


}
