package BLL;

import java.sql.Date;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import DLL.ControllerHistorial;
import repository.Validaciones;

public class Historial {

	private int idHistorial;
	private Date fechaRegis;
	private String tipoRegis;
	private String detalle;
	private int fkUsuario;
	private int fkMascota;

	private static ControllerHistorial controller = new ControllerHistorial();

	public Historial(int idHistorial, Date fechaRegis, String tipoRegis, String detalle, int fkUsuario, int fkMascota) {
		super();
		this.idHistorial = idHistorial;
		this.fechaRegis = fechaRegis;
		this.tipoRegis = tipoRegis;
		this.detalle = detalle;
		this.fkUsuario = fkUsuario;
		this.fkMascota = fkMascota;
	}

	public Historial(Date fechaRegis, String tipoRegis, String detalle, int fkUsuario, int fkMascota) {
		super();
		this.idHistorial = 0;
		this.fechaRegis = fechaRegis;
		this.tipoRegis = tipoRegis;
		this.detalle = detalle;
		this.fkUsuario = fkUsuario;
		this.fkMascota = fkMascota;
	}

	public int getIdHistorial() {
		return idHistorial;
	}

	public void setIdHistorial(int idHistorial) {
		this.idHistorial = idHistorial;
	}

	public Date getFechaRegis() {
		return fechaRegis;
	}

	public void setFechaRegis(Date fechaRegis) {
		this.fechaRegis = fechaRegis;
	}

	public String getTipoRegis() {
		return tipoRegis;
	}

	public void setTipoRegis(String tipoRegis) {
		this.tipoRegis = tipoRegis;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public int getFkUsuario() {
		return fkUsuario;
	}

	public void setFkUsuario(int fkUsuario) {
		this.fkUsuario = fkUsuario;
	}

	public int getFkMascota() {
		return fkMascota;
	}

	public void setFkMascota(int fkMascota) {
		this.fkMascota = fkMascota;
	}

	@Override
	public String toString() {
		return "Fecha: " + fechaRegis + "\nTipo: " + tipoRegis + "\nDetalle: " + detalle;
	}

	public static void crearHistorial(Usuario usuario, Mascota mascota) {

		String[] tipos = { "Consulta", "Vacunación", "Guardería", "Tratamiento", "Observación" };

		Date fechaRegis = Validaciones.validarFecha("Ingrese la fecha del registro: ");

		String tipoRegis = (String) JOptionPane.showInputDialog(null, "Seleccione el tipo de registro:",
				"Historial", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

		if (tipoRegis != null) {
			String detalle = Validaciones.validarString("Ingrese el detalle del historial: ");

			if (controller.agregarHistorial(new Historial(fechaRegis, tipoRegis, detalle, usuario.getId(), mascota.getId()))) {
				JOptionPane.showMessageDialog(null, "Historial agregado correctamente.");
			} else {
				JOptionPane.showMessageDialog(null, "No se pudo agregar el historial.");
			}
		}
	}

	// cuando se hace un servicio (consulta, vacuna, guarderia) queda anotado solo en el historial
	// polimorfismo: no importa que tipo de servicio sea, cada uno sabe armar su resumen
	public static boolean registrarServicio(Servicio servicio, Usuario usuario, Mascota mascota) {
		Historial h = new Historial(new Date(System.currentTimeMillis()), servicio.getTipoHistorial(),
				servicio.resumen(), usuario.getId(), mascota.getId());
		return controller.agregarHistorial(h);
	}

	public static LinkedList<Historial> mostrarHistoriales() {
		return controller.mostrarHistorial();
	}

	public static LinkedList<Historial> mostrarHistorialPorMascota(Mascota mascota) {
		return controller.mostrarHistorialPorMascota(mascota);
	}

	// muestra todo el historial de una mascota en un solo mensaje
	public static void mostrarHistorialMascota(Mascota mascota) {
		LinkedList<Historial> historiales = mostrarHistorialPorMascota(mascota);

		if (historiales.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay historial registrado de: " + mascota.getNombreMascota());
			return;
		}

		String texto = "Historial de " + mascota.getNombreMascota() + ":\n\n";
		for (Historial h : historiales) {
			texto += h.toString() + "\n----------------\n";
		}
		JOptionPane.showMessageDialog(null, texto);
	}
}