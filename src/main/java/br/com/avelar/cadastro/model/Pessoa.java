package br.com.avelar.cadastro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "pessoa")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    @NotNull @NotBlank @NotEmpty @Size(min = 3, max = 150)
    private String nome;

    @Column(name = "email")
    @NotNull @NotBlank @NotEmpty @Email
    private String email;

    @Column(name = "cpf")
    @NotNull @NotBlank @NotEmpty
    private String cpf;

    @Column(name = "data_nascimento")
    @NotNull
    private ZonedDateTime dataNascimento;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "foto_id", referencedColumnName = "id")
    private Foto foto;

}
