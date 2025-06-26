package mx.ipn.escom.ProyectoFinal.Utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Color;


import mx.ipn.escom.ProyectoFinal.models.Sismo; 

public class PdfGenerator {

    public static ByteArrayInputStream generateSismoPdf(Sismo sismo) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("Reporte de Sismo", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Información del sismo
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            document.add(new Paragraph("Fecha: " + sismo.getFecha(), infoFont));
            document.add(new Paragraph("Hora: " + sismo.getHora(), infoFont));
            document.add(new Paragraph("Latitud: " + sismo.getLatitud(), infoFont));
            document.add(new Paragraph("Longitud: " + sismo.getLongitud(), infoFont));
            document.add(new Paragraph("Magnitud: " + sismo.getMagnitud(), infoFont));
            document.add(new Paragraph("Profundidad: " + sismo.getProfundidad() + " km", infoFont));
            document.add(new Paragraph("Zona: " + sismo.getZona(), infoFont));

            document.close();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
