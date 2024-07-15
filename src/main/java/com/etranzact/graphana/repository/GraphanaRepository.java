package com.etranzact.graphana.repository;

import com.etranzact.graphana.Entity.Graphana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface GraphanaRepository extends JpaRepository<Graphana, Integer> {
}
