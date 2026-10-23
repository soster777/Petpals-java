package BLL;

import java.sql.Date;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import DLL.ControllerConsulta;
import DLL.ControllerMascota;
import DLL.ControllerTurno;
import DLL.ControllerVacunacion;
import excepciones.DatoInvalidoException;
import repository.Validaciones;

public class Veterinario extends Usuario implements Validaciones {

	private static ControllerMascota controllerMascota = new ControllerMascota();
	private static ControllerTurno controllerTurno = new ControllerTurno();
	private static ControllerConsulta controllerConsulta = new ControllerConsulta();
	private static ControllerVacunacion controllerVacunacion = new ControllerVacunacion();

	public Veterinario(int id, String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	public Veterinario(String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super(nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	@Override
	public String toString() {
		return "Dr/a. " + nombre + " " + apellido;
	}

	@Override
	public void menu() {

		String[] opcV = { "Mis turnos", "Atender turno", "Cargar historial", "Ver historial de una mascota", "Salir" };

		int opcionV;

		do {
			opcionV = JOptionPane.showOptionDialog(null,
					"Hola " + this.getNombre() + " veterinario/a! Bienvenido/a a PetPals \n¿Qué va a realizar hoy?:",
					"Menú veterinario", 0, 0, null, opcV, opcV[0]);

			switch (opcionV) {
			case 0:
				verMisTurnos();
				break;

			case 1:
				atenderTurno();
				break;

			case 2:
				Mascota mascotaCargar = elegirMascota();
				if (mascotaCargar != null) {
					Historial.crearHistorial(this, mascotaCargar);
				}
				break;

			case 3:
				Mascota mascotaVer = elegirMascota();
				if (mascotaVer != null) {
					JOptionPane.showMessageDialog(null, mascotaVer.ficha());
					Historial.mostrarHistorialMascota(mascotaVer);
				}
				break;

			default:
				break;
			}
		} while (opcionV != 4 && opcionV != -1);
	}

	private void verMisTurnos() {
		LinkedList<Turno> turnos = controllerTurno.mostrarTurnosPorVeterinario(this.getId());

		if (turnos.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No tenés turnos confirmados");
			return;
		}

		String texto = "Tus turnos confirmados:\n\n";
		for (Turno t : turnos) {
			texto += t + "\n";
			if (t.getObservaciones() != null) {
				texto += "     Obs: " + t.getObservaciones() + "\n";
			}
		}
		JOptionPane.showMessageDialog(null, texto);
	}

	// atender un turno: se carga la consulta o la vacuna, queda en el historial y se le avisa al dueño
	private void atenderTurno() {
		Turno turno = Validaciones.elegirDeLista("Seleccione el turno a atender:", "Atender turno",
				controllerTurno.mostrarTurnosPorVeterinario(this.getId()));
		if (turno == null) {
			return;
		}

		JOptionPane.showMessageDialog(null, "Paciente:\n" + turno.getMascota().ficha());

		try {
			Servicio servicio;
			int idServicio;

			if (turno.getTipo().equals("vacunacion")) {
				Vacunacion vacuna = cargarVacuna(turno);
				servicio = vacuna;
				idServicio = controllerVacunacion.agregar(vacuna);
			} else {
				Consulta consulta = cargarConsulta(turno);
				servicio = consulta;
				idServicio = controllerConsulta.agregar(consulta);
			}

			if (idServicio > 0) {
				turno.marcarRealizado();
				controllerTurno.actualizarTurno(turno);
				Historial.registrarServicio(servicio, this, turno.getMascota());
				Notificacion.enviar(turno.getMascota().getDuenio().getId(),
						servicio.getTipoHistorial() + " de " + turno.getMascota().getNombreMascota(),
						servicio.resumen(), turno.getTipo().equals("vacunacion") ? "vacunacion" : "turno",
						turno.getId(), 0);
				JOptionPane.showMessageDialog(null, "Turno atendido y guardado en el historial.\n\n" + servicio);
			}
		} catch (IllegalArgumentException | DatoInvalidoException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private Consulta cargarConsulta(Turno turno) {
		String motivo = turno.getObservaciones() != null ? turno.getObservaciones()
				: Validaciones.validarString("Motivo de la consulta:");
		String diagnostico = Validaciones.validarString("Diagnóstico:");
		String tratamiento = Validaciones.validarTextoOpcional("Tratamiento");
		String observaciones = Validaciones.validarTextoOpcional("Observaciones");
		double precio = Validaciones.validarDouble("Precio de la consulta:");

		return new Consulta("Consulta veterinaria", precio, motivo, diagnostico, tratamiento, observaciones,
				turno.getId());
	}

	private Vacunacion cargarVacuna(Turno turno) {
		String nombre = Validaciones.validarString("Nombre de la vacuna:");
		String dosis = Validaciones.validarString("Dosis (ej: 1ra dosis, refuerzo anual):");
		double precio = Validaciones.validarDouble("Precio:");
		Date hoy = new Date(System.currentTimeMillis());

		Date proxima = null;
		int otraDosis = JOptionPane.showConfirmDialog(null, "¿Necesita otra dosis?", "Próxima dosis",
				JOptionPane.YES_NO_OPTION);
		if (otraDosis == JOptionPane.YES_OPTION) {
			proxima = Validaciones.validarFecha("Fecha de la próxima dosis");
		}

		return new Vacunacion(nombre, "Vacuna " + nombre, precio, dosis, hoy, proxima, turno.getId());
	}

	public Mascota elegirMascota() {
		LinkedList<Mascota> mascotas = controllerMascota.mostrarMascotas();

		if (mascotas.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay mascotas registradas");
			return null;
		}

		return Validaciones.elegirDeLista("Seleccione una mascota:", "Mascotas", mascotas);
	}
}
