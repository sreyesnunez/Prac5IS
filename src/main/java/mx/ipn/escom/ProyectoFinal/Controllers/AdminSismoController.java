package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.Utils.PdfGenerator;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.repositories.SismoRepository;
import mx.ipn.escom.ProyectoFinal.repositories.UserRepository;
import mx.ipn.escom.ProyectoFinal.models.Usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;



@Controller
@RequestMapping("/admin/sismos-crud")
public class AdminSismoController {

    private final SismoRepository sismoRepository;
    private final UserRepository userRepository;
    

    public AdminSismoController(SismoRepository sismoRepository, UserRepository userRepository) {
        this.sismoRepository = sismoRepository;
        this.userRepository = userRepository;
    }

    // Mostrar lista de sismos
    @GetMapping
    public String listarSismos(Model model, @AuthenticationPrincipal User user) {
        List<Sismo> sismos = sismoRepository.findAll();
        model.addAttribute("sismos", sismos);

        agregarTema(model, user);
        return "sismos_admin_crud";
    }

    // Mostrar formulario para agregar
    @GetMapping("/agregar")
    public String mostrarFormularioAgregar(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("sismo", new Sismo());
        agregarTema(model, user);
        return "agregar_sismo";
    }

    // Procesar agregar
    @PostMapping("/agregar")
    public String agregarSismo(@ModelAttribute Sismo sismo) {
        sismoRepository.save(sismo);
        return "redirect:/admin/sismos-crud";
    }

    // Mostrar formulario para editar
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        Sismo sismo = sismoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sismo no encontrado"));
        model.addAttribute("sismo", sismo);

        agregarTema(model, user);
        return "editar_sismo";
    }

    // Procesar editar
    @PostMapping("/editar")
    public String editarSismo(@ModelAttribute Sismo sismo) {
        sismoRepository.save(sismo);
        return "redirect:/admin/sismos-crud";
    }

    // Eliminar sismo
    @GetMapping("/eliminar/{id}")
    public String eliminarSismo(@PathVariable Long id) {
        sismoRepository.deleteById(id);
        return "redirect:/admin/sismos-crud";
    }

    @GetMapping("/mapa/descargar-pdf/{id}")
    public ResponseEntity<byte[]> descargarSismoPdfAdmin(@PathVariable("id") Long id) {
        Sismo sismo = sismoRepository.findById(id).orElse(null);
        if (sismo == null) {
            return ResponseEntity.notFound().build();
        }
    
        ByteArrayInputStream bis = PdfGenerator.generateSismoPdf(sismo);
    
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reporte_sismo_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
    
    
    


    // Función para agregar tema claro/oscuro
    private void agregarTema(Model model, User user) {
        Optional<Usuario> usuarioOptional = userRepository.findByEmail(user.getUsername());
        if (usuarioOptional.isPresent()) {
            String tema = usuarioOptional.get().getPreferenciaUsuario() != null ? usuarioOptional.get().getPreferenciaUsuario().getTema() : "claro";
            model.addAttribute("tema", tema);
        }
    }
}
