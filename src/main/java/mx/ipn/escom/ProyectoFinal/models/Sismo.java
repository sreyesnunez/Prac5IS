package mx.ipn.escom.ProyectoFinal.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "sismos")
public class Sismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La fecha no puede estar vacía")
    @Column(name = "fecha")
    private String fecha;

    @Pattern(regexp = "^\\d{2}:\\d{2}:\\d{2}$", message = "La hora debe tener formato HH:mm:ss")
    @Column(name = "hora")
    private String hora;

    @DecimalMin(value = "0.0", inclusive = false, message = "La magnitud debe ser mayor que 0")
    @Column(name = "magnitud", nullable = false)
    private double magnitud;

    @Column(name = "zona")
    private String zona;
    
    @DecimalMin(value = "-90.0", message = "Latitud inválida (mínimo -90.0)")
    @DecimalMax(value = "90.0", message = "Latitud inválida (máximo 90.0)")
    @Column(name = "latitud")
    private double latitud;

    @DecimalMin(value = "-180.0", message = "Longitud inválida (mínimo -180.0)")
    @DecimalMax(value = "180.0", message = "Longitud inválida (máximo 180.0)")
    @Column(name = "longitud")
    private double longitud;

    @DecimalMin(value = "0.0", message = "La profundidad no puede ser negativa")
    @Column(name = "profundidad")
    private double profundidad;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(double magnitud) {
        this.magnitud = magnitud;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(double profundidad) {
        this.profundidad = profundidad;
    }
}
