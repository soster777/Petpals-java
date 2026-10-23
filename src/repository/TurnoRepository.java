package repository;

import java.util.LinkedList;

import BLL.Turno;

// DAO de turnos: ademas del agregar/mostrar generico tiene busquedas propias
public interface TurnoRepository extends CrudRepository<Turno> {
	LinkedList<Turno> mostrarTurnosPorDuenio(int idDuenio);
	LinkedList<Turno> mostrarTurnosPorVeterinario(int idVeterinario);
	LinkedList<Turno> mostrarTurnosPorEstado(String estado);
	LinkedList<Turno> mostrarAgenda();
	boolean actualizarTurno(Turno turno);
}
