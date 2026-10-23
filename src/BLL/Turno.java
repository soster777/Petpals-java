package BLL;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import excepciones.DatoInvalidoException;

public class Turno {

	// estados posibles de un turno (los mismos que el ENUM de la base)
	public static final String PENDIENTE = "pendiente";
	public static final String CONFIRMADA = "confirmada";
	public static final String CANCELADA = "cancelada";
	public static final String REALIZADA = "realizada";

	public static final String[] TIPOS = { "consulta", "vacunacion", "guarderia" };

	private int id;
	private Date fecha;
	private Time hora;
	private String estado;
	private String tipo;
	private String observaciones;
	private int idVeterinario; // 0 = todavia sin asignar
	private String nombreVeterinario;
	private Mascota mascota;

	public Turno(int id, Date fecha, Time hora, String estado, String tipo, String observaciones, int idVeterinario,
			String nombreVeterinario, Mascota mascota) {
		this.id = id;
		this.fecha = fecha;
		this.hora = hora;
		this.estado = estado;
		this.tipo = tipo;
		this.observaciones = observaciones;
		this.idVeterinario = idVeterinario;
		this.nombreVeterinario = nombreVeterinario;
		this.mascota = mascota;
	}

	// turno nuevo: arranca pendiente y sin veterinario
	public Turno(Date fecha, Time hora, String tipo, String observaciones, Mascota mascota) throws DatoInvalidoException {
		validarFecha(fecha);
		validarTipo(tipo);
		this.fecha = fecha;
		this.hora = hora;
		this.tipo = tipo;
		this.observaciones = observaciones;
		this.mascota = mascota;
		this.estado = PENDIENTE;
	}

	// no se puede sacar un turno para un dia que ya paso
	public static void validarFecha(Date fecha) throws DatoInvalidoException {
		if (fecha == null) {
			throw new DatoInvalidoException("La fecha del turno es obligatoria.");
		}
		if (fecha.toLocalDate().isBefore(LocalDate.now())) {
			throw new DatoInvalidoException("No se puede dar un turno para una fecha que ya pasó.");
		}
	}

	public static void validarTipo(String tipo) throws DatoInvalidoException {
		for (String t : TIPOS) {
			if (t.equals(tipo)) {
				return;
			}
		}
		throw new DatoInvalidoException("Tipo de turno inválido: " + tipo);
	}

	// un turno se puede modificar o cancelar mientras no este cancelado ni realizado
	public boolean estaActivo() {
		return PENDIENTE.equals(estado) || CONFIRMADA.equals(estado);
	}

	public void reprogramar(Date nuevaFecha, Time nuevaHora) throws DatoInvalidoException {
		if (!estaActivo()) {
			throw new DatoInvalidoException("Solo se pueden modificar turnos pendientes o confirmados.");
		}
		validarFecha(nuevaFecha);
		this.fecha = nuevaFecha;
		this.hora = nuevaHora;
	}

	public void confirmar(int idVeterinario, String nombreVeterinario) throws DatoInvalidoException {
		if (!PENDIENTE.equals(estado)) {
			throw new DatoInvalidoException("Solo se pueden confirmar turnos pendientes.");
		}
		this.idVeterinario = idVeterinario;
		this.nombreVeterinario = nombreVeterinario;
		this.estado = CONFIRMADA;
	}

	public void cancelar() throws DatoInvalidoException {
		if (!estaActivo()) {
			throw new DatoInvalidoException("Este turno ya está " + estado + ".");
		}
		this.estado = CANCELADA;
	}

	public void marcarRealizado() throws DatoInvalidoException {
		if (!CONFIRMADA.equals(estado)) {
			throw new DatoInvalidoException("Solo se pueden atender turnos confirmados.");
		}
		this.estado = REALIZADA;
	}

	public String getTipoTexto() {
		switch (tipo) {
		case "vacunacion":
			return "Vacunación";
		case "guarderia":
			return "Guardería";
		default:
			return "Consulta";
		}
	}

	public String getHoraTexto() {
		return hora.toString().substring(0, 5);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public Time getHora() {
		return hora;
	}

	public String getEstado() {
		return estado;
	}

	public String getTipo() {
		return tipo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public int getIdVeterinario() {
		return idVeterinario;
	}

	public String getNombreVeterinario() {
		return nombreVeterinario;
	}

	public Mascota getMascota() {
		return mascota;
	}

	@Override
	public String toString() {
		String texto = fecha + " " + getHoraTexto() + " - " + getTipoTexto() + " - " + mascota.getNombreMascota();
		if (mascota.getDuenio() != null) {
			texto += " (" + mascota.getDuenio().getNombre() + " " + mascota.getDuenio().getApellido() + ")";
		}
		if (nombreVeterinario != null) {
			texto += " - Vet: " + nombreVeterinario;
		}
		return texto + " [" + estado + "]";
	}
}
