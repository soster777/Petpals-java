package BLL;

import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import DLL.ControllerMascota;
import DLL.ControllerSuscripcion;
import DLL.ControllerTurno;
import excepciones.DatoInvalidoException;
import repository.Validaciones;

public class Duenio extends Usuario implements Validaciones {

	private static ControllerMascota controllerMascota = new ControllerMascota();
	private static ControllerTurno controllerTurno = new ControllerTurno();
	private static ControllerSuscripcion controllerSuscripcion = new ControllerSuscripcion();

	public Duenio(int id, String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super(id, nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	public Duenio(String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super(nombre, apellido, dni, tel, mail, contrasenia, rol);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido + " (" + mail + ")";
	}

	@Override
	public void menu() {

		String[] opcD = { "Registrar mascota", "Solicitar turno", "Mis turnos", "Ver historial",
				"Notificaciones", "Planes", "Salir" };

		int opcionD;

		do {
			opcionD = JOptionPane.showOptionDialog(null,
					"Hola " + this.getNombre() + " dueño/a! Bienvenido/a a PetPals \n¿En que te podemos ayudar?:", "", 0, 0, null,
					opcD, opcD[0]);

			switch (opcionD) {
			case 0:
				registrarMascota();
				break;

			case 1:
				solicitarTurno();
				break;

			case 2:
				verMisTurnos();
				break;

			case 3:
				Mascota mascotaHistorial = buscarMascota();
				if (mascotaHistorial != null) {
					Historial.mostrarHistorialMascota(mascotaHistorial);
				}
				break;

			case 4:
				verNotificaciones();
				break;

			case 5:
				menuPlanes();
				break;

			default:
				break;
			}
		} while (opcionD != 6 && opcionD !=-1);
	}

	private void registrarMascota() {
		Mascota nueva = new Mascota(
				Validaciones.validarTextoSinNumeros("Ingrese el nombre de su mascota: "),
				Validaciones.validarTextoSinNumeros("Ingrese la especie: "),
				Validaciones.validarTextoSinNumeros("Ingrese la raza: "),
				Validaciones.validarFecha("Ingrese la fecha de nacimiento: "),
				elegirSexo(),
				Validaciones.validarDouble("Ingrese el peso en kg: "),
				Validaciones.validarTextoOpcional("Ingrese observaciones (alergias, cuidados, etc)"),
				this);

		if (controllerMascota.agregarMascota(nueva)) {
			JOptionPane.showMessageDialog(null, "Su mascota se registro correctamente!");
		}
	}

	public static String elegirSexo() {
		String[] sexos = { "Macho", "Hembra" };
		int elegido = JOptionPane.showOptionDialog(null, "Seleccione el sexo:", "Sexo", 0, 0, null, sexos, sexos[0]);
		return elegido == 1 ? "Hembra" : "Macho";
	}

	private void solicitarTurno() {
		Mascota mascota = buscarMascota();
		if (mascota == null) {
			return;
		}

		String[] servicios = { "Consulta", "Vacunación", "Guardería" };
		int servicioElegido = JOptionPane.showOptionDialog(null, "¿Qué servicio desea solicitar?", "Servicios", 0, 0,
				null, servicios, servicios[0]);
		if (servicioElegido == -1) {
			return;
		}

		Date fecha = Validaciones.validarFecha("Ingrese el día que quiere el turno");
		Time hora = Validaciones.validarHora("Ingrese la hora");
		String observaciones = Validaciones.validarTextoOpcional("Contanos el motivo o algo que debamos saber");

		try {
			Turno turno = new Turno(fecha, hora, Turno.TIPOS[servicioElegido], observaciones, mascota);
			if (controllerTurno.agregar(turno) > 0) {
				JOptionPane.showMessageDialog(null, "Turno solicitado!\n" + turno
						+ "\n\nQueda pendiente hasta que la veterinaria lo confirme. Te va a llegar una notificación.");
			}
		} catch (DatoInvalidoException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private void verMisTurnos() {
		LinkedList<Turno> turnos = controllerTurno.mostrarTurnosPorDuenio(this.getId());

		if (turnos.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No tenés turnos registrados");
			return;
		}

		String texto = "Tus turnos:\n\n";
		LinkedList<Turno> activos = new LinkedList<Turno>();
		for (Turno t : turnos) {
			texto += t + "\n";
			if (t.estaActivo()) {
				activos.add(t);
			}
		}

		if (activos.isEmpty()) {
			JOptionPane.showMessageDialog(null, texto);
			return;
		}

		String[] opciones = { "Cancelar un turno", "Volver" };
		int opcion = JOptionPane.showOptionDialog(null, texto, "Mis turnos", 0, 0, null, opciones, opciones[1]);

		if (opcion == 0) {
			Turno turno = Validaciones.elegirDeLista("¿Qué turno querés cancelar?", "Cancelar turno", activos);
			if (turno != null) {
				try {
					turno.cancelar();
					controllerTurno.actualizarTurno(turno);
					JOptionPane.showMessageDialog(null, "Turno cancelado.");
				} catch (DatoInvalidoException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		}
	}

	private void verNotificaciones() {
		LinkedList<Notificacion> notificaciones = Notificacion.mostrarPorDuenio(this.getId());

		if (notificaciones.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No tenés notificaciones");
			return;
		}

		// muestro las ultimas 10 para que no quede un mensaje gigante
		String texto = "Tus notificaciones:\n\n";
		int cantidad = Math.min(10, notificaciones.size());
		for (int i = 0; i < cantidad; i++) {
			texto += notificaciones.get(i) + "\n\n";
		}
		JOptionPane.showMessageDialog(null, texto);
	}

	private void menuPlanes() {
		Suscripcion actual = controllerSuscripcion.buscarVigente(this.getId());

		if (actual != null) {
			String[] opciones = { "Cancelar suscripción", "Volver" };
			int opcion = JOptionPane.showOptionDialog(null,
					"Tenés el " + actual + "\n" + actual.getPlan().getDescripcion(), "Mi plan", 0, 0, null, opciones,
					opciones[1]);
			if (opcion == 0 && controllerSuscripcion.cancelar(actual)) {
				JOptionPane.showMessageDialog(null, "Suscripción cancelada.");
			}
			return;
		}

		Plan plan = Validaciones.elegirDeLista("No tenés ningún plan. Elegí uno para suscribirte:", "Planes",
				controllerSuscripcion.mostrarPlanes());

		if (plan != null) {
			Suscripcion nueva = new Suscripcion(this.getId(), plan);
			if (controllerSuscripcion.agregar(nueva) > 0) {
				Notificacion.enviar(this.getId(), "Bienvenido/a al plan " + plan.getTipo().toUpperCase(),
						"Tu suscripción está activa hasta el " + nueva.getFechaFin() + ".", "suscripcion", 0, 0);
				JOptionPane.showMessageDialog(null, "Te suscribiste al plan " + plan.getTipo().toUpperCase() + "!");
			}
		}
	}

	public Mascota buscarMascota() {
		LinkedList<Mascota> mascotas = controllerMascota.mostrarMascotasPorDuenio(this);

		if (mascotas.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay mascotas registradas");
			return null;
		}

		String[] nombres = new String[mascotas.size()];

		for (int i = 0; i < nombres.length; i++) {
			nombres[i] = mascotas.get(i).getNombreMascota();
		}

		int elegido = JOptionPane.showOptionDialog(null, "Seleccione una mascota: ", "Mascotas", 0, 0, null, nombres, nombres[0]);

		if (elegido == -1) {
			return null;
		}

		return mascotas.get(elegido);
	}
}
