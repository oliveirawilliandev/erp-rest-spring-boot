package br.com.willian.service; // Pacote da camada de serviço

import jakarta.annotation.PostConstruct; // Importa anotação usada para executar métodos após o Spring inicializar o bean
import net.sf.jasperreports.engine.JRException; // Exceção padrão do JasperReports
import net.sf.jasperreports.engine.JasperCompileManager; // Classe responsável por compilar arquivos .jrxml em objetos JasperReport
import net.sf.jasperreports.engine.JasperReport; // Representa um relatório já compilado e pronto para ser usado
import net.sf.jasperreports.engine.util.JRLoader;
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

        logger.info("[JASPER-SRV] Solicitação de relatório | path={}", path);

        // Sempre trabalhar com .jasper como chave única
        String jasperPath = path.replace(".jrxml", ".jasper");
        String cacheKey = jasperPath;

        // 🔹 Verifica cache primeiro
        JasperReport report = cache.get(cacheKey);
        if (report != null) {
            logger.info("[JASPER-SRV] CACHE HIT | {}", cacheKey);
            return report;
        }

        logger.info("[JASPER-SRV] CACHE MISS | {}", cacheKey);

        // 🔹 Tenta carregar .jasper pré-compilado
        try (InputStream jasperStream = getClass().getResourceAsStream(jasperPath)) {

            if (jasperStream != null) {
                logger.info("[JASPER-SRV] Carregando .jasper pré-compilado | {}", jasperPath);

                long start = System.currentTimeMillis();

                report = (JasperReport) JRLoader.loadObject(jasperStream);

                long time = System.currentTimeMillis() - start;

                cache.put(cacheKey, report);

                logger.info("[JASPER-SRV] .jasper carregado e cacheado | tempo={}ms | cacheSize={}",
                        time, cache.size());

                return report;
            }

        } catch (Exception e) {
            logger.warn("[JASPER-SRV] Erro ao carregar .jasper | {}", e.getMessage());
        }

        // 🔴 Fallback: compilar .jrxml (somente se não existir .jasper)
        logger.warn("[JASPER-SRV] .jasper não encontrado, compilando .jrxml | {}", path);

        try (InputStream jrxmlStream = getClass().getResourceAsStream(path)) {

            if (jrxmlStream == null) {
                logger.error("[JASPER-SRV] Template não encontrado | {}", path);
                throw new RuntimeException("Template not found: " + path);
            }

            long start = System.currentTimeMillis();

            report = JasperCompileManager.compileReport(jrxmlStream);

            long time = System.currentTimeMillis() - start;

            // 🔹 salva no mesmo cacheKey (.jasper)
            cache.put(cacheKey, report);

            logger.info("[JASPER-SRV] .jrxml compilado e cacheado | tempo={}ms | cacheSize={}",
                    time, cache.size());

            return report;

        } catch (JRException e) {
            logger.error("[JASPER-SRV] Erro ao compilar relatório | {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("[JASPER-SRV] Erro inesperado | {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar relatório: " + path, e);
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