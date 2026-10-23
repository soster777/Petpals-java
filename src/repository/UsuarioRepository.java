package repository;

import java.util.LinkedList;
import java.util.List;
import BLL.Usuario;

// DAO de usuarios
public interface UsuarioRepository {
    void agregarUsuario(Usuario usuario);
    List<Usuario> mostrarUsuarios();
	<T> T login(String nombre, String password);
	LinkedList<Usuario> mostrarDuenios();
	void eliminarUsuario(Usuario usuario);
	void editarUsuario(Usuario usuario);
}
