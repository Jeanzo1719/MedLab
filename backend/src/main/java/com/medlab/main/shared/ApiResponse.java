package com.medlab.main.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
@Builder 
public class ApiResponse<T> {
  int code;
  String message;
  T data;
}
