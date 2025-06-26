package mx.ipn.escom.ProyectoFinal.models;

import jakarta.persistence.*;

@Entity
@Table(name = "localidades")
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String estado;
    private Double latitud;
    private Double longitud;
    private Integer pobTotal;
    private Integer pobMasculina;
    private Integer pobFemenina;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Integer getPobTotal() { return pobTotal; }
    public void setPobTotal(Integer pobTotal) { this.pobTotal = pobTotal; }

    public Integer getPobMasculina() { return pobMasculina; }
    public void setPobMasculina(Integer pobMasculina) { this.pobMasculina = pobMasculina; }

    public Integer getPobFemenina() { return pobFemenina; }
    public void setPobFemenina(Integer pobFemenina) { this.pobFemenina = pobFemenina; }
}
