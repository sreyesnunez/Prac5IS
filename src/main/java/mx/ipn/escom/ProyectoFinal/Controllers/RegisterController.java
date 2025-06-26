package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, Model model) {
        if (userService.registerUser(nombre, email, password)) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El correo ya está registrado.");
            return "register";
        }
    }
}
