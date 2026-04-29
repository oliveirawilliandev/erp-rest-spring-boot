// pages/About/index.js
import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
    FiArrowLeft, FiMail, FiGithub, FiLinkedin, FiUser, FiCode, 
    FiBriefcase, FiHeart, FiBookOpen, FiCalendar, 
    FiMapPin, FiServer, FiCloud, FiTrendingUp, FiPower,
    FiAward, FiCheckCircle
} from 'react-icons/fi';
import { FaGraduationCap } from 'react-icons/fa';

import './styles.css';
import logoImage from '../../assets/logoerp.png';

export default function About() {
    const currentYear = new Date().getFullYear();
    const birthYear = 1997;
    const age = currentYear - birthYear;
    const navigate = useNavigate();
    const fullName = localStorage.getItem('fullName');

      // Função para pegar apenas o primeiro nome
function getFirstName(fullName) {
    if (!fullName) return '';
    // Divide a string em partes usando espaço como separador
    const nameParts = fullName.trim().split(' ');
    // Pega a primeira parte
    return nameParts[0];
}

// Função que limita o tamanho do texto
function limitTextLength(text, maxLength = 10) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Combinando as duas estratégias
function getDisplayName(fullName) {
    if (!fullName) return '';
    
    // Primeiro: pega o primeiro nome
    let firstName = fullName.trim().split(' ')[0];
    
    // Segundo: limita o tamanho
    const MAX_LENGTH = 12;
    if (firstName.length > MAX_LENGTH) {
        firstName = firstName.substring(0, MAX_LENGTH) + '...';
    }
    
    return firstName;
}

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token || token === 'undefined' || token === 'null') {
            navigate('/');
        }
            window.scrollTo(0, 0); 

    }, [navigate]);

    function logout() {
        localStorage.clear();
        navigate('/');
    }

    const developerInfo = {
        name: "Willian Ferreira de Oliveira",
        age: age,
        location: "Blumenau, Santa Catarina - Brasil",
        role: "Back-End Developer Java",
        rolePt: "Desenvolvedor Back-End Java",
        email: "oliveira.willian.dev@gmail.com",
        github: "https://github.com/oliveirawilliandev",
        linkedin: "https://www.linkedin.com/in/oliveirawilliandev/",
        bio: "Passionate about technology and creating efficient solutions for businesses. Software Engineering student with solid experience in Java and Spring Boot.",
        bioPt: "Apaixonado por tecnologia e por criar soluções eficientes para empresas. Estudante de Engenharia de Software com sólida experiência em Java e Spring Boot.",
    };

    const academicInfo = {
        degree: "Analysis and Systems Development",
        degreePt: "Análise e Desenvolvimento de Sistemas",
        currentStudy: "Software Engineering",
        currentStudyPt: "Engenharia de Software (Cursando)",
        institution: "UniCesumar - Universidade de Blumenau",
    };

    const disciplines = [
        { semester: "1ª Série", courses: [ "ENGENHARIA DE SOFTWARE" ]},
        { semester: "2ª Série", courses: [ "Algoritmos e Lógica de Programação", "Linguagem e Técnicas de Programação" ]},
        { semester: "3ª Série", courses: [ "Fundamentos e Arquitetura de Computadores", "Redes de Computadores" ]},
        { semester: "4ª Série", courses: [ "GO - Projeto de Vida", "Matemática Aplicada à Computação" ]},
        { semester: "5ª Série", courses: [ "Banco de Dados", "Sistemas Operacionais" ]},
        { semester: "6ª Série", courses: [ "Gestão de Projetos Tecnológicos", "Programação Orientada a Objetos" ]},
        { semester: "7ª Série", courses: [ "Análise e Projeto Orientado a Objetos" ]},
        { semester: "8ª Série", courses: [ "Estruturas de Dados", "Programação Front End" ]},
        { semester: "9ª Série", courses: [ "Estruturas, Pesquisa e Ordenação de Dados", "Imersão Profissional: Projeto de Interface", "Interface Humano-Computador" ]},
        { semester: "10ª Série", courses: [ "Programação Avançada", "Programação para Dispositivos Móveis" ]}
    ];

    const experiences = [
        {
            company: "Metalúrgica Weg",
            period: "2 anos",
            role: "Professional Experience",
            rolePt: "Experiência Profissional",
            description: "Industrial sector experience",
            descriptionPt: "Experiência no setor industrial"
        },
        {
            company: "Gráfica Carton Druck",
            period: "6 anos",
            role: "Professional Experience",
            rolePt: "Experiência Profissional",
            description: "Experience in the printing industry",
            descriptionPt: "Experiência no setor gráfico"
        }
    ];

    const backendTechnologies = [
        "Java 17+", "Spring Boot", "Spring Security", "Spring Data JPA",
        "Hibernate", "JWT", "JasperReports", "Swagger/OpenAPI",
        "JUnit 5", "Mockito", "RESTful API", "Maven"
    ];

    const devopsTechnologies = [
        "Docker", "PostgreSQL", "Git", "GitHub Actions"
    ];

    const courses = [
        {
            name: "Java COMPLETO - Programação Orientada a Objetos",
            instructor: "Prof. Nélio Alves",
            year: "2023",
            type: "online"
        },
        {
            name: "Spring Boot - Swagger, Docker, Kubernetes, JWT, JUnit, Mockito, React, AWS e GCP",
            instructor: "Erudio",
            year: "2024",
            type: "online"
        }
    ];

    // NOVOS CURSOS ADICIONADOS
    const professionalCourses = [
        {
            name: "Aprendizagem Industrial em Informática",
            institution: "SENAI/SC - Blumenau",
            year: "2014",
            type: "professional",
            icon: "💻",
            modules: [
                "Conceitos de Bancos de Dados",
                "Conceitos de Programação",
                "Desenvolvimento de Páginas de Internet",
                "Edição de Textos",
                "Editores de Apresentação",
                "Ética, cidadania e meio ambiente",
                "Fundamentos da matemática",
                "Fundamentos de comunicação oral e escrita",
                "Fundamentos de Internet",
                "Fundamentos de Sistemas Operacionais e Aplicativos",
                "Inglês Aplicado à Informática",
                "Introdução à Linguagem de Programação",
                "Introdução a Redes de Computadores",
                "Montagem e Manutenção de Computadores e Periféricos",
                "Organização e preparação para o trabalho",
                "Planilhas Eletrônicas",
                "Saúde e segurança do trabalho"
            ]
        },
        {
            name: "Desenvolvedor Back-end - Java",
            institution: "SENAI/SC em parceria com Entra21",
            year: "2024",
            type: "professional",
            icon: "☕",
            modules: [
                "Módulo Básico",
                "Módulo Complementar",
                "Módulo Específico"
            ]
        }
    ];

    return (
        <div className="about-page-container">
            <header>
                <img src={logoImage} alt="ERP Logo" />
                <span>Welcome, <strong>{getDisplayName(fullName).toUpperCase()} </strong></span>
                <button className="about-page-logout-btn" onClick={logout} type="button">
                    <FiPower size={18} color="#E67E22" />
                </button>
            </header>

            <div className="about-page-title-row">
                <Link to="/dashboard" className="about-page-back-button">
                    <FiArrowLeft size={24} />
                </Link>
                <h1>About Me</h1>
            </div>

            <div className="about-page-content">
                {/* Seção de Perfil */}
                <div className="about-page-profile-section">
                    <div className="about-page-profile-image">
                        <img 
                            src="https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/foto.png" 
                            alt="Willian Oliveira"
                        />
                    </div>
                    <h2 className="about-page-profile-name">{developerInfo.name}</h2>
                    <p className="about-page-age-location">
                        <FiCalendar size={14} />{developerInfo.age} anos &nbsp;|&nbsp;
                        <FiMapPin size={14} /> {developerInfo.location}
                    </p>
                    <p className="about-page-role">
                        {developerInfo.role} | {developerInfo.rolePt}
                    </p>
                    <div className="about-page-social-links">
                        <a href={`mailto:${developerInfo.email}`} className="about-page-social-link">
                            <FiMail size={20} /> Email
                        </a>
                        <a href={developerInfo.github} target="_blank" rel="noopener noreferrer" className="about-page-social-link">
                            <FiGithub size={20} /> GitHub
                        </a>
                        <a href={developerInfo.linkedin} target="_blank" rel="noopener noreferrer" className="about-page-social-link">
                            <FiLinkedin size={20} /> LinkedIn
                        </a>
                    </div>
                </div>

                {/* Grid de Informações Principais */}
                <div className="about-page-info-grid">
                    <div className="about-page-info-card">
                        <FaGraduationCap size={32} color="#E67E22" />
                        <h3>Academic | Formação</h3>
                        <p><strong>{academicInfo.degree}</strong><br />{academicInfo.degreePt}</p>
                        <p className="about-page-institution">{academicInfo.institution}</p>
                        <p><strong>{academicInfo.currentStudy}</strong><br />{academicInfo.currentStudyPt}</p>
                    </div>

                    <div className="about-page-info-card">
                        <FiBriefcase size={32} color="#2E8B57" />
                        <h3>Experience | Experiência</h3>
                        {experiences.map((exp, index) => (
                            <div key={index} className="about-page-experience-item">
                                <strong>{exp.company}</strong>
                                <span className="about-page-period">{exp.period}</span>
                                <p className="about-page-exp-description">{exp.descriptionPt}</p>
                            </div>
                        ))}
                    </div>

                    <div className="about-page-info-card">
                        <FiCode size={32} color="#FF8C00" />
                        <h3>Courses | Cursos</h3>
                        {courses.map((course, index) => (
                            <div key={index} className="about-page-course-item">
                                <strong>{course.name}</strong>
                                <p className="about-page-instructor">{course.instructor}</p>
                                <span className="about-page-year">{course.year}</span>
                            </div>
                        ))}
                    </div>

                    <div className="about-page-info-card">
                        <FiHeart size={32} color="#DC143C" />
                        <h3>ERP Oliveira</h3>
                        <p>
                            Complete ERP system for business management.<br />
                            Sistema ERP completo para gestão empresarial.
                        </p>
                        <p className="about-page-version">Version | Versão: 1.0.0</p>
                    </div>
                </div>

                {/* NOVA SEÇÃO: Cursos Profissionalizantes */}
                <div className="about-page-professional-courses-section">
                    <h3><FiAward size={24} /> Professional Courses | Cursos Profissionalizantes</h3>
                    <div className="about-page-professional-courses-grid">
                        {professionalCourses.map((course, index) => (
                            <div key={index} className="about-page-professional-course-card">
                                <div className="about-page-professional-course-header">
                                    <span className="about-page-course-icon">{course.icon}</span>
                                    <div className="about-page-professional-course-title">
                                        <h4>{course.name}</h4>
                                        <p className="about-page-professional-institution">
                                            {course.institution} | {course.year}
                                        </p>
                                    </div>
                                </div>
                                <div className="about-page-professional-course-modules">
                                    <p className="about-page-modules-title">
                                        <FiCheckCircle size={14} /> Modules | Módulos:
                                    </p>
                                    <div className="about-page-modules-grid">
                                        {course.modules.map((module, mIdx) => (
                                            <span key={mIdx} className="about-page-module-tag">
                                                {module}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Disciplinas Cursadas */}
                <div className="about-page-disciplines-section">
                    <h3><FiBookOpen size={24} /> Academic Disciplines | Disciplinas Cursadas | Analysis and Systems Development</h3>
                    <div className="about-page-disciplines-grid">
                        {disciplines.map((item, idx) => (
                            <div key={idx} className="about-page-discipline-card">
                                <h4>{item.semester}</h4>
                                <ul>
                                    {item.courses.map((course, cIdx) => (
                                        <li key={cIdx}>{course}</li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Tecnologias - Backend */}
                <div className="about-page-tech-section">
                    <h3><FiServer size={24} /> Backend Technologies | Tecnologias Backend</h3>
                    <div className="about-page-tech-stack">
                        {backendTechnologies.map((tech, index) => (
                            <span key={index} className="about-page-tech-tag about-page-tech-tag-backend">{tech}</span>
                        ))}
                    </div>
                </div>

                {/* Tecnologias - DevOps */}
                <div className="about-page-tech-section">
                    <h3><FiCloud size={24} /> DevOps & Infrastructure | DevOps e Infraestrutura</h3>
                    <div className="about-page-tech-stack">
                        {devopsTechnologies.map((tech, index) => (
                            <span key={index} className="about-page-tech-tag about-page-tech-tag-devops">{tech}</span>
                        ))}
                    </div>
                </div>

                {/* Sobre o Projeto */}
                <div className="about-page-project-info">
                    <FiTrendingUp size={24} />
                    <h3>About the Project | Sobre o Projeto</h3>
                    <p>
                        This ERP system was developed with a modern and scalable architecture,
                        using Java Spring Boot on the backend and React on the frontend.
                        It includes complete CRUD operations, JWT authentication, 
                        file import/export (CSV, XLSX, PDF), JasperReports integration,
                        and full HATEOAS support.
                        <br /><br />
                        Este sistema ERP foi desenvolvido com arquitetura moderna e escalável,
                        utilizando Java Spring Boot no backend e React no frontend.
                        Inclui operações CRUD completas, autenticação JWT,
                        importação/exportação de arquivos (CSV, XLSX, PDF), integração com JasperReports
                        e suporte completo a HATEOAS.
                    </p>
                </div>
            </div>
        </div>
    );
}