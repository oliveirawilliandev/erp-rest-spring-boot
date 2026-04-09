// [API-001] Importa a biblioteca axios para fazer requisições HTTP
import axios from 'axios';

// [API-002] Cria uma instância configurada do axios
const api = axios.create({
     baseURL: 'http://localhost', // URL base do backend (servidor local)
});

// [API-003] Exporta a instância configurada para ser usada em outros arquivos
export default api;