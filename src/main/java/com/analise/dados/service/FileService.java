package com.analise.dados.service;

import com.analise.dados.models.Cliente;
import com.analise.dados.models.Item;
import com.analise.dados.models.Venda;
import com.analise.dados.models.Vendedor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FileService implements IFileService{

    private static final String CAMINHO_DEFAULT = System.getenv("HOMEPATH");

    private List<Vendedor> vendedores;
    private List<Cliente> clientes;
    private List<Venda> vendas;

    @Scheduled(fixedRate = 10000)
    @Override
    public void analisarArquivos() throws IOException {
        this.readerFiles();
        this.createFiles();
    }


    public void readerFiles() throws IOException {
        vendedores = new ArrayList<>();
        clientes = new ArrayList<>();
        vendas = new ArrayList<>();
        File diretorio = new File(CAMINHO_DEFAULT+"/data/in");
        FilenameFilter textFilefilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".dat");
        };

        File[] files = diretorio.listFiles(textFilefilter);
        List<String> listaDadosText = new ArrayList<>();
        if (files != null && files.length > 0) {
            for(File file : files) {
                Path dadosEntrada = file.toPath();
                listaDadosText.addAll(Files.readAllLines(dadosEntrada));
            }
            for (String linha: listaDadosText) {
                String[] linhaCorreta = linha.split("ç");
                setData(linhaCorreta);
            }
        }
    }

    public void createFiles() throws IOException {
        Path path = Paths.get(CAMINHO_DEFAULT+"/data/out");
        Path diretorio = Files.createDirectories(path);
        Path pathFile = Paths.get(diretorio.toString(), "data.done.dat");
        if (Files.notExists(pathFile)) {
            Files.createFile(pathFile);
        }
        this.enviarMensagem(pathFile);
    }

    private void enviarMensagem(Path pathFile) throws IOException {
        int quantidadeClientes = this.clientes.size();
        int quantidadeVendedores = this.vendedores.size();
        String[] informacoesVenda = this.informacoesVenda().split(",");

        String mensagem = "Quantidade clientes = "+quantidadeClientes+ "\nQuantidade Vendedores = "+quantidadeVendedores+
                "\nId da venda mais cara é "+informacoesVenda[0]+ "\nO pior vendedor é "+informacoesVenda[1];
        byte[] mensagemFile = mensagem.getBytes(StandardCharsets.UTF_8);
        Files.write(pathFile, mensagemFile);
    }

    private void setData(String[] data) {
        int id = Integer.parseInt(data[0]);
        switch (id) {
            case 1:
                this.vendedores.add(Vendedor.builder()
                        .id(Long.parseLong(data[0]))
                        .cpf(data[1])
                        .name(data[2])
                        .salary(Double.parseDouble(data[3]))
                        .build());
                break;
            case 2:
                this.clientes.add(Cliente.builder()
                        .id(Long.parseLong(data[0]))
                        .cnpj(data[1])
                        .name(data[2])
                        .businessArea(data[3])
                        .build());
                break;
            case 3:
                this.vendas.add(Venda.builder()
                        .id(Long.parseLong(data[0]))
                        .saleId(Long.parseLong(data[1]))
                        .itens(montarItem(data[2]))
                        .salesMan(data[3])
                        .build());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
    }

    private List<Item> montarItem(String itensText) {
        List<Item> itens = new ArrayList<>();
        String[] itensSeparados = itensText.replace("[", "").replace("]", "").split(",");

        long tamanhoArray = Arrays.stream(itensSeparados).count();
        for (int i = 0; i < (int) tamanhoArray; i++) {
            String[] itemText = itensSeparados[i].split("-");
            Item item = Item.builder()
                    .id(Long.parseLong(itemText[0]))
                    .quantity(Long.parseLong(itemText[1]))
                    .price(Double.parseDouble(itemText[2]))
                    .build();
            itens.add(item);
        }
        return itens;
    }

    private String informacoesVenda() {
        final Double[] valorMaiorVenda = new Double[1];
        final Double[] valorMenorVenda = new Double[1];
        final Long[] idVenda = new Long[1];
        final String[] piorVendedor = new String[1];

        valorMaiorVenda[0] = 0D;
        valorMenorVenda[0] = 0D;
        this.vendas.forEach(venda -> {

            AtomicReference<Double> valorVenda = new AtomicReference<>(0D);
            venda.getItens().forEach(item -> {
                Double valor = item.getPrice() * item.getQuantity().doubleValue();
                if (valor > valorVenda.get()) {
                    valorVenda.set(valor);
                }
            });
            if (valorVenda.get() > valorMaiorVenda[0]) {
                valorMaiorVenda[0] = valorVenda.get();
                idVenda[0] = venda.getSaleId();
            }

            if (valorVenda.get() < valorMenorVenda[0] || valorMenorVenda[0] == 0D) {
                valorMenorVenda[0] = valorVenda.get();
                piorVendedor[0] = venda.getSalesMan();
            }
        });

        return idVenda[0] + "," + piorVendedor[0];
    }
}
