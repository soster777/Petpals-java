package GUI;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import BLL.Duenio;
import BLL.Empleado;
import BLL.Usuario;
import BLL.Veterinario;

public class Main {

	public static void main(String[] args) {
		String[] acciones = { "Login", "Registrar", "Salir" };
        int menu = 0;
        
        do {
        	
        	menu = JOptionPane.showOptionDialog(null, "", null, 0, JOptionPane.DEFAULT_OPTION,
        			new ImageIcon(Main.class.getResource("/img/pp.png")), acciones, acciones[0]);
            
            switch (menu) {
            
                case 0:              
                    Usuario usuario = Usuario.iniciarSesion();
                    if (usuario != null) {
                        if (usuario instanceof Duenio) {
                            JOptionPane.showMessageDialog(null, "Bienvenido: " + usuario.getNombre());
                            usuario.menu();
                        } else if (usuario instanceof Veterinario) {
                            JOptionPane.showMessageDialog(null, "Bienvenido: " + usuario.getNombre());
                            usuario.menu();
                        } else if (usuario instanceof Empleado) {
                            JOptionPane.showMessageDialog(null, "Bienvenido: " + usuario.getNombre());
                            usuario.menu();
                        }
                    }
                    break;

                case 1: 
                	Usuario.registrarse();
                    break;
            }
            
        } while (menu != 2 && menu != -1);
    }

}
