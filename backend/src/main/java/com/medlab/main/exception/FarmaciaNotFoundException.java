package com.medlab.main.exception;

public class FarmaciaNotFoundException extends RuntimeException {

  public FarmaciaNotFoundException(Integer id) {
    super("No existe la farmacia con id " + id);
  }
}
