package repository;

import java.util.LinkedList;

// interfaz generica para el patron DAO: T es la clase que guarda cada controller
// (Consulta, Vacunacion, Guarderia, Notificacion...)
public interface CrudRepository<T> {
	int agregar(T objeto);
	LinkedList<T> mostrar();
}
