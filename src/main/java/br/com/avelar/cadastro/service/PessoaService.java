package br.com.avelar.cadastro.service;

import br.com.avelar.cadastro.dto.PessoaDTO;
import br.com.avelar.cadastro.exceptions.DataIntegrityException;
import br.com.avelar.cadastro.exceptions.ResourceNotFoundException;
import br.com.avelar.cadastro.model.Foto;
import br.com.avelar.cadastro.model.Pessoa;
import br.com.avelar.cadastro.repository.PessoaRepository;
import br.com.avelar.cadastro.utils.ImageUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EntityManager em;

    public Pessoa save(Pessoa pessoa) throws IOException {
        byte[] bytesFoto = pessoa.getFoto().getBytes();

        if (ImageUtils.guessImageFormat(bytesFoto).equalsIgnoreCase("GIF")) {
            throw new DataIntegrityException("Formato de imagem GIF não suportado");
        }

        pessoa.getFoto().setBytes(ImageUtils.compressImage(bytesFoto));

        return pessoaRepository.save(pessoa);
    }

    public Pessoa update(PessoaDTO pessoaDTO) throws IOException {
        if (pessoaDTO == null) throw new IllegalArgumentException("DTO cannot be null");
        if (pessoaDTO.getId() == null) throw new IllegalArgumentException("ID cannot be null");

        Optional<Pessoa> optionalPessoa = pessoaRepository.findByIdWithPhoto(pessoaDTO.getId());

        if (optionalPessoa.isEmpty()) throw new ResourceNotFoundException("Pesssoa cannot be found");

        Pessoa pessoa = optionalPessoa.get();
        pessoa = pessoaDTO.toEntityUpdate(pessoa);

        byte[] bytesFoto = Base64.getDecoder().decode(pessoaDTO.getFoto());

        if (ImageUtils.guessImageFormat(bytesFoto).equalsIgnoreCase("GIF")) {
            throw new DataIntegrityException("Formato de imagem GIF não suportado");
        }

        pessoa.getFoto().setBytes(ImageUtils.compressImage(bytesFoto));

        return pessoaRepository.save(pessoa);
    }

    public Page<Pessoa> findAll(Integer page, Integer rows, String orderBy, String direction) {
        Pageable pageable = PageRequest.of(page, rows,
                                            Sort.Direction.fromString(direction), orderBy);
        return pessoaRepository.findAll(pageable);
    }

    public Foto findPhoto(Long id) {
        return pessoaRepository.findPhoto(id);
    }

    public Page<Pessoa> filter(String nome, String cpf, String email, LocalDate nascimento) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pessoa> query = cb.createQuery(Pessoa.class);
        Root<Pessoa> pessoaRoot = query.from(Pessoa.class);

        List<javax.persistence.criteria.Predicate> predicateList = new ArrayList<>();

        if (nome != null) {
            Path<String> pathNome = pessoaRoot.get("nome");
            predicateList.add(cb.like(pathNome, "%" + nome + "%"));
        }

        if (cpf != null) {
            Path<String> pathCpf = pessoaRoot.get("cpf");
            predicateList.add(cb.equal(pathCpf, cpf.replace(".", "")
                                                    .replace("-", "")));
        }

        if (email != null) {
            Path<String> pathEmail = pessoaRoot.get("email");
            predicateList.add(cb.equal(pathEmail, email));
        }

        if (nascimento != null) {
            Expression<ZonedDateTime> campoNascimento =
                                    cb.function("date", ZonedDateTime.class, pessoaRoot.get("dataNascimento"));
            predicateList.add(cb.equal(campoNascimento, nascimento.atStartOfDay(ZoneId.of("UTC"))));
        }

        javax.persistence.criteria.Predicate[] predicates =
                                                new javax.persistence.criteria.Predicate[predicateList.size()];
        predicateList.toArray(predicates);

        query.where(predicates);

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.desc(pessoaRoot.get("nome")));
        query.orderBy(orderList);

        TypedQuery<Pessoa> typedQuery = em.createQuery(query);

        List<Pessoa> pessoas = typedQuery.getResultList();

        return new PageImpl<Pessoa>(pessoas);
    }

    public void delete(Long id) {
        pessoaRepository.deleteById(id);
    }

}