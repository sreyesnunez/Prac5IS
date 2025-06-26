package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.services.UserService;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
public class UsuarioController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UsuarioController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/usuario")
    public String usuarioPerfil(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            String tema = usuario.getPreferenciaUsuario() != null ? usuario.getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
            return "usuario";
        } else {
            return "redirect:/login?error=user_not_found";
        }
    }

    @GetMapping("/usuario/editar")
    public String mostrarFormularioEdicion(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
            return "perfil_editar";
        } else {
            return "redirect:/login?error=user_not_found";
        }
    }
    

    @PostMapping("/usuario/editar")
    public String procesarEdicion(@Valid @ModelAttribute("usuario") Usuario usuarioEditado,
                                  BindingResult result,
                                  @RequestParam(name = "nuevaPassword", required = false) String nuevaPassword,
                                  @AuthenticationPrincipal User user,
                                  Model model) {

        if (result.hasErrors()) {
            return "perfil_editar";
        }

        boolean actualizado = userService.actualizarUsuario(user.getUsername(), usuarioEditado, nuevaPassword);
        if (!actualizado) {
            model.addAttribute("error", "No se pudo actualizar el perfil.");
            return "perfil_editar";
        }

        return "redirect:/usuario?exito";
    }

    @PostMapping("/usuario/cambiar-tema")
    public String cambiarTemaUsuario(@RequestParam("tema") String tema, @AuthenticationPrincipal User user) {
        userService.actualizarTemaUsuario(user.getUsername(), tema);
        return "redirect:/usuario";
    }

}
