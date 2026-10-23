package repository;

import java.util.LinkedList;
import BLL.Duenio;
import BLL.Mascota;

// DAO de mascotas: define que operaciones tiene que tener el controller
public interface MascotaRepository {
	boolean agregarMascota(Mascota mascota);
	LinkedList<Mascota> mostrarMascotas();
	LinkedList<Mascota> mostrarMascotasPorDuenio(Duenio duenio);
	boolean eliminarMascota(Mascota mascota);
	boolean editarMascota(Mascota mascota);
}
