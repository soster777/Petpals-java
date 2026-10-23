package repository;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.swing.JOptionPane;

public interface Validaciones {

	static boolean contieneNumeros(String str) {
		if (str == null) return false;
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	// chequeo simple: tiene que tener @ y un punto despues
	static boolean esMailValido(String mail) {
		if (mail == null) return false;
		int arroba = mail.indexOf("@");
		return arroba > 0 && mail.indexOf(".", arroba) > arroba + 1 && !mail.endsWith(".");
	}

	static boolean esTelefonoValido(String tel, int largoMinimo) {
		if (tel == null || tel.length() < largoMinimo || tel.length() > 15) return false;
		for (char c : tel.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	// la veterinaria atiende de 8 a 20
	static boolean esHoraValida(String hora) {
		if (hora == null || !hora.matches("\\d{1,2}:\\d{2}")) return false;
		String[] partes = hora.split(":");
		int h = Integer.parseInt(partes[0]);
		int m = Integer.parseInt(partes[1]);
		return h >= 8 && h < 20 && m >= 0 && m < 60;
	}

	static String validarString(String mensaje) {
		String dato;
		do {
			dato = JOptionPane.showInputDialog(mensaje);
			if (dato == null) {
				dato = "";
			}
			dato = dato.trim();
			if (dato.isEmpty()) {
				JOptionPane.showMessageDialog(null, "El campo no puede estar vacío.");
			}
		} while (dato.isEmpty());

		return dato;
	}

	// para campos que se pueden dejar vacios (observaciones, medicacion, etc)
	static String validarTextoOpcional(String mensaje) {
		String dato = JOptionPane.showInputDialog(mensaje + " (opcional)");
		if (dato == null || dato.trim().isEmpty()) {
			return null;
		}
		return dato.trim();
	}

	static String validarTextoSinNumeros(String mensaje) {
		String dato;
		boolean tieneNumeros;
		do {
			dato = JOptionPane.showInputDialog(mensaje);
			if (dato == null) {
				dato = "";
			}
			dato = dato.trim();
			tieneNumeros = contieneNumeros(dato);
			if (dato.isEmpty()) {
				JOptionPane.showMessageDialog(null, "El campo no puede estar vacío.");
			} else if (tieneNumeros) {
				JOptionPane.showMessageDialog(null, "El campo no puede contener números.");
			}
		} while (dato.isEmpty() || tieneNumeros);

		return dato;
	}

	static int validarInt(String mensaje) {
		String dato;
		boolean esValido;
		do {
			dato = JOptionPane.showInputDialog(mensaje);
			if (dato == null) {
				dato = "";
			}
			dato = dato.trim();
			esValido = true;
			if (dato.isEmpty()) {
				JOptionPane.showMessageDialog(null, "El campo no puede estar vacío.");
				esValido = false;
			} else {
				try {
					Integer.parseInt(dato);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Debe ingresar un número entero válido.");
					esValido = false;
				}
			}
		} while (!esValido);

		return Integer.parseInt(dato);
	}

	// acepta decimales con punto o coma (ej: 3.5 o 3,5)
	static double validarDouble(String mensaje) {
		String dato;
		double numero = 0;
		boolean esValido;
		do {
			dato = JOptionPane.showInputDialog(mensaje);
			if (dato == null) {
				dato = "";
			}
			dato = dato.trim().replace(",", ".");
			esValido = true;
			try {
				numero = Double.parseDouble(dato);
				if (numero <= 0) {
					JOptionPane.showMessageDialog(null, "El número tiene que ser mayor a 0.");
					esValido = false;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Debe ingresar un número válido (ejemplo: 3.5).");
				esValido = false;
			}
		} while (!esValido);

		return numero;
	}

	// el telefono se guarda como texto, asi entran numeros de cualquier largo
	static String validarTelefono(String mensaje, int largoMinimo) {
		String dato;
		boolean esValido;
		do {
			dato = JOptionPane.showInputDialog(mensaje);
			if (dato == null) {
				dato = "";
			}
			dato = dato.trim().replace(" ", "").replace("-", "");
			esValido = esTelefonoValido(dato, largoMinimo);
			if (!esValido) {
				JOptionPane.showMessageDialog(null, "El teléfono debe tener solo números, entre " + largoMinimo + " y 15 dígitos.");
			}
		} while (!esValido);

		return dato;
	}

	static String validarMail(String mensaje) {
		String mail;
		boolean esValido;
		do {
			mail = validarString(mensaje);
			esValido = esMailValido(mail);
			if (!esValido) {
				JOptionPane.showMessageDialog(null, "Ingrese un mail válido (ejemplo: nombre@mail.com).");
			}
		} while (!esValido);

		return mail;
	}

	static Date validarFecha(String mensaje) {
		String fecha;
		boolean esValido;
		do {
			fecha = JOptionPane.showInputDialog(mensaje + " (formato AAAA-MM-DD)");
			if (fecha == null) {
				fecha = "";
			}
			fecha = fecha.trim();
			esValido = true;
			if (fecha.isEmpty()) {
				JOptionPane.showMessageDialog(null, "La fecha no puede estar vacía.");
				esValido = false;
			} else {
				try {
					Date.valueOf(fecha);
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(null, "Formato de fecha inválido. Por favor, use AAAA-MM-DD.");
					esValido = false;
				}
			}
		} while (!esValido);

		return Date.valueOf(fecha);
	}

	static Time validarHora(String mensaje) {
		String hora;
		boolean esValido;
		do {
			hora = JOptionPane.showInputDialog(mensaje + " (formato HH:MM, de 08:00 a 19:59)");
			if (hora == null) {
				hora = "";
			}
			hora = hora.trim();
			esValido = esHoraValida(hora);
			if (!esValido) {
				JOptionPane.showMessageDialog(null, "Hora inválida. Use HH:MM entre las 08:00 y las 19:59.");
			}
		} while (!esValido);

		return Time.valueOf(hora + ":00");
	}

	// metodo generico: sirve para elegir de cualquier lista (mascotas, turnos, planes...)
	// muestra el toString() de cada elemento y devuelve el elegido, o null si cancela
	static <T> T elegirDeLista(String mensaje, String titulo, List<T> lista) {
		if (lista == null || lista.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No hay datos para mostrar.");
			return null;
		}
		Object[] opciones = lista.toArray();
		Object elegido = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE, null,
				opciones, opciones[0]);
		if (elegido == null) {
			return null;
		}
		return lista.get(lista.indexOf(elegido));
	}
}
