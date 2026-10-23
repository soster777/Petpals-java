package tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import repository.Validaciones;

class ValidacionesTest {

	@Test
	void detectaNumerosEnUnTexto() {
		assertTrue(Validaciones.contieneNumeros("Toby2"));
		assertFalse(Validaciones.contieneNumeros("Toby"));
		assertFalse(Validaciones.contieneNumeros(null));
	}

	@Test
	void validaMails() {
		assertTrue(Validaciones.esMailValido("valen@gmail.com"));
		assertFalse(Validaciones.esMailValido("valengmail.com"));
		assertFalse(Validaciones.esMailValido("valen@gmail"));
		assertFalse(Validaciones.esMailValido("@gmail.com"));
	}

	@Test
	void validaTelefonos() {
		// given: un celular de La Plata, que con int no entraba
		String telefono = "2215557777";
		// when / then
		assertTrue(Validaciones.esTelefonoValido(telefono, 7));
		assertFalse(Validaciones.esTelefonoValido("12345", 7));
		assertFalse(Validaciones.esTelefonoValido("11-4420-2249", 7));
	}

	@Test
	void validaHorarioDeAtencion() {
		assertTrue(Validaciones.esHoraValida("08:00"));
		assertTrue(Validaciones.esHoraValida("19:30"));
		assertFalse(Validaciones.esHoraValida("20:00"));
		assertFalse(Validaciones.esHoraValida("7:59"));
		assertFalse(Validaciones.esHoraValida("10:75"));
		assertFalse(Validaciones.esHoraValida("diez"));
	}
}
