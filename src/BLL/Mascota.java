package BLL;

import java.sql.Date;

import repository.Validaciones;

public class Mascota implements Validaciones{
    protected int id;
    protected String nombreMascota;
    protected String especie;
    protected String raza;
    protected Date fechanac;
    protected String sexo;
    protected double peso;
    protected String observaciones;
    protected Duenio duenio;

	public Mascota(int id, String nombreMascota, String especie, String raza, Date fechanac, String sexo, double peso,
			String observaciones, Duenio duenio) {
		super();
		this.id = id;
		this.nombreMascota = nombreMascota;
		this.especie = especie;
		this.raza = raza;
		this.fechanac = fechanac;
		this.sexo = sexo;
		this.peso = peso;
		this.observaciones = observaciones;
		this.duenio = duenio;
	}

	public Mascota(String nombreMascota, String especie, String raza, Date fechanac, String sexo, double peso,
			String observaciones, Duenio duenio) {
		this(0, nombreMascota, especie, raza, fechanac, sexo, peso, observaciones, duenio);
	}

	public Mascota() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombreMascota() {
		return nombreMascota;
	}

	public void setNombreMascota(String nombreMascota) {
		if (nombreMascota == null || nombreMascota.trim().isEmpty() || Validaciones.contieneNumeros(nombreMascota)) {
			throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío ni contener números.");
		}
		this.nombreMascota = nombreMascota;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		if (especie == null || especie.trim().isEmpty() || Validaciones.contieneNumeros(especie)) {
			throw new IllegalArgumentException("La especie no puede estar vacía ni contener números.");
		}
		this.especie = especie;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		if (raza == null || raza.trim().isEmpty() || Validaciones.contieneNumeros(raza)) {
			throw new IllegalArgumentException("La raza no puede estar vacía ni contener números.");
		}
		this.raza = raza;
	}

	public Date getFechanac() {
		return fechanac;
	}

	public void setFechanac(Date fechanac) {
		this.fechanac = fechanac;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public double getPeso() {
		return peso;
	}

	// el peso ahora es decimal (un gato de 3.5 kg antes no se podia cargar)
	public void setPeso(double peso) {
		if (peso <= 0) {
			throw new IllegalArgumentException("El peso tiene que ser mayor a 0.");
		}
		this.peso = peso;
	}

	public String getObservaciones() {
		return observaciones;
	}

	// las observaciones son opcionales y pueden tener numeros ("vacuna 2 dosis")
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Duenio getDuenio() {
		return duenio;
	}

	public void setDuenio(Duenio duenio) {
		this.duenio = duenio;
	}

	public String ficha() {
		return "Nombre: " + nombreMascota + "\nEspecie: " + especie + "\nRaza: " + raza + "\nNacimiento: " + fechanac
				+ "\nSexo: " + sexo + "\nPeso: " + peso + " kg\nObservaciones: "
				+ (observaciones == null ? "-" : observaciones);
	}

	// lo uso para mostrarla en las listas para elegir
	@Override
	public String toString() {
		String texto = nombreMascota + " (" + especie + ")";
		if (duenio != null && duenio.getNombre() != null) {
			texto += " - dueño/a: " + duenio.getNombre() + " " + duenio.getApellido();
		}
		return texto;
	}
}
