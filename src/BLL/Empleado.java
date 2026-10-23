package BLL;

import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import DLL.ControllerGuarderia;
import DLL.ControllerMascota;
import DLL.ControllerTurno;
import DLL.ControllerUsuario;
import excepciones.DatoInvalidoException;
import repository.Validaciones;

public class Empleado extends Usuario implements Validaciones {

	private static ControllerUsuario<Usuario> controllerUsuario = new ControllerUsuario<Usuario>();
	private static ControllerMascota controllerMascota = new ControllerMascota();
	private static ControllerTurno controllerTurno = new ControllerTurno();
	private static ControllerGuarderia controllerGuarderia = new ControllerGuarderia();

	public Empleado(int id, String nombre, String apellido, int dni, String tel, String mail, String contrasenia,
			Rol rol) {
		super(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	public Empleado(String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super(nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido + " (empleado/a)";
	}

	@Override
	public void menu() {

		String[] opcE = { "Consultar agenda", "Registrar turno", "Confirmar turnos", "Modificar turno", "Cancelar turno",
				"Registrar guardería", "Modificar datos de mascota", "Enviar notificación", "Salir" };

		int opcionE;

		do {
			opcionE = JOptionPane.showOptionDialog(null,
					"Hola " + this.getNombre() + " empleado/a! Bienvenido/a a PetPals \n¿Qué va a realizar hoy?:",
					"Menú empleado", 0, 0, null, opcE, opcE[0]);

			switch (opcionE) {
			case 0:
				consultarAgenda();
				break;
			case 1:
				registrarTurno();
				break;
			case 2:
				confirmarTurnos();
				break;
			case 3:
				modificarTurno();
				break;
			case 4:
				cancelarTurno();
				break;
			case 5:
				registrarGuarderia();
				break;
			case 6:
				modificarMascota();
				break;
			case 7:
				enviarNotificacion();
				break;
			default:
				break;
			}

		} while (opcionE != 8 && opcionE != -1);
	}

	private void consultarAgenda() {
		LinkedList<Turno> agenda = controllerTurno.mostrarAgenda();

		if (agenda.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay turnos agendados");
			return;
		}

		String texto = "Agenda (" + agenda.size() + " turnos):\n\n";
		for (Turno t : agenda) {
			texto += t + "\n";
		}
		JOptionPane.showMessageDialog(null, texto);
	}

	// polimorfismo: de todos los usuarios me quedo con los que son Veterinario
	private Veterinario elegirVeterinario() {
		LinkedList<Veterinario> veterinarios = new LinkedList<Veterinario>();
		for (Usuario u : controllerUsuario.mostrarUsuarios()) {
			if (u instanceof Veterinario) {
				veterinarios.add((Veterinario) u);
			}
		}
		return Validaciones.elegirDeLista("Seleccione el veterinario/a:", "Veterinarios", veterinarios);
	}

	// cuando el empleado registra el turno ya queda confirmado
	private void registrarTurno() {
		Mascota mascota = Validaciones.elegirDeLista("Seleccione la mascota:", "Mascotas",
				controllerMascota.mostrarMascotas());
		if (mascota == null) {
			return;
		}

		String tipo = (String) JOptionPane.showInputDialog(null, "Seleccione el servicio:", "Servicios",
				JOptionPane.QUESTION_MESSAGE, null, Turno.TIPOS, Turno.TIPOS[0]);
		if (tipo == null) {
			return;
		}

		Date fecha = Validaciones.validarFecha("Ingrese el día del turno");
		Time hora = Validaciones.validarHora("Ingrese la hora del turno");
		String observaciones = Validaciones.validarTextoOpcional("Observaciones");

		try {
			Turno turno = new Turno(fecha, hora, tipo, observaciones, mascota);
			Veterinario vet = null;
			// la guarderia no necesita veterinario
			if (!tipo.equals("guarderia")) {
				vet = elegirVeterinario();
				if (vet == null) {
					return;
				}
			}
			turno.confirmar(vet == null ? 0 : vet.getId(), vet == null ? null : vet.getNombre() + " " + vet.getApellido());

			if (controllerTurno.agregar(turno) > 0) {
				Notificacion.enviarPorTurno(turno, "Turno confirmado",
						"Tenés un turno de " + turno.getTipoTexto() + " para " + mascota.getNombreMascota() + " el "
								+ turno.getFecha() + " a las " + turno.getHoraTexto() + ".");
				JOptionPane.showMessageDialog(null, "Turno registrado correctamente!\n" + turno);
			}
		} catch (DatoInvalidoException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	// los turnos que piden los dueños llegan pendientes y aca se les asigna veterinario
	private void confirmarTurnos() {
		Turno turno = Validaciones.elegirDeLista("Turnos pendientes de confirmar:", "Confirmar turno",
				controllerTurno.mostrarTurnosPorEstado(Turno.PENDIENTE));
		if (turno == null) {
			return;
		}

		String obs = turno.getObservaciones() == null ? "" : "\nObservaciones: " + turno.getObservaciones();
		String[] opciones = { "Confirmar", "Rechazar", "Volver" };
		int opcion = JOptionPane.showOptionDialog(null, turno + obs, "Confirmar turno", 0, 0, null, opciones,
				opciones[0]);

		try {
			if (opcion == 0) {
				Veterinario vet = null;
				if (!turno.getTipo().equals("guarderia")) {
					vet = elegirVeterinario();
					if (vet == null) {
						return;
					}
				}
				turno.confirmar(vet == null ? 0 : vet.getId(), vet == null ? null : vet.getNombre() + " " + vet.getApellido());
				controllerTurno.actualizarTurno(turno);
				Notificacion.enviarPorTurno(turno, "Turno confirmado",
						"Tu turno de " + turno.getTipoTexto() + " para " + turno.getMascota().getNombreMascota()
								+ " del " + turno.getFecha() + " a las " + turno.getHoraTexto() + " está confirmado.");
				JOptionPane.showMessageDialog(null, "Turno confirmado.");

			} else if (opcion == 1) {
				turno.cancelar();
				controllerTurno.actualizarTurno(turno);
				Notificacion.enviarPorTurno(turno, "Turno rechazado", "No tenemos disponibilidad para el turno del "
						+ turno.getFecha() + " a las " + turno.getHoraTexto() + ". Por favor pedí otro horario.");
				JOptionPane.showMessageDialog(null, "Turno rechazado. Se le avisó al dueño/a.");
			}
		} catch (DatoInvalidoException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void modificarTurno() {
		Turno turno = Validaciones.elegirDeLista("Seleccione el turno a modificar:", "Modificar turno",
				controllerTurno.mostrarAgenda());
		if (turno == null) {
			return;
		}

		Date nuevaFecha = Validaciones.validarFecha("Ingrese el nuevo día");
		Time nuevaHora = Validaciones.validarHora("Ingrese la nueva hora");

		try {
			turno.reprogramar(nuevaFecha, nuevaHora);
			controllerTurno.actualizarTurno(turno);
			Notificacion.enviarPorTurno(turno, "Turno reprogramado", "Tu turno de " + turno.getTipoTexto() + " para "
					+ turno.getMascota().getNombreMascota() + " pasó al " + nuevaFecha + " a las " + turno.getHoraTexto() + ".");
			JOptionPane.showMessageDialog(null, "Turno modificado correctamente!");
		} catch (DatoInvalidoException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void cancelarTurno() {
		Turno turno = Validaciones.elegirDeLista("Seleccione el turno a cancelar:", "Cancelar turno",
				controllerTurno.mostrarAgenda());
		if (turno == null) {
			return;
		}

		int confirmar = JOptionPane.showConfirmDialog(null,
				"¿Seguro que desea cancelar el turno de " + turno.getMascota().getNombreMascota() + "?", "Cancelar turno",
				JOptionPane.YES_NO_OPTION);

		if (confirmar == JOptionPane.YES_OPTION) {
			try {
				turno.cancelar();
				controllerTurno.actualizarTurno(turno);
				Notificacion.enviarPorTurno(turno, "Turno cancelado", "Se canceló el turno de " + turno.getTipoTexto()
						+ " del " + turno.getFecha() + " a las " + turno.getHoraTexto() + ".");
				JOptionPane.showMessageDialog(null, "Turno cancelado correctamente!");
			} catch (DatoInvalidoException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	// ingreso de una mascota a la guarderia: sale de un turno de guarderia confirmado
	private void registrarGuarderia() {
		LinkedList<Turno> turnosGuarderia = new LinkedList<Turno>();
		for (Turno t : controllerTurno.mostrarTurnosPorEstado(Turno.CONFIRMADA)) {
			if (t.getTipo().equals("guarderia")) {
				turnosGuarderia.add(t);
			}
		}
		if (turnosGuarderia.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay turnos de guardería confirmados");
			return;
		}

		Turno turno = Validaciones.elegirDeLista("Seleccione el turno de guardería:", "Guardería", turnosGuarderia);
		if (turno == null) {
			return;
		}

		Date salida = Validaciones.validarFecha("Ingrese la fecha de salida");
		String alimentacion = Validaciones.validarString("Ingrese la alimentación:");
		String medicacion = Validaciones.validarTextoOpcional("Ingrese la medicación");
		double precio = Validaciones.validarDouble("Ingrese el precio total:");

		try {
			Guarderia guarderia = new Guarderia("Estadía en guardería", precio, turno.getFecha(), salida, alimentacion,
					medicacion, turno.getObservaciones(), this.getId(), turno.getMascota());

			int idGuarderia = controllerGuarderia.agregar(guarderia);
			if (idGuarderia > 0) {
				turno.marcarRealizado();
				controllerTurno.actualizarTurno(turno);
				Historial.registrarServicio(guarderia, this, turno.getMascota());
				Notificacion.enviar(turno.getMascota().getDuenio().getId(), "Ingreso a guardería",
						turno.getMascota().getNombreMascota() + " quedó en la guardería. " + guarderia.resumen(),
						"guarderia", turno.getId(), idGuarderia);
				JOptionPane.showMessageDialog(null,
						"Guardería registrada: " + guarderia.getDias() + " días, total $" + guarderia.getPrecio());
			}
		} catch (IllegalArgumentException | DatoInvalidoException e) {
			// multicatch: el constructor valida las fechas y el turno valida el estado
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void modificarMascota() {
		Mascota mascota = Validaciones.elegirDeLista("Seleccione la mascota:", "Mascotas",
				controllerMascota.mostrarMascotas());
		if (mascota == null) {
			return;
		}

		String[] campos = { "Peso", "Observaciones", "Raza", "Volver" };
		int campo = JOptionPane.showOptionDialog(null, mascota.ficha() + "\n\n¿Qué dato quiere modificar?",
				"Modificar mascota", 0, 0, null, campos, campos[0]);

		switch (campo) {
		case 0:
			mascota.setPeso(Validaciones.validarDouble("Ingrese el nuevo peso en kg:"));
			break;
		case 1:
			mascota.setObservaciones(Validaciones.validarTextoOpcional("Ingrese las observaciones"));
			break;
		case 2:
			mascota.setRaza(Validaciones.validarTextoSinNumeros("Ingrese la raza:"));
			break;
		default:
			return;
		}

		if (controllerMascota.editarMascota(mascota)) {
			JOptionPane.showMessageDialog(null, "Datos actualizados!\n\n" + mascota.ficha());
		}
	}

	private void enviarNotificacion() {
		LinkedList<Usuario> duenios = controllerUsuario.mostrarDuenios();
		Usuario duenio = Validaciones.elegirDeLista("Seleccione el dueño/a:", "Enviar notificación", duenios);
		if (duenio == null) {
			return;
		}

		String titulo = Validaciones.validarString("Título:");
		if (titulo.length() > 45) {
			titulo = titulo.substring(0, 45);
		}
		String mensaje = Validaciones.validarString("Mensaje:");

		Notificacion.enviar(duenio.getId(), titulo, mensaje, "general", 0, 0);
		JOptionPane.showMessageDialog(null, "Notificación enviada a " + duenio.getNombre());
	}
}
