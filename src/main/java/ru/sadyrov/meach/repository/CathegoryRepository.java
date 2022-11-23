package ru.sadyrov.meach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sadyrov.meach.domain.Cathegory;

public interface CathegoryRepository extends JpaRepository<Cathegory, Long> {

}
