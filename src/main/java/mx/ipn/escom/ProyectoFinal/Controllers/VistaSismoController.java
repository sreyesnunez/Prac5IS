package mx.ipn.escom.ProyectoFinal.Controllers;

import mx.ipn.escom.ProyectoFinal.Utils.PdfGenerator;
import mx.ipn.escom.ProyectoFinal.models.Sismo;
import mx.ipn.escom.ProyectoFinal.services.SismoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
public class VistaSismoController {

    @Autowired
    private SismoService sismoService;
    

    @GetMapping("/mapa")
    public String mostrarMapa(Model model) {
        List<Sismo> sismos = sismoService.obtenerTodosLosSismos();
        model.addAttribute("sismos", sismos);
        return "mapa";
    }
    
    @GetMapping("/mapa/descargar-pdf/{id}")
    public ResponseEntity<byte[]> descargarSismoPdfUsuario(@PathVariable("id") Long id) {
        Sismo sismo = sismoService.obtenerSismoPorId(id);
        if (sismo == null) {
            return ResponseEntity.notFound().build();
        }
    
        ByteArrayInputStream bis = PdfGenerator.generateSismoPdf(sismo);
    
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=reporte_sismo_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
    
    
}
