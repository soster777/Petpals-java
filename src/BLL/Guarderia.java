package BLL;

import java.sql.Date;
import java.time.temporal.ChronoUnit;

public class Guarderia extends Servicio {

	private Date fechaIngreso;
	private Date fechaSalida;
	private String alimentacion;
	private String medicacion;
	private String observaciones;
	private int idEmpleado;
	private Mascota mascota;

	public Guarderia(int id, String descripcion, double precio, Date fechaIngreso, Date fechaSalida,
			String alimentacion, String medicacion, String observaciones, int idEmpleado, Mascota mascota) {
		super(id, descripcion, precio);
		if (fechaSalida.before(fechaIngreso)) {
			throw new IllegalArgumentException("La fecha de salida no puede ser anterior a la de ingreso.");
		}
		this.fechaIngreso = fechaIngreso;
		this.fechaSalida = fechaSalida;
		this.alimentacion = alimentacion;
		this.medicacion = medicacion;
		this.observaciones = observaciones;
		this.idEmpleado = idEmpleado;
		this.mascota = mascota;
	}

	public Guarderia(String descripcion, double precio, Date fechaIngreso, Date fechaSalida, String alimentacion,
			String medicacion, String observaciones, int idEmpleado, Mascota mascota) {
		this(0, descripcion, precio, fechaIngreso, fechaSalida, alimentacion, medicacion, observaciones, idEmpleado,
				mascota);
	}

	// cantidad de dias que se queda (si entra y sale el mismo dia cuenta 1)
	public long getDias() {
		long dias = ChronoUnit.DAYS.between(fechaIngreso.toLocalDate(), fechaSalida.toLocalDate());
		return Math.max(dias, 1);
	}

	@Override
	public String getTipoHistorial() {
		return "Guardería";
	}

	@Override
	public String resumen() {
		String texto = "Del " + fechaIngreso + " al " + fechaSalida + " (" + getDias() + " días). Alimentación: "
				+ alimentacion;
		if (medicacion != null) {
			texto += ". Medicación: " + medicacion;
		}
		return texto;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public Date getFechaSalida() {
		return fechaSalida;
	}

	public String getAlimentacion() {
		return alimentacion;
	}

	public String getMedicacion() {
		return medicacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public int getIdEmpleado() {
		return idEmpleado;
	}

	public Mascota getMascota() {
		return mascota;
	}
}
