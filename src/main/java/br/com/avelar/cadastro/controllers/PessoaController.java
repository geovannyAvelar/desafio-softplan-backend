package br.com.avelar.cadastro.controllers;

import br.com.avelar.cadastro.dto.PessoaDTO;
import br.com.avelar.cadastro.dto.PessoaExibicaoDTO;
import br.com.avelar.cadastro.model.Foto;
import br.com.avelar.cadastro.service.PessoaService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping
    public void save(@Valid @RequestBody PessoaDTO pessoaDto) throws IOException {
        pessoaService.save(pessoaDto.toEntity());
    }

    @PutMapping
    public void update(@Valid @RequestBody PessoaDTO pessoaDto) throws IOException {
        pessoaService.update(pessoaDto);
    }

    @GetMapping("/filtro")
    public Page<PessoaExibicaoDTO> filter(@RequestParam(value = "nome", required = false) String nome,
                                          @RequestParam(value = "cpf", required = false) String cpf,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "nascimento", required = false) LocalDate nascimento) {
        return pessoaService.filter(nome, cpf, email, nascimento)
                            .map((p) -> new PessoaExibicaoDTO(p));
    }

    @GetMapping
    public Page<PessoaExibicaoDTO> findAll(
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "10") Integer rows,
                        @RequestParam(required = false, defaultValue = "nome") String orderBy,
                        @RequestParam(required = false, defaultValue = "DESC") String direction) {
        return pessoaService
                    .findAll(page, rows, orderBy, direction)
                    .map((p) -> new PessoaExibicaoDTO(p));
    }

    @GetMapping(value = "/foto/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> findPhoto(@PathVariable("id") Long id) {
        Foto foto = pessoaService.findPhoto(id);

        if (foto != null) {
            return ResponseEntity.ok(foto.getBytes());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pessoaService.delete(id);
    }

}
