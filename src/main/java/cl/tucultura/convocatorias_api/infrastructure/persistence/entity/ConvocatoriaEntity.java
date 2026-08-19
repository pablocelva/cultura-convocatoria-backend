package cl.tucultura.convocatorias_api.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convocatorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
 
    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String categoria;

    private BigDecimal monto;
    @Column(length = 10)
    private String moneda;

    @Column(nullable = false)
    private LocalDateTime fechaApertura;

    @Column(nullable = false)
    private LocalDateTime fechaCierre;

    @Column(nullable = false, length = 500)
    private String urlOficial;

    @Column(length = 50)
    private String estado;

    @Column(columnDefinition = "jsonb")
    private String requisitosJson;

    @Column(columnDefinition = "jsonb")
    private String documentacionJson;

    @Column(name = "fuente_id")
    private UUID fuenteId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
