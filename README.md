# 📋 AgroSat SOAP Service

> **Space Connect – Tecnologia Espacial Aplicada a Desafios Reais**  
> Web Service SOAP de Relatórios e Análise de Risco Agrícola

---

## 👥 Integrantes

| RM | Nome |
|---|---|
| 557538 | David Cordeiro |
| 555619 | Tiago Morais |
| 557065 | Vinicius Augusto |
| 556892 | Guilherme Lunghini |
| 99856 | Marchel Augusto |

---

## 📋 Sobre o Projeto

O **AgroSat SOAP Service** é o serviço de relatórios da arquitetura SOA do Space Connect. Implementado com **Spring Web Services**, expõe operações SOAP para consolidação de dados históricos de monitoramento agrícola e análise automatizada de risco por fazenda.

Este serviço acessa **diretamente o mesmo banco Oracle** utilizado pela REST API — sem replicação de dados — e expõe seu contrato via **WSDL gerado automaticamente** a partir de um schema XSD. Ele é consumido exclusivamente pela AgroSat REST API, demonstrando o princípio SOA de separação de responsabilidades e baixo acoplamento.

---

## 🏗️ Arquitetura e Características

### Papel na Arquitetura SOA

```
AgroSat REST API
        ↓  (SOAP/XML)
AgroSat SOAP Service
        ↓
Oracle Database
(TB_MON_CLIMATICO, TB_MON_VEGETACAO, TB_ALERTA_AGRICOLA, TB_FAZENDA)
```

### Características Técnicas

| Característica | Detalhe |
|---|---|
| **Framework** | Spring Boot 3.4.5 + Spring Web Services |
| **Linguagem** | Java 21 |
| **Protocolo** | SOAP 1.1 sobre HTTP |
| **Contrato** | Contract-first via XSD → WSDL automático |
| **Banco de dados** | Oracle (mesmo schema da REST API e do .NET) |
| **ORM** | Spring Data JPA + Hibernate |
| **Geração WSDL** | DefaultWsdl11Definition (Spring WS) |
| **Porta** | 8082 |
| **WSDL URL** | `http://localhost:8082/ws/agrosatmonitor.wsdl` |
| **Endpoint URL** | `http://localhost:8082/ws` |
| **Namespace** | `http://agrosatmonitor.com/soap` |

---

## 📁 Estrutura de Pacotes

```
com.agrosatmonitor.soap/
├── AgroSatSoapApplication.java
├── config/
│   └── WebServiceConfig.java        ← @EnableWs, WSDL, XSD, Servlet
├── endpoint/
│   ├── RelatorioEndpoint.java       ← @PayloadRoot ConsultarRelatorioFazenda
│   └── RiscoEndpoint.java           ← @PayloadRoot ProcessarRiscoAgricola
├── service/
│   ├── RelatorioService.java        ← Agrega dados históricos do Oracle
│   └── RiscoService.java            ← Calcula pontuação e nível de risco
├── repository/
│   ├── FazendaRepository.java
│   ├── ClimaticoRepository.java     ← Queries de AVG temperatura/umidade
│   ├── VegetacaoRepository.java     ← Query de AVG NDVI
│   └── AlertaRepository.java        ← Count de alertas por fazenda
├── entity/
│   ├── Fazenda.java
│   ├── MonitoramentoClimatico.java
│   ├── MonitoramentoVegetacao.java
│   └── AlertaAgricola.java
├── dto/
│   ├── RelatorioFazendaDto.java     ← Dados consolidados do relatório
│   └── RiscoAgricolaDto.java        ← Resultado da análise de risco
├── enums/
│   ├── TipoAlerta.java
│   ├── NivelRisco.java
│   └── NivelSaudeVegetacao.java
└── exception/
    └── ResourceNotFoundException.java
```
# 📊 Diagramas — AgroSat SOAP Service

---

## 🏗️ Diagrama de Arquitetura

```mermaid
graph TB
    subgraph Consumidor["🚀 AgroSat REST API :8081"]
        RC[RelatorioController]
        RS[RelatorioService]
        WST[WebServiceTemplate]
    end

    subgraph SOAP["📋 AgroSat SOAP Service :8082"]
        DISP[MessageDispatcherServlet /ws]
        RE[RelatorioEndpoint]
        RKE[RiscoEndpoint]
        RELSV[RelatorioService]
        RISKSV[RiscoService]
        REPO[Repositories]
    end

    subgraph DB["🗄️ Oracle Database"]
        FAZ[(TB_FAZENDA)]
        CLI[(TB_MON_CLIMATICO)]
        VEG[(TB_MON_VEGETACAO)]
        ALT[(TB_ALERTA_AGRICOLA)]
    end

    subgraph Contrato["📄 Contrato SOAP"]
        XSD[agrosatmonitor.xsd]
        WSDL[agrosatmonitor.wsdl]
    end

    RC --> RS
    RS --> WST
    WST -->|"ConsultarRelatorioRequest XML"| DISP
    WST -->|"ProcessarRiscoRequest XML"| DISP
    DISP --> RE
    DISP --> RKE
    RE --> RELSV
    RKE --> RISKSV
    RELSV --> REPO
    RISKSV --> REPO
    REPO --> FAZ
    REPO --> CLI
    REPO --> VEG
    REPO --> ALT
    RE -->|"ConsultarRelatorioResponse XML"| WST
    RKE -->|"ProcessarRiscoResponse XML"| WST
    XSD --> WSDL

    style Consumidor fill:#cce5ff,color:#000
    style SOAP fill:#fde8c8,color:#000
    style DB fill:#f8d7da,color:#000
    style Contrato fill:#d4edda,color:#000
```

---

## 🔄 Fluxograma — ConsultarRelatorioFazenda

```mermaid
flowchart TD
    A(["Início: SOAP ConsultarRelatorioRequest"]) --> B[MessageDispatcherServlet recebe XML]
    B --> C[Rotear para RelatorioEndpoint via @PayloadRoot]
    C --> D[Extrair fazendaId do elemento XML]
    D --> E[Extrair dataInicio e dataFim opcionais]
    E --> F{Fazenda existe no Oracle?}
    F -->|Não| ERR1[Lançar ResourceNotFoundException]
    F -->|Sim| G[FazendaRepository: buscar nome da fazenda]
    G --> H[ClimaticoRepository: AVG temperatura]
    H --> I[ClimaticoRepository: AVG umidade]
    I --> J[ClimaticoRepository: SUM precipitacao]
    J --> K[VegetacaoRepository: AVG ndvi]
    K --> L[AlertaRepository: COUNT alertas]
    L --> M{dataInicio fornecida?}
    M -->|Não| N[Usar últimos 30 dias como período]
    M -->|Sim| O[Usar datas informadas]
    N --> P[Montar RelatorioFazendaDto com todas as métricas]
    O --> P
    P --> Q[RelatorioEndpoint: construir XML de resposta via DOM]
    Q --> R(["SOAP ConsultarRelatorioResponse — 200 OK"])

    style A fill:#28a745,color:#fff
    style R fill:#007bff,color:#fff
    style ERR1 fill:#dc3545,color:#fff
    style H fill:#fff3cd,color:#000
    style I fill:#fff3cd,color:#000
    style J fill:#fff3cd,color:#000
    style K fill:#fff3cd,color:#000
    style L fill:#fff3cd,color:#000
```

---

## 🔄 Fluxograma — ProcessarRiscoAgricola

```mermaid
flowchart TD
    A(["Início: SOAP ProcessarRiscoRequest"]) --> B[MessageDispatcherServlet recebe XML]
    B --> C[Rotear para RiscoEndpoint via @PayloadRoot]
    C --> D[Extrair fazendaId do elemento XML]
    D --> E{Fazenda existe?}
    E -->|Não| ERR1[Lançar ResourceNotFoundException]
    E -->|Sim| F[Iniciar pontuação em zero]
    F --> G[ClimaticoRepository: AVG temperatura]
    G --> H{Temperatura maior que 38 graus?}
    H -->|Sim| I[Pontuação + 30 pontos]
    H -->|Não| J{Temperatura maior que 32 graus?}
    J -->|Sim| K[Pontuação + 15 pontos]
    J -->|Não| L[ClimaticoRepository: AVG umidade]
    I --> L
    K --> L
    L --> M{Umidade menor que 30 por cento?}
    M -->|Sim| N[Pontuação + 25 pontos]
    M -->|Não| O[VegetacaoRepository: AVG ndvi]
    N --> O
    O --> P{NDVI menor que 0.10?}
    P -->|Sim| Q[Pontuação + 35 pontos]
    P -->|Não| R{NDVI menor que 0.25?}
    R -->|Sim| S[Pontuação + 20 pontos]
    R -->|Não| T[AlertaRepository: COUNT alertas]
    Q --> T
    S --> T
    T --> U{Quantidade de alertas maior que 5?}
    U -->|Sim| V[Pontuação + 2 por alerta máx 20]
    U -->|Não| W[Classificar nível de risco]
    V --> W
    W --> X{Pontuação maior ou igual a 70?}
    X -->|Sim| Y[Nível CRITICO — intervenção imediata]
    X -->|Não| Z{Pontuação maior ou igual a 45?}
    Z -->|Sim| AA[Nível ALTO — monitorar diariamente]
    Z -->|Não| AB{Pontuação maior ou igual a 20?}
    AB -->|Sim| AC[Nível MEDIO — monitoramento regular]
    AB -->|Não| AD[Nível BAIXO — condições favoráveis]
    Y --> AE[Montar ProcessarRiscoResponse via DOM]
    AA --> AE
    AC --> AE
    AD --> AE
    AE --> AF(["SOAP ProcessarRiscoResponse — 200 OK"])

    style A fill:#28a745,color:#fff
    style AF fill:#007bff,color:#fff
    style ERR1 fill:#dc3545,color:#fff
    style Y fill:#dc3545,color:#fff
    style AA fill:#fd7e14,color:#fff
    style AC fill:#ffc107,color:#000
    style AD fill:#28a745,color:#fff
```

---

## 🧩 Diagrama de Classes

```mermaid
classDiagram
    direction TB

    class RelatorioEndpoint {
        <<Endpoint>>
        -String NAMESPACE
        -RelatorioService relatorioService
        +consultarRelatorio(Element) Element
        -getOptionalText(Element, String) String
        -addElement(Document, Element, String, String) void
    }

    class RiscoEndpoint {
        <<Endpoint>>
        -String NAMESPACE
        -RiscoService riscoService
        +processarRisco(Element) Element
        -addElement(Document, Element, String, String) void
    }

    class RelatorioService {
        -FazendaRepository fazendaRepository
        -ClimaticoRepository climaticoRepository
        -VegetacaoRepository vegetacaoRepository
        -AlertaRepository alertaRepository
        +consultarRelatorio(Long, String, String) RelatorioFazendaDto
    }

    class RiscoService {
        -FazendaRepository fazendaRepository
        -ClimaticoRepository climaticoRepository
        -VegetacaoRepository vegetacaoRepository
        -AlertaRepository alertaRepository
        +processarRisco(Long) RiscoAgricolaDto
    }

    class RelatorioFazendaDto {
        -Long fazendaId
        -String nomeFazenda
        -Double temperaturaMedia
        -Double umidadeMedia
        -Double precipitacaoTotal
        -Double ndviMedio
        -Long quantidadeAlertas
        -String periodoInicio
        -String periodoFim
    }

    class RiscoAgricolaDto {
        -Long fazendaId
        -String nivelRisco
        -Double pontuacaoRisco
        -String motivo
        -String recomendacao
        -String dataAnalise
    }

    class FazendaRepository {
        <<Repository>>
        +findById(Long) Optional~Fazenda~
        +save(Fazenda) Fazenda
    }

    class ClimaticoRepository {
        <<Repository>>
        +mediaTemperatura(Long) Double
        +mediaUmidade(Long) Double
        +totalPrecipitacao(Long) Double
    }

    class VegetacaoRepository {
        <<Repository>>
        +mediaNdvi(Long) Double
        +findByFazendaIdOrderByDataLeituraDesc(Long) List
    }

    class AlertaRepository {
        <<Repository>>
        +countByFazendaId(Long) long
        +findByFazendaIdOrderByDataGeracaoDesc(Long) List
    }

    class Fazenda {
        -Long id
        -String nome
        -Double latitude
        -Double longitude
        -Double areaHectares
        -String cidade
        -String estado
        -LocalDateTime dataCadastro
    }

    class MonitoramentoClimatico {
        -Long id
        -Long fazendaId
        -Double temperatura
        -Double umidade
        -Double precipitacao
        -Double velocidadeVento
        -LocalDateTime dataLeitura
        -LocalDateTime dataCriacao
    }

    class MonitoramentoVegetacao {
        -Long id
        -Long fazendaId
        -Double ndvi
        -Integer nivelSaudeVegetacaoCodigo
        -LocalDateTime dataLeitura
    }

    class AlertaAgricola {
        -Long id
        -Long fazendaId
        -Integer tipoAlertaCodigo
        -Integer nivelRiscoCodigo
        -String descricao
        -LocalDateTime dataGeracao
    }

    class WebServiceConfig {
        <<Configuration>>
        +messageDispatcherServlet(ApplicationContext) ServletRegistrationBean
        +defaultWsdl11Definition(XsdSchema) DefaultWsdl11Definition
        +agrosatmonitorSchema() XsdSchema
    }

    RelatorioEndpoint --> RelatorioService : delega
    RiscoEndpoint --> RiscoService : delega
    RelatorioService --> RelatorioFazendaDto : retorna
    RiscoService --> RiscoAgricolaDto : retorna
    RelatorioService --> FazendaRepository : usa
    RelatorioService --> ClimaticoRepository : AVG queries
    RelatorioService --> VegetacaoRepository : AVG ndvi
    RelatorioService --> AlertaRepository : count
    RiscoService --> FazendaRepository : valida
    RiscoService --> ClimaticoRepository : AVG queries
    RiscoService --> VegetacaoRepository : AVG ndvi
    RiscoService --> AlertaRepository : count
    FazendaRepository ..> Fazenda : gerencia
    ClimaticoRepository ..> MonitoramentoClimatico : gerencia
    VegetacaoRepository ..> MonitoramentoVegetacao : gerencia
    AlertaRepository ..> AlertaAgricola : gerencia
    WebServiceConfig ..> RelatorioEndpoint : registra
    WebServiceConfig ..> RiscoEndpoint : registra
```

---

## 🗃️ Diagrama de Entidades (Oracle — apenas leitura)

```mermaid
erDiagram
    TB_FAZENDA {
        NUMBER ID_FAZENDA PK
        VARCHAR2 NM_FAZENDA
        NUMBER NR_LATITUDE
        NUMBER NR_LONGITUDE
        NUMBER NR_AREA_HECTARES
        VARCHAR2 NM_CIDADE
        CHAR SG_ESTADO
        TIMESTAMP DT_CADASTRO
    }

    TB_MON_CLIMATICO {
        NUMBER ID_MON_CLI PK
        NUMBER ID_FAZENDA FK
        NUMBER NR_TEMPERATURA
        NUMBER NR_UMIDADE
        NUMBER NR_PRECIPITACAO
        NUMBER NR_VEL_VENTO
        TIMESTAMP DT_LEITURA
        TIMESTAMP DT_CRIACAO
    }

    TB_MON_VEGETACAO {
        NUMBER ID_MON_VEG PK
        NUMBER ID_FAZENDA FK
        NUMBER NR_NDVI
        NUMBER TP_NIVEL_SAUDE
        TIMESTAMP DT_LEITURA
        TIMESTAMP DT_CRIACAO
    }

    TB_ALERTA_AGRICOLA {
        NUMBER ID_ALERTA PK
        NUMBER ID_FAZENDA FK
        NUMBER TP_ALERTA
        VARCHAR2 DS_ALERTA
        NUMBER TP_NIVEL_RISCO
        TIMESTAMP DT_GERACAO
    }

    TB_FAZENDA ||--o{ TB_MON_CLIMATICO : "referenciada em"
    TB_FAZENDA ||--o{ TB_MON_VEGETACAO : "referenciada em"
    TB_FAZENDA ||--o{ TB_ALERTA_AGRICOLA : "referenciada em"
```
### Recursos XSD/WSDL

```
src/main/resources/
└── wsdl/
    └── agrosatmonitor.xsd    ← Schema XML (contract-first)
```

---

## 🔌 Operações SOAP

### Operação 1 — `ConsultarRelatorioFazenda`

Retorna dados agregados de monitoramento climático e de vegetação de uma fazenda em um período.

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:agro="http://agrosatmonitor.com/soap">
   <soapenv:Header/>
   <soapenv:Body>
      <agro:ConsultarRelatorioRequest>
         <agro:fazendaId>1</agro:fazendaId>
         <agro:dataInicio>2025-01-01</agro:dataInicio>
         <agro:dataFim>2025-12-31</agro:dataFim>
      </agro:ConsultarRelatorioRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <agro:ConsultarRelatorioResponse xmlns:agro="http://agrosatmonitor.com/soap">
         <agro:fazendaId>1</agro:fazendaId>
         <agro:nomeFazenda>Fazenda Santa Cruz</agro:nomeFazenda>
         <agro:temperaturaMedia>29.3</agro:temperaturaMedia>
         <agro:umidadeMedia>62.5</agro:umidadeMedia>
         <agro:precipitacaoTotal>18.2</agro:precipitacaoTotal>
         <agro:ndviMedio>0.5421</agro:ndviMedio>
         <agro:quantidadeAlertas>3</agro:quantidadeAlertas>
         <agro:periodoInicio>2025-01-01</agro:periodoInicio>
         <agro:periodoFim>2025-12-31</agro:periodoFim>
      </agro:ConsultarRelatorioResponse>
   </SOAP-ENV:Body>
</soapenv:Envelope>
```

---

### Operação 2 — `ProcessarRiscoAgricola`

Analisa os indicadores mais recentes da fazenda e retorna o nível de risco com recomendação agronômica.

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:agro="http://agrosatmonitor.com/soap">
   <soapenv:Header/>
   <soapenv:Body>
      <agro:ProcessarRiscoRequest>
         <agro:fazendaId>1</agro:fazendaId>
      </agro:ProcessarRiscoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <agro:ProcessarRiscoResponse xmlns:agro="http://agrosatmonitor.com/soap">
         <agro:fazendaId>1</agro:fazendaId>
         <agro:nivelRisco>MEDIO</agro:nivelRisco>
         <agro:pontuacaoRisco>32.0</agro:pontuacaoRisco>
         <agro:motivo>Temperatura acima da média (31.4°C). 3 alertas registrados.</agro:motivo>
         <agro:recomendacao>Monitoramento regular. Acompanhar previsão climática.</agro:recomendacao>
         <agro:dataAnalise>2025-05-29T14:32:10</agro:dataAnalise>
      </agro:ProcessarRiscoResponse>
   </SOAP-ENV:Body>
</soapenv:Envelope>
```

---

## 📊 Lógica de Cálculo de Risco

O `RiscoService` aplica um sistema de pontuação composta sobre os indicadores mais recentes:

| Indicador | Condição | Pontos |
|---|---|---|
| Temperatura | > 38°C | +30 |
| Temperatura | > 32°C | +15 |
| Umidade | < 30% | +25 |
| NDVI | < 0.10 (crítico) | +35 |
| NDVI | < 0.25 (baixo) | +20 |
| Alertas registrados | > 5 alertas | +2 por alerta (máx. 20) |

**Classificação final:**

| Pontuação | Nível | Recomendação |
|---|---|---|
| ≥ 70 | `CRITICO` | Intervenção imediata — contate agrônomo |
| 45 – 69 | `ALTO` | Monitorar diariamente e revisar irrigação |
| 20 – 44 | `MEDIO` | Monitoramento regular e acompanhar clima |
| < 20 | `BAIXO` | Condições favoráveis — manter boas práticas |

---

## 🗂️ Schema XSD

O contrato do serviço é definido em `src/main/resources/wsdl/agrosatmonitor.xsd` e segue a abordagem **contract-first**. O Spring WS gera o WSDL automaticamente a partir desse schema:

```
agrosatmonitor.xsd define:
  ├── ConsultarRelatorioRequest   (fazendaId, dataInicio?, dataFim?)
  ├── ConsultarRelatorioResponse  (fazendaId, nomeFazenda, temperaturaMedia,
  │                                umidadeMedia, precipitacaoTotal, ndviMedio,
  │                                quantidadeAlertas, periodoInicio, periodoFim)
  ├── ProcessarRiscoRequest       (fazendaId)
  └── ProcessarRiscoResponse      (fazendaId, nivelRisco, pontuacaoRisco,
                                   motivo, recomendacao, dataAnalise)
```

---

## ⚙️ Configuração

### application.properties

```properties
server.port=8082

# Oracle (mesmo schema da REST API)
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
spring.datasource.username=SEU_RM
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
```

### WebServiceConfig — como o WSDL é exposto

```java
@Bean(name = "agrosatmonitor")
public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema schema) {
    DefaultWsdl11Definition def = new DefaultWsdl11Definition();
    def.setPortTypeName("AgroSatMonitorPort");
    def.setLocationUri("/ws");
    def.setTargetNamespace("http://agrosatmonitor.com/soap");
    def.setSchema(schema);
    return def;
}
```

O Spring WS monta automaticamente o WSDL em `http://localhost:8082/ws/agrosatmonitor.wsdl`.

---

## ▶️ Como Executar

**Pré-requisitos:** Java 21, Maven 3.9+, Oracle FIAP acessível.

> Este serviço deve ser iniciado **antes** da REST API para que o endpoint de relatórios funcione.

```bash
cd agrosat-soap-service
mvn clean package -DskipTests
mvn spring-boot:run
```

**Verificar se está rodando:**
```
http://localhost:8082/ws/agrosatmonitor.wsdl
```
Se retornar o XML do WSDL, o serviço está operacional.

---

## 🧪 Testando com SoapUI

1. Abra o SoapUI e crie um novo projeto SOAP
2. Informe a URL do WSDL: `http://localhost:8082/ws/agrosatmonitor.wsdl`
3. O SoapUI importará automaticamente as duas operações
4. Preencha o `fazendaId` no request e execute

---

## 🗄️ Tabelas Oracle Utilizadas

Este serviço acessa as seguintes tabelas do schema compartilhado (criadas pelo `script_oracle.sql`):

| Tabela | Operações | Finalidade |
|---|---|---|
| `TB_FAZENDA` | SELECT | Valida existência e obtém nome da fazenda |
| `TB_MON_CLIMATICO` | AVG SELECT | Calcula médias de temperatura e umidade |
| `TB_MON_VEGETACAO` | AVG SELECT | Calcula média de NDVI |
| `TB_ALERTA_AGRICOLA` | COUNT SELECT | Conta alertas para cálculo de risco |

> **Nenhuma tabela é criada ou alterada** por este serviço. Toda DDL é gerenciada pelo `script_oracle.sql`.

---


