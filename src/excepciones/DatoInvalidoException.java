package excepciones;

// excepcion propia para cuando un dato no cumple una regla del negocio
// (por ejemplo, sacar un turno para una fecha que ya paso)
public class DatoInvalidoException extends Exception {

	public DatoInvalidoException(String mensaje) {
		super(mensaje);
	}
}
