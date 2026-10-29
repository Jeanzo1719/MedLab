package com.medlab.main.service;

public interface AprobacionFarmaciasService {

  String ESTADO_PENDIENTE = "pendiente";
  String ESTADO_APROBADA = "aprobada";
  String ESTADO_RECHAZADA = "rechazada";

  void approveFarmacia(Integer farmaciaId, Integer usuarioAdminId);

  void rejectFarmacia(Integer farmaciaId, Integer usuarioAdminId);
}
