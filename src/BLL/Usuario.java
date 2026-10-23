package BLL;

import javax.swing.JOptionPane;

import DLL.ControllerUsuario;
import repository.Hashing;
import repository.Validaciones;

public abstract class Usuario implements Validaciones{
    protected int id;
    protected String nombre;
    protected String apellido;
    protected int dni;
    protected String tel;
    protected String mail;
    protected String contrasenia;
    protected Rol rol;
    private static ControllerUsuario controller = new ControllerUsuario();
    
	public Usuario(int id, String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.tel = tel;
		this.mail = mail;
		this.contrasenia = contrasenia;
		this.rol = rol;
	}
    
	public Usuario(String nombre, String apellido, int dni, String tel, String mail, String contrasenia, Rol rol) {
		super();
		this.id = 0;
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.tel = tel;
		this.mail = mail;
		this.contrasenia = contrasenia;
		this.rol = rol;
	}

	public Usuario() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		if (nombre == null || nombre.trim().isEmpty() || Validaciones.contieneNumeros(nombre)) {
			throw new IllegalArgumentException("El nombre no puede estar vacío ni contener números.");
		}
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		if (apellido == null || apellido.trim().isEmpty() || Validaciones.contieneNumeros(apellido)) {
			throw new IllegalArgumentException("El apellido no puede estar vacío ni contener números.");
		}
		this.apellido = apellido;
	}

	public int getDni() {
		return dni;
	}

	public void setDni(int dni) {
		this.dni = dni;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		if (!Validaciones.esTelefonoValido(tel, 7)) {
			throw new IllegalArgumentException("El teléfono debe tener solo números y al menos 7 dígitos.");
		}
		this.tel = tel;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public static ControllerUsuario getController() {
		return controller;
	}

	public static void setController(ControllerUsuario controller) {
		Usuario.controller = controller;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", dni=" + dni + ", tel=" + tel
				+ ", mail=" + mail + ", rol=" + rol + "]";
	}
	
	public static Usuario iniciarSesion() {
		String mail = JOptionPane.showInputDialog("Ingrese mail");
		// si toca cancelar vuelve al menu en vez de tirar error
		if (mail == null || mail.trim().isEmpty()) {
			return null;
		}

		String contrasenia = JOptionPane.showInputDialog("Ingrese contraseña");
		if (contrasenia == null || contrasenia.isEmpty()) {
			return null;
		}

		return controller.login(mail.trim(), contrasenia);
	}
	
	public abstract void menu();
	
	public static void registrarse() {
	
		String nombre = Validaciones.validarTextoSinNumeros("Ingrese su nombre: ");
		String apellido = Validaciones.validarTextoSinNumeros("Ingrese su apellido: ");
		int dni = Validaciones.validarInt("Ingrese su DNI: ");
		String tel = Validaciones.validarTelefono("Ingrese su teléfono: ", 7);
		String mail = Validaciones.validarMail("Ingrese su mail: ");
		String contrasenia = Validaciones.validarString("Ingrese su contrasenia: ");
		String contraseniaOculta = Hashing.hash(contrasenia);
		
		getController().agregarUsuario(new Duenio(nombre,apellido,dni,tel,mail,contraseniaOculta,Rol.DUENIO));
			
	}
	
}
