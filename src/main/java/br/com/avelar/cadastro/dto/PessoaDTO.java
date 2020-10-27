package br.com.avelar.cadastro.dto;

import br.com.avelar.cadastro.model.Foto;
import br.com.avelar.cadastro.model.Pessoa;
import br.com.avelar.cadastro.validators.CPF;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;

@Data
@NoArgsConstructor @AllArgsConstructor
public class PessoaDTO {

    private Long id;

    @NotNull @NotBlank @NotEmpty @Size(min = 3, max = 150)
    private String nome;

    @NotNull @NotBlank @NotEmpty @Email
    private String email;

    @NotNull @NotBlank @NotEmpty @CPF
    private String cpf;

    @NotNull
    private String foto;

    @NotNull
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dataNascimento;

    public Pessoa toEntity() throws UnsupportedEncodingException {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(this.nome);
        pessoa.setCpf(this.cpf.replace(".", "").replace("-", ""));
        pessoa.setEmail(this.email);
        pessoa.setDataNascimento(dataNascimento.atStartOfDay(ZoneId.of("UTC")));

        if (this.foto != null) {
            Foto foto = new Foto();
            this.foto = this.foto.replaceAll("data:image/jpeg;base64,", "")
                                 .replaceAll("data:image/png;base64,", "");

            foto.setBytes(Base64.getDecoder().decode(this.foto));

            pessoa.setFoto(foto);
        }

        return pessoa;
    }

    public Pessoa toEntityUpdate(Pessoa pessoa) {
        if (pessoa == null) throw new IllegalArgumentException("Entity cannot be null");
        if (pessoa.getId() == null) throw new IllegalStateException("Cannot update a entity without ID");

        pessoa.setNome(this.nome);
        pessoa.setCpf(this.cpf.replace(".", "").replace("-", ""));
        pessoa.setEmail(this.email);
        pessoa.setDataNascimento(dataNascimento.atStartOfDay(ZoneId.of("UTC")));

        this.foto = this.foto.replaceAll("data:image/jpeg;base64,", "")
                             .replaceAll("data:image/png;base64,", "");

        return pessoa;
    }

}
