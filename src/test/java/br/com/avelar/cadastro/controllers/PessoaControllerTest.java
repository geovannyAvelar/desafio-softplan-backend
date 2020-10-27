package br.com.avelar.cadastro.controllers;

import br.com.avelar.cadastro.AbstractTest;
import br.com.avelar.cadastro.dto.PessoaDTO;
import br.com.avelar.cadastro.dto.PessoaExibicaoDTO;
import br.com.avelar.cadastro.model.Foto;
import br.com.avelar.cadastro.model.Pessoa;
import br.com.avelar.cadastro.service.PessoaService;
import br.com.avelar.cadastro.utils.GeradorCPF;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class PessoaControllerTest extends AbstractTest {

    @InjectMocks
    private PessoaController pessoaController;

    @Mock
    private PessoaService pessoaService;

    private MockMvc mockMvc;

    private static String imageBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAcDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==";

    private static byte[] imageBytes =
                Base64.getDecoder().decode(imageBase64.replaceAll("data:image/jpeg;base64,", "")
                                                      .replaceAll("data:image/png;base64,", ""));

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pessoaController).build();
    }

    @Test
    public void shouldSave() throws Exception {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("TESTE");
        pessoaDTO.setCpf(GeradorCPF.cpf(true));
        pessoaDTO.setEmail("teste@teste.com");
        pessoaDTO.setDataNascimento(LocalDate.now());
        pessoaDTO.setFoto(imageBase64);

        Pessoa pessoaWithId = pessoaDTO.toEntity();
        pessoaWithId.setId(1l);

        when(pessoaService.save(pessoaDTO.toEntity())).thenReturn(pessoaWithId);

        String requestContent = super.mapToJson(pessoaDTO);

        mockMvc.perform(post("/pessoa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shouldFailWithInvalidEmail() throws Exception {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("TESTE");
        pessoaDTO.setCpf(GeradorCPF.cpf(true));
        pessoaDTO.setEmail("teste");
        pessoaDTO.setDataNascimento(LocalDate.now());

        String requestContent = super.mapToJson(pessoaDTO);

        mockMvc.perform(post("/pessoa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldFailWithInvalidCPF() throws Exception {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setNome("TESTE");
        pessoaDTO.setCpf("485.448.574-11");
        pessoaDTO.setEmail("teste");
        pessoaDTO.setDataNascimento(LocalDate.now());

        String requestContent = super.mapToJson(pessoaDTO);

        mockMvc.perform(post("/pessoa")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestContent))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldfindAll() throws Exception {
        List<Pessoa> listPessoas = new LinkedList<>();
        listPessoas.add(new Pessoa(1l, "Geovanny", "geovanny@email.com",
                                "729.993.320-92", ZonedDateTime.now(), new Foto(1l, imageBytes)));
        listPessoas.add(new Pessoa(2l, "Fulano","fulano@email.com",
                                "383.087.930-02", ZonedDateTime.now(), new Foto(2l, imageBytes)));

        Page<Pessoa> page = new PageImpl<Pessoa>(listPessoas);

        when(pessoaService.findAll(0, 10, "nome", "DESC")).thenReturn(page);

        MvcResult result = findAllRequest(0, 10, null, null);
        String responseContent = result.getResponse().getContentAsString();
        assertEquals(super.mapToJson(page.map((p) -> new PessoaExibicaoDTO(p))), responseContent);
    }

    @Test
    public void shouldFindAllWithPagination() throws Exception {
        List<Pessoa> listPessoas = new LinkedList<>();

        for (int i = 0; i < 20; i++) {
            Long id = Long.valueOf(i);
            listPessoas.add(new Pessoa(id, "ABCDEFG", "abcdefg@email.com",
                            GeradorCPF.cpf(true), ZonedDateTime.now(), new Foto(id, imageBytes)));
        }

        Page<Pessoa> firstPage = new PageImpl<Pessoa>(listPessoas.subList(0, 9));
        Page<Pessoa> secondPage = new PageImpl<Pessoa>(listPessoas.subList(10, 19));
        Page<Pessoa> thirdPage = Page.empty(PageRequest.of(2, 10));

        when(pessoaService.findAll(0, 10, "nome", "DESC")).thenReturn(firstPage);
        when(pessoaService.findAll(1, 10, "nome", "DESC")).thenReturn(secondPage);
        when(pessoaService.findAll(2, 10, "nome", "DESC")).thenReturn(thirdPage);

        MvcResult resultFirstPage = findAllRequest(0, 10, null, null);
        String responseContentFirstPage = resultFirstPage.getResponse().getContentAsString();
        assertEquals(super.mapToJson(firstPage.map((p) -> new PessoaExibicaoDTO(p))), responseContentFirstPage);

        MvcResult resultSecondPage = findAllRequest(1, 10, null, null);
        String responseContentSecondPage = resultSecondPage.getResponse().getContentAsString();
        assertEquals(super.mapToJson(secondPage.map((p) -> new PessoaExibicaoDTO(p))), responseContentSecondPage);

        MvcResult resultThirdPage = findAllRequest(2, 10, null, null);
        String responseContentThirdPage = resultThirdPage.getResponse().getContentAsString();
        assertEquals(super.mapToJson(thirdPage.map((p) -> new PessoaExibicaoDTO(p))), responseContentThirdPage);
    }

    private MvcResult findAllRequest(Integer page, Integer rows, String orderBy, String direction) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/pessoa");

        if (page != null) {
            requestBuilder.param("page", page.toString());
        }

        if (rows != null) {
            requestBuilder.param("rows", rows.toString());
        }

        if (orderBy != null) {
            requestBuilder.param("orderBy", orderBy);
        }

        if (direction != null) {
            requestBuilder.param("direction", direction);
        }

        requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder)
                        .andExpect(status().isOk())
                        .andReturn();
    }

}
