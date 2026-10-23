package BLL;

import java.sql.Timestamp;
import java.util.LinkedList;

import DLL.ControllerNotificacion;

public class Notificacion {

	private int id;
	private String titulo;
	private String mensaje;
	private Timestamp fechaEnvio;
	private String tipo; // turno, vacunacion, guarderia, suscripcion o general
	private int idDuenio;
	private int idTurno; // 0 si no esta asociada a un turno
	private int idGuarderia; // 0 si no esta asociada a una guarderia

	private static ControllerNotificacion controller = new ControllerNotificacion();

	public Notificacion(int id, String titulo, String mensaje, Timestamp fechaEnvio, String tipo, int idDuenio,
			int idTurno, int idGuarderia) {
		this.id = id;
		this.titulo = titulo;
		this.mensaje = mensaje;
		this.fechaEnvio = fechaEnvio;
		this.tipo = tipo;
		this.idDuenio = idDuenio;
		this.idTurno = idTurno;
		this.idGuarderia = idGuarderia;
	}

	public Notificacion(String titulo, String mensaje, String tipo, int idDuenio, int idTurno, int idGuarderia) {
		this(0, titulo, mensaje, new Timestamp(System.currentTimeMillis()), tipo, idDuenio, idTurno, idGuarderia);
	}

	// atajo para mandarle una notificacion al dueño desde cualquier parte del sistema
	public static void enviar(int idDuenio, String titulo, String mensaje, String tipo, int idTurno, int idGuarderia) {
		controller.agregar(new Notificacion(titulo, mensaje, tipo, idDuenio, idTurno, idGuarderia));
	}

	public static void enviarPorTurno(Turno turno, String titulo, String mensaje) {
		enviar(turno.getMascota().getDuenio().getId(), titulo, mensaje, "turno", turno.getId(), 0);
	}

	public static LinkedList<Notificacion> mostrarPorDuenio(int idDuenio) {
		return controller.mostrarPorDuenio(idDuenio);
	}

	public int getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public Timestamp getFechaEnvio() {
		return fechaEnvio;
	}

	public String getTipo() {
		return tipo;
	}

	public int getIdDuenio() {
		return idDuenio;
	}

	public int getIdTurno() {
		return idTurno;
	}

	public int getIdGuarderia() {
		return idGuarderia;
	}

	@Override
	public String toString() {
		return fechaEnvio.toString().substring(0, 16) + " - " + titulo + "\n" + mensaje;
	}
}
