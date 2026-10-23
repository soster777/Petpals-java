package BLL;

import java.sql.Date;

public class Vacunacion extends Servicio {

	private String nombreVacuna;
	private String dosis;
	private Date fechaAplicacion;
	private Date proximaDosis; // puede ser null si no hace falta otra dosis
	private int idTurno;

	public Vacunacion(int id, String nombreVacuna, String descripcion, double precio, String dosis,
			Date fechaAplicacion, Date proximaDosis, int idTurno) {
		super(id, descripcion, precio);
		if (proximaDosis != null && !proximaDosis.after(fechaAplicacion)) {
			throw new IllegalArgumentException("La próxima dosis tiene que ser después de la fecha de aplicación.");
		}
		this.nombreVacuna = nombreVacuna;
		this.dosis = dosis;
		this.fechaAplicacion = fechaAplicacion;
		this.proximaDosis = proximaDosis;
		this.idTurno = idTurno;
	}

	public Vacunacion(String nombreVacuna, String descripcion, double precio, String dosis, Date fechaAplicacion,
			Date proximaDosis, int idTurno) {
		this(0, nombreVacuna, descripcion, precio, dosis, fechaAplicacion, proximaDosis, idTurno);
	}

	@Override
	public String getTipoHistorial() {
		return "Vacunación";
	}

	@Override
	public String resumen() {
		String texto = nombreVacuna + " (" + dosis + ")";
		if (proximaDosis != null) {
			texto += ". Próxima dosis: " + proximaDosis;
		}
		return texto;
	}

	public String getNombreVacuna() {
		return nombreVacuna;
	}

	public String getDosis() {
		return dosis;
	}

	public Date getFechaAplicacion() {
		return fechaAplicacion;
	}

	public Date getProximaDosis() {
		return proximaDosis;
	}

	public int getIdTurno() {
		return idTurno;
	}
}
