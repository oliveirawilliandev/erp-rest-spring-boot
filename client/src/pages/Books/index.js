// [BOOKS-001] Importa React e hooks (useState, useEffect, useCallback)
import React, { useState, useEffect, useCallback } from 'react';

// [BOOKS-002] Importa hooks de navegação e link do React Router
import { Link, useNavigate } from 'react-router-dom';

// [BOOKS-003] Importa ícones de poder, editar e deletar da biblioteca react-icons
import { FiPower, FiEdit, FiTrash2 } from 'react-icons/fi';

// [BOOKS-004] Importa instância do axios configurada
import api from '../../services/api';

// [BOOKS-005] Importa estilos específicos do componente
import './styles.css';

// [BOOKS-006] Importa logo da aplicação
import logoImage from '../../assets/logoerp.png';

// [BOOKS-007] Componente de listagem de livros
export default function Books() {

    // [BOOKS-008] Estado para armazenar lista de livros
    const [books, setBooks] = useState([]);
    
    // [BOOKS-009] Estado para controlar paginação (página atual)
    const [page, setPage] = useState(0);

    // [BOOKS-010] Recupera nome do usuário do localStorage
    const userName = localStorage.getItem('userName');
    
    // [BOOKS-011] Recupera token de acesso do localStorage
    const accessToken = localStorage.getItem('accessToken');
    
    // [BOOKS-012] Hook para navegação programática
    const navigate = useNavigate();

    // [BOOKS-013] Função para logout (limpa storage e redireciona)
    async function logout() {
        localStorage.clear(); // Remove todos os dados do localStorage
        navigate('/'); // Redireciona para tela de login
    }

    // [BOOKS-014] Função para editar livro (navega para formulário com ID)
    async function editBook(id) {
        try {
            navigate(`/books/new/${id}`); // Navega para página de edição
        } catch (error) {
            alert("Edit failed! Try again."); // Alerta de erro
        }
    }

    // [BOOKS-015] Função para deletar livro
    async function deleteBook(id) {
        try {
            // [BOOKS-016] Requisição DELETE com token no header
            await api.delete(`api/books/v1/${id}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}` // Token JWT
                }
            });
            // [BOOKS-017] Atualiza lista local (remove o livro deletado)
            setBooks(books.filter(book => book.id !== id));
        } catch (err) {
            alert("Delete failed! Try again."); // Alerta de erro
        }
    }

    // [BOOKS-018] Função memoizada para carregar mais livros (paginação)
    const fetchMoreBooks = useCallback(async () => {
        try {
            // [BOOKS-019] Requisição GET paginada
            const response = await api.get('/api/books/v1', {
                headers: {
                    Authorization: `Bearer ${accessToken}` // Token JWT
                },
                params: {
                    page: page, // Página atual
                    size: 4, // 4 itens por página
                    direction: 'asc' // Ordem ascendente
                }
            });
            
            // [BOOKS-020] Extrai lista de livros da resposta (com fallback para array vazio)
            const newBooks = response.data?._embedded?.books || [];
            
            // [BOOKS-021] Adiciona novos livros à lista existente
            setBooks([...books, ...newBooks]);
            
            // [BOOKS-022] Incrementa página para próxima requisição
            setPage(page + 1);
        } catch (err) {
            // [BOOKS-023] Se token expirou (401), faz logout automaticamente
            if (err.response?.status === 401) {
                logout();
            }
        }
    }, [accessToken, page, books]); // Dependências do useCallback

    // [BOOKS-024] useEffect executado na montagem do componente
    useEffect(() => {
        // [BOOKS-025] Se não tem token, redireciona para login
        if (!accessToken) {
            navigate('/');
            return;
        }
        // [BOOKS-026] Carrega primeira página de livros
        fetchMoreBooks();
    }, []); // Array vazio = executa apenas uma vez

    // [BOOKS-027] Renderiza a lista de livros
    return (
        <div className="book-container">
            {/* [BOOKS-028] Header com logo, boas-vindas e botões */}
            <header>
                <img src={logoImage} alt="Erudio" />
                <span>Welcome, <strong> {(userName || 'Convidado').toUpperCase()} </strong>!</span>
                <Link className="button" to="/books/new/0">Add New Book</Link>
                <button onClick={logout} type="button">
                    <FiPower size={18} color="#251FC5" />
                </button>
            </header>

            {/* [BOOKS-029] Título da seção */}
            <h1>Registered Books</h1>
            
            {/* [BOOKS-030] Lista de livros */}
            <ul>
                {books.map(book => (
                    <li key={book.id}>
                        {/* [BOOKS-031] Título do livro */}
                        <strong>Title: </strong>
                        <p>{book.title}</p>
                        
                        {/* [BOOKS-032] Autor do livro */}
                        <strong>Author: </strong>
                        <p>{book.author}</p>
                        
                        {/* [BOOKS-033] Preço formatado em moeda brasileira (R$) */}
                        <strong>Price: </strong>
                        <p>{Intl.NumberFormat('pt-BR', { 
                            style: 'currency', 
                            currency: 'BRL' 
                        }).format(book.price)}</p>
                        
                        {/* [BOOKS-034] Data de lançamento formatada (YYYY-MM-DD → DD/MM/YYYY) */}
                        <strong>Release Date: </strong>
                        <p>
                            {book.launchDate
                                ? book.launchDate.split('T')[0].split('-').reverse().join('/')
                                : ''}
                        </p>
                        
                        {/* [BOOKS-035] Botão de edição */}
                        <button onClick={() => editBook(book.id)} type='button'>
                            <FiEdit size={20} color='#251FC5' />
                        </button>

                        {/* [BOOKS-036] Botão de exclusão */}
                        <button onClick={() => deleteBook(book.id)} type='button'>
                            <FiTrash2 size={20} color='#251FC5' />
                        </button>
                    </li>
                ))}
            </ul>

            {/* [BOOKS-037] Botão para carregar mais livros (paginação infinita) */}
            <button className='button' onClick={fetchMoreBooks} type='button'>
                Load More
            </button>
        </div>
    );
}