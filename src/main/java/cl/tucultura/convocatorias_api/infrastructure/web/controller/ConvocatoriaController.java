package cl.tucultura.convocatorias_api.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.tucultura.convocatorias_api.application.service.ConvocatoriaService;
import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.infrastructure.web.dto.ConvocatoriaRequestDTO;
import cl.tucultura.convocatorias_api.infrastructure.web.dto.ConvocatoriaResponseDTO;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/convocatorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConvocatoriaController {
    private final ConvocatoriaService service;

    @GetMapping
    public ResponseEntity<List<ConvocatoriaResponseDTO>> listarActivas() {
        List<Convocatoria> dominio = service.listarActivas();
        List<ConvocatoriaResponseDTO> response = dominio.stream()
                .map(ConvocatoriaResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvocatoriaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return service.obtenerPorId(id)
                .map(ConvocatoriaResponseDTO::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ConvocatoriaResponseDTO>> buscar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria) {
        
        if (estado != null) {
            try {
                Convocatoria.EstadoConvocatoria.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado inválido. Valores permitidos: ABIERTA, PROXIMAMENTE, CERRADA, CANCELADA");
            }
        }

        List<Convocatoria> resultados = service.buscarPorFiltros(estado != null ? estado.toUpperCase() : null, categoria);
        List<ConvocatoriaResponseDTO> response = resultados.stream()
                .map(ConvocatoriaResponseDTO::fromDomain)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ConvocatoriaResponseDTO> crear(@Valid @RequestBody ConvocatoriaRequestDTO request) {
        Convocatoria dominio = request.toDomain();
        Convocatoria creada = service.crearConvocatoria(dominio);
        return ResponseEntity.ok(ConvocatoriaResponseDTO.fromDomain(creada));
    }
}
