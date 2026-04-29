package br.com.willian.service; // Pacote da camada de serviço

import com.google.zxing.BarcodeFormat; // Define o formato do código (QR Code)
import com.google.zxing.WriterException; // Exceção para erros de escrita do código
import com.google.zxing.client.j2se.MatrixToImageWriter; // Converte BitMatrix em imagem
import com.google.zxing.common.BitMatrix; // Representação binária do QR Code
import com.google.zxing.qrcode.QRCodeWriter; // Responsável por gerar o QR Code
import org.slf4j.Logger; // Interface de logging SLF4J
import org.slf4j.LoggerFactory; // Factory para criação de loggers
import org.springframework.stereotype.Service; // Marca a classe como Service do Spring

import java.io.ByteArrayInputStream; // Converte byte[] em InputStream
import java.io.ByteArrayOutputStream; // Armazena a imagem em memória
import java.io.InputStream; // Retorno padrão para streams

@Service // Serviço gerenciado pelo Spring
public class QRCodeService {

    // Cria um logger estático para esta classe com SLF4J
    private static final Logger logger = LoggerFactory.getLogger(QRCodeService.class); // Logger para rastreamento

    // [SERVICE-TRACE: QR-SRV-001]
    // Gera um QR Code a partir de uma URL
    public InputStream generateQRCODE(String url, int width, int height) throws Exception {

        logger.info("[QR-SRV-001] Geração de QR Code solicitada | url={} | width={} | height={}",
                url, width, height); // Log da solicitação com parâmetros

        // Validações básicas
        if (url == null || url.trim().isEmpty()) {
            logger.error("[QR-SRV-001] URL inválida ou vazia | url={}", url); // Log de erro
            throw new IllegalArgumentException("URL não pode ser nula ou vazia"); // Exceção
        }

        if (width <= 0 || height <= 0) {
            logger.error("[QR-SRV-001] Dimensões inválidas | width={} | height={}", width, height); // Log de erro
            throw new IllegalArgumentException("Largura e altura devem ser maiores que zero"); // Exceção
        }

        logger.debug("[QR-SRV-001] Parâmetros validados com sucesso"); // Log de validação


        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter(); // Instancia o gerador de QR Code
            logger.debug("[QR-SRV-001] QRCodeWriter instanciado"); // Log de instanciação

            logger.debug("[QR-SRV-001] Codificando conteúdo | url={} | formato={} | width={} | height={}",
                    url, BarcodeFormat.QR_CODE, width, height); // Log de codificação

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    url,                     // Conteúdo do QR Code
                    BarcodeFormat.QR_CODE,   // Tipo do código
                    width,                   // Largura da imagem
                    height                   // Altura da imagem
            );

            logger.debug("[QR-SRV-001] BitMatrix gerado com sucesso | dimensões={}x{}",
                    bitMatrix.getWidth(), bitMatrix.getHeight()); // Log da matriz gerada

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // Buffer em memória
            logger.debug("[QR-SRV-001] ByteArrayOutputStream criado"); // Log do buffer

            MatrixToImageWriter.writeToStream(
                    bitMatrix,               // Matriz do QR Code
                    "PNG",                   // Formato da imagem
                    outputStream             // Stream de saída
            );

            logger.debug("[QR-SRV-001] Imagem PNG gerada | tamanho={} bytes", outputStream.size()); // Log do tamanho

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); // Converte para InputStream
            logger.debug("[QR-SRV-001] InputStream criado a partir da imagem"); // Log da conversão


            logger.info("[QR-SRV-001] QR Code gerado com sucesso | url={} | width={} | height={} | tamanho={} bytes ",
                    url, width, height, outputStream.size() ); // Log de sucesso com métricas

            return inputStream; // Retorna como InputStream

        } catch (WriterException e) {
            logger.error("[QR-SRV-001] Erro ao gerar QR Code (WriterException) | url={} | width={} | height={} | erro={}",
                    url, width, height, e.getMessage(), e); // Log de erro específico do ZXing
            throw new Exception("Falha ao gerar QR Code para URL: " + url, e); // Relança com mensagem mais clara
        } catch (IllegalArgumentException e) {
            logger.error("[QR-SRV-001] Parâmetros inválidos para geração do QR Code | url={} | width={} | height={} | erro={}",
                    url, width, height, e.getMessage(), e); // Log de erro de validação
            throw e; // Relança exceção
        } catch (Exception e) {
            logger.error("[QR-SRV-001] Erro inesperado ao gerar QR Code | url={} | width={} | height={} | erro={}",
                    url, width, height, e.getMessage(), e); // Log de erro inesperado
            throw new Exception("Erro inesperado ao gerar QR Code: " + e.getMessage(), e); // Relança
        }
    }


}