package DLL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Conexion {
	private static String URL ="jdbc:mysql://localhost:3306/PetPals";
	private static String USER = "root";
	private static String PASSWORD ="";

	private static Connection conect;
	private static Conexion instance;

	// singleton: hay una sola conexion para toda la app
	private Conexion() {
		try {
			conect = DriverManager.getConnection(URL,USER,PASSWORD);
			System.out.println("Se conectó");
		} catch (SQLException e) {
			System.out.println("No se conectó: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.\nRevisá que MySQL esté prendido y que exista la base PetPals.");
		}
	}
	public static Conexion getInstance() {
		if(instance ==null) {
			instance = new Conexion();
		}
		return instance;
	}
	public Connection getConnection() {
		return conect;
	}

}
