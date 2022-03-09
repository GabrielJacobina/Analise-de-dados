package com.analise.dados.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SpringExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Test
    void analisaDados_quandoBemSucedido() throws IOException {
        Path pathFile = this.criarArquivos();
        this.escreveArquivo(pathFile);

        assertThatCode(() -> fileService.analisarArquivos())
                .doesNotThrowAnyException();
    }

    @Test
    void analisaDados_quandoNaoTemLinhasNosArquivos() throws IOException {
        this.criarArquivos();

        assertThatCode(() -> fileService.analisarArquivos())
                .doesNotThrowAnyException();
    }

    @Test
    void analisaDados_quandoNaoExisteArquivos() {
        assertThatCode(() -> fileService.analisarArquivos())
                .doesNotThrowAnyException();
    }

    Path criarArquivos() throws IOException {
        Path path = Paths.get(System.getenv("HOMEPATH")+"/data/in");
        Path diretorio = Files.createDirectories(path);
        Path pathFile = Paths.get(diretorio.toString(), "data.dat");
        if (Files.notExists(pathFile)) {
            Files.createFile(pathFile);
        }
        return pathFile;
    }

    void escreveArquivo(Path pathFile) throws IOException {
        String mensagem = "002ç2345675433444345çEduardo PereiraçRural\n" +
                "003ç10ç[1-10-100,2-30-2.50,3-40-3.10]çPedro\n" +
                "001ç1234567891234çPedroç50000" +
                "\n001ç3245678865434çPauloç40000.99";
        byte[] mensagemFile = mensagem.getBytes(StandardCharsets.UTF_8);
        Files.write(pathFile, mensagemFile);
    }

}
