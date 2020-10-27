package br.com.avelar.cadastro.repository;

import br.com.avelar.cadastro.model.Foto;
import br.com.avelar.cadastro.model.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    @Query("select p from Pessoa p join fetch p.foto where p.id = :id")
    public Optional<Pessoa> findByIdWithPhoto(Long id);

    @Query("select p.foto from Pessoa p where p.id = :id")
    public Foto findPhoto(Long id);

}
