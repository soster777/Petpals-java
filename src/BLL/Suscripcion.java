package BLL;

import java.sql.Date;
import java.time.LocalDate;

public class Suscripcion {

	public static final String ACTIVA = "activa";
	public static final String CANCELADA = "cancelada";

	private int id;
	private Date fechaInicio;
	private Date fechaFin;
	private String estado;
	private int idDuenio;
	private Plan plan;

	public Suscripcion(int id, Date fechaInicio, Date fechaFin, String estado, int idDuenio, Plan plan) {
		this.id = id;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.estado = estado;
		this.idDuenio = idDuenio;
		this.plan = plan;
	}

	// suscripcion nueva: arranca hoy y dura un mes
	public Suscripcion(int idDuenio, Plan plan) {
		this(0, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusMonths(1)), ACTIVA, idDuenio, plan);
	}

	// esta vigente si no se cancelo y todavia no paso la fecha de fin
	public boolean estaVigente() {
		return ACTIVA.equals(estado) && !fechaFin.toLocalDate().isBefore(LocalDate.now());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getIdDuenio() {
		return idDuenio;
	}

	public Plan getPlan() {
		return plan;
	}

	@Override
	public String toString() {
		return "Plan " + plan.getTipo().toUpperCase() + " vigente hasta el " + fechaFin;
	}
}
