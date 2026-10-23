package BLL;

public class Plan {

	private int id;
	private String tipo; // premium o vip
	private double precio;
	private String descripcion;

	public Plan(int id, String tipo, double precio, String descripcion) {
		this.id = id;
		this.tipo = tipo;
		this.precio = precio;
		this.descripcion = descripcion;
	}

	public int getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public double getPrecio() {
		return precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	@Override
	public String toString() {
		return "Plan " + tipo.toUpperCase() + " - $" + precio + " por mes: " + descripcion;
	}
}
