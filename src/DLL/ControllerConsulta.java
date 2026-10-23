package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import BLL.Consulta;
import repository.CrudRepository;

public class ControllerConsulta implements CrudRepository<Consulta> {

	private static Connection con = Conexion.getInstance().getConnection();

	@Override
	public int agregar(Consulta c) {
		try {
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO Consultas (descripcion, precio, motivo, diagnostico, tratamiento, observaciones, fk_turno) VALUES (?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, c.getDescripcion());
			stmt.setDouble(2, c.getPrecio());
			stmt.setString(3, c.getMotivo());
			stmt.setString(4, c.getDiagnostico());
			stmt.setString(5, c.getTratamiento());
			stmt.setString(6, c.getObservaciones());
			stmt.setInt(7, c.getIdTurno());
			stmt.executeUpdate();
			ResultSet claves = stmt.getGeneratedKeys();
			return claves.next() ? claves.getInt(1) : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public LinkedList<Consulta> mostrar() {
		LinkedList<Consulta> lista = new LinkedList<Consulta>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM Consultas").executeQuery();
			while (rs.next()) {
				lista.add(new Consulta(rs.getInt("id_consulta"), rs.getString("descripcion"), rs.getDouble("precio"),
						rs.getString("motivo"), rs.getString("diagnostico"), rs.getString("tratamiento"),
						rs.getString("observaciones"), rs.getInt("fk_turno")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
