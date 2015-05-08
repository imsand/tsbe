package SMSTracker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


class DBController {

	private static DBController instance = null;

	protected DBController() {
		initDBConnection();
	}

	public static DBController getInstance() {
		if (instance == null) {
			instance = new DBController();
		}
		return instance;
	}

	// private static final DBController dbcontroller = new DBController();
	private Connection connection;
	// Pfad zur Datenbankdatei (Direkt unter dem User Nathanaelsantschi)
	private static final String DB_PATH = System.getProperty("user.home") + "/"
			+ "databasemoc.db";
	
	// Datenbanktreiber werden geladen
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Fehler beim Laden des JDBC-Treibers");
			e.printStackTrace();
		}
	}

	private void initDBConnection() {
		try {
			if (connection != null)
				return;
			System.out.println("Creating Connection to Database...");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			if (!connection.isClosed())
				System.out.println("...Connection established");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (!connection.isClosed() && connection != null) {
						connection.close();
						if (connection.isClosed())
							System.out.println("Connection to Database closed");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public int y;
	public void handleDB(String s) {
	
		try {
			//initDBConnection();
			//String s = "$GPSACP: 131853.000,4657.3384N,00726.6036E,2.0,567.7,3,199.92,1.44,0.77,080515,04";
			// String
			// s="$GPSACP:080220.454,4542.82691N,01344.26820E,259.07,3,2.1,0.1,0.0,0.0,270705,09";
			// String s = "$GPSACP:080220.324,435,,3453,,,,,,,";
			if (s.contains(",,")) {
				System.out.println("test");
			} else {
				// Ersetzt erste "" in String durch nichts ""
				s = s.replace("N", ""); // Dadurch wird der Anwendungsbereich
										// auf die nördlichen und östlichen
										// Bereiche der Erde limitiert
				s = s.replace("E", "");
				s = s.replace("$GPSACP:", "");
				s = s.replace("GPSACP:", "");
				String[] a = s.split(",");

				// s=s.

				// UTC, latitude, longitude, hdop, altitude, fix, cog, spkm,
				// spkn, date, nsat

				// Timestamp gibt long zurück
				// System.currentTimeMillis()

				Statement stmt = connection.createStatement();
				// stmt.executeUpdate("DROP TABLE IF EXISTS mocdata;");
				// stmt.executeUpdate("CREATE TABLE mocdata (UTC, latitude, longitude, hdop, altitude, fix, cog, spkm, spkn, date, nsat);");
				stmt.execute("INSERT INTO mocdata (UTC, latitude, longitude, hdop, altitude, fix, cog, spkm, spkn, date, nsat) VALUES ("
						+ a[0]
						+ ","
						+ a[1]
						+ ","
						+ a[2]
						+ ","
						+ a[3]
						+ ","
						+ a[4]
						+ ","
						+ a[5]
						+ ","
						+ a[6]
						+ ","
						+ a[7]
						+ ","
						+ a[8] + "," + a[9] + "," + a[10] + ");");
				
				System.out.println("geschrieben " +s);

				// PreparedStatement ps = connection
				// .prepareStatement("INSERT INTO mocdata VALUES (?, ?, ?);");

				// System.out.println(zahl[0]);

				// Wenn Int Wert vorhanden kann dies in Klammer so angegeben
				// werden:
				// ps.setInt(1, zahl1);
				/*
				 * ps.setInt(1, 321); ps.setInt(2, 321); ps.setInt(3, 321);
				 * ps.addBatch();
				 */
				/*
				 * ps.setString(1, "Anton Antonius"); ps.setString(2,
				 * "Anton's Alarm"); ps.setDate(3, Date.valueOf("2009-10-01"));
				 * ps.setInt(4, 123); ps.setDouble(5, 98.76); ps.addBatch();
				 */
				connection.setAutoCommit(true);
				// ps.executeBatch();
				// connection.setAutoCommit(true);

				ResultSet rs = stmt.executeQuery("SELECT * FROM mocdata;");
				while (rs.next()) {
					// System.out.println("altitude = " +
					// rs.getInt("altitude"));
					// System.out.println("tempo = " + rs.getInt("tempo"));
					// System.out.println("time = " + rs.getInt("time"));
				}
				rs.close();
				if (y == 5) {
					connection.close();
				} else {
					y = y + 1;
					System.out.println(y);			
					
				}
				
				//connection.close();

			}

		} catch (SQLException e) {
			System.err.println("Couldn't handle DB-Query");
			e.printStackTrace();
		}
	}
}

//haha