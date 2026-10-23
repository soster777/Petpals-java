package BLL;

public class Consulta extends Servicio {

	private String motivo;
	private String diagnostico;
	private String tratamiento;
	private String observaciones;
	private int idTurno;

	public Consulta(int id, String descripcion, double precio, String motivo, String diagnostico, String tratamiento,
			String observaciones, int idTurno) {
		super(id, descripcion, precio);
		this.motivo = motivo;
		this.diagnostico = diagnostico;
		this.tratamiento = tratamiento;
		this.observaciones = observaciones;
		this.idTurno = idTurno;
	}

	public Consulta(String descripcion, double precio, String motivo, String diagnostico, String tratamiento,
			String observaciones, int idTurno) {
		this(0, descripcion, precio, motivo, diagnostico, tratamiento, observaciones, idTurno);
	}

	@Override
	public String getTipoHistorial() {
		return "Consulta";
	}

	@Override
	public String resumen() {
		String texto = "Motivo: " + motivo + ". Diagnóstico: " + diagnostico;
		if (tratamiento != null) {
			texto += ". Tratamiento: " + tratamiento;
		}
		return texto;
	}

	public String getMotivo() {
		return motivo;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public String getTratamiento() {
		return tratamiento;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public int getIdTurno() {
		return idTurno;
	}
}
