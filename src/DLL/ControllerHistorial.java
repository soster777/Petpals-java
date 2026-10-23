package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import BLL.Historial;
import BLL.Mascota;

public class ControllerHistorial {

	private static Connection con = Conexion.getInstance().getConnection();

	public boolean agregarHistorial(Historial historial) {
		try {
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO Historiales (fecha_regis, tipo_regis, detalle, fk_usuario, fk_mascota) VALUES (?, ?, ?, ?, ?)");

			statement.setDate(1, historial.getFechaRegis());
			statement.setString(2, historial.getTipoRegis());
			statement.setString(3, historial.getDetalle());
			statement.setInt(4, historial.getFkUsuario());
			statement.setInt(5, historial.getFkMascota());

			return statement.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public LinkedList<Historial> mostrarHistorial() {

		LinkedList<Historial> historiales = new LinkedList<Historial>();

		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM Historiales");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int idHistorial = rs.getInt("id_historial");
				java.sql.Date fechaRegis = rs.getDate("fecha_regis");
				String tipoRegis = rs.getString("tipo_regis");
				String detalle = rs.getString("detalle");
				int fkUsuario = rs.getInt("fk_usuario");
				int fkMascota = rs.getInt("fk_mascota");

				historiales.add(new Historial(idHistorial, fechaRegis, tipoRegis, detalle, fkUsuario, fkMascota));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return historiales;
	}

	public LinkedList<Historial> mostrarHistorialPorMascota(Mascota mascota) {

		LinkedList<Historial> historiales = new LinkedList<Historial>();

		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM Historiales WHERE fk_mascota = ? ORDER BY fecha_regis DESC, id_historial DESC");

			stmt.setInt(1, mascota.getId());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				int idHistorial = rs.getInt("id_historial");
				java.sql.Date fechaRegis = rs.getDate("fecha_regis");
				String tipoRegis = rs.getString("tipo_regis");
				String detalle = rs.getString("detalle");
				int fkUsuario = rs.getInt("fk_usuario");
				int fkMascota = rs.getInt("fk_mascota");

				historiales.add(new Historial(idHistorial, fechaRegis, tipoRegis, detalle, fkUsuario, fkMascota));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return historiales;
	}

	public void editarHistorial(Historial historial) {
		try {
			PreparedStatement statement = con.prepareStatement(
					"UPDATE Historiales SET fecha_regis = ?, tipo_regis = ?, detalle = ?, fk_usuario = ?, fk_mascota = ? WHERE id_historial = ?");

			statement.setDate(1, historial.getFechaRegis());
			statement.setString(2, historial.getTipoRegis());
			statement.setString(3, historial.getDetalle());
			statement.setInt(4, historial.getFkUsuario());
			statement.setInt(5, historial.getFkMascota());
			statement.setInt(6, historial.getIdHistorial());

			int filas = statement.executeUpdate();

			if (filas > 0) {
				JOptionPane.showMessageDialog(null, "Historial modificado correctamente.");
			} else {
				JOptionPane.showMessageDialog(null, "No se encontró el historial.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "No se pudo modificar el historial.");
		}
	}

	public void eliminarHistorial(int idHistorial) {
		try {
			PreparedStatement statement = con.prepareStatement("DELETE FROM Historiales WHERE id_historial = ?");

			statement.setInt(1, idHistorial);

			int filas = statement.executeUpdate();

			if (filas > 0) {
				JOptionPane.showMessageDialog(null, "Historial eliminado correctamente.");
			} else {
				JOptionPane.showMessageDialog(null, "No se encontró el historial.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "No se pudo eliminar el historial.");
		}
	}
}