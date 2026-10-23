package DLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import BLL.Duenio;
import BLL.Veterinario;
import BLL.Empleado;
import BLL.Rol;
import BLL.Usuario;
import repository.Hashing;
import repository.UsuarioRepository;

public class ControllerUsuario<T extends Usuario> implements UsuarioRepository {

	private static Connection con = Conexion.getInstance().getConnection();

	// arma el objeto segun el rol, asi no repito el switch en cada metodo
	private T crearUsuario(ResultSet rs) throws SQLException {
		int id = rs.getInt("id_usuario");
		String nombre = rs.getString("nombre");
		String apellido = rs.getString("apellido");
		int dni = rs.getInt("dni");
		String tel = rs.getString("tel");
		String mail = rs.getString("mail");
		String contrasenia = rs.getString("contrasenia");
		Rol rol = Rol.valueOf(rs.getString("rol").toUpperCase());

		switch (rol) {
		case DUENIO:
			return (T) new Duenio(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
		case VETERINARIO:
			return (T) new Veterinario(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
		case EMPLEADO:
			return (T) new Empleado(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
		default:
			System.out.println("Tipo de usuario desconocido: " + rol);
			return null;
		}
	}

	@Override
	public T login(String mail, String contrasenia) {
		T usuario = null;
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM Usuarios WHERE mail = ?");
			stmt.setString(1, mail);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				if (Hashing.verificar(contrasenia, rs.getString("contrasenia"))) {
					usuario = crearUsuario(rs);
				} else {
					JOptionPane.showMessageDialog(null, "La contraseña es incorrecta");
				}
			} else {
				JOptionPane.showMessageDialog(null, "No existe un usuario con ese mail");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return usuario;
	}

	@Override
	public void agregarUsuario(Usuario usuario) {
		if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() || repository.Validaciones.contieneNumeros(usuario.getNombre())) {
			JOptionPane.showMessageDialog(null, "Error: El nombre es obligatorio y no puede contener números.");
			return;
		}
		if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty() || repository.Validaciones.contieneNumeros(usuario.getApellido())) {
			JOptionPane.showMessageDialog(null, "Error: El apellido es obligatorio y no puede contener números.");
			return;
		}
		if (!repository.Validaciones.esTelefonoValido(usuario.getTel(), 7)) {
			JOptionPane.showMessageDialog(null, "Error: El teléfono es obligatorio y debe tener al menos 7 dígitos.");
			return;
		}
		try {
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO Usuarios (nombre, apellido, dni, tel, mail, contrasenia, rol) VALUES (?,?,?,?,?,?,?)");
			statement.setString(1, usuario.getNombre());
			statement.setString(2, usuario.getApellido());
			statement.setInt(3, usuario.getDni());
			statement.setString(4, usuario.getTel());
			statement.setString(5, usuario.getMail());
			statement.setString(6, usuario.getContrasenia());
			statement.setString(7, usuario.getRol().toString().toLowerCase());

			int filas = statement.executeUpdate();
			if (filas > 0) {
				JOptionPane.showMessageDialog(null, "Usuario registrado correctamente. Ya podés iniciar sesión.");
			}

		} catch (SQLIntegrityConstraintViolationException e) {
			// mail y dni son UNIQUE en la base
			JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese mail o DNI");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public LinkedList<Usuario> mostrarUsuarios() {
		LinkedList<Usuario> usuarios = new LinkedList<>();
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM Usuarios");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				T usuario = crearUsuario(rs);
				if (usuario != null) {
					usuarios.add(usuario);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usuarios;
	}

	@Override
	public LinkedList<Usuario> mostrarDuenios() {
		LinkedList<Usuario> duenios = new LinkedList<>();
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM Usuarios WHERE rol = 'duenio'");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				duenios.add(crearUsuario(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return duenios;
	}

	@Override
	public void eliminarUsuario(Usuario usuario) {
		try {
			PreparedStatement statement = con.prepareStatement("DELETE FROM Usuarios WHERE id_usuario = ?");
			statement.setInt(1, usuario.getId());

			int filas = statement.executeUpdate();
			if (filas > 0) {
				System.out.println("Usuario eliminado correctamente.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void editarUsuario(Usuario usuario) {
		if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() || repository.Validaciones.contieneNumeros(usuario.getNombre())) {
			JOptionPane.showMessageDialog(null, "Error: El nombre es obligatorio y no puede contener números.");
			return;
		}
		if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty() || repository.Validaciones.contieneNumeros(usuario.getApellido())) {
			JOptionPane.showMessageDialog(null, "Error: El apellido es obligatorio y no puede contener números.");
			return;
		}
		if (!repository.Validaciones.esTelefonoValido(usuario.getTel(), 7)) {
			JOptionPane.showMessageDialog(null, "Error: El teléfono es obligatorio y debe tener al menos 7 dígitos.");
			return;
		}
		try {
			PreparedStatement statement = con.prepareStatement(
			"UPDATE Usuarios SET nombre = ?, apellido = ?, dni = ?, tel = ?, mail = ?, contrasenia = ?, rol = ? WHERE id_usuario = ?");

			statement.setString(1, usuario.getNombre());
			statement.setString(2, usuario.getApellido());
			statement.setInt(3, usuario.getDni());
			statement.setString(4, usuario.getTel());
			statement.setString(5, usuario.getMail());
			statement.setString(6, usuario.getContrasenia());
			statement.setString(7, usuario.getRol().toString().toLowerCase());
			statement.setInt(8, usuario.getId());

			int filas = statement.executeUpdate();
			if (filas > 0) {
				System.out.println("Usuario editado correctamente.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
