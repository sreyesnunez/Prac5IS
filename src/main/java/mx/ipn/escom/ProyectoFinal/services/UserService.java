package mx.ipn.escom.ProyectoFinal.services;

import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.models.Rol;
import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.repositories.RoleRepository;
import mx.ipn.escom.ProyectoFinal.repositories.PreferenciaUsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PreferenciaUsuarioRepository preferenciaUsuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public boolean registerUser(String nombre, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; // Usuario ya existe
        }

        String hashedPassword = passwordEncoder.encode(password);
        Usuario newUser = new Usuario();
        newUser.setNombre(nombre);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        Rol roleUser = roleRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("El rol ROLE_USER no existe en la BD."));
        newUser.setRoles(Collections.singleton(roleUser));

        userRepository.save(newUser);

        // Crear preferencia por defecto (claro)
        PreferenciaUsuario preferencia = new PreferenciaUsuario();
        preferencia.setUsuario(newUser);
        preferencia.setTema("claro");
        preferenciaUsuarioRepository.save(preferencia);

        return true;
    }

    @Transactional
    public boolean actualizarUsuario(String emailOriginal, Usuario usuarioActualizado, String nuevaContraseña) {
        Optional<Usuario> optionalUsuario = userRepository.findByEmail(emailOriginal);
        if (optionalUsuario.isEmpty()) {
            return false;
        }

        Usuario usuarioExistente = optionalUsuario.get();
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());

        if (nuevaContraseña != null && !nuevaContraseña.isBlank()) {
            usuarioExistente.setPassword(passwordEncoder.encode(nuevaContraseña));
        }

        userRepository.save(usuarioExistente);
        return true;
    }

    @Transactional
    public boolean actualizarTemaUsuario(String email, String tema) {
        Optional<Usuario> usuarioOpt = userRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        PreferenciaUsuario preferencia = usuario.getPreferenciaUsuario();
        if (preferencia == null) {
            preferencia = new PreferenciaUsuario();
            preferencia.setUsuario(usuario);
        }
        preferencia.setTema(tema);
        preferenciaUsuarioRepository.save(preferencia);
        return true;
    }
    
    @Transactional
    public boolean eliminarUsuarioPorId(Long id) {
        Optional<Usuario> usuarioOptional = userRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Primero eliminar relaciones explícitas (si no hay cascade)
            if (usuario.getPreferenciaUsuario() != null) {
                preferenciaUsuarioRepository.delete(usuario.getPreferenciaUsuario());
            }

            usuario.getRoles().clear(); // Limpiar relación con roles
            userRepository.save(usuario); // Guardar sin roles antes de borrar

            userRepository.delete(usuario); // Ahora sí, eliminar el usuario
            return true;
        }
        return false;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .toArray(String[]::new))
                .build();
    }
}
