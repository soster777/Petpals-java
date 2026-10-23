package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import BLL.Duenio;
import BLL.Mascota;
import BLL.Rol;
import repository.MascotaRepository;
import repository.Validaciones;

public class ControllerMascota implements MascotaRepository {

	private static Connection con = Conexion.getInstance().getConnection();

	// traigo la mascota junto con los datos del dueño (JOIN), asi se puede mostrar de quien es
	private static final String SELECT_MASCOTAS = "SELECT m.*, u.nombre, u.apellido, u.mail FROM Mascotas m "
			+ "JOIN Usuarios u ON u.id_usuario = m.fk_usuario_duenio ";

	private Mascota crearMascota(ResultSet rs) throws SQLException {
		Duenio duenio = new Duenio(rs.getInt("fk_usuario_duenio"), rs.getString("nombre"), rs.getString("apellido"), 0,
				"", rs.getString("mail"), "", Rol.DUENIO);

		return new Mascota(rs.getInt("id_mascota"), rs.getString("nombre_masc"), rs.getString("especie"),
				rs.getString("raza"), rs.getDate("fecha_nac"), rs.getString("sexo"), rs.getDouble("peso"),
				rs.getString("observaciones"), duenio);
	}

	private boolean datosValidos(Mascota mascota) {
		if (mascota.getNombreMascota() == null || mascota.getNombreMascota().trim().isEmpty() || Validaciones.contieneNumeros(mascota.getNombreMascota())) {
			JOptionPane.showMessageDialog(null, "Error: El nombre de la mascota es obligatorio y no puede contener números.");
			return false;
		}
		if (mascota.getEspecie() == null || mascota.getEspecie().trim().isEmpty() || Validaciones.contieneNumeros(mascota.getEspecie())) {
			JOptionPane.showMessageDialog(null, "Error: La especie es obligatoria y no puede contener números.");
			return false;
		}
		if (mascota.getRaza() == null || mascota.getRaza().trim().isEmpty() || Validaciones.contieneNumeros(mascota.getRaza())) {
			JOptionPane.showMessageDialog(null, "Error: La raza es obligatoria y no puede contener números.");
			return false;
		}
		if (mascota.getPeso() <= 0) {
			JOptionPane.showMessageDialog(null, "Error: El peso tiene que ser mayor a 0.");
			return false;
		}
		return true;
	}

	@Override
	public boolean agregarMascota(Mascota mascota) {
		if (!datosValidos(mascota)) {
			return false;
		}
		try {
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO Mascotas (nombre_masc, especie, raza, fecha_nac, sexo, peso, observaciones, fk_usuario_duenio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
			);

			statement.setString(1, mascota.getNombreMascota());
			statement.setString(2, mascota.getEspecie());
			statement.setString(3, mascota.getRaza());
			statement.setDate(4, mascota.getFechanac());
			statement.setString(5, mascota.getSexo());
			statement.setDouble(6, mascota.getPeso());
			statement.setString(7, mascota.getObservaciones());
			statement.setInt(8, mascota.getDuenio().getId());

			return statement.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public LinkedList<Mascota> mostrarMascotas() {
		LinkedList<Mascota> mascotas = new LinkedList<Mascota>();
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_MASCOTAS + "ORDER BY m.nombre_masc");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				mascotas.add(crearMascota(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mascotas;
	}

	@Override
	public LinkedList<Mascota> mostrarMascotasPorDuenio(Duenio duenio) {
		LinkedList<Mascota> mascotas = new LinkedList<Mascota>();
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_MASCOTAS + "WHERE m.fk_usuario_duenio = ? ORDER BY m.nombre_masc");
			stmt.setInt(1, duenio.getId());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				mascotas.add(crearMascota(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mascotas;
	}

	@Override
	public boolean eliminarMascota(Mascota mascota) {
		try {
			PreparedStatement statement = con.prepareStatement("DELETE FROM Mascotas WHERE id_mascota = ?");
			statement.setInt(1, mascota.getId());
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean editarMascota(Mascota mascota) {
		if (!datosValidos(mascota)) {
			return false;
		}
		try {
			PreparedStatement statement = con.prepareStatement(
					"UPDATE Mascotas SET nombre_masc = ?, especie = ?, raza = ?, fecha_nac = ?, sexo = ?, peso = ?, observaciones = ? WHERE id_mascota = ?"
			);

			statement.setString(1, mascota.getNombreMascota());
			statement.setString(2, mascota.getEspecie());
			statement.setString(3, mascota.getRaza());
			statement.setDate(4, mascota.getFechanac());
			statement.setString(5, mascota.getSexo());
			statement.setDouble(6, mascota.getPeso());
			statement.setString(7, mascota.getObservaciones());
			statement.setInt(8, mascota.getId());

			return statement.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
