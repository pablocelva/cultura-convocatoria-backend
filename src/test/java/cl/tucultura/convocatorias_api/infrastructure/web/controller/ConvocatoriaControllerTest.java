package cl.tucultura.convocatorias_api.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.tucultura.convocatorias_api.application.service.ConvocatoriaService;
import cl.tucultura.convocatorias_api.domain.model.Convocatoria;
import cl.tucultura.convocatorias_api.domain.valueobject.*;

@WebMvcTest(ConvocatoriaController.class)
class ConvocatoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConvocatoriaService service;

    private Convocatoria crearDominio(UUID id, Convocatoria.EstadoConvocatoria estado) {
        return new Convocatoria(
            id, new Titulo("Beca Música 2026"), new Descripcion("Beca para artistas emergentes"),
            Convocatoria.TipoConvocatoria.BECA, new Categoria("Música"),
            new Monto(BigDecimal.valueOf(5000000), "CLP"),
            LocalDateTime.of(2026, 8, 1, 0, 0),
            LocalDateTime.of(2026, 12, 31, 23, 59),
            new UrlOficial("https://cultura.gob.cl"), estado,
            List.of("Ser mayor de 18"), List.of("CV"), null
        );
    }

    @Test
    void listarActivas_retorna200ConLista() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.listarActivas()).thenReturn(List.of(crearDominio(id, Convocatoria.EstadoConvocatoria.ABIERTA)));

        mockMvc.perform(get("/api/convocatorias"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].titulo").value("Beca Música 2026"))
            .andExpect(jsonPath("$[0].tipo").value("BECA"))
            .andExpect(jsonPath("$[0].estado").value("ABIERTA"));
    }

    @Test
    void listarActivas_listaVacia_retorna200ConArrayVacio() throws Exception {
        when(service.listarActivas()).thenReturn(List.of());

        mockMvc.perform(get("/api/convocatorias"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void obtenerPorId_existente_retorna200() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.obtenerPorId(id)).thenReturn(Optional.of(crearDominio(id, Convocatoria.EstadoConvocatoria.ABIERTA)));

        mockMvc.perform(get("/api/convocatorias/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Beca Música 2026"))
            .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void obtenerPorId_noExistente_retorna404() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/convocatorias/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void crear_retorna200ConConvocatoriaCreada() throws Exception {
        UUID id = UUID.randomUUID();
        Convocatoria creada = crearDominio(id, Convocatoria.EstadoConvocatoria.ABIERTA);
        when(service.crearConvocatoria(any())).thenReturn(creada);

        mockMvc.perform(post("/api/convocatorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "titulo": "Beca Música 2026",
                        "descripcion": "Beca para artistas emergentes",
                        "tipo": "BECA",
                        "categoria": "Música",
                        "monto": 5000000,
                        "moneda": "CLP",
                        "fechaApertura": "2026-09-01T00:00:00",
                        "fechaCierre": "2026-12-31T23:59:59",
                        "urlOficial": "https://cultura.gob.cl"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.titulo").value("Beca Música 2026"));
    }

    @Test
    void crear_bodyVacio_retorna400ConErrores() throws Exception {
        mockMvc.perform(post("/api/convocatorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.details.titulo").value("El título es obligatorio"))
            .andExpect(jsonPath("$.details.descripcion").value("La descripción es obligatoria"))
            .andExpect(jsonPath("$.details.tipo").value("El tipo es obligatorio"));
    }

    @Test
    void crear_tipoInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/convocatorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "titulo": "Beca",
                        "descripcion": "Desc",
                        "tipo": "INVALIDO",
                        "categoria": "Música",
                        "monto": 1000000,
                        "moneda": "CLP",
                        "fechaApertura": "2026-09-01T00:00:00",
                        "fechaCierre": "2026-12-31T23:59:59",
                        "urlOficial": "https://ejemplo.org"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details.tipo").value("Tipo inválido"));
    }

    @Test
    void crear_fechaCierreAntesDeApertura_retorna400() throws Exception {
        mockMvc.perform(post("/api/convocatorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "titulo": "Beca",
                        "descripcion": "Desc",
                        "tipo": "BECA",
                        "categoria": "Música",
                        "monto": 1000000,
                        "moneda": "CLP",
                        "fechaApertura": "2026-12-31T00:00:00",
                        "fechaCierre": "2026-09-01T00:00:00",
                        "urlOficial": "https://ejemplo.org"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("La fecha de cierre debe ser posterior a la fecha de apertura."));
    }

    @Test
    void crear_urlInvalida_retorna400() throws Exception {
        mockMvc.perform(post("/api/convocatorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "titulo": "Beca",
                        "descripcion": "Desc",
                        "tipo": "BECA",
                        "categoria": "Música",
                        "monto": 1000000,
                        "moneda": "CLP",
                        "fechaApertura": "2026-09-01T00:00:00",
                        "fechaCierre": "2026-12-31T23:59:59",
                        "urlOficial": "no-es-url"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details.urlOficial").value("La URL debe tener un formato válido"));
    }

    @Test
    void buscar_ambosFiltros_retorna200() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.buscarPorFiltros("ABIERTA", "Música"))
            .thenReturn(List.of(crearDominio(id, Convocatoria.EstadoConvocatoria.ABIERTA)));

        mockMvc.perform(get("/api/convocatorias/buscar")
                .param("estado", "ABIERTA")
                .param("categoria", "Música"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].titulo").value("Beca Música 2026"));
    }

    @Test
    void buscar_sinFiltros_retorna200() throws Exception {
        when(service.buscarPorFiltros(null, null))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/convocatorias/buscar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscar_estadoInvalido_retorna400() throws Exception {
        mockMvc.perform(get("/api/convocatorias/buscar")
                .param("estado", "INVALIDO"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Estado inválido. Valores permitidos: ABIERTA, PROXIMAMENTE, CERRADA, CANCELADA"));
    }
}
