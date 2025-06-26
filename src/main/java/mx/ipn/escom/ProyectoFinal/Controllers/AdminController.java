package mx.ipn.escom.ProyectoFinal.Controllers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import jakarta.validation.Valid;
import mx.ipn.escom.ProyectoFinal.models.Localidad;
import mx.ipn.escom.ProyectoFinal.models.Rol;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.RoleRepository;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.LocalidadService;
import mx.ipn.escom.ProyectoFinal.services.SismoService;
import mx.ipn.escom.ProyectoFinal.services.UserService;


@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SismoService sismoService;
    private final LocalidadService localidadService;



    public AdminController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, 
    UserService userService, SismoService sismoService, LocalidadService localidadService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.sismoService = sismoService;
        this.localidadService = localidadService;
    }

    

    @GetMapping("/admin")
    public String adminPerfil(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
    
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            String tema = usuario.getPreferenciaUsuario() != null ? usuario.getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        } else {
            return "redirect:/login?error=user_not_found";
        }
    
        return "admin"; 
    }
    

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }
        return "dashboard";
    }
    

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model, @AuthenticationPrincipal User user) {
        List<Usuario> usuarios = userRepository.findAll();
        model.addAttribute("usuarios", usuarios);
    
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }
        return "usuarios";
    }
    

    @CrossOrigin(origins = "*") // Permite llamadas desde cualquier origen (para Postman)
    @GetMapping("/api/usuarios")
    @ResponseBody
    public ResponseEntity<List<Usuario>> listarUsuariosApi() {
        List<Usuario> usuarios = userRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @CrossOrigin(origins = "*") // Permite llamadas desde cualquier origen (para Postman)
    @GetMapping("/api/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = userRepository.findById(id);
        
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Usuario no encontrado"));
        }
    }

    @CrossOrigin(origins = "*") // Permite llamadas desde cualquier origen (para Postman)
    @PostMapping("/api/usuarios")
    @ResponseBody
    public ResponseEntity<?> agregarUsuario(@RequestBody Map<String, String> datos) {
        String nombre = datos.get("nombre");
        String email = datos.get("email");
        String password = datos.get("password");
        String rolNombre = datos.get("rol");

        // Validar si ya existe un usuario con ese email
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "El correo ya está registrado."));
        }

        // Buscar el rol en la BD
        Rol rol = roleRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear usuario y asignar rol
        Usuario nuevoUsuario = new Usuario(nombre, email, passwordEncoder.encode(password));
        nuevoUsuario.setRoles(Collections.singleton(rol));
        userRepository.save(nuevoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "Usuario creado exitosamente"));
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/api/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        Optional<Usuario> usuarioOptional = userRepository.findById(id);
    
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Usuario no encontrado"));
        }
    
        Usuario usuario = usuarioOptional.get();
        usuario.setNombre(datos.get("nombre"));
        usuario.setEmail(datos.get("email"));
    
        // Verificar si se proporcionó un nuevo rol en la solicitud
        if (datos.containsKey("rol")) {
            Rol nuevoRol = roleRepository.findByNombre(datos.get("rol"))
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    
            // 🛠 Aquí creamos un nuevo conjunto mutable en lugar de modificar el existente
            Set<Rol> roles = new HashSet<>();
            roles.add(nuevoRol);
            usuario.setRoles(roles);
        }
    
        userRepository.save(usuario);
    
        return ResponseEntity.ok(Collections.singletonMap("message", "Usuario actualizado correctamente"));
    }
    
    @CrossOrigin(origins = "*")
    @DeleteMapping("/api/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuarioApi(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Usuario no encontrado"));
        }
    
        userRepository.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Usuario eliminado correctamente"));
    }

    @GetMapping("/admin/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuarioPorId(id);
        return "redirect:/admin/usuarios";
    }


    
    @GetMapping("/admin/agregar")
    public String mostrarFormularioAgregar(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }
        return "agregar_usuario";
    }
    
    
    

    @PostMapping("/admin/agregar")
    public String agregarUsuario(@RequestParam String nombre,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String rol) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/admin/agregar?error=exists";
        }

        // Crear usuario y asignar rol
        Usuario nuevoUsuario = new Usuario(nombre, email, passwordEncoder.encode(password));
        Rol role = roleRepository.findByNombre(rol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        nuevoUsuario.setRoles(Collections.singleton(role));
        userRepository.save(nuevoUsuario);

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        model.addAttribute("usuario", usuario);
    
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }
    
        return "editar_usuario";
    }    
    
    

    @PostMapping("/admin/editar")
    public String editarUsuario(@RequestParam Long id,
                                @RequestParam String nombre,
                                @RequestParam String email,
                                @RequestParam String rol) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(nombre);
        usuario.setEmail(email);

        // Asegurar que la colección de roles sea modificable
        Set<Rol> roles = new HashSet<>(usuario.getRoles());
        roles.clear(); // Limpiar roles anteriores

        Rol nuevoRol = roleRepository.findByNombre(rol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        roles.add(nuevoRol); // Agregar el nuevo rol

        usuario.setRoles(roles); // Asignar la nueva lista de roles
        userRepository.save(usuario);

        return "redirect:/admin/usuarios";
    }

    // Mostrar el formulario de edición de perfil de Admin
    @GetMapping("/admin/editar-perfil")
    public String mostrarFormularioEditarPerfil(@AuthenticationPrincipal User user, Model model) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);
            String tema = usuario.getPreferenciaUsuario() != null ? usuario.getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        } else {
            return "redirect:/login?error=user_not_found";
        }

        return "perfil_admin_editar";
    }


    // Procesar el formulario de edición de perfil de Admin
    @PostMapping("/admin/editar-perfil")
    public String procesarEdicionPerfil(@Valid @ModelAttribute("usuario") Usuario usuarioEditado,
                                        BindingResult result,
                                        @RequestParam(name = "nuevaPassword", required = false) String nuevaPassword,
                                        @AuthenticationPrincipal User user,
                                        Model model) {

        if (result.hasErrors()) {
            return "perfil_admin_editar";
        }

        boolean actualizado = userService.actualizarUsuario(user.getUsername(), usuarioEditado, nuevaPassword);
        if (!actualizado) {
            model.addAttribute("error", "No se pudo actualizar el perfil.");
            return "perfil_admin_editar";
        }

        return "redirect:/admin?exito"; // Redirigir al perfil de admin tras éxito
    }

    @PostMapping("/admin/cambiar-tema")
    public String cambiarTemaAdmin(@RequestParam("tema") String tema, @AuthenticationPrincipal User user) {
        userService.actualizarTemaUsuario(user.getUsername(), tema);
        return "redirect:/admin";
    }

    @GetMapping("/admin/mapa")
    public String mostrarMapaAdmin(
        @RequestParam(name = "fecha", required = false) String fecha,
        @RequestParam(name = "hora", required = false) String hora,
        @RequestParam(name = "magnitud", required = false) Double magnitud,
        @RequestParam(name = "profundidad", required = false) Double profundidad,
        Model model,
        @AuthenticationPrincipal User user
    ) {
        List<Sismo> sismos = sismoService.obtenerTodosLosSismos();

        if (fecha != null && !fecha.isEmpty()) {
            sismos = sismos.stream()
                    .filter(s -> s.getFecha().equals(fecha))
                    .toList();
        }

        if (hora != null && !hora.isEmpty()) {
            sismos = sismos.stream()
                .filter(s -> s.getHora() != null && s.getHora().startsWith(hora))
                .toList();
        }

        if (magnitud != null) {
            sismos = sismos.stream()
                    .filter(s -> s.getMagnitud() >= magnitud)
                    .toList();
        }

        if (profundidad != null) {
            sismos = sismos.stream()
                    .filter(s -> s.getProfundidad() <= profundidad)
                    .toList();
        }


        List<Localidad> localidades = localidadService.obtenerTodas();
        model.addAttribute("localidades", localidades);

        model.addAttribute("sismos", sismos);

        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }

        return "mapa_admin";
    }
    
}
