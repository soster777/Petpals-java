package BLL;

// clase abstracta: Consulta, Vacunacion y Guarderia son servicios de la veterinaria
// todas tienen descripcion y precio, y cada una arma su propio resumen para el historial
public abstract class Servicio {

	protected int id;
	protected String descripcion;
	protected double precio;

	public Servicio(int id, String descripcion, double precio) {
		if (precio < 0) {
			throw new IllegalArgumentException("El precio no puede ser negativo.");
		}
		this.id = id;
		this.descripcion = descripcion;
		this.precio = precio;
	}

	// polimorfismo: cada servicio lo implementa a su manera
	public abstract String getTipoHistorial();

	public abstract String resumen();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	@Override
	public String toString() {
		return getTipoHistorial() + ": " + resumen() + " ($" + precio + ")";
	}
}
