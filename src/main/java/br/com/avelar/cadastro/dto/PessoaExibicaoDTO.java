package br.com.avelar.cadastro.dto;

import br.com.avelar.cadastro.model.Pessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PessoaExibicaoDTO {

    private Long id;

    private String nome;

    private String email;

    private String cpf;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dataNascimento;

    public PessoaExibicaoDTO(Pessoa pessoa) {
        if (pessoa == null) return;

        this.id = pessoa.getId();
        this.nome = pessoa.getNome();
        this.email = pessoa.getEmail();
        this.cpf = pessoa.getCpf();

        if (pessoa.getDataNascimento() != null) {
            this.dataNascimento = pessoa.getDataNascimento().toLocalDate();
        }
    }


}
