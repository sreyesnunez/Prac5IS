package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class SimuladorController {

    @Autowired
    private LocalidadService localidadService;

    @Autowired
    private UserRepository userRepository;

    // Vista para usuario
    @GetMapping("/simulador")
    public String mostrarSimuladorUsuario(@AuthenticationPrincipal User user, Model model) {
        List<Localidad> localidades = localidadService.obtenerTodas();
        model.addAttribute("localidades", localidades);

        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = Optional.ofNullable(usuarioOptional.get().getPreferenciaUsuario())
                    .map(pref -> pref.getTema())
                    .orElse("claro");
            model.addAttribute("tema", tema);
        }

        return "simulador";
    }

    // Vista para admin
    @GetMapping("/admin/simulador")
    public String mostrarSimuladorAdmin(@AuthenticationPrincipal User user, Model model) {
        List<Localidad> localidades = localidadService.obtenerTodas();
        model.addAttribute("localidades", localidades);

        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = Optional.ofNullable(usuarioOptional.get().getPreferenciaUsuario())
                    .map(pref -> pref.getTema())
                    .orElse("claro");
            model.addAttribute("tema", tema);
        }

        return "simulador";
    }
}
