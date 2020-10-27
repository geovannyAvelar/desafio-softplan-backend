package br.com.avelar.cadastro.repository;

import br.com.avelar.cadastro.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

}
