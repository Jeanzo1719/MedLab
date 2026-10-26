package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

  List<Categoria> findAllByOrderByNombreAsc();
}