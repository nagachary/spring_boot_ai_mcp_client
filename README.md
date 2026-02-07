# Spring Boot AI MCP Client

**[Java 21](https://www.oracle.com/java/)** | **[Spring Boot 3.4.2](https://spring.io/projects/spring-boot)** | **[Spring AI](https://spring.io/projects/spring-ai)** | **[Docker](https://www.docker.com/)** | **[MIT License](https://opensource.org/licenses/MIT)**

A production-ready **Model Context Protocol (MCP) Client** built with Spring Boot and Spring AI, enabling seamless integration between AI models and MCP servers for intelligent, context-aware operations.

---

## Table of Contents

- [Overview](#-overview)
- [Core Features](#-core-features)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Docker Deployment](#-docker-deployment)
- [API Endpoints](#-api-endpoints)
- [Use Cases](#-use-cases)
- [Tech Stack](#-tech-stack)
- [Troubleshooting](#-troubleshooting)

---

## Overview

The **MCP Client** serves as an intelligent intermediary that connects AI language models (via Ollama) with MCP servers to execute context-aware operations. It enables AI models to interact with external tools and services through a standardized protocol, creating powerful AI-driven workflows.

### Purpose

- **Bridge AI Models and Tools**: Connect Ollama-powered AI models with MCP server capabilities
- **Enable Tool Execution**: Allow AI models to call external tools and services programmatically
- **Streamline Workflows**: Automate complex operations through natural language commands
- **Enhance AI Capabilities**: Extend AI functionality beyond text generation to actionable operations

---

## Core Features

### AI Integration
- **Ollama Support**: Direct integration with local or remote Ollama instances
- **Model Flexibility**: Support for multiple models (Mistral, Llama, etc.)
- **Configurable Parameters**: Fine-tune temperature, token limits, and model behavior
- **Real-time Communication**: Efficient streaming responses via Server-Sent Events (SSE)

### MCP Protocol
- **SSE Transport**: Low-latency, persistent connection to MCP servers
- **Tool Discovery**: Automatic detection of available MCP server tools
- **Dynamic Invocation**: Runtime tool execution based on AI decisions
- **Error Recovery**: Graceful handling of connection failures and timeouts

### Authentication & Security
- **Token-based Authentication**: Secure JWT authentication with MCP servers
- **API Key Management**: Environment-based credential handling
- **Sliding Token Refresh**: Automatic token renewal for continuous sessions
- **Secure Configuration**: Externalized secrets via environment variables

### Production Readiness
- **Virtual Threads**: Java 21 virtual threads for high concurrency
- **Health Monitoring**: Actuator endpoints for health checks and diagnostics
- **Structured Logging**: Comprehensive request/response tracking
- **Docker Support**: Full containerization with Docker Compose

---

##  Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     MCP Client Application                      │
│                                                                 │
│  ┌──────────────┐         ┌──────────────┐         ┌──────────┐ │
│  │              │         │              │         │          │ │
│  │  User/API    │────────▶│   Spring AI  │────────▶│  Ollama  │ │
│  │  Request     │         │  Controller  │         │  Model   │ │
│  │              │         │              │         │          │ │
│  └──────────────┘         └──────┬───────┘         └──────────┘ │ 
│                                   │                             │
│                                   │                             │
│                           ┌───────▼────────┐                    │
│                           │                │                    │
│                           │   MCP Client   │                    │
│                           │   (SSE)        │                    │
│                           │                │                    │
│                           └───────┬────────┘                    │
│                                   │                             │
└───────────────────────────────────┼─────────────────────────────┘
                                    │
                                    │ SSE Connection
                                    │
                        ┌───────────▼────────────┐
                        │                        │
                        │    MCP Server          │
                        │    (Port 8088)         │
                        │                        │
                        └───────────┬────────────┘
                                    │
                                    │
                        ┌───────────▼────────────┐
                        │                        │
                        │   GitHub API / Tools   │
                        │                        │
                        └────────────────────────┘
```

### Request Flow

```
1. User Input
   ↓
2. Spring AI Controller receives request
   ↓
3. AI Model (Ollama) processes natural language
   ↓
4. Model determines if tool execution is needed
   ↓
5. MCP Client authenticates with MCP Server (JWT)
   ↓
6. MCP Client sends tool request via SSE
   ↓
7. MCP Server executes tool (e.g., GitHub API)
   ↓
8. Server returns results to MCP Client
   ↓
9. AI Model incorporates results into response
   ↓
10. Final response returned to user
```

### Component Interaction

| Component                | Responsibility                       | Technology       |
|--------------------------|--------------------------------------|------------------|
| **User Interface**       | Receives natural language requests   | REST API         |
| **Spring AI Controller** | Orchestrates AI and MCP interactions | Spring WebFlux   |
| **Ollama Client**        | Communicates with AI models          | Spring AI Ollama |
| **MCP Client**           | Manages MCP server connection        | Spring AI MCP    |
| **MCP Server**           | Executes tools and operations        | Remote service   |
| **Authentication Layer** | Secures MCP communication            | JWT tokens       |

---

## Quick Start

### Prerequisites

- **Java 21+**
- **Maven 3.9+**
- **Ollama** (running locally or remotely)
- **MCP Server** (running and accessible)
- **Docker & Docker Compose** (for containerized deployment)

### Local Development

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/spring_boot_ai_mcp_client.git
cd spring_boot_ai_mcp_client
```

2. **Configure environment**
```bash
# Set required environment variables
export SPRING_AI_MCP_CLIENT_SSE_URL=http://localhost:8088/mcp/sse
export MCP_SERVER_AUTH_API_KEY=your-api-key
export SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
```

3. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

4. **Verify startup**
```bash
curl http://localhost:8089/actuator/health
```

### Docker Deployment

1. **Create environment file**
```bash
cp .env.example .env
nano .env  # Add your configuration
```

2. **Deploy with Docker Compose**
```bash
docker-compose up -d --build
```

3. **Verify deployment**
```bash
docker-compose ps
curl http://localhost:8089/actuator/health
```

---

## Configuration

### Environment Variables

Create a `.env` file with the following configuration:

```bash
# MCP Server Connection
SPRING_AI_MCP_CLIENT_SSE_URL=http://localhost:8088/mcp/sse
MCP_SERVER_AUTH_API_KEY=your-secure-api-key
MCP_SERVER_AUTH_TOKEN_ENDPOINT=http://localhost:8088/mcp/auth/token

# Ollama Configuration
SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL=mistral
```

### Docker Network Configuration

| Scenario                               | MCP Server URL                     | Ollama URL                          |
|----------------------------------------|------------------------------------|-------------------------------------|
| **Local development**                  | `http://localhost:8088`            | `http://localhost:11434`            |
| **Client in Docker, services on host** | `http://host.docker.internal:8088` | `http://host.docker.internal:11434` |
| **All services in Docker**             | `http://mcp-server:8088`           | `http://ollama:11434`               |

---

### Health Checks

```bash
# Application health
curl http://localhost:8089/actuator/health

# Container status
docker-compose ps

# View detailed logs
docker-compose logs --tail=100 mcp-client
```

---

## API Endpoints

### Health & Monitoring

| Endpoint           | Method | Description               |
|--------------------|--------|---------------------------|
| `/actuator/health` | GET    | Application health status |
| `/actuator/info`   | GET    | Application information   |
| `/actuator/beans`  | GET    | Spring beans information  |

### Example Requests

**Health Check:**
```bash
curl http://localhost:8089/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

---

## Use Cases

### 1. AI-Powered GitHub Operations
**Scenario:** Query and manage GitHub pull requests using natural language

**Example:**
```
User: "Show me all open pull requests in the java-must-read repository"
→ AI Model processes request
→ MCP Client calls GitHub tool via MCP Server
→ Results formatted and returned
```

### 2. Automated Workflow Execution
**Scenario:** Execute multi-step operations based on AI analysis

**Example:**
```
User: "Analyze the latest PRs and summarize common issues"
→ AI fetches PRs via MCP
→ AI analyzes content
→ AI generates summary report
```

### 3. Intelligent Data Retrieval
**Scenario:** Query external systems through natural language

**Example:**
```
User: "What's the status of PRs from last week?"
→ AI interprets date range
→ MCP Client retrieves filtered data
→ AI presents formatted results
```

---

## Advantages

### For Developers
**Simplified Integration**: No manual API coding - AI handles tool selection  
**Type Safety**: Strongly-typed Spring Boot architecture  
**Rapid Development**: Spring AI abstracts complex integrations  
**Testability**: Comprehensive actuator endpoints for monitoring  
**Scalability**: Virtual threads enable high concurrency

### For Operations
**Docker Ready**: One-command deployment with Docker Compose  
**Health Monitoring**: Built-in actuator endpoints  
**Configuration Management**: Environment-based settings  
**Resource Efficiency**: Lightweight JRE runtime image  
**Auto-restart**: Automatic recovery from failures

### For AI Applications
**Tool Augmentation**: Extend AI capabilities beyond text generation  
**Context Awareness**: AI can access real-time external data  
**Flexible Execution**: Dynamic tool selection based on context  
**Error Handling**: Graceful degradation on tool failures   
**Multi-model Support**: Works with various Ollama models

---

## Tech Stack

| Technology         | Version  | Purpose                                  |
|--------------------|----------|------------------------------------------|
| **Java**           | 21       | Runtime environment with virtual threads |
| **Spring Boot**    | 3.4.2    | Application framework                    |
| **Spring AI**      | 2.0.0-M2 | AI model integration                     |
| **Spring AI MCP**  | 2.0.0-M2 | MCP protocol implementation              |
| **Spring WebFlux** | 6.2.2    | Reactive web framework                   |
| **Ollama**         | -        | Local AI model serving                   |
| **Maven**          | 3.9+     | Build automation                         |
| **Docker**         | -        | Containerization                         |

---

## Troubleshooting

### Client won't start

**Symptom:** Container exits immediately

**Solution:**
```bash
# Check logs
docker-compose logs mcp-client

# Common issues:
# - Port 8089 already in use → Change port in docker-compose.yml
# - Java version mismatch → Verify Dockerfile uses Java 21
```

### Authentication failures

**Symptom:** 401 Unauthorized errors

**Solution:**
```bash
# Verify API key matches server
# In server .env:
MCP_SERVER_SHARED_API_KEY=secret-key

# In client .env:
MCP_SERVER_AUTH_API_KEY=secret-key  # Must match!
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/spring_boot_ai_mcp_client/issues)
- **Documentation**: [Wiki](https://github.com/yourusername/spring_boot_ai_mcp_client/wiki)
- **MCP Protocol**: [Model Context Protocol Docs](https://modelcontextprotocol.io/)
- **Spring AI**: [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)

---

**Built with️ using Spring Boot, Spring AI, and Docker**
