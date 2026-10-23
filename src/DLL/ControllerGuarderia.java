package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import BLL.Guarderia;
import repository.CrudRepository;

public class ControllerGuarderia implements CrudRepository<Guarderia> {

	private static Connection con = Conexion.getInstance().getConnection();

	@Override
	public int agregar(Guarderia g) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Guarderias (descripcion, precio, fecha_ing, fecha_sal, alimentacion, medicacion, observaciones, fk_recepcionista, fk_mascota) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, g.getDescripcion());
			stmt.setDouble(2, g.getPrecio());
			stmt.setDate(3, g.getFechaIngreso());
			stmt.setDate(4, g.getFechaSalida());
			stmt.setString(5, g.getAlimentacion());
			stmt.setString(6, g.getMedicacion());
			stmt.setString(7, g.getObservaciones());
			stmt.setInt(8, g.getIdEmpleado());
			stmt.setInt(9, g.getMascota().getId());
			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			return claves.next() ? claves.getInt(1) : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public LinkedList<Guarderia> mostrar() {
		LinkedList<Guarderia> lista = new LinkedList<Guarderia>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM Guarderias ORDER BY fecha_ing DESC").executeQuery();
			while (rs.next()) {
				lista.add(new Guarderia(rs.getInt("id_guarderia"), rs.getString("descripcion"), rs.getDouble("precio"),
						rs.getDate("fecha_ing"), rs.getDate("fecha_sal"), rs.getString("alimentacion"),
						rs.getString("medicacion"), rs.getString("observaciones"), rs.getInt("fk_recepcionista"), null));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
