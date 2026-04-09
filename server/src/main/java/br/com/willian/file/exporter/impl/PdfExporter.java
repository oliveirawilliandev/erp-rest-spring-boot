package br.com.willian.file.exporter.impl; // Pacote da implementação de exportadores

import br.com.willian.dto.v1.EmployeesDTO; // DTO de funcionários
import br.com.willian.file.exporter.contract.EmployeesExporter; // Contrato para exportadores
import br.com.willian.model.PurchaseMock; // Mock de compras para teste

import br.com.willian.services.JasperReportCache; // Serviço de cache de relatórios Jasper
import br.com.willian.services.QRCodeService; // Serviço de geração de QR Code
import net.sf.jasperreports.engine.*; // Engine do JasperReports
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource; // DataSource a partir de coleções
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.beans.factory.annotation.Autowired; // Injeção de dependência
import org.springframework.core.io.ByteArrayResource; // Recurso em memória
import org.springframework.core.io.Resource; // Representação de recurso
import org.springframework.stereotype.Component; // Componente Spring

import java.io.ByteArrayOutputStream; // Stream para escrita em memória
import java.io.InputStream; // Stream para leitura de arquivos
import java.util.Collections; // Utilitários para coleções
import java.util.HashMap; // Implementação de Map
import java.util.List; // Interface List
import java.util.Map; // Interface Map

@Component // Define a classe como um componente Spring
public class PdfExporter implements EmployeesExporter { // Implementa o contrato de exportação

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(PdfExporter.class); // Logger para rastreamento

    @Autowired // Injeção de dependência
    private QRCodeService qrCodeService; // Serviço para geração de QR Code

    @Autowired // Injeção de dependência
    private JasperReportCache jasperReportCache; // Cache de relatórios compilados

    // [EMP-EXPORTER-PDF-004]
    // Exporta uma lista de Employees
    @Override // Sobrescreve método da interface
    public Resource exportEmployees(List<EmployeesDTO> employees) throws Exception {

        logger.info("[EMP-EXPORTER-PDF-004] Exportação PDF de lista iniciada | totalRegistros={}",
                employees != null ? employees.size() : 0); // Log da quantidade

        // Validação da lista
        if (employees == null || employees.isEmpty()) {
            logger.warn("[EMP-EXPORTER-PDF-004] Lista de funcionários vazia ou nula | gerando PDF vazio"); // Log de aviso
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Busca template no classpath
        String templatePath = "/templates/employees.jrxml"; // Caminho do template
        logger.debug("[EMP-EXPORTER-PDF-004] Buscando template: {}", templatePath); // Log da busca

        InputStream inputStream = getClass().getResourceAsStream(templatePath); // Carrega template
        if (inputStream == null) { // validação: arquivo existe?
            logger.error("[EMP-EXPORTER-PDF-004] Template não encontrado | path={}", templatePath); // Log de erro
            throw new RuntimeException("Template file not found: " + templatePath); // Exceção
        }
        logger.debug("[EMP-EXPORTER-PDF-004] Template carregado com sucesso | path={}", templatePath); // Log de sucesso

        // Obtém relatório compilado do cache
        JasperReport jasperReport = jasperReportCache.getReport(templatePath); // compila .jrxml para objeto JasperReport
        logger.debug("[EMP-EXPORTER-PDF-004] Relatório obtido do cache | report={}", jasperReport.getName()); // Log do relatório

        // Converte lista em datasource compatível com Jasper
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees); // DataSource a partir da lista
        logger.debug("[EMP-EXPORTER-PDF-004] DataSource criado | registros={}", employees.size()); // Log do datasource

        // Parâmetros do relatório
        Map<String, Object> parameters = new HashMap<>(); // mapa vazio - adicionar parâmetros do template aqui
        logger.debug("[EMP-EXPORTER-PDF-004] Parâmetros preparados | count={}", parameters.size()); // Log dos parâmetros

        // Preenche relatório com dados e parâmetros
        logger.debug("[EMP-EXPORTER-PDF-004] Preenchendo relatório..."); // Log de início
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource); // processa relatório
        logger.debug("[EMP-EXPORTER-PDF-004] Relatório preenchido | páginas={}", jasperPrint.getPages().size()); // Log de conclusão

        // Exporta para PDF em memória
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) { // try-with-resources garante fechamento do stream
            logger.debug("[EMP-EXPORTER-PDF-004] Exportando para PDF..."); // Log de início da exportação

            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream); // renderiza JasperPrint para bytes PDF

            byte[] bytes = outputStream.toByteArray(); // Converte para array de bytes
            logger.debug("[EMP-EXPORTER-PDF-004] PDF gerado | tamanho={} bytes", bytes.length); // Log do tamanho

            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.info("[EMP-EXPORTER-PDF-004] Exportação PDF de lista concluída | totalRegistros={} | tamanho={} bytes | tempo={}ms | páginas={}",
                    employees.size(), bytes.length, duration, jasperPrint.getPages().size()); // Log de sucesso

            return new ByteArrayResource(bytes); // encapsula bytes em Resource do Spring para download
        } catch (Exception e) {
            logger.error("[EMP-EXPORTER-PDF-004] Erro ao exportar PDF | erro={}", e.getMessage(), e); // Log de erro
            throw e; // Relança exceção
        }
    }

    // [EMP-EXPORTER-PDF-005]
    // Exporta um employee
    @Override // Sobrescreve método da interface
    public Resource exportEmployee(EmployeesDTO employees) throws Exception {

        logger.info("[EMP-EXPORTER-PDF-005] Exportação PDF de funcionário único iniciada | id={} | nome={} {}",
                employees.getId(), employees.getFirstName(), employees.getLastName()); // Log detalhado

        if (employees == null) {
            logger.error("[EMP-EXPORTER-PDF-005] Funcionário nulo recebido para exportação"); // Log de erro
            throw new IllegalArgumentException("Funcionário não pode ser nulo"); // Exceção
        }

        long startTime = System.currentTimeMillis(); // Inicia contagem

        // Cria lista mock de compras para teste
        logger.debug("[EMP-EXPORTER-PDF-005] Criando dados mock de compras"); // Log de criação
        List<PurchaseMock> items = List.of( // Lista de compras mock
                new PurchaseMock("Mouse Logitech MX Master 3", 2, 450.00),
                new PurchaseMock("Teclado Mecânico Redragon", 1, 380.00),
                new PurchaseMock("Monitor LG 27\" IPS", 2, 1200.00),
                new PurchaseMock("Headset HyperX Cloud II", 1, 520.00),
                new PurchaseMock("Notebook Dell Inspiron 15", 1, 4200.00),
                new PurchaseMock("SSD NVMe 1TB Kingston", 3, 650.00),
                new PurchaseMock("Memória RAM 16GB DDR4", 4, 320.00),
                new PurchaseMock("Webcam Logitech C920", 2, 480.00),
                new PurchaseMock("Cadeira Gamer ThunderX3", 1, 980.00),
                new PurchaseMock("Mesa Escritório 1.60m", 1, 750.00),
                new PurchaseMock("Switch 24 Portas TP-Link", 1, 890.00),
                new PurchaseMock("Roteador Wi-Fi 6", 2, 670.00),
                new PurchaseMock("Impressora Multifuncional HP", 1, 1100.00),
                new PurchaseMock("Cabo HDMI 2m", 5, 35.00),
                new PurchaseMock("Mousepad Extra Grande", 3, 90.00),
                new PurchaseMock("Estabilizador 1000VA", 2, 420.00),
                new PurchaseMock("HD Externo 2TB Seagate", 2, 540.00),
                new PurchaseMock("Dock Station USB-C", 2, 390.00),
                new PurchaseMock("Teclado Numérico USB", 2, 120.00),
                new PurchaseMock("Kit Ferramentas Manutenção TI", 1, 260.00)
        );
        logger.debug("[EMP-EXPORTER-PDF-005] Dados mock criados | totalItens={}", items.size()); // Log dos itens mock

        employees.setItems(items); // Adiciona itens ao funcionário
        logger.debug("[EMP-EXPORTER-PDF-005] Itens adicionados ao funcionário"); // Log da adição

        // Template principal (employee)
        String mainTemplatePath = "/templates/employee.jrxml"; // Caminho do template principal
        logger.debug("[EMP-EXPORTER-PDF-005] Buscando template principal: {}", mainTemplatePath); // Log da busca

        InputStream mainTemplateStream = getClass().getResourceAsStream(mainTemplatePath); // template pai (dados do funcionário)
        if (mainTemplateStream == null) {
            logger.error("[EMP-EXPORTER-PDF-005] Template principal não encontrado | path={}", mainTemplatePath); // Log de erro
            throw new RuntimeException("Template file not found: " + mainTemplatePath); // Exceção
        }
        logger.debug("[EMP-EXPORTER-PDF-005] Template principal carregado"); // Log de sucesso

        // Template secundario (venda)
        String subTemplatePath = "/templates/venda.jrxml"; // Caminho do template filho
        logger.debug("[EMP-EXPORTER-PDF-005] Buscando template secundário: {}", subTemplatePath); // Log da busca

        InputStream subReportSream = getClass().getResourceAsStream(subTemplatePath); // template filho (venda)
        if (subReportSream == null) {
            logger.error("[EMP-EXPORTER-PDF-005] Template secundário não encontrado | path={}", subTemplatePath); // Log de erro
            throw new RuntimeException("Template file not found: " + subTemplatePath); // Exceção
        }
        logger.debug("[EMP-EXPORTER-PDF-005] Template secundário carregado"); // Log de sucesso

        // Obtém relatórios compilados do cache
        logger.debug("[EMP-EXPORTER-PDF-005] Obtendo relatórios do cache"); // Log de obtenção
        JasperReport mainReport = jasperReportCache.getReport(mainTemplatePath); // Relatório principal
        JasperReport subReport = jasperReportCache.getReport(subTemplatePath); // Relatório secundário
        logger.debug("[EMP-EXPORTER-PDF-005] Relatórios obtidos | main={} | sub={}",
                mainReport.getName(), subReport.getName()); // Log dos relatórios

        // Gera QR Code
        logger.debug("[EMP-EXPORTER-PDF-005] Gerando QR Code | conteúdo={}", employees.getQrCode()); // Log do QR Code
        InputStream qrCodeStream = qrCodeService.generateQRCODE(employees.getQrCode(), 200, 200); // Gera QR Code
        logger.debug("[EMP-EXPORTER-PDF-005] QR Code gerado | tamanho={} bytes", qrCodeStream.available()); // Log do QR Code

        // Parâmetros do relatório
        Map<String, Object> parameters = new HashMap<>(); // Mapa de parâmetros
        parameters.put("SUB_REPORT_DATA_SOURCE", subReport); // injeta sub-relatório compilado no parâmetro esperado pelo template
        parameters.put("QR_CODEIMAGE", qrCodeStream); // InputStream da imagem QR Code
        logger.debug("[EMP-EXPORTER-PDF-005] Parâmetros preparados | SUB_REPORT_DATA_SOURCE, QR_CODEIMAGE"); // Log dos parâmetros

        // DataSource principal (um único funcionário)
        JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(Collections.singletonList(employees)); // wrap single object em lista
        logger.debug("[EMP-EXPORTER-PDF-005] DataSource principal criado"); // Log do datasource

        // Preenche relatório principal
        logger.debug("[EMP-EXPORTER-PDF-005] Preenchendo relatório principal..."); // Log de início
        JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainDataSource); // preenche relatório principal
        logger.debug("[EMP-EXPORTER-PDF-005] Relatório preenchido | páginas={}", jasperPrint.getPages().size()); // Log de conclusão

        // Exporta para PDF em memória
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) { // Try-with-resources
            logger.debug("[EMP-EXPORTER-PDF-005] Exportando para PDF..."); // Log de início da exportação

            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream); // exporta para PDF em memória

            byte[] bytes = outputStream.toByteArray(); // Converte para array de bytes
            logger.debug("[EMP-EXPORTER-PDF-005] PDF gerado | tamanho={} bytes", bytes.length); // Log do tamanho

            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.info("[EMP-EXPORTER-PDF-005] Exportação PDF de funcionário único concluída | id={} | itens={} | páginas={} | tamanho={} bytes | tempo={}ms",
                    employees.getId(), items.size(), jasperPrint.getPages().size(), bytes.length, duration); // Log de sucesso

            return new ByteArrayResource(bytes); // retorna como Resource para controller
        } catch (Exception e) {
            logger.error("[EMP-EXPORTER-PDF-005] Erro ao exportar PDF do funcionário | id={} | erro={}",
                    employees.getId(), e.getMessage(), e); // Log de erro
            throw e; // Relança exceção
        }
    }
}