# To-Do List - por João Paulo Teixeira de Siles

Aplicação completa de gerenciamento de tarefas com backend em Java/Spring Boot e frontend em Vue.js/Quasar.

## Visão Geral

Sistema de to-do list com autenticação de usuários, CRUD de tarefas, envio de e-mails de boas-vindas e documentação automatizada via Swagger.

## Estrutura do Projeto

```
todolist/
├── backend/          # API REST em Spring Boot
├── frontend/         # Interface web em Quasar/Vue.js
└── docker-compose.yml
```

---

## Tecnologias Utilizadas

### Backend

| Tecnologia | Versão | Justificativa |
|------------|--------|---------------|
| **Java** | 21 | Versão LTS mais recente, com melhorias de performance e recursos modernos como virtual threads. |
| **Spring Boot** | 3.5.8 | Framework robusto para desenvolvimento rápido de APIs REST. Simplifica configuração e oferece ecossistema maduro. |
| **Spring Data JPA** | - | Abstração de persistência que reduz código boilerplate e facilita operações com banco de dados. |
| **Spring Security** | - | Solução completa para autenticação e autorização. Utiliza Basic Auth para proteger endpoints de tarefas. |
| **PostgreSQL** | 16+ | Banco de dados relacional robusto, open-source, com excelente suporte a transações e escalabilidade. |
| **RabbitMQ** | - | Message broker para processamento assíncrono. Usado para envio de e-mails de boas-vindas sem bloquear a requisição. |
| **Spring Mail** | - | Integração nativa para envio de e-mails via SMTP. |
| **Swagger/OpenAPI** | 2.7.0 | Documentação interativa da API. Permite testar endpoints diretamente pelo navegador. |

### Frontend

| Tecnologia | Justificativa |
|------------|---------------|
| **Vue.js 3** | Framework reativo com curva de aprendizado suave e excelente performance. |
| **Quasar Framework** | Framework UI completo baseado em Vue.js. Oferece componentes prontos e design responsivo. |
| **TypeScript** | Tipagem estática que aumenta a segurança e produtividade no desenvolvimento. |

### Infraestrutura

| Tecnologia | Justificativa |
|------------|---------------|
| **Docker** | Containerização para ambiente consistente entre desenvolvimento e produção. |
| **Docker Compose** | Orquestração de múltiplos containers (backend, RabbitMQ, frontend). |

---

## Pré-requisitos

- Docker e Docker Compose
- Node.js 18+ (para desenvolvimento frontend)
- Java 21+ (para desenvolvimento backend)

## Execução

### Com Docker (recomendado)

```bash
# Subir todos os serviços
docker-compose up -d --build

# Backend disponível em: http://localhost:8080
# Frontend disponível em: http://localhost:3000
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

### Desenvolvimento Local

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

---

## Endpoints Principais

### Usuários
- `POST /users/` - Cadastro de usuário
- `POST /users/login` - Login (retorna credenciais para Basic Auth)

### Tarefas (requer autenticação)
- `POST /tasks/` - Criar tarefa
- `GET /tasks/` - Listar tarefas do usuário
- `GET /tasks/{id}` - Detalhar tarefa
- `PUT /tasks/{id}` - Atualizar tarefa
- `DELETE /tasks/{id}` - Excluir tarefa

---

## Documentação da API

Acesse a documentação interativa em:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Variáveis de Ambiente

Configure o arquivo `.env` na raiz do projeto:

```env
# Banco de dados
PG-DB-URL=jdbc:postgresql://host:porta/database
PG-DB-USER=usuario
PG-DB-PASSWORD=senha

# E-mail (SMTP)
MAIL_HOST=smtp.exemplo.com
MAIL_PORT=587
MAIL_USERNAME=email@exemplo.com
MAIL_PASSWORD=senha
```

---