// [NEWBOOK-001] Importa React e hooks useState, useEffect
import React, { useState, useEffect } from 'react';

// [NEWBOOK-002] Importa hooks de navegação e parâmetros da URL
import { useNavigate, Link, useParams } from 'react-router-dom';

// [NEWBOOK-003] Importa ícone de seta para esquerda (voltar)
import { FiArrowLeft } from 'react-icons/fi';

// [NEWBOOK-004] Importa instância do axios configurada
import api from '../../services/api';

// [NEWBOOK-005] Importa estilos específicos do componente
import './styles.css';

// [NEWBOOK-006] Importa logo da aplicação
import logoImage from '../../assets/logo.svg';

// [NEWBOOK-007] Componente de criação/edição de livros
export default function NewBook() {

    // [NEWBOOK-008] Estado para armazenar ID do livro (edição)
    const [id, setId] = useState(null);
    
    // [NEWBOOK-009] Estado para armazenar autor
    const [author, setAuthor] = useState('');
    
    // [NEWBOOK-010] Estado para armazenar data de lançamento
    const [launchDate, setLaunchDate] = useState('');
    
    // [NEWBOOK-011] Estado para armazenar preço
    const [price, setPrice] = useState('');
    
    // [NEWBOOK-012] Estado para armazenar título
    const [title, setTitle] = useState('');
    
    // [NEWBOOK-013] Recupera nome do usuário do localStorage
    const userName = localStorage.getItem('userName');
    
    // [NEWBOOK-014] Recupera token de acesso do localStorage
    const accessToken = localStorage.getItem('accessToken');
    
    // [NEWBOOK-015] Hook para navegação programática
    const navigate = useNavigate();
    
    // [NEWBOOK-016] Hook para capturar parâmetro bookId da URL
    const { bookId } = useParams();

    // [NEWBOOK-017] Função para carregar dados do livro na edição
    async function loadBook() {
        try {
            // [NEWBOOK-018] Requisição GET para buscar livro por ID
            const response = await api.get(`api/books/v1/${bookId}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}` // Token JWT
                }
            });
            
            // [NEWBOOK-019] Preenche estados com dados do livro
            setId(response.data.id); // ID do livro
            setTitle(response.data.title); // Título
            setAuthor(response.data.author); // Autor
            setPrice(response.data.price); // Preço
            
            // [NEWBOOK-020] Ajusta data (remove hora, mantém apenas YYYY-MM-DD)
            const adjustedDate = response.data.launchDate.split("T", 10)[0];
            setLaunchDate(adjustedDate); // Data formatada para input date
            
        } catch (error) {
            alert("Error recovering Book! Try again!"); // Alerta de erro
            navigate('/books'); // Redireciona para lista
        }
    }

    // [NEWBOOK-021] useEffect executado na montagem e quando bookId muda
    useEffect(() => {
        // [NEWBOOK-022] Se não tem token, redireciona para login
        if (!accessToken) {
            navigate('/');
        }
        
        // [NEWBOOK-023] Se bookId for '0' (novo livro), não carrega dados
        if (bookId === '0') return; 
        
        // [NEWBOOK-024] Caso contrário, carrega livro para edição
        loadBook();
    }, [bookId]); // Dependência: bookId

    /* [NEWBOOK-025] Função de conversão de data para UTC (comentada - não utilizada)
    function convertToUTC(dateString) {
        if (!dateString) return dateString;
        
        const [year, month, day] = dateString.split('-');
        
        // Usa Date do JavaScript que já lida com datas inválidas
        const date = new Date(year, month - 1, parseInt(day) + 1);
        
        // Formata corretamente
        const fixedYear = date.getFullYear();
        const fixedMonth = String(date.getMonth() + 1).padStart(2, '0');
        const fixedDay = String(date.getDate()).padStart(2, '0');
        
        return `${fixedYear}-${fixedMonth}-${fixedDay}`;
    }
    */

    // [NEWBOOK-026] Função para salvar (criar) ou atualizar livro
    async function saveOrUpdate(e) {
        e.preventDefault(); // Previne comportamento padrão do formulário
        
        // [NEWBOOK-027] Dados do livro (conversão UTC comentada)
        // const launchDateUTC = convertToUTC(launchDate)
        // const data = { author, launchDate: launchDateUTC, price, title };
        const data = { author, launchDate, price, title };
        
        try {
            // [NEWBOOK-028] Criação de novo livro (bookId === '0')
            if (bookId === '0') {
                await api.post('api/books/v1', data, {
                    headers: {
                        Authorization: `Bearer ${accessToken}` // Token JWT
                    }
                });
            } 
            // [NEWBOOK-029] Atualização de livro existente
            else {
                data.id = id; // Adiciona ID aos dados
                await api.put('api/books/v1', data, {
                    headers: {
                        Authorization: `Bearer ${accessToken}` // Token JWT
                    }
                });
            }
            
            // [NEWBOOK-030] Redireciona para lista de livros após sucesso
            navigate('/books');
            
        } catch (err) {
            console.error(err.response || err); // Log do erro
            alert("Error while recording Book! Try again!"); // Alerta de erro
        }
    }

    // [NEWBOOK-031] Renderiza o formulário de criação/edição
    return (
        <div className="new-book-container">
            <div className="content">
                {/* [NEWBOOK-032] Seção lateral com informações */}
                <section className="form">
                    <img src={logoImage} alt="Erudio" />
                    
                    {/* [NEWBOOK-033] Título dinâmico (Add New ou Update) */}
                    <h1>{bookId === '0' ? 'Add New' : 'Update'}</h1>
                    
                    {/* [NEWBOOK-034] Descrição dinâmica */}
                    <p>Enter the book information and click on {bookId === '0' ? "'Add'" : "'Update'"} !</p>
                    
                    {/* [NEWBOOK-035] Link para voltar à lista */}
                    <Link className="back-link" to="/books">
                        <FiArrowLeft size={16} color="#241fc5" />
                        Back to Book
                    </Link>
                </section>
                
                {/* [NEWBOOK-036] Formulário principal */}
                <form onSubmit={saveOrUpdate}>
                    {/* [NEWBOOK-037] Campo de título */}
                    <input 
                        placeholder="Title" 
                        value={title} 
                        onChange={e => setTitle(e.target.value)} 
                    />
                    
                    {/* [NEWBOOK-038] Campo de autor */}
                    <input 
                        placeholder="Author" 
                        value={author} 
                        onChange={e => setAuthor(e.target.value)} 
                    />
                    
                    {/* [NEWBOOK-039] Campo de data (type date) */}
                    <input 
                        type="date" 
                        value={launchDate} 
                        onChange={e => setLaunchDate(e.target.value)} 
                    />
                    
                    {/* [NEWBOOK-040] Campo de preço */}
                    <input 
                        placeholder="Price" 
                        value={price} 
                        onChange={e => setPrice(e.target.value)} 
                    />

                    {/* [NEWBOOK-041] Botão de submit (Add ou Update) */}
                    <button className='button' type='submit'>
                        {bookId === '0' ? 'Add' : 'Update'}
                    </button>
                </form>
            </div>
        </div>
    );
}