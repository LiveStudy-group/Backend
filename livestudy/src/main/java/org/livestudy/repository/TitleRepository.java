package org.livestudy.repository;

import org.livestudy.domain.title.Title;
import org.livestudy.domain.title.TitleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {
    List<Title> findAll();

    Optional<Title> findByCode(TitleCode code);
}
