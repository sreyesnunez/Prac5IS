package mx.ipn.escom.ProyectoFinal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import mx.ipn.escom.ProyectoFinal.models.PreferenciaUsuario;
import mx.ipn.escom.ProyectoFinal.models.Rol;
import mx.ipn.escom.ProyectoFinal.models.Usuario;
import mx.ipn.escom.ProyectoFinal.repositories.PreferenciaUsuarioRepository;
import mx.ipn.escom.ProyectoFinal.repositories.RoleRepository;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PreferenciaUsuarioRepository preferenciaUsuarioRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private Rol rolMock;
    private Usuario usuarioMock;

    @BeforeEach
    void setup() {
        rolMock = new Rol();
        rolMock.setNombre("ROLE_USER");

        usuarioMock = new Usuario();
        usuarioMock.setNombre("Victor");
        usuarioMock.setEmail("victor@example.com");
        usuarioMock.setPassword("original123");
    }

    @Test
    void testRegisterUser_UsuarioNuevo() {
        when(userRepository.findByEmail(usuarioMock.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(rolMock));

        boolean result = userService.registerUser("Victor", usuarioMock.getEmail(), "123456");

        assertTrue(result);
        verify(userRepository).save(any(Usuario.class));
        verify(preferenciaUsuarioRepository).save(any(PreferenciaUsuario.class));
    }

    @Test
    void testRegisterUser_UsuarioExistente() {
        when(userRepository.findByEmail(usuarioMock.getEmail())).thenReturn(Optional.of(usuarioMock));

        boolean result = userService.registerUser("Victor", usuarioMock.getEmail(), "123456");

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testActualizarUsuario_ConNuevaContraseña() {
        when(userRepository.findByEmail("victor@example.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode("nueva123")).thenReturn("hashedNueva");

        Usuario actualizado = new Usuario();
        actualizado.setNombre("NuevoNombre");
        actualizado.setEmail("nuevo@example.com");

        boolean result = userService.actualizarUsuario("victor@example.com", actualizado, "nueva123");

        assertTrue(result);
        verify(userRepository).save(argThat(u ->
            u.getNombre().equals("NuevoNombre") &&
            u.getEmail().equals("nuevo@example.com") &&
            u.getPassword().equals("hashedNueva")
        ));
    }

    @Test
    void testActualizarUsuario_SinNuevaContraseña() {
        when(userRepository.findByEmail("victor@example.com")).thenReturn(Optional.of(usuarioMock));

        Usuario actualizado = new Usuario();
        actualizado.setNombre("NuevoNombre");
        actualizado.setEmail("nuevo@example.com");

        boolean result = userService.actualizarUsuario("victor@example.com", actualizado, null);

        assertTrue(result);
        verify(userRepository).save(argThat(u ->
            u.getNombre().equals("NuevoNombre") &&
            u.getEmail().equals("nuevo@example.com") &&
            u.getPassword().equals("original123") // No se cambia la contraseña
        ));
    }

    @Test
    void testActualizarUsuario_UsuarioNoEncontrado() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        boolean result = userService.actualizarUsuario("noexiste@example.com", new Usuario(), "123");

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testActualizarTemaUsuario_ConPreferenciaExistente() {
        PreferenciaUsuario preferencia = new PreferenciaUsuario();
        preferencia.setTema("claro");

        usuarioMock.setPreferenciaUsuario(preferencia);
        when(userRepository.findByEmail("victor@example.com")).thenReturn(Optional.of(usuarioMock));

        boolean result = userService.actualizarTemaUsuario("victor@example.com", "oscuro");

        assertTrue(result);
        assertEquals("oscuro", preferencia.getTema());
        verify(preferenciaUsuarioRepository).save(preferencia);
    }

    @Test
    void testActualizarTemaUsuario_SinPreferenciaExistente() {
        usuarioMock.setPreferenciaUsuario(null);
        when(userRepository.findByEmail("victor@example.com")).thenReturn(Optional.of(usuarioMock));

        boolean result = userService.actualizarTemaUsuario("victor@example.com", "oscuro");

        assertTrue(result);
        verify(preferenciaUsuarioRepository).save(argThat(p ->
            p.getUsuario().equals(usuarioMock) &&
            p.getTema().equals("oscuro")
        ));
    }

    @Test
    void testActualizarTemaUsuario_UsuarioNoEncontrado() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        boolean result = userService.actualizarTemaUsuario("noexiste@example.com", "oscuro");

        assertFalse(result);
        verify(preferenciaUsuarioRepository, never()).save(any());
    }

    @Test
    void testEliminarUsuarioPorId_ConPreferenciaYRoles() {
        PreferenciaUsuario preferencia = new PreferenciaUsuario();
        preferencia.setUsuario(usuarioMock);
        usuarioMock.setPreferenciaUsuario(preferencia);
        usuarioMock.setRoles(new HashSet<>(Set.of(new Rol("ROLE_USER"))));

        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        boolean result = userService.eliminarUsuarioPorId(1L);

        assertTrue(result);
        assertTrue(usuarioMock.getRoles().isEmpty());
        verify(preferenciaUsuarioRepository).delete(preferencia);
        verify(userRepository, times(1)).save(usuarioMock); // antes y después de limpiar roles
        verify(userRepository).delete(usuarioMock);
    }

    @Test
    void testEliminarUsuarioPorId_SinPreferencia() {
        usuarioMock.setPreferenciaUsuario(null);
        usuarioMock.setRoles(new HashSet<>(Set.of(new Rol("ROLE_USER"))));

        when(userRepository.findById(2L)).thenReturn(Optional.of(usuarioMock));

        boolean result = userService.eliminarUsuarioPorId(2L);

        assertTrue(result);
        verify(preferenciaUsuarioRepository, never()).delete(any());
        verify(userRepository, times(1)).save(usuarioMock);
        verify(userRepository).delete(usuarioMock);
    }

    @Test
    void testEliminarUsuarioPorId_UsuarioNoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = userService.eliminarUsuarioPorId(99L);

        assertFalse(result);
        verify(preferenciaUsuarioRepository, never()).delete(any());
        verify(userRepository, never()).delete(any());
    }

}
