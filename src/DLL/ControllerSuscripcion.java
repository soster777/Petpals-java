package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import BLL.Plan;
import BLL.Suscripcion;
import repository.CrudRepository;

public class ControllerSuscripcion implements CrudRepository<Suscripcion> {

	private static Connection con = Conexion.getInstance().getConnection();

	private static final String SELECT_SUSCRIPCIONES = "SELECT s.*, p.tipo_plan, p.precio, p.descripcion FROM Suscripciones s "
			+ "JOIN Planes p ON p.id_plan = s.fk_plan ";

	private Suscripcion crearSuscripcion(ResultSet rs) throws SQLException {
		Plan plan = new Plan(rs.getInt("fk_plan"), rs.getString("tipo_plan"), rs.getDouble("precio"),
				rs.getString("descripcion"));
		return new Suscripcion(rs.getInt("id_suscripcion"), rs.getDate("fecha_inicio"), rs.getDate("fecha_fin"),
				rs.getString("estado"), rs.getInt("fk_usuario_duenio"), plan);
	}

	public LinkedList<Plan> mostrarPlanes() {
		LinkedList<Plan> planes = new LinkedList<Plan>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM Planes ORDER BY precio").executeQuery();
			while (rs.next()) {
				planes.add(new Plan(rs.getInt("id_plan"), rs.getString("tipo_plan"), rs.getDouble("precio"),
						rs.getString("descripcion")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return planes;
	}

	@Override
	public int agregar(Suscripcion s) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Suscripciones (fecha_inicio, fecha_fin, estado, fk_usuario_duenio, fk_plan) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, s.getFechaInicio());
			stmt.setDate(2, s.getFechaFin());
			stmt.setString(3, s.getEstado());
			stmt.setInt(4, s.getIdDuenio());
			stmt.setInt(5, s.getPlan().getId());
			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			if (claves.next()) {
				s.setId(claves.getInt(1));
				return s.getId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public LinkedList<Suscripcion> mostrar() {
		LinkedList<Suscripcion> lista = new LinkedList<Suscripcion>();
		try {
			ResultSet rs = con.prepareStatement(SELECT_SUSCRIPCIONES + "ORDER BY s.fecha_inicio DESC").executeQuery();
			while (rs.next()) {
				lista.add(crearSuscripcion(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	// devuelve la suscripcion vigente del dueño, o null si no tiene
	public Suscripcion buscarVigente(int idDuenio) {
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_SUSCRIPCIONES
					+ "WHERE s.fk_usuario_duenio = ? AND s.estado = 'activa' AND s.fecha_fin >= CURDATE() ORDER BY s.fecha_fin DESC LIMIT 1");
			stmt.setInt(1, idDuenio);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return crearSuscripcion(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean cancelar(Suscripcion s) {
		try {
			PreparedStatement stmt = con.prepareStatement("UPDATE Suscripciones SET estado = 'cancelada' WHERE id_suscripcion = ?");
			stmt.setInt(1, s.getId());
			if (stmt.executeUpdate() > 0) {
				s.setEstado(Suscripcion.CANCELADA);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
