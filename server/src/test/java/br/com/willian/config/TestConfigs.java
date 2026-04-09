package br.com.willian.config;

/**
 * Interface de constantes utilizadas nos testes da aplicação.
 *
 * Centraliza valores fixos para:
 * - Configuração de porta do servidor de testes
 * - Cabeçalhos HTTP comuns
 * - Origens (CORS) utilizadas nos cenários de teste
 *
 * Observação:
 * - Em interfaces, todas as constantes são implicitamente
 *   public static final.
 * - Esta interface não deve conter lógica, apenas constantes.
 */
public interface TestConfigs {

    //Porta padrão utilizada pelo servidor durante a execução dos testes.
    int SERVER_PORT = 8888;

     //Nome do header HTTP utilizado para autenticação (ex: Bearer Token).
    String HEADER_PARAM_AUTHORIZATION = "Authorization";


     // Nome do header HTTP utilizado para controle de origem (CORS).
     String HEADER_PARAM_ORIGIN = "Origin";


    // Origem permitida para testes simulando o domínio Erudio.
    String ORIGIN_ERUDIO = "https://erudio.com.br";

    //  Origem permitida para testes oliveirawillianDev o domínio oliveirawillianDev.
    String ORIGIN_OLIVEIRAWILLIANDEV = "https://oliveirawilliandev.com.br";


    // Origem local utilizada em testes executados em ambiente de desenvolvimento.
    String ORIGIN_LOCAL = "http://localhost:8080/";
}
