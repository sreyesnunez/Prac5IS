package mx.ipn.escom.ProyectoFinal.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Carga login.html
    }

    @GetMapping("/redirect")
    public String redirectUser(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin";
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                return "redirect:/usuario";
            }
        }
        return "redirect:/login?error=true";
    }
}

