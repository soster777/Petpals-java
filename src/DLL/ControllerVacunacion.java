package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import BLL.Vacunacion;
import repository.CrudRepository;

public class ControllerVacunacion implements CrudRepository<Vacunacion> {

	private static Connection con = Conexion.getInstance().getConnection();

	@Override
	public int agregar(Vacunacion v) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Vacunaciones (nombre_vac, descripcion, precio, dosis, fecha_aplic, prox_dosis, fk_turno) VALUES (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, v.getNombreVacuna());
			stmt.setString(2, v.getDescripcion());
			stmt.setDouble(3, v.getPrecio());
			stmt.setString(4, v.getDosis());
			stmt.setDate(5, v.getFechaAplicacion());
			stmt.setDate(6, v.getProximaDosis());
			stmt.setInt(7, v.getIdTurno());
			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			return claves.next() ? claves.getInt(1) : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public LinkedList<Vacunacion> mostrar() {
		LinkedList<Vacunacion> lista = new LinkedList<Vacunacion>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM Vacunaciones ORDER BY fecha_aplic DESC").executeQuery();
			while (rs.next()) {
				lista.add(new Vacunacion(rs.getInt("id_vacunacion"), rs.getString("nombre_vac"),
						rs.getString("descripcion"), rs.getDouble("precio"), rs.getString("dosis"),
						rs.getDate("fecha_aplic"), rs.getDate("prox_dosis"), rs.getInt("fk_turno")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
