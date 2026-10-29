package com.medlab.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.medlab.main.dto.DisponibilidadSospechosaDto;
import com.medlab.main.service.CalificacionesSospechosasService;

import com.medlab.main.dto.AuditoriaEstadoDto;
import com.medlab.main.dto.CalificacionAdminDto;
import com.medlab.main.dto.DisponibilidadAdminDto;
import com.medlab.main.dto.FarmaciaConAlertasDto;
import com.medlab.main.dto.FarmaciaHistorialDto;
import com.medlab.main.dto.PaginaDto;
import com.medlab.main.dto.RevisionInactividadDto;
import com.medlab.main.dto.UsuarioHistorialDto;
import com.medlab.main.service.AdministradorAutenticadoProvider;
import com.medlab.main.service.CalificacionAdminService;
import com.medlab.main.service.GestionAdminService;
import com.medlab.main.service.InactividadFarmaciaService;

import lombok.RequiredArgsConstructor;

/**
 * Panel del administrador (HU-9). REST como el panel de la farmacia: son consultas y borrados
 * de un solo usuario, no pantallas públicas en vivo. Sin lógica propia: cada método primero
 * confirma quién es el administrador y después delega en el service.
 */
@RestController
@RequestMapping("/api/panel-admin")
@RequiredArgsConstructor
public class PanelAdminController {

  private final GestionAdminService gestionService;
  private final CalificacionAdminService calificacionService;
  private final InactividadFarmaciaService inactividadService;
  private final AdministradorAutenticadoProvider administradorAutenticado;
  private final CalificacionesSospechosasService sospechosasService;

  @GetMapping("/usuarios")
  public PaginaDto<UsuarioHistorialDto> usuarios(@RequestParam(defaultValue = "") String rol,
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.usuarios(rol, pagina, tamano));
  }

  @GetMapping("/farmacias")
  public PaginaDto<FarmaciaHistorialDto> farmacias(@RequestParam(defaultValue = "") String estado,
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.farmacias(estado, pagina, tamano));
  }

  @GetMapping("/farmacias/alertas")
  public PaginaDto<FarmaciaConAlertasDto> farmaciasConAlertas(
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.farmaciasConAlertas(pagina, tamano));
  }

  @GetMapping("/farmacias/{farmaciaId}/auditoria")
  public PaginaDto<AuditoriaEstadoDto> auditoria(@PathVariable Integer farmaciaId,
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.auditoria(farmaciaId, pagina, tamano));
  }

  @GetMapping("/farmacias/{farmaciaId}/disponibilidades")
  public PaginaDto<DisponibilidadAdminDto> disponibilidades(@PathVariable Integer farmaciaId,
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.disponibilidades(farmaciaId, pagina, tamano));
  }

  @GetMapping("/disponibilidades/{disponibilidadId}/calificaciones")
  public PaginaDto<CalificacionAdminDto> calificaciones(@PathVariable Integer disponibilidadId,
      @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamano) {
    administradorAutenticado.obtenerId();
    return PaginaDto.de(gestionService.calificaciones(disponibilidadId, pagina, tamano));
  }

  @DeleteMapping("/calificaciones/{calificacionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminarCalificacion(@PathVariable Long calificacionId) {
    Integer administradorId = administradorAutenticado.obtenerId();
    calificacionService.eliminar(calificacionId, administradorId);
  }

  /** Lanza a mano la misma revisión que hace el job diario; sirve para probar sin esperar. */
  @PostMapping("/inactividad/revisar")
  public RevisionInactividadDto revisarInactividad() {
    administradorAutenticado.obtenerId();
    return inactividadService.revisar();
  }

  @GetMapping("/disponibilidades/sospechosas")
  public List<DisponibilidadSospechosaDto> sospechosas() {
    administradorAutenticado.obtenerId();
    return sospechosasService.listar();
  }
}