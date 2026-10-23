package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;

import BLL.Duenio;
import BLL.Mascota;
import BLL.Rol;
import BLL.Turno;
import repository.TurnoRepository;

public class ControllerTurno implements TurnoRepository {

	private static Connection con = Conexion.getInstance().getConnection();

	// junto turno + mascota + dueño + veterinario para mostrar todo en la agenda
	private static final String SELECT_TURNOS = "SELECT t.*, m.nombre_masc, m.especie, m.raza, m.fecha_nac, m.sexo, m.peso, "
			+ "m.observaciones AS obs_mascota, m.fk_usuario_duenio, d.nombre AS nombre_duenio, d.apellido AS apellido_duenio, "
			+ "d.mail AS mail_duenio, CONCAT(v.nombre, ' ', v.apellido) AS nombre_vet " + "FROM Turnos t "
			+ "JOIN Mascotas m ON m.id_mascota = t.fk_mascota "
			+ "JOIN Usuarios d ON d.id_usuario = m.fk_usuario_duenio "
			+ "LEFT JOIN Usuarios v ON v.id_usuario = t.fk_usuario_veterinario ";

	private static final String ORDEN = " ORDER BY t.fecha_turno, t.hora_turno";

	private Turno crearTurno(ResultSet rs) throws SQLException {
		Duenio duenio = new Duenio(rs.getInt("fk_usuario_duenio"), rs.getString("nombre_duenio"),
				rs.getString("apellido_duenio"), 0, "", rs.getString("mail_duenio"), "", Rol.DUENIO);

		Mascota mascota = new Mascota(rs.getInt("fk_mascota"), rs.getString("nombre_masc"), rs.getString("especie"),
				rs.getString("raza"), rs.getDate("fecha_nac"), rs.getString("sexo"), rs.getDouble("peso"),
				rs.getString("obs_mascota"), duenio);

		return new Turno(rs.getInt("id_turno"), rs.getDate("fecha_turno"), rs.getTime("hora_turno"),
				rs.getString("estado"), rs.getString("tipo_turno"), rs.getString("observaciones"),
				rs.getInt("fk_usuario_veterinario"), rs.getString("nombre_vet"), mascota);
	}

	private LinkedList<Turno> buscar(String where, Object parametro) {
		LinkedList<Turno> turnos = new LinkedList<Turno>();
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_TURNOS + where + ORDEN);
			if (parametro instanceof Integer) {
				stmt.setInt(1, (Integer) parametro);
			} else if (parametro instanceof String) {
				stmt.setString(1, (String) parametro);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				turnos.add(crearTurno(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return turnos;
	}

	// devuelve el id generado, o 0 si no se pudo guardar
	@Override
	public int agregar(Turno turno) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Turnos (fecha_turno, hora_turno, estado, tipo_turno, observaciones, fk_usuario_veterinario, fk_mascota) VALUES (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, turno.getFecha());
			stmt.setTime(2, turno.getHora());
			stmt.setString(3, turno.getEstado());
			stmt.setString(4, turno.getTipo());
			stmt.setString(5, turno.getObservaciones());
			if (turno.getIdVeterinario() > 0) {
				stmt.setInt(6, turno.getIdVeterinario());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setInt(7, turno.getMascota().getId());

			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			if (claves.next()) {
				turno.setId(claves.getInt(1));
				return turno.getId();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public LinkedList<Turno> mostrar() {
		return buscar("", null);
	}

	// agenda: turnos activos de hoy en adelante
	@Override
	public LinkedList<Turno> mostrarAgenda() {
		return buscar("WHERE t.fecha_turno >= CURDATE() AND t.estado IN ('pendiente', 'confirmada')", null);
	}

	@Override
	public LinkedList<Turno> mostrarTurnosPorDuenio(int idDuenio) {
		return buscar("WHERE m.fk_usuario_duenio = ?", idDuenio);
	}

	@Override
	public LinkedList<Turno> mostrarTurnosPorVeterinario(int idVeterinario) {
		return buscar("WHERE t.fk_usuario_veterinario = ? AND t.estado = 'confirmada'", idVeterinario);
	}

	@Override
	public LinkedList<Turno> mostrarTurnosPorEstado(String estado) {
		return buscar("WHERE t.estado = ?", estado);
	}

	@Override
	public boolean actualizarTurno(Turno turno) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"UPDATE Turnos SET fecha_turno = ?, hora_turno = ?, estado = ?, fk_usuario_veterinario = ? WHERE id_turno = ?");
			stmt.setDate(1, turno.getFecha());
			stmt.setTime(2, turno.getHora());
			stmt.setString(3, turno.getEstado());
			if (turno.getIdVeterinario() > 0) {
				stmt.setInt(4, turno.getIdVeterinario());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			stmt.setInt(5, turno.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
