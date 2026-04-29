package br.com.willian.service; // Pacote da camada de serviço

import jakarta.annotation.PostConstruct; // Importa anotação usada para executar métodos após o Spring inicializar o bean
import net.sf.jasperreports.engine.JRException; // Exceção padrão do JasperReports
import net.sf.jasperreports.engine.JasperCompileManager; // Classe responsável por compilar arquivos .jrxml em objetos JasperReport
import net.sf.jasperreports.engine.JasperReport; // Representa um relatório já compilado e pronto para ser usado
import org.springframework.stereotype.Service; // Anotação que registra esta classe como um Service gerenciado pelo Spring
import java.io.InputStream; // Stream para leitura de arquivos
import java.util.Map; // Interface Map usada para armazenar os relatórios compilados em memória
import java.util.concurrent.ConcurrentHashMap; // Implementação thread-safe de Map (segura para múltiplas requisições simultâneas)
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers

@Service // Define a classe como um serviço Spring
public class JasperReportCache {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(JasperReportCache.class); // Logger para rastreamento

    // Cache em memória que guarda relatórios já compilados
    // Key   -> caminho do arquivo JRXML
    // Value -> objeto JasperReport compilado
    //
    // ConcurrentHashMap garante segurança em ambientes multi-thread
    // (ex: várias requisições HTTP acessando ao mesmo tempo)
    private final Map<String, JasperReport> cache = new ConcurrentHashMap<>();

    // [SERVICE-TRACE: JASPER-SRV-001]
    // Método executado após a construção do bean para verificar o cache
    @PostConstruct // Executa após a injeção de dependências
    public void init() {
        logger.info("[JASPER-SRV-001] JasperReportCache inicializado | cacheSize={}", cache.size()); // Log de inicialização
        logger.debug("[JASPER-SRV-001] Cache configurado como ConcurrentHashMap | thread-safe=true"); // Log de configuração
    }

    // [SERVICE-TRACE: JASPER-SRV-002]
    // Método responsável por retornar um relatório compilado
    // Se ele já estiver no cache, reutiliza
    // Se não existir, compila e armazena
    public JasperReport getReport(String path) throws JRException {

        logger.info("[JASPER-SRV-002] Busca por relatório solicitada | path={}", path); // Log da solicitação

        // Verifica se o relatório já foi compilado anteriormente
        // Isso evita recompilar o JRXML a cada requisição
        JasperReport report = cache.get(path);
        if (report != null) {
            logger.debug("[JASPER-SRV-002] Relatório encontrado no cache | path={}", path); // Log de cache hit
            logger.info("[JASPER-SRV-002] Retornando relatório do cache | path={}", path); // Log de retorno
            return report; // Retorna relatório do cache
        }

        logger.debug("[JASPER-SRV-002] Relatório não encontrado no cache | path={}", path); // Log de cache miss
        logger.info("[JASPER-SRV-002] Iniciando compilação do relatório | path={}", path); // Log de início de compilação

        // Carrega o arquivo JRXML a partir do classpath da aplicação
        // Exemplo de path: "/templates/employee.jrxml"
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            logger.error("[JASPER-SRV-002] Template não encontrado | path={}", path); // Log de erro
            // Caso o arquivo não seja encontrado, lança exceção
            // Isso evita NullPointerException mais à frente
            throw new RuntimeException("Template not found: " + path); // Exceção
        }

        logger.debug("[JASPER-SRV-002] Template carregado com sucesso | path={} | stream={}", path, stream); // Log de carregamento

        try {
            // Compila o arquivo JRXML em um objeto JasperReport
            // Esse processo é pesado e por isso não deve acontecer toda requisição
            long startTime = System.currentTimeMillis(); // Inicia contagem
            report = JasperCompileManager.compileReport(stream); // Compila o relatório
            long endTime = System.currentTimeMillis(); // Finaliza contagem
            long duration = endTime - startTime; // Calcula duração

            logger.debug("[JASPER-SRV-002] Compilação concluída | path={} | tempoCompilacao={}ms", path, duration); // Log de compilação

            // Armazena o relatório compilado no cache
            // Assim nas próximas chamadas ele será reutilizado
            cache.put(path, report);
            logger.debug("[JASPER-SRV-002] Relatório armazenado no cache | path={} | cacheSize={}", path, cache.size()); // Log de armazenamento

            logger.info("[JASPER-SRV-002] Relatório compilado e cacheado com sucesso | path={} | tempoCompilacao={}ms",
                    path, duration); // Log de sucesso com tempo

            // Retorna o relatório compilado para uso na geração do PDF
            return report; // Retorna relatório compilado

        } catch (JRException e) {
            logger.error("[JASPER-SRV-002] Erro na compilação do relatório | path={} | erro={}",
                    path, e.getMessage(), e); // Log de erro detalhado
            throw e; // Relança exceção
        } catch (Exception e) {
            logger.error("[JASPER-SRV-002] Erro inesperado | path={} | erro={}",
                    path, e.getMessage(), e); // Log de erro inesperado
            throw new RuntimeException("Erro ao processar relatório: " + path, e); // Exceção
        }
    }

    // [SERVICE-TRACE: JASPER-SRV-003]
    // Método para limpar o cache (útil para recarregar relatórios após alterações)
    public void clearCache() {
        int tamanhoAntes = cache.size(); // Tamanho antes da limpeza
        logger.info("[JASPER-SRV-003] Limpeza de cache solicitada | tamanhoAntes={}", tamanhoAntes); // Log de limpeza

        cache.clear(); // Limpa o cache

        logger.info("[JASPER-SRV-003] Cache limpo com sucesso | tamanhoDepois={}", cache.size()); // Log de conclusão
    }

    // [SERVICE-TRACE: JASPER-SRV-004]
    // Método para obter o tamanho atual do cache
    public int getCacheSize() {
        int tamanho = cache.size(); // Obtém tamanho
        logger.debug("[JASPER-SRV-004] Tamanho do cache consultado | cacheSize={}", tamanho); // Log de consulta
        return tamanho; // Retorna tamanho
    }
}