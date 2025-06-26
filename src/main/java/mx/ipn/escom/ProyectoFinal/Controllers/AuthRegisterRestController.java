package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRegisterRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> apiRegister(@RequestBody Map<String, String> requestData) {
        String nombre = requestData.get("nombre");
        String email = requestData.get("email");
        String password = requestData.get("password");

        Map<String, String> response = new HashMap<>();

        if (userService.registerUser(nombre, email, password)) {
            response.put("message", "Usuario registrado con éxito");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "El correo ya está registrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
