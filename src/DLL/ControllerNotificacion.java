package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;

import BLL.Notificacion;
import repository.CrudRepository;

public class ControllerNotificacion implements CrudRepository<Notificacion> {

	private static Connection con = Conexion.getInstance().getConnection();

	private Notificacion crearNotificacion(ResultSet rs) throws SQLException {
		return new Notificacion(rs.getInt("id_notificacion"), rs.getString("titulo"), rs.getString("mensaje"),
				rs.getTimestamp("fecha_env"), rs.getString("tipo_noti"), rs.getInt("fk_usuario_duenio"),
				rs.getInt("fk_turno"), rs.getInt("fk_guarderia"));
	}

	@Override
	public int agregar(Notificacion n) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Notificaciones (titulo, mensaje, fecha_env, tipo_noti, fk_usuario_duenio, fk_turno, fk_guarderia) VALUES (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, n.getTitulo());
			stmt.setString(2, n.getMensaje());
			stmt.setTimestamp(3, n.getFechaEnvio());
			stmt.setString(4, n.getTipo());
			stmt.setInt(5, n.getIdDuenio());
			// los id opcionales van como NULL si son 0
			if (n.getIdTurno() > 0) {
				stmt.setInt(6, n.getIdTurno());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			if (n.getIdGuarderia() > 0) {
				stmt.setInt(7, n.getIdGuarderia());
			} else {
				stmt.setNull(7, Types.INTEGER);
			}
			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			return claves.next() ? claves.getInt(1) : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public LinkedList<Notificacion> mostrar() {
		LinkedList<Notificacion> lista = new LinkedList<Notificacion>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM Notificaciones ORDER BY fecha_env DESC").executeQuery();
			while (rs.next()) {
				lista.add(crearNotificacion(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	// las mas nuevas primero
	public LinkedList<Notificacion> mostrarPorDuenio(int idDuenio) {
		LinkedList<Notificacion> lista = new LinkedList<Notificacion>();
		try {
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM Notificaciones WHERE fk_usuario_duenio = ? ORDER BY fecha_env DESC, id_notificacion DESC");
			stmt.setInt(1, idDuenio);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				lista.add(crearNotificacion(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
