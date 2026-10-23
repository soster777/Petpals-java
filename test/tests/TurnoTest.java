package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import BLL.Mascota;
import BLL.Turno;
import excepciones.DatoInvalidoException;

class TurnoTest {

	private Mascota mascota = new Mascota(1, "Toby", "Perro", "Caniche", Date.valueOf("2020-03-15"), "Macho", 8.5, null, null);
	private Date maniana = Date.valueOf(LocalDate.now().plusDays(1));
	private Time diezYMedia = Time.valueOf("10:30:00");

	@Test
	void unTurnoNuevoArrancaPendiente() throws DatoInvalidoException {
		Turno turno = new Turno(maniana, diezYMedia, "consulta", null, mascota);
		assertEquals(Turno.PENDIENTE, turno.getEstado());
		assertTrue(turno.estaActivo());
	}

	@Test
	void noSePuedeSacarTurnoParaUnaFechaPasada() {
		Date ayer = Date.valueOf(LocalDate.now().minusDays(1));
		assertThrows(DatoInvalidoException.class, () -> new Turno(ayer, diezYMedia, "consulta", null, mascota));
	}

	@Test
	void noAceptaTiposDeTurnoInventados() {
		assertThrows(DatoInvalidoException.class, () -> new Turno(maniana, diezYMedia, "peluqueria", null, mascota));
	}

	@Test
	void confirmarAsignaElVeterinario() throws DatoInvalidoException {
		Turno turno = new Turno(maniana, diezYMedia, "vacunacion", null, mascota);
		turno.confirmar(2, "Martin Perez");
		assertEquals(Turno.CONFIRMADA, turno.getEstado());
		assertEquals(2, turno.getIdVeterinario());
	}

	@Test
	void soloSeAtiendenTurnosConfirmados() throws DatoInvalidoException {
		Turno turno = new Turno(maniana, diezYMedia, "consulta", null, mascota);
		assertThrows(DatoInvalidoException.class, () -> turno.marcarRealizado());

		turno.confirmar(2, "Martin Perez");
		turno.marcarRealizado();
		assertEquals(Turno.REALIZADA, turno.getEstado());
		assertFalse(turno.estaActivo());
	}

	@Test
	void unTurnoCanceladoNoSePuedeReprogramar() throws DatoInvalidoException {
		Turno turno = new Turno(maniana, diezYMedia, "consulta", null, mascota);
		turno.cancelar();
		assertThrows(DatoInvalidoException.class, () -> turno.reprogramar(maniana, diezYMedia));
		assertThrows(DatoInvalidoException.class, () -> turno.cancelar());
	}
}
