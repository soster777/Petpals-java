package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import BLL.Consulta;
import BLL.Guarderia;
import BLL.Plan;
import BLL.Servicio;
import BLL.Suscripcion;
import BLL.Vacunacion;

class ServicioTest {

	@Test
	void cadaServicioSabeSuTipoDeHistorial() {
		// polimorfismo: los guardo como Servicio y cada uno responde distinto
		Servicio consulta = new Consulta("Consulta", 8000, "Vomitos", "Gastritis", "Dieta blanda", null, 1);
		Servicio vacuna = new Vacunacion("Antirrabica", "Vacuna", 5000, "Refuerzo anual", Date.valueOf("2026-10-01"), null, 2);

		assertEquals("Consulta", consulta.getTipoHistorial());
		assertEquals("Vacunación", vacuna.getTipoHistorial());
		assertTrue(consulta.resumen().contains("Gastritis"));
	}

	@Test
	void laGuarderiaCuentaLosDias() {
		Guarderia g = new Guarderia("Estadia", 30000, Date.valueOf("2026-11-01"), Date.valueOf("2026-11-04"),
				"Balanceado", null, null, 3, null);
		assertEquals(3, g.getDias());

		Guarderia mismoDia = new Guarderia("Estadia", 10000, Date.valueOf("2026-11-01"), Date.valueOf("2026-11-01"),
				"Balanceado", null, null, 3, null);
		assertEquals(1, mismoDia.getDias());
	}

	@Test
	void laSalidaNoPuedeSerAntesDelIngreso() {
		assertThrows(IllegalArgumentException.class, () -> new Guarderia("Estadia", 10000, Date.valueOf("2026-11-05"),
				Date.valueOf("2026-11-01"), "Balanceado", null, null, 3, null));
	}

	@Test
	void laProximaDosisTieneQueSerDespues() {
		assertThrows(IllegalArgumentException.class, () -> new Vacunacion("Sextuple", "Vacuna", 5000, "1ra dosis",
				Date.valueOf("2026-10-01"), Date.valueOf("2026-09-01"), 1));
	}

	@Test
	void noHayPreciosNegativos() {
		assertThrows(IllegalArgumentException.class,
				() -> new Consulta("Consulta", -100, "Control", "Sano", null, null, 1));
	}

	@Test
	void unaSuscripcionNuevaDuraUnMes() {
		Plan premium = new Plan(1, "premium", 15000, "Descuentos");
		Suscripcion s = new Suscripcion(1, premium);

		assertTrue(s.estaVigente());
		assertEquals(LocalDate.now().plusMonths(1), s.getFechaFin().toLocalDate());

		s.setEstado(Suscripcion.CANCELADA);
		assertFalse(s.estaVigente());
	}
}
