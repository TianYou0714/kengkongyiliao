# 🏥 小智医疗（XiaoZhiMed）项目完全吃透教程

> 读完本文档，你就能把这个项目自信地写在简历上，应对任何面试提问。

---

## 目录

- [第一章：项目概览](#第一章项目概览)
- [第二章：架构设计](#第二章架构设计)
- [第三章：代码逐文件详解](#第三章代码逐文件详解)
- [第四章：核心流程](#第四章核心流程)
- [第五章：设计思想](#第五章设计思想)
- [第六章：简历与面试](#第六章简历与面试)

---

## 关键术语速查

| 术语 | 全称 | 一句话解释 |
|------|------|-----------|
| **LLM** | Large Language Model | 大语言模型，就是 ChatGPT / 通义千问这种 AI |
| **LangChain4j** | — | Java 的 LLM 集成框架，用注解简化 AI 开发 |
| **@AiService** | — | LangChain4j 核心注解，把接口变成 AI Agent |
| **RAG** | Retrieval-Augmented Generation | 先检索知识库再让 LLM 回答，减少幻觉 |
| **Function Calling** | — | LLM 不只是说话，还能调用代码里的函数 |
| **@Tool** | — | LangChain4j 注解，标记 LLM 可以调用的方法 |
| **Embedding** | — | 把文本转成向量（一串数字），用于相似度搜索 |
| **Pinecone** | — | 云向量数据库，存向量 + 做相似度检索 |
| **ChatMemory** | — | 对话记忆，让 AI 记住之前聊了什么 |
| **Flux** | — | Spring WebFlux 的响应式流类型，0~N 个异步数据 |
| **Streaming** | — | 流式输出，LLM 一个字一个字地返回，像 ChatGPT 打字效果 |
| **Token** | — | LLM 处理文本的最小单元，约等于 1 个中文字或 0.75 个英文词 |
| **SSE** | Server-Sent Events | 服务器推送事件，HTTP 流式传输的标准方式 |
| **Prompt** | — | 提示词，发给 LLM 的指令文本 |
| **System Prompt** | — | 系统提示词，定义 AI 的人设、能力、规则 |

---

# 第一章：项目概览

## 1.1 这个项目是什么？

> **一句话**：基于 Spring Boot 3 + LangChain4j + 阿里通义千问大模型，构建的 **AI 医疗智能导诊与预约挂号系统**。

模拟北京协和医院的 AI 客服"小智"，用户打开网页就能像一个真实的对话窗口一样：
- 问医疗问题 → AI 回答
- 问"我头疼挂什么科" → AI 推荐科室
- 说"帮我挂号" → AI 真的往 MySQL 里写入一条预约记录
- 说"取消挂号" → AI 从 MySQL 里删掉记录
- 问"你们医院在哪" → AI 从知识库里检索出医院地址回答

**这不是一个玩具 Demo**，它集成了：
- 大模型调用（阿里通义千问 Qwen3.7-plus）
- RAG 检索增强生成（Pinecone 向量数据库）
- Function Calling 工具调用（操作真实 MySQL 数据库）
- 多轮对话记忆持久化（MongoDB）
- 流式输出（WebFlux token 级别实时推送）
- 前后端分离（Vue 3 + Element Plus 聊天 UI）

## 1.2 项目的业务场景

| 用户说的 | 小智做的 | 背后技术 |
|----------|----------|----------|
| "你好" | 自我介绍："我是小智，北京协和医院的智能客服" | 系统提示词 + LLM 生成 |
| "我最近头疼，该挂什么科？" | 推荐神经内科，并介绍科室信息 | RAG 检索 Pinecone 知识库 |
| "帮我挂神经内科，明天上午" | 先确认姓名、身份证号，确认后写入 MySQL | Function Calling → MyBatis-Plus INSERT |
| "我叫张三，身份证 110101199001011234" | 记住信息，继续确认其他字段 | LLM 上下文记忆 + MongoDB 持久化 |
| "好的，确认预约" | 写入数据库，返回"预约成功" | AppointmentTools.bookAppointment |
| "帮我取消这个预约" | 从 MySQL 匹配并删除记录 | AppointmentTools.cancelAppointment |
| "你们医院怎么走？" | 返回协和医院地址 + 公交线路 | RAG 检索 Pinecone |
| 关掉网页，明天再打开 | 对话继续，记得昨天聊了什么 | MongoDB ChatMemory 恢复 |

## 1.3 技术栈全景

| 层级 | 技术 | 版本 | 作用 |
|------|------|------|------|
| **语言** | Java | 17 | 主开发语言 |
| **框架** | Spring Boot | 3.2.6 | 应用框架 |
| **AI 框架** | LangChain4j | 1.0.0-beta3 | LLM 集成核心框架 |
| **大模型** | 阿里通义千问 Qwen3.7-plus | — | 对话生成 + 工具调用决策 |
| **嵌入模型** | DashScope text-embedding-v3 | — | 文档向量化 |
| **向量数据库** | Pinecone (云服务) | — | RAG 知识库存储与检索 |
| **关系数据库** | MySQL | 8.0 | 业务数据（预约记录） |
| **文档数据库** | MongoDB | — | 对话记忆持久化 |
| **ORM** | MyBatis-Plus | 3.5.11 | MySQL 数据访问 |
| **响应式** | Spring WebFlux + Reactor | — | 流式 HTTP 响应 |
| **API 文档** | Knife4j (Swagger) | 4.3.0 | API 文档自动生成 |
| **前端** | Vue 3 + Element Plus | 3.x | 聊天 UI |
| **前端构建** | Vite | 5.x | 前端开发服务器 |
| **前端依赖** | axios, marked, uuid | — | HTTP 请求、Markdown 渲染、会话 ID |
| **文档解析** | Apache PDFBox | — | PDF 知识库文档解析 |
| **模板引擎** | Freemarker | 2.3.31 | MyBatis-Plus 代码生成器 |
| **工具库** | Lombok | 1.18.30 | 减少样板代码 |

## 1.4 项目目录结构

```
XiaoZhiMed/
├── pom.xml                                    # Maven 依赖管理
├── src/main/java/org/example/langchain4j/
│   ├── Langchain4jApplication.java            # 启动入口
│   ├── config/
│   │   ├── EmbeddingStoreConfig.java           # Pinecone 向量库配置
│   │   ├── MemoryChatAssistantConfig.java      # 内存记忆 Bean
│   │   ├── SeparateChatAssistantConfig.java    # 分离会话记忆 Bean
│   │   └── XiaozhiAgentConfig.java             # 小智 Agent 核心配置
│   ├── controller/
│   │   ├── UploadKnowledgeLibraryController.java  # 知识库上传接口
│   │   └── XiaozhiController.java                 # 聊天接口
│   ├── entity/
│   │   ├── Appointment.java                    # 预约实体 (MySQL)
│   │   ├── ChatFormDTO.java                    # 聊天请求 DTO
│   │   └── ChatMessages.java                   # 聊天记忆实体 (MongoDB)
│   ├── generator/
│   │   └── CodeGenerator.java                  # MyBatis-Plus 代码生成器
│   ├── mapper/
│   │   └── AppointmentMapper.java              # MyBatis-Plus Mapper
│   ├── service/
│   │   ├── Assistant.java                      # [已废弃] 基础 AI 服务
│   │   ├── MemoryChatAssistant.java            # 内存记忆测试 AI 服务
│   │   ├── SeparateChatAssistant.java          # 分离会话 AI 服务
│   │   ├── XiaozhiAgent.java                   # ⭐ 核心 AI Agent 接口
│   │   ├── AppointmentService.java             # 预约业务接口
│   │   ├── UploadKnowledgeLibraryService.java  # 知识库上传接口
│   │   └── impl/
│   │       ├── AppointmentServiceImpl.java      # 预约业务实现
│   │       └── UploadKnowledgeLibraryServiceImpl.java  # 知识库上传实现
│   ├── store/
│   │   └── MongoChatMemoryStore.java           # MongoDB 记忆存储实现
│   └── tools/
│       ├── AppointmentTools.java               # ⭐ 预约工具 (Function Calling)
│       └── CalculatorTools.java                # 计算器工具
├── src/main/resources/
│   ├── application.properties                  # 应用配置
│   ├── mapper/
│   │   └── AppointmentMapper.xml               # MyBatis XML 映射
│   ├── prompts/
│   │   ├── assistant.txt                       # 基础助手提示词
│   │   └── xiaozhi-prompt-template.txt         # ⭐ 小智系统提示词
│   ├── knowledge/                              # RAG 知识库文档
│   │   ├── 医院信息.md / .txt / .pdf
│   │   ├── 科室信息.md / .txt / .pdf
│   │   ├── 神经内科.md
│   │   ├── 口腔科.md
│   │   ├── 人工智能.md
│   │   └── 测试.txt
│   └── xiaozhi-ui/                             # Vue 3 前端项目
│       ├── index.html
│       ├── package.json
│       ├── vite.config.js
│       └── src/
│           ├── App.vue
│           ├── main.js
│           ├── style.css
│           └── components/ChatWindow.vue
└── src/test/java/org/example/langchain4j/
    ├── Langchain4jApplicationTests.java
    ├── EmbeddingTest.java
    ├── MongoCrudTest.java
    ├── PromptTest.java
    ├── RAGTest.java
    └── ToolsTest.java
```

## 1.5 数据库设计

### MySQL — `appointment` 表（预约挂号记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT (自增主键) | 预约记录 ID |
| `username` | VARCHAR | 预约人姓名 |
| `id_card` | VARCHAR | 身份证号 |
| `department` | VARCHAR | 预约科室 |
| `date` | VARCHAR | 预约日期 (格式: 2025-04-14) |
| `time` | VARCHAR | 预约时段 (上午 / 下午) |
| `doctor_name` | VARCHAR | 医生姓名 (可选) |

### MongoDB — `chat_messages` 集合（对话记忆）

| 字段 | 类型 | 说明 |
|------|------|------|
| `_id` | ObjectId | MongoDB 自动生成 |
| `memoryId` | int | 会话 ID（对应前端的 UUID 转换值） |
| `content` | String | 完整的聊天记录 JSON（序列化的 List\<ChatMessage\>） |

### Pinecone — `xiaozhi-index`（向量知识库）

| 字段 | 说明 |
|------|------|
| `id` | 向量唯一 ID |
| `values` | 嵌入向量（浮点数数组） |
| `metadata` | 原始文本片段 |

## 1.6 快速理解：三个"第一次"

如果你是第一次看这个项目，建议按这个顺序理解：

1. **先看** `prompts/xiaozhi-prompt-template.txt` — 了解小智的"人设"
2. **再看** `XiaozhiAgent.java` — 理解 AI Agent 怎么定义
3. **然后看** `XiaozhiAgentConfig.java` — 理解 Agent 的零件怎么装配

这三个文件加起来不到 100 行，但是整个系统的中枢神经。

---

# 第二章：架构设计

## 2.1 总体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                    用户浏览器                                          │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │              Vue 3 + Element Plus 单页应用                      │  │
│  │  ChatWindow.vue                                                │  │
│  │  ┌──────────┐  ┌──────────────────┐  ┌────────────────────┐   │  │
│  │  │ 侧边栏    │  │  消息列表         │  │  输入框 + 发送按钮   │   │  │
│  │  │ Logo     │  │  (v-for 渲染)    │  │  @keyup.enter      │   │  │
│  │  │ 新聊天   │  │  marked 渲染 MD  │  │  axios POST 流式    │   │  │
│  │  └──────────┘  └──────────────────┘  └────────────────────┘   │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                              │                                       │
│              localStorage    │  POST /api/xiaozhi/chat               │
│              (memoryId)      │  Content-Type: application/json       │
│                              │  Accept: text/stream                  │
└──────────────────────────────┼───────────────────────────────────────┘
                               │
                    Vite Proxy (/api → localhost:8080)
                               │
┌──────────────────────────────┼───────────────────────────────────────┐
│                    Spring Boot (端口 8080)                             │
│                              │                                       │
│  ┌───────────────────────────┼───────────────────────────────────┐  │
│  │                    CONTROLLER 层                                │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │  XiaozhiController                                      │  │  │
│  │  │  @PostMapping("/xiaozhi/chat")                          │  │  │
│  │  │  produces = "text/stream;charset=utf-8"                 │  │  │
│  │  │  → 返回 Flux<String> (响应式流)                           │  │  │
│  │  └──────────────────────────┬──────────────────────────────┘  │  │
│  │                             │ 委托                             │  │
│  └─────────────────────────────┼─────────────────────────────────┘  │
│                                │                                    │
│  ┌─────────────────────────────┼─────────────────────────────────┐  │
│  │                    AI SERVICE 层 (LangChain4j 动态代理)         │  │
│  │                             ▼                                  │  │
│  │  ┌─────────────────────────────────────────────────────────┐  │  │
│  │  │            XiaozhiAgent (@AiService 接口)                │  │  │
│  │  │                                                         │  │  │
│  │  │  每当 chat(memoryId, userMessage) 被调用：               │  │  │
│  │  │                                                         │  │  │
│  │  │  ① 从 MongoDB 加载该 memoryId 的历史对话                  │  │  │
│  │  │  ② 读取系统提示词 (xiaozhi-prompt-template.txt)           │  │  │
│  │  │  ③ 拼装完整 Prompt = 系统提示词 + 历史对话 + 用户消息     │  │  │
│  │  │  ④ 调用 Qwen3.7-plus (流式)                              │  │  │
│  │  │  ⑤ LLM 思考：需要调工具吗？需要检索知识库吗？              │  │  │
│  │  │  ⑥ 如果需要 → 调 AppointmentTools / Pinecone RAG        │  │  │
│  │  │  ⑦ 收集结果 → 送回 LLM → 生成最终回复                    │  │  │
│  │  │  ⑧ 保存对话到 MongoDB                                    │  │  │
│  │  │  ⑨ 以 Flux<String> 流式返回                              │  │  │
│  │  └─────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    CONFIG 层 (Bean 装配)                        │  │
│  │  EmbeddingStoreConfig ──→ PineconeEmbeddingStore (向量库连接)   │  │
│  │  XiaozhiAgentConfig    ──→ ChatMemoryProvider (记忆提供者)     │  │
│  │                        ──→ ContentRetriever   (RAG 检索器)     │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    TOOLS 层 (Function Calling)                 │  │
│  │  AppointmentTools (@Component)                                 │  │
│  │  ├── @Tool("预约挂号")    bookAppointment()   → MySQL INSERT   │  │
│  │  ├── @Tool("取消预约挂号") cancelAppointment()  → MySQL DELETE  │  │
│  │  └── @Tool("查询是否有号源") queryDepartment()  → 排班查询 stub │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    STORE 层 (记忆持久化)                        │  │
│  │  MongoChatMemoryStore implements ChatMemoryStore               │  │
│  │  ├── getMessages(memoryId)     → MongoDB 查询 + JSON 反序列化  │  │
│  │  ├── updateMessages(memoryId)  → MongoDB upsert + JSON 序列化  │  │
│  │  └── deleteMessages(memoryId)  → MongoDB remove               │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    SERVICE 层 (业务逻辑)                        │  │
│  │  AppointmentService / Impl  (MyBatis-Plus ServiceImpl)        │  │
│  │  └── getOne(): 多条件查询预约是否存在 (LambdaQueryWrapper)      │  │
│  │  UploadKnowledgeLibraryService / Impl                          │  │
│  │  └── uploadKnowledgeLibrary(): 接收文件 → 解析 → 向量化 → 存储 │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
                               │
               ┌───────────────┼───────────────┐
               ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────────┐
        │  MySQL    │   │ MongoDB  │   │  Pinecone     │
        │appointment│   │chat_     │   │  (云向量库)    │
        │  表       │   │messages  │   │ xiaozhi-index │
        └──────────┘   └──────────┘   └──────────────┘
```

## 2.2 分层架构设计思想

```
┌─────────────────┐
│  前端 (Vue 3)    │  ← 展示层：用户交互
├─────────────────┤
│  Controller 层   │  ← 接入层：HTTP 请求/响应，Stream 控制
├─────────────────┤
│  AI Service 层   │  ← ⭐ AI 核心层：LangChain4j 动态代理
│  (@AiService)    │     这是 LangChain4j 特有的层次
├─────────────────┤
│  Config 层       │  ← 装配层：Bean 创建和依赖注入
├─────────────────┤
│  Tools 层        │  ← 工具层：暴露给 LLM 的函数
├─────────────────┤
│  Service 层      │  ← 业务层：传统业务逻辑
├─────────────────┤
│  Store/Mapper 层  │  ← 数据访问层：MongoDB / MySQL
├─────────────────┤
│  数据库层         │  ← MySQL + MongoDB + Pinecone
└─────────────────┘
```

### 与传统 Spring Boot 项目的区别

| 传统项目 | 这个项目 | 新增概念 |
|----------|----------|----------|
| Controller 调用 Service | Controller 调用 **@AiService 接口** | AI Agent 作为服务 |
| Service 写死业务逻辑 | LLM **自主决策**调用哪个 Tool | 控制权从代码转移到 AI |
| 数据库存业务数据 | 多了 **向量数据库** 存语义向量 | RAG 知识检索 |
| 无状态服务 | MongoDB 持久化**对话记忆** | 多轮对话上下文 |

## 2.3 核心组件关系图（UML 风格）

```
┌──────────────────────────────────────────────────────────┐
│                    <<interface>>                          │
│                    XiaozhiAgent                           │
│                    @AiService                             │
├──────────────────────────────────────────────────────────┤
│ + chat(memoryId: int, userMessage: String): Flux<String> │
└──────────────────────────────────────────────────────────┘
        │                    ▲
        │ 动态代理生成        │ 依赖注入
        ▼                    │
┌──────────────────┐  ┌──────┴──────────────┐
│ LangChain4j      │  │ XiaozhiController    │
│ 运行时框架        │  │ @RestController     │
│ (生成代理实现)    │  └─────────────────────┘
└──────────────────┘
        │
        │ 运行时自动装配
        ▼
┌──────────────────────────────────────────────────────────┐
│              XiaozhiAgentConfig (@Configuration)          │
├──────────────────────────────────────────────────────────┤
│ + chatMemoryProviderXiaozhi(): ChatMemoryProvider         │
│ + contentRetrieverPinecone(): ContentRetriever            │
└──────────────────────────────────────────────────────────┘
        │                    │
        ▼                    ▼
┌──────────────────┐  ┌──────────────────────────────────┐
│ MongoChatMemory  │  │ EmbeddingStoreContentRetriever    │
│ Store            │  │  ├── embeddingModel (DashScope)   │
│ (实现 ChatMemory │  │  └── embeddingStore (Pinecone)    │
│  Store 接口)     │  └──────────────────────────────────┘
└──────────────────┘
        │
        ▼
┌──────────────────┐
│ MongoDB          │
│ chat_messages    │
└──────────────────┘


┌──────────────────────────────────────────────────────────┐
│              AppointmentTools (@Component)                │
├──────────────────────────────────────────────────────────┤
│ + @Tool bookAppointment(Appointment): String             │
│ + @Tool cancelAppointment(Appointment): String           │
│ + @Tool queryDepartment(...): boolean                    │
└──────────────────────────────────────────────────────────┘
        │
        ▼
┌──────────────────┐
│ AppointmentService│
│ (IService<Appt>)  │
└──────────────────┘
        │
        ▼
┌──────────────────┐
│ MySQL            │
│ appointment 表    │
└──────────────────┘
```

## 2.4 为什么这样设计？——架构决策记录

### 决策 1：为什么使用 LangChain4j 而不是直接调 OpenAI API？

```
直接调 API:                          使用 LangChain4j:

Controller                           @AiService 接口
  ↓                                    ↓ (接口, 不是实现)
手动构建 HTTP Request               LangChain4j 动态代理自动:
手动拼接 Prompt                      ① 加载 ChatMemory
手动调 OpenAI HTTP API               ② 拼装 Prompt
手动解析 Response JSON               ③ 调 LLM
手动处理 Tool Calling                ④ 自动调用 @Tool 方法
手动管理对话历史                      ⑤ 自动保存对话
  ↓                                    ↓
300+ 行代码                          一个接口 = 20 行
```

**核心思想**：LangChain4j 把 AI 应用的常见模式（Prompt 管理、记忆管理、工具调用、RAG 检索）抽象成了注解和接口，你**声明需求而不是写实现**。

### 决策 2：为什么用 MongoDB 存对话记忆而不是 MySQL？

- 对话记忆是非结构化的 JSON 序列化数据
- 每条对话的格式不固定（文字、工具调用、工具结果交替）
- MongoDB 的文档模型天然适合存 JSON
- upsert 操作：一个会话永远只存一条记录
- 不需要建表、不需要迁移

### 决策 3：为什么用 Pinecone 而不是本地向量库？

- Pinecone 是托管的云服务，不需要自己维护
- 但配置中也有被注释掉的内存向量存储 `InMemoryEmbeddingStore`，用于开发测试
- 生产环境切换只需改一个 Bean

### 决策 4：为什么流式输出用 WebFlux 而不是 Servlet？

- Servlet 是同步的：一个线程等 LLM 完整回复 → 用户等好久
- WebFlux 是响应式的：`Flux<String>` 每生成一个 token 就推送一个 → 用户看到逐字输出
- 用户体验巨大提升（类似 ChatGPT 的打字效果）

---

# 第三章：代码逐文件详解

> 按"阅读优先级"排序，**⭐ 标记的必须看懂**。

---

## 第一优先级：核心链路（必须看懂）

### ⭐ XiaozhiAgent.java — AI Agent 接口定义

```java
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,        // 显式绑定 Bean，不用自动匹配
        streamingChatModel = "qwenStreamingChatModel",     // 使用阿里 Qwen3.7-plus 流式模型
        chatMemoryProvider = "chatMemoryProviderXiaozhi",  // MongoDB 持久化记忆 (20条窗口)
        tools = "appointmentTools",                        // 预约工具 (Function Calling)
        contentRetriever = "contentRetrieverPinecone"      // Pinecone RAG 检索器
)
public interface XiaozhiAgent {

    @SystemMessage(fromResource = "prompts/xiaozhi-prompt-template.txt")
    Flux<String> chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
```

#### 逐行解释

**`@AiService`** — LangChain4j 最核心的注解

| 属性 | 值 | 含义 |
|------|-----|------|
| `wiringMode` | `EXPLICIT` | 通过 Bean 名称显式绑定。Spring 容器中可能有多个 ChatModel Bean，必须指定用哪个 |
| `streamingChatModel` | `"qwenStreamingChatModel"` | 用流式聊天模型，返回 `Flux<String>` 逐 token 输出 |
| `chatMemoryProvider` | `"chatMemoryProviderXiaozhi"` | 会话记忆提供者，每次调用时根据 memoryId 加载历史对话 |
| `tools` | `"appointmentTools"` | LLM 可调用的 Java 工具 Bean。LLM 会自动判断是否需要调用、填充参数 |
| `contentRetriever` | `"contentRetrieverPinecone"` | RAG 内容检索器，从向量库中搜索相关知识注入 Prompt |

**`@SystemMessage(fromResource = "...")`** — 系统提示词

从 `resources/prompts/xiaozhi-prompt-template.txt` 读取，定义了小智的**人设、能力、规则**。这个提示词会被放在每轮对话的最前面。

**方法签名 `Flux<String> chat(@MemoryId int memoryId, @UserMessage String userMessage)`**

| 部分 | 含义 |
|------|------|
| `Flux<String>` | 返回类型。不是 `String`，是响应式流！LLM 每生成一个 token 就发射一次 |
| `@MemoryId int memoryId` | 会话标识。同一 memoryId = 同一对话上下文。来自前端的 UUID 转换值 |
| `@UserMessage String userMessage` | 用户输入。会自动加到对话中作为 UserMessage |

#### 关键理解

**这个接口没有实现类！** LangChain4j 在 Spring 启动时通过动态代理自动生成实现。你写的是"我要什么"，框架帮你实现"怎么做"。

---

### ⭐ XiaozhiAgentConfig.java — Agent 零件装配

```java
@Configuration
public class XiaozhiAgentConfig {

    @Autowired private MongoChatMemoryStore mongoChatMemoryStore;
    @Autowired private EmbeddingModel embeddingModel;
    @Autowired private EmbeddingStore<TextSegment> embeddingStore;

    // ① 聊天记忆提供者
    @Bean
    public ChatMemoryProvider chatMemoryProviderXiaozhi() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)            // 只保留最近 20 条消息
                .chatMemoryStore(mongoChatMemoryStore)  // 持久化到 MongoDB
                .build();
    }

    // ② Pinecone RAG 检索器
    @Bean
    public ContentRetriever contentRetrieverPinecone() {
        return EmbeddingStoreContentRetriever
                .builder()
                .embeddingModel(embeddingModel)     // 用哪个嵌入模型把问题向量化
                .embeddingStore(embeddingStore)     // 在哪个向量库中搜索
                .maxResults(10)                     // 最多返回 10 条
                .minScore(0.5)                      // 最低相似度 0.5，低于的不返回
                .build();
    }
}
```

#### `chatMemoryProviderXiaozhi()` 详解

```java
return memoryId -> MessageWindowChatMemory.builder()
        .id(memoryId)                           // 用 memoryId 隔离不同用户
        .maxMessages(20)                        // 滑动窗口：只保留最近 20 条
        .chatMemoryStore(mongoChatMemoryStore)  // 持久化策略：存 MongoDB
        .build();
```

- `memoryId -> ...` 是 Lambda 表达式。每次请求进来时，LangChain4j 调用这个 Lambda，传入 memoryId，返回该会话的 ChatMemory
- `maxMessages(20)` 是滑动窗口机制。对话超过 20 条时，最旧的自动丢弃。目的：
  - 控制 Token 消耗（每条历史消息都消耗 Token，LLM 按 Token 收费）
  - 防止上下文窗口溢出
- `chatMemoryStore(mongoChatMemoryStore)` 指定存储器。如果不指定，默认是内存存储，重启丢失

#### `contentRetrieverPinecone()` 详解

```java
return EmbeddingStoreContentRetriever.builder()
        .embeddingModel(embeddingModel)   // 把用户问题也用同一个模型向量化
        .embeddingStore(embeddingStore)   // Pinecone 向量存储
        .maxResults(10)                  // Top-10 相似文档
        .minScore(0.5)                   // 相关性阈值
        .build();
```

**RAG 检索流程**：
1. 用户问"神经内科有哪些专家"
2. `embeddingModel` 把这句话向量化 → 一个 N 维的浮点数数组
3. 在 Pinecone 的 `xiaozhi-index` 中搜索最相似的向量
4. 返回相似度 ≥ 0.5 的前 10 条文档片段
5. 这些片段作为"参考资料"注入 LLM 的 Prompt
6. LLM 基于参考资料生成回复

---

### ⭐ XiaozhiController.java — 聊天 REST 接口

```java
@Tag(name = "小智")
@RestController
@RequestMapping("/xiaozhi")
public class XiaozhiController {

    @Autowired
    private XiaozhiAgent xiaozhiAgent;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatFormDTO chatFormDTO) {
        return xiaozhiAgent.chat(chatFormDTO.getMemoryId(), chatFormDTO.getMessage());
    }
}
```

#### 关键点

**`produces = "text/stream;charset=utf-8"`**
- 告诉浏览器：这个响应是流式的，不要等完整响应再显示
- 浏览器收到一个 chunk 就能立刻渲染一个 chunk
- 这就是 ChatGPT 逐字输出效果的实现方式

**返回 `Flux<String>`**
- `Flux` 是 Project Reactor 的核心类型，表示 0~N 个异步数据流
- 每个 `String` 是 LLM 生成的一个 token（一个或多个字符）
- Spring WebFlux 自动将 `Flux<String>` 序列化为 SSE (Server-Sent Events) 流

---

### ⭐ AppointmentTools.java — Function Calling 工具

```java
@Component
public class AppointmentTools {

    @Autowired
    private AppointmentService appointmentService;

    // --- 工具 1: 预约挂号 ---
    @Tool(name = "预约挂号", value = "根据参数，先执行工具方法queryDepartment查询是否可预约，" +
            "并直接给用户回答是否可预约，并让用户确认所有预约信息，用户确认后再进行预约。" +
            "如果用户没有提供具体的医生姓名，请从向量存储中找到一位医生。")
    public String bookAppointment(Appointment appointment) {
        Appointment appointmentDB = appointmentService.getOne(appointment);
        if (appointmentDB == null) {
            appointment.setId(null);           // 防止 LLM 幻觉生成假 ID
            if (appointmentService.save(appointment)) {
                return "预约成功，并返回预约详情";
            } else {
                return "预约失败";
            }
        }
        return "您在相同的科室和时间已有预约";
    }

    // --- 工具 2: 取消预约 ---
    @Tool(name = "取消预约挂号",
           value = "根据参数，查询预约是否存在；如果存在则删除预约记录并返回"取消预约成功"，否则返回"取消预约失败"")
    public String cancelAppointment(Appointment appointment) {
        if (appointment == null) {
            return "参数无效，无法取消预约";
        }
        Appointment appointmentDB = appointmentService.getOne(appointment);
        if (appointmentDB != null) {
            boolean removed = appointmentService.removeById(appointmentDB.getId());
            return removed ? "取消预约成功" : "取消预约失败";
        }
        return "您没有预约记录，请核对预约科室、时间等信息";
    }

    // --- 工具 3: 查询号源 ---
    @Tool(name = "查询是否有号源",
           value = "根据科室名称、日期、时间段和医生名称（可选）查询是否有可预约号源，并返回结果")
    public boolean queryDepartment(
            @P(value = "科室名称") String name,
            @P(value = "日期") String date,
            @P(value = "时间，可选值：上午、下午") String time,
            @P(value = "医生名称", required = false) String doctorName
    ) {
        // TODO: 对接真实排班系统
        // 当前是 Stub（桩代码），始终返回 true
        return true;
    }
}
```

#### `@Tool` 注解详解

```java
@Tool(name = "预约挂号",       // LLM 看到的工具名称
      value = "根据参数..."    // LLM 看到的工具描述（决定什么时候调用）
)
```

- `name`：LLM 用它来决定"我需要调用预约挂号工具"
- `value`：LLM 用它来理解工具的功能、前置条件、使用时机。**这段描述直接决定了 Function Calling 的准确性！**

#### `@P` 注解 — 参数描述

```java
public boolean queryDepartment(
        @P(value = "科室名称") String name,      // LLM 知道这个参数填科室名
        @P(value = "日期") String date,          // LLM 知道填日期
        @P(value = "时间，可选值：上午、下午") String time,  // LLM 知道只能填上午/下午
        @P(value = "医生名称", required = false) String doctorName  // 可选参数
)
```

`@P` 注解让 LLM 理解每个参数的含义和约束，从而正确地从用户对话中提取参数。

---

## 第二优先级：数据和持久化

### Appointment.java — 预约实体

```java
@Data
@TableName("appointment")
public class Appointment implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;            // 自增主键

    @TableField("username")
    private String username;    // 预约人姓名

    @TableField("id_card")
    private String idCard;      // 身份证号

    @TableField("department")
    private String department;  // 预约科室

    @TableField("date")
    private String date;        // 预约日期

    @TableField("time")
    private String time;        // 预约时段

    @TableField("doctor_name")
    private String doctorName;  // 医生姓名（可选）
}
```

- `@Data`：Lombok，自动生成 getter/setter/toString/equals/hashCode
- `@TableName("appointment")`：MyBatis-Plus，映射到 MySQL 的 `appointment` 表
- `@TableId(type = IdType.AUTO)`：主键自增策略
- 这个实体既作为 ORM 实体，也直接作为 Function Calling 的参数接收对象

### MongoChatMemoryStore.java — MongoDB 记忆存储

```java
@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private MongoTemplate mongoTemplate;

    // ① 读取对话历史
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if (chatMessages == null) {
            return new LinkedList<>();    // 新会话，返回空列表
        }
        // 反序列化: JSON 字符串 → List<ChatMessage>
        return ChatMessageDeserializer.messagesFromJson(chatMessages.getContent());
    }

    // ② 保存对话历史
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        Update update = new Update();
        // 序列化: List<ChatMessage> → JSON 字符串
        update.set("content", ChatMessageSerializer.messagesToJson(messages));
        // upsert: 存在则更新，不存在则插入
        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    // ③ 删除对话历史
    @Override
    public void deleteMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
```

#### 关键设计

1. **`implements ChatMemoryStore`**：这是 LangChain4j 的 SPI 接口，实现了就能接入框架的记忆管理体系
2. **upsert 策略**：同一个 memoryId 永远只有一条 Mongo 文档，所以用 `upsert` 而不是 `insert` + `update`
3. **JSON 序列化**：使用 LangChain4j 内置的 `ChatMessageSerializer` / `ChatMessageDeserializer`，而不是手写 Jackson。因为 ChatMessage 是多态的（UserMessage、AiMessage、ToolExecutionResultMessage...），手动序列化容易出错
4. **每个 memoryId 独立**：不同用户/不同会话隔离，互不干扰

### AppointmentServiceImpl.java — 预约查询

```java
@Service
public class AppointmentServiceImpl
        extends ServiceImpl<AppointmentMapper, Appointment>
        implements AppointmentService {

    @Override
    public Appointment getOne(Appointment appointment) {
        LambdaQueryWrapper<Appointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Appointment::getUsername, appointment.getUsername());
        queryWrapper.eq(Appointment::getIdCard, appointment.getIdCard());
        queryWrapper.eq(Appointment::getDepartment, appointment.getDepartment());
        queryWrapper.eq(Appointment::getDate, appointment.getDate());
        queryWrapper.eq(Appointment::getTime, appointment.getTime());
        return baseMapper.selectOne(queryWrapper);
    }
}
```

- 继承 `ServiceImpl<AppointmentMapper, Appointment>`：获得 MyBatis-Plus 内置的 CRUD 方法（`save`、`removeById` 等）
- `LambdaQueryWrapper`：MyBatis-Plus 的类型安全查询构造器。`Appointment::getUsername` 通过方法引用避免字符串硬编码
- 五条件查询：姓名 + 身份证 + 科室 + 日期 + 时间 = 唯一预约

---

## 第三优先级：配置和基础设施

### EmbeddingStoreConfig.java — Pinecone 配置

```java
@Configuration
public class EmbeddingStoreConfig {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PineconeEmbeddingStore.builder()
                .apiKey("pcsk_...")
                .index("xiaozhi-index")
                .nameSpace("xiaozhi-namespace")
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS")
                        .region("us-east-1")
                        .dimension(embeddingModel.dimension())  // 从嵌入模型动态获取维度
                        .build())
                .build();
    }
}
```

- `PineconeEmbeddingStore`：LangChain4j 的 Pinecone 集成，封装了 Pinecone SDK
- `index("xiaozhi-index")`：Pinecone 中的索引名，所有向量存在这个索引下
- `nameSpace("xiaozhi-namespace")`：命名空间，同一索引下可以分多个命名空间隔离数据
- `dimension(embeddingModel.dimension())`：关键！向量维度必须与嵌入模型匹配
- ⚠️ **安全警告**：API Key 硬编码在代码中是严重安全问题，生产环境必须用环境变量

### application.properties — 全局配置

```properties
spring.application.name=langchain4j
server.port=8080

# LLM 配置 (阿里通义千问 OpenAI 兼容 API)
langchain4j.open-ai.chat-model.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
langchain4j.open-ai.chat-model.api-key=${DASHSCOPE_API_KEY}
langchain4j.open-ai.chat-model.model-name=qwen-plus

# 嵌入模型配置
langchain4j.community.dashscope.embedding-model.api-key=sk-...
langchain4j.community.dashscope.embedding-model.model-name=text-embedding-v3

# 流式模型配置
langchain4j.community.dashscope.streaming-chat-model.api-key=sk-...
langchain4j.community.dashscope.streaming-chat-model.model-name=qwen3.7-plus

# MongoDB 配置
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.username=admin
spring.data.mongodb.password=123456

# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/xiaozhi?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 日志
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
```

#### 为什么使用 OpenAI 兼容 API？

- 阿里 DashScope 提供了兼容 OpenAI 格式的 API 端点
- LangChain4j 的 `open-ai` 模块不仅能连 OpenAI，还能连任何 OpenAI 兼容的服务（Ollama、vLLM、DashScope...）
- `base-url` 从 `https://api.openai.com/v1` 改为 `https://dashscope.aliyuncs.com/compatible-mode/v1` 就切到了阿里云

#### 三个模型的区别

| 配置前缀 | 用途 |
|----------|------|
| `langchain4j.open-ai.chat-model` | 普通聊天模型 (`qwen-plus`)，被 SeparateChatAssistant 和 MemoryChatAssistant 使用 |
| `langchain4j.community.dashscope.embedding-model` | 嵌入模型 (`text-embedding-v3`)，用于 RAG 文档向量化 |
| `langchain4j.community.dashscope.streaming-chat-model` | 流式聊天模型 (`qwen3.7-plus`)，被 XiaozhiAgent 使用 |

---

### ⭐ xiaozhi-prompt-template.txt — 小智人设

```
你的名字是"小智"，你是一家名为"北京协和医院"的智能客服。
你是一个训练有素的医疗顾问和医疗伴诊助手。
你态度友好、礼貌且言辞简洁。

1、请仅在用户发起第一次会话时，和用户打个招呼，并介绍你是谁。
2、作为一个训练有素的医疗顾问：
   请基于当前临床实践和研究，针对患者提出的特定健康问题，提供详细、准确且实用的医疗建议。
   请同时考虑可能的病因、诊断流程、治疗方案以及预防措施，并给出在不同情境下的应对策略。
   对于药物治疗，请特别指明适用的药品名称、剂量和疗程。
   如果需要进一步的检查或就医，也请明确指示。
3、作为医疗伴诊助手，你可以回答用户就医流程中的相关问题，主要包含以下功能：
   AI分导诊：根据患者的病情和就医需求，智能推荐最合适的科室。
   AI挂号助手：实现智能查询是否有挂号号源服务；实现智能预约挂号服务；实现智能取消挂号服务。
4、你必须遵守的规则如下：
   在获取挂号预约详情或取消挂号预约之前，你必须确保自己知晓用户的
   姓名（必选）、身份证号（必选）、预约科室（必选）、预约日期（必选）、
   预约时间（必选，格式：上午 或 下午）、预约医生（可选）。
   当被问到其他领域的咨询时，要表示歉意并说明你无法在这方面提供帮助。
5、请在回答的结果中适当包含一些轻松可爱的图标和表情。
6、今天是 {{current_date}}。
```

#### 提示词工程分析

| 要素 | 内容 | 目的 |
|------|------|------|
| **角色定义** | "小智，北京协和医院智能客服" | 设定 AI 的身份认知 |
| **能力边界** | "医疗顾问 + 伴诊助手" | 明确 AI 能做什么 |
| **行为规范** | "友好、礼貌、言辞简洁" | 控制回复风格 |
| **必填信息规则** | 姓名、身份证号、科室、日期、时间 | **这是防止 LLM 瞎编信息的关键约束** |
| **拒绝策略** | "其他领域要表示歉意" | 防止超出能力范围的回答 |
| **动态变量** | `{{current_date}}` | LangChain4j 会自动替换为当前日期 |

**为什么提示词这么重要？**
- 在 LangChain4j 架构中，提示词 = Agent 的"宪法"
- Function Calling 的准确性、RAG 的触发时机、对话的风格，都取决于提示词怎么写
- `{{current_date}}` 这种模板变量由 LangChain4j 的 `PromptTemplate` 引擎自动填充

---

### UploadKnowledgeLibraryServiceImpl.java — 知识库上传

```java
@Service
public class UploadKnowledgeLibraryServiceImpl implements UploadKnowledgeLibraryService {

    @Autowired private EmbeddingStore<TextSegment> embeddingStore;
    @Autowired private EmbeddingModel embeddingModel;

    @Override
    public void uploadKnowledgeLibrary(MultipartFile[] files) {
        List<Document> documents = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // ① 保存为临时文件
                File tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
                file.transferTo(tempFile);

                // ② 根据文件类型选择解析器
                Document document;
                if (fileName.endsWith(".pdf")) {
                    document = FileSystemDocumentLoader.loadDocument(
                        tempFile.getAbsolutePath(),
                        new ApachePdfBoxDocumentParser()     // PDF 专用解析器
                    );
                } else {
                    document = FileSystemDocumentLoader.loadDocument(
                        tempFile.getAbsolutePath()           // 默认解析器 (txt/md)
                    );
                }
                documents.add(document);
                tempFile.delete();  // ③ 清理临时文件
            }
        }

        // ④ 批量向量化并存入 Pinecone
        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)    // 存到 Pinecone
                .embeddingModel(embeddingModel)    // 用 DashScope 向量化
                .build()
                .ingest(documents);                // 内部自动完成: 分块→向量化→存储
    }
}
```

**`EmbeddingStoreIngestor` 内部做了什么？**

```
Document (原始文件)
    ↓
DocumentSplitter (默认按段落/句子切分)
    ↓
List<TextSegment> (多个文本片段)
    ↓
EmbeddingModel.embedAll() (每条片段 → 向量)
    ↓
EmbeddingStore.addAll() (向量 + 元数据存入 Pinecone)
```

---

## 第四优先级：辅助模块

### ChatWindow.vue — 前端聊天组件（核心代码）

```javascript
// ① 初始化：读取或生成 UUID 作为 memoryId
const initUUID = () => {
    let storedUUID = localStorage.getItem('user_uuid')
    if (!storedUUID) {
        storedUUID = uuidToNumber(uuidv4())   // UUID → 6位数字
        localStorage.setItem('user_uuid', storedUUID)
    }
    uuid.value = storedUUID
}

// ② 发送消息（核心请求）
const sendRequest = (message) => {
    axios.post('/api/xiaozhi/chat',              // Vite 代理到 localhost:8080
        { memoryId: uuid.value, message },
        {
            responseType: 'stream',              // ⭐ 关键：流式模式
            onDownloadProgress: (e) => {
                const fullText = e.event.target.responseText  // 累积的全部文本
                let newText = fullText.substring(lastMsg.content.length)
                lastMsg.content += newText        // 增量追加新 token
                scrollToBottom()                  // 实时滚动
            },
        }
    )
}

// ③ Markdown 渲染
const markdownToHtml = (content) => {
    marked.setOptions({ breaks: true, gfm: true })
    return marked.parse(content)
}
```

### CalculatorTools.java — 计算器工具

```java
@Component
public class CalculatorTools {
    @Tool(name = "加法", value = "返回两个参数相加之和")
    double sum(@ToolMemoryId int memoryId, @P("加数1") double a, @P("加数2") double b) {
        return a + b;
    }

    @Tool(name = "平方根", value = "返回给定参数的平方根")
    double squareRoot(@ToolMemoryId int memoryId, double x) {
        return Math.sqrt(x);
    }
}
```

这个工具仅被 `SeparateChatAssistant` 使用，用于验证工具调用机制。

### CodeGenerator.java — MyBatis-Plus 代码生成器

这是一个**开发工具**，不是运行时代码。执行它会：
1. 连接 MySQL `xiaozhi` 数据库
2. 读取 `appointment` 表结构
3. 自动生成 `Appointment.java`、`AppointmentMapper.java`、`AppointmentMapper.xml`
4. 使用 Freemarker 模板引擎渲染输出

---

# 第四章：核心流程

## 4.1 完整对话流程（端到端）

```
时间线                      动作                                  数据/状态
───────                    ──────                                ──────────

T0  用户打开网页
    │
    ├── Vue 挂载 (onMounted)
    │   ├── initUUID(): 从 localStorage 读取或生成新 UUID
    │   │   UUID "a3f2b1c4..." → 取前6位16进制 → 转数字 → memoryId = 123456
    │   │
    │   └── hello(): 自动发送 "你好"
    │
    ▼
T1  axios POST /api/xiaozhi/chat              { memoryId: 123456, message: "你好" }
    │
    ▼
T2  Vite Dev Server 代理转发                  /api → http://localhost:8080
    │
    ▼
T3  XiaozhiController.chat()                  接收 ChatFormDTO
    │  └── xiaozhiAgent.chat(123456, "你好")
    │
    ▼
T4  LangChain4j 动态代理拦截
    │
    ├─→ Step 1: chatMemoryProviderXiaozhi.get(123456)
    │   └── MongoChatMemoryStore.getMessages(123456)
    │       └── MongoDB: db.chat_messages.findOne({memoryId: 123456})
    │           └── 结果: null (新会话，没有历史)
    │           └── 返回: 空的 LinkedList
    │
    ├─→ Step 2: 加载系统提示词
    │   └── 读取 xiaozhi-prompt-template.txt
    │   └── 替换 {{current_date}} → "2025-04-14"
    │
    ├─→ Step 3: 组装 Prompt
    │   ┌─────────────────────────────────────────┐
    │   │ [SystemMessage]                          │
    │   │ 你的名字是"小智"...                       │
    │   │ ...完整提示词...                          │
    │   │ 今天是 2025-04-14                        │
    │   ├─────────────────────────────────────────┤
    │   │ [UserMessage]                            │
    │   │ 你好                                     │
    │   └─────────────────────────────────────────┘
    │
    ├─→ Step 4: 调用 Qwen3.7-plus (流式)
    │   └── HTTP POST https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
    │       Headers: { Authorization: "Bearer sk-..." }
    │       Body: { model: "qwen3.7-plus", messages: [...], stream: true }
    │
    ├─→ Step 5: Qwen 开始流式返回
    │   └── token 1: "您" → token 2: "好" → token 3: "！" → ...
    │
    ├─→ Step 6: 每个 token 通过 Flux<String> 推送给 Controller
    │   └── Controller → HTTP Response (text/stream) → 浏览器
    │
    ├─→ Step 7: 前端 onDownloadProgress 逐 token 渲染
    │   └── lastMsg.content += "您" → marked 渲染 → 显示
    │   └── ...（打字机效果）
    │
    └─→ Step 8: LLM 回复完成，保存对话
        └── MongoChatMemoryStore.updateMessages(123456, [...])
            └── MongoDB upsert

最终用户看到:  "您好！我是小智，北京协和医院的智能客服 😊 请问有什么可以帮您的？"
```

## 4.2 Function Calling 流程（预约挂号）

这是 LangChain4j 最精妙的能力 —— LLM 不只是说话，它真的能操作数据库。

```
场景：用户已经告诉过姓名和身份证号，现在说"帮我挂神经内科，明天上午"

───────

Step 1: LLM 分析用户意图
    ┌─────────────────────────────────────────────────────────┐
    │ LLM 内部推理（不可见，但大致如此）：                        │
    │ "用户要挂号。已有信息：姓名=张三，身份证=110101...          │
    │  科室=神经内科，日期=2025-04-15，时间=上午"                │
    │ "系统提示词要求：先查询号源，再让用户确认，最后才预约"       │
    │ "我先调 queryDepartment 查号源"                          │
    └─────────────────────────────────────────────────────────┘

Step 2: LLM 返回 tool_call（而不是文本回复）
    ┌─────────────────────────────────────────────────────────┐
    │ LLM 的响应不是文字，而是一个 JSON：                        │
    │ {                                                       │
    │   "tool_calls": [{                                      │
    │     "id": "call_abc123",                                │
    │     "type": "function",                                 │
    │     "function": {                                       │
    │       "name": "查询是否有号源",                            │
    │       "arguments": "{\"name\":\"神经内科\",               │
    │                      \"date\":\"2025-04-15\",            │
    │                      \"time\":\"上午\"}"                  │
    │     }                                                   │
    │   }]                                                    │
    │ }                                                       │
    └─────────────────────────────────────────────────────────┘

Step 3: LangChain4j 框架截获 tool_call
    │
    ├── 解析 JSON → 找到 "查询是否有号源"
    ├── 在 Spring 容器中搜索匹配的 @Tool 方法
    ├── 找到 AppointmentTools.queryDepartment()
    ├── 用 JSON arguments 构造方法参数
    │   ├── name = "神经内科"
    │   ├── date = "2025-04-15"
    │   └── time = "上午"
    └── 反射调用: queryDepartment("神经内科", "2025-04-15", "上午", null) → 返回 true

Step 4: 工具结果返回给 LLM
    ┌─────────────────────────────────────────────────────────┐
    │ 框架把工具执行的返回值封装成 ToolExecutionResultMessage    │
    │ LLM 收到后知道：神经内科明天上午有号                         │
    └─────────────────────────────────────────────────────────┘

Step 5: LLM 回复用户，让用户确认
    "神经内科明天（2025-04-15）上午有号源 ✅ 请确认以下预约信息..."

Step 6: 用户说 "确认"
    ├── LLM 再次发出 tool_call → "预约挂号"
    ├── LangChain4j 反射调用 → bookAppointment(appointment)
    │   ├── appointmentService.getOne(appointment) → null (没有重复)
    │   ├── appointment.setId(null)  // 防幻觉
    │   └── appointmentService.save(appointment) → MySQL INSERT
    └── 返回: "预约成功"

Step 7: LLM 生成最终回复
    "预约成功！🎉 预约详情：📋 神经内科 📅 2025年4月15日 ⏰ 上午 👤 张三"
```

### Function Calling 的关键理解

```
┌──────────────────────────────────────────────────────────┐
│                    控制反转 (IoC)                          │
│                                                          │
│  传统编程：代码决定调用什么函数                               │
│    if (用户说"挂号") { bookAppointment(); }                │
│                                                          │
│  AI Agent：LLM 决定调用什么函数                              │
│    LLM 分析意图 → 选合适的 @Tool → 自动填参数 → 框架执行      │
│                                                          │
│  代码从"决策者"变成"能力提供者"                              │
└──────────────────────────────────────────────────────────┘
```

## 4.3 RAG 检索增强生成流程

```
场景：用户问 "神经内科有哪些专家？"

───────

Step 1: 用户问题向量化
    "神经内科有哪些专家？"
        ↓
    DashScope text-embedding-v3
        ↓
    [0.023, -0.451, 0.891, ..., -0.302]  ← 向量

Step 2: Pinecone 相似度搜索
    SELECT * FROM xiaozhi-index
    WHERE namespace = 'xiaozhi-namespace'
    ORDER BY cosine_similarity DESC
    LIMIT 10

    #1 相似度 0.92: "神经内科专家包括彭斌教授（脑血管病）、崔丽英教授..."
    #2 相似度 0.87: "彭斌教授，主任医师，擅长脑血管病、头痛、眩晕..."
    #3 相似度 0.81: "崔丽英教授：擅长运动神经元病、周围神经病..."
    #4-10 相似度 <0.5 → 被 minScore(0.5) 过滤掉

Step 3: 检索结果注入 Prompt
    [SystemMessage] 你是小智...
    [UserMessage] 神经内科有哪些专家？
    [检索到的上下文] 1. 神经内科专家包括... 2. 彭斌教授... 3. 崔丽英教授...

Step 4: LLM 基于上下文生成回复
    "根据知识库信息，神经内科的主要专家有：
     👨‍⚕️ 彭斌教授 — 主任医师，擅长脑血管病、头痛、眩晕
     👩‍⚕️ 崔丽英教授 — 擅长运动神经元病、周围神经病
     ..."
```

### 为什么需要 RAG？

```
没有 RAG:                         有 RAG:

用户: "神经内科有哪些专家？"        用户: "神经内科有哪些专家？"
  ↓                                ↓
LLM 只能靠训练数据"猜"             从 Pinecone 检索到真实的科室文档
  ↓                                ↓
可能编造不存在的专家               基于真实数据回答
("幻觉" Hallucination)             准确、可信
```

## 4.4 知识库上传流程

```
POST /documents/upload
Content-Type: multipart/form-data
files: [神经内科.md, 口腔科.md, 医院信息.pdf]

    ↓

UploadKnowledgeLibraryController.uploadKnowledgeLibrary(files)

    ↓

UploadKnowledgeLibraryServiceImpl.uploadKnowledgeLibrary(files)
│
├── for each file:
│   ├── 保存为临时文件 (File.createTempFile)
│   ├── 判断文件类型:
│   │   ├── .pdf → ApachePdfBoxDocumentParser
│   │   └── .md/.txt → 默认 DocumentParser
│   ├── 加载为 Document 对象
│   └── 删除临时文件
│
└── EmbeddingStoreIngestor.ingest(documents)
    ├── DocumentSplitter.split()    → 切分成 TextSegment
    ├── EmbeddingModel.embed()      → 向量化
    └── EmbeddingStore.add()        → 存入 Pinecone

返回: "上传成功"
```

## 4.5 对话记忆生命周期

```
会话生命周期

前端
├── 首次打开 → 生成 UUID → 存 localStorage
├── UUID 转 memoryId (6位数字)
└── 点"新聊天" → 清除 localStorage → 刷新 → 新 UUID

后端
├── memoryId 不存在 → MongoChatMemoryStore → 返回空列表
│   → MessageWindowChatMemory 初始为空
├── 每轮对话后 → updateMessages() → upsert 到 MongoDB
├── 消息超过 20 条 → 最旧的自动丢弃（滑动窗口）
└── 用户长期不访问 → MongoDB 数据保留（持久化，不丢）

MongoDB
└── chat_messages 集合
    { _id: ObjectId(...), memoryId: 123456,
      content: "[{\"type\":\"USER\",...},{\"type\":\"AI\",...},...]"
```

滑动窗口原理：

```
第 1 轮: [msg1]                        ← 1条
第 2 轮: [msg1, msg2]                  ← 2条
...
第 20 轮: [msg1, msg2, ..., msg20]     ← 20条（满）
第 21 轮: [msg2, msg3, ..., msg21]     ← msg1 被丢弃
第 22 轮: [msg3, msg4, ..., msg22]     ← msg2 被丢弃
```

为什么用滑动窗口？Token 是收费的，每轮把 100 条历史全发过去成本极高，而且太久远的历史对当前对话几乎无用。20 条是兼顾上下文连贯性和成本的经验值。

## 4.6 系统启动流程

```
应用启动 (Spring Boot)
    │
    ├── 1. 加载 application.properties
    │   ├── 解析 ${DASHSCOPE_API_KEY} 环境变量
    │   ├── 创建 OpenAiChatModel Bean (qwen-plus)
    │   ├── 创建 DashScopeEmbeddingModel Bean (text-embedding-v3)
    │   └── 创建 DashScopeStreamingChatModel Bean (qwen3.7-plus)
    │
    ├── 2. 执行 @Configuration 类
    │   ├── EmbeddingStoreConfig → 创建 PineconeEmbeddingStore Bean
    │   ├── XiaozhiAgentConfig → 创建 ChatMemoryProvider + ContentRetriever Bean
    │   ├── MemoryChatAssistantConfig → 创建 ChatMemory Bean
    │   └── SeparateChatAssistantConfig → 创建 ChatMemoryProvider Bean
    │
    ├── 3. 扫描 @AiService 接口
    │   ├── XiaozhiAgent → LangChain4j 生成动态代理 → 注册 Spring Bean
    │   ├── MemoryChatAssistant → 生成代理 → 注册 Bean
    │   └── SeparateChatAssistant → 生成代理 → 注册 Bean
    │
    ├── 4. 扫描 @Component / @Service
    │   ├── AppointmentTools, CalculatorTools → 注册 Bean
    │   ├── MongoChatMemoryStore → 注册 Bean
    │   └── AppointmentServiceImpl → 注册 Bean
    │
    ├── 5. 启动嵌入式 Tomcat (端口 8080)
    │
    └── 6. 应用就绪，等待请求
```

---

# 第五章：设计思想

## 5.1 核心思想一：声明式 AI Agent（最重要）

### 传统方式 vs LangChain4j 方式

**传统方式（如果没有 LangChain4j）**：

```java
// 你需要手写 300+ 行代码
@PostMapping("/chat")
public String chat(@RequestBody ChatFormDTO dto) {
    // 1. 手动从数据库加载历史
    List<Message> history = mongoTemplate.find(...);

    // 2. 手动拼接系统提示词
    String systemPrompt = Files.readString(Path.of("prompts/xiaozhi.txt"));
    systemPrompt = systemPrompt.replace("{{current_date}}", LocalDate.now().toString());

    // 3. 手动构建请求体
    List<Map<String, String>> messages = new ArrayList<>();
    messages.add(Map.of("role", "system", "content", systemPrompt));
    for (Message msg : history) {
        messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
    }
    messages.add(Map.of("role", "user", "content", dto.getMessage()));

    // 4. 手动调 HTTP API
    String response = restTemplate.postForObject(
        "https://dashscope.aliyuncs.com/...", ..., String.class);

    // 5. 手动解析 tool_calls（极其复杂）
    // 6. 手动保存对话
    // ... 又是 50 行
}
```

**LangChain4j 方式**：

```java
// 20 行，一个接口
@AiService(
    streamingChatModel = "qwenStreamingChatModel",
    chatMemoryProvider = "chatMemoryProviderXiaozhi",
    tools = "appointmentTools",
    contentRetriever = "contentRetrieverPinecone"
)
public interface XiaozhiAgent {
    @SystemMessage(fromResource = "prompts/xiaozhi-prompt-template.txt")
    Flux<String> chat(@MemoryId int memoryId, @UserMessage String userMessage);
}
```

### 核心洞察

```
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│    LangChain4j 的本质：把 AI 应用模式抽象成注解                │
│                                                              │
│    你要的不是调用 API → 你要的是"一个有记忆、会查资料、       │
│    能操作系统的 AI 助手"                                       │
│                                                              │
│    框架帮你解决"怎么做"：                                      │
│      @AiService       → 如何成为 AI 服务                      │
│      chatMemoryProvider → 如何记住对话                        │
│      tools            → 如何操作数据库                        │
│      contentRetriever → 如何查知识库                          │
│      @SystemMessage   → 如何定义人设                          │
│      @MemoryId        → 如何隔离会话                          │
│                                                              │
│    你只需要声明"我要什么"                                      │
└──────────────────────────────────────────────────────────────┘
```

就像 Spring 的 `@Autowired` 让你不用手动 `new` 对象，LangChain4j 的 `@AiService` 让你不用手动写 AI 调用流水线。

## 5.2 核心思想二：控制反转从代码转移到 AI

```
传统的业务逻辑控制:                 AI Agent 的业务逻辑控制:

  if (意图 == "挂号") {              LLM 分析意图
      bookAppointment();              ↓
  } else if (意图 == "取消") {       LLM 决定: 调 bookAppointment Tool
      cancelAppointment();            ↓
  } else if (意图 == "查询") {       LLM 自动填参数
      queryDepartment();              ↓
  } else {                           框架自动执行 Java 方法
      generalReply();                 ↓
  }                                  LLM 基于结果生成回复

  ❌ 每个分支都要手写                ✅ 不需要写任何分支判断
  ❌ 新功能 = 新 if 分支             ✅ 新功能 = 新 @Tool + 描述
  ❌ 边界情况需要枚举                ✅ LLM 泛化处理各种表达方式
```

**这改变了什么？**

```
传统程序员的角色：用代码描述所有可能的逻辑路径
AI 时代程序员的角色：给 AI 提供能力和约束，让 AI 自己决策

代码量大幅下降，但"提示词工程"变成新的关键技能
这个项目的代码本身很简单（每个类几十行），
但组合起来的能力非常强大 —— 这就是 AI 时代的编程范式
```

## 5.3 核心思想三：RAG = 给 LLM 外挂知识库

LLM 的知识截止于训练数据。协和医院的科室信息、专家名单、地址电话，训练数据里**可能有也可能没有**，即使有也可能**过时或不准确**。

**解决方案：RAG**

```
                          ┌──────────────────┐
                          │   用户提问         │
                          └────────┬─────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │                             │
                    ▼                             ▼
          ┌─────────────────┐           ┌─────────────────┐
          │  向量化提问       │           │  Prompt:         │
          │  [0.02, -0.45..] │           │  "你是小智..."    │
          └────────┬────────┘           │  + 用户问题       │
                   │                    │  + 检索结果       │
                   ▼                    └────────┬────────┘
          ┌─────────────────┐                    │
          │  Pinecone        │                    │
          │  相似度搜索       │                    │
          │  Top-K 相关文档   │                    │
          └────────┬────────┘                    │
                   │                             │
                   └──────────┬──────────────────┘
                              ▼
                    ┌──────────────────┐
                    │  LLM 生成回复     │
                    │  基于真实知识库    │
                    └──────────────────┘
```

**面试金句**：> "RAG 本质上是给 LLM 外挂了一个可实时更新的知识库，解决了 LLM 知识过时和幻觉问题。"

## 5.4 核心思想四：Function Calling = 给 LLM 外挂"手"

```
┌──────────────────────────────────────────────────────────┐
│   LLM = 大脑（思考 + 决策）                                │
│   Function Calling = 手（执行操作）                        │
│                                                          │
│   大脑分析后决定"我要做什么" → 手去执行 → 结果传回大脑      │
│                                                          │
│   在这个项目中：                                           │
│   - LLM 决定要预约挂号                                     │
│   - @Tool bookAppointment() 就是执行预约的"手"            │
│   - 手通过 MyBatis-Plus 把数据写入 MySQL                  │
│   - 结果传回 LLM，LLM 生成友好回复                         │
└──────────────────────────────────────────────────────────┘
```

**面试金句**：> "Function Calling 机制让 LLM 从'聊天机器人'升级为'能执行操作的智能体'。"

## 5.5 核心思想五：ChatMemory = 会话状态管理

HTTP 是无状态的，LLM API 也是无状态的。怎么让 AI "记住"上一轮说了什么？

| 策略 | 这个项目哪里用了 | 特点 |
|------|-----------------|------|
| **内存记忆** (In-Memory) | `MemoryChatAssistantConfig` | 重启丢失，仅测试用 |
| **持久化记忆** (MongoDB) | `XiaozhiAgentConfig` | 重启不丢，按 memoryId 隔离 |
| **持久化 + 滑动窗口** | `XiaozhiAgentConfig` (maxMessages=20) | 持久化 + 自动裁剪 |

**面试金句**：> "基于 MongoDB 实现分布式会话记忆，不同用户通过 memoryId 隔离，滑动窗口控制 Token 消耗。"

## 5.6 核心思想六：流式输出 = 用户体验关键

```
非流式 (普通 HTTP):
  用户发送消息 → 等待 3-5 秒 → 一次性显示全部回复
  用户感知: "卡住了吗？" → 焦虑 → 差体验

流式 (SSE/Streaming):
  用户发送消息 → 0.5 秒 → "您" → 0.1秒 → "好" → 0.1秒 → "！"
  用户感知: "AI 在打字" → 自然 → 好体验（像 ChatGPT）
```

| 层 | 技术 | 作用 |
|-----|------|------|
| LLM 层 | Qwen3.7-plus `stream: true` | LLM 逐 token 返回 |
| 后端层 | WebFlux `Flux<String>` + `text/stream` | token 逐个推送到 HTTP 响应 |
| 前端层 | axios `responseType: 'stream'` + `onDownloadProgress` | 增量渲染到页面 |

## 5.7 三大 AI 开发范式总结

```
┌──────────────────────────────────────────────────────────────┐
│              AI 应用开发的三大范式                              │
│                                                              │
│  1. RAG（检索增强生成）                                        │
│     外挂知识库 → LLM 回答基于真实数据，减少幻觉                  │
│     适用：客服、文档问答、知识库检索                             │
│                                                              │
│  2. Function Calling（工具调用）                               │
│     LLM 操作真实系统 → 不只是聊天，能真正做事                    │
│     适用：自动化流程、数据操作、系统集成                         │
│                                                              │
│  3. Agent（智能体）                                           │
│     RAG + Function Calling + ChatMemory = Agent               │
│     LLM 在上下文中自主决策：该说话还是该调工具？                  │
│     该查资料还是该执行操作？                                    │
│     适用：复杂业务场景的端到端 AI 助手                          │
│                                                              │
│  这个项目 = 三个范式全都有 ✅                                   │
└──────────────────────────────────────────────────────────────┘
```

## 5.8 项目亮点总结（面试用）

1. **声明式 AI Agent 架构**：通过 `@AiService` 接口 + 注解，将 LLM 调用、对话记忆、工具调用、RAG 检索四大能力统一在一个接口中，无需手写任何 LLM API 调用代码

2. **完整的 Agent 闭环**：LLM 不仅回答问题，还能自主决策调用 Function Calling 操作 MySQL 数据库，实现从"理解意图 → 查询号源 → 确认信息 → 写入数据库"的完整业务闭环

3. **RAG + Function Calling 结合**：知识库检索和工具调用不是独立的，LLM 在同一轮对话中可以同时查知识库（获取专家信息）和调工具（执行预约操作）

4. **分布式会话管理**：基于 MongoDB 实现 ChatMemory 持久化，支持多用户、多会话隔离，滑动窗口控制 Token 成本

5. **端到端流式体验**：从 LLM → WebFlux → 前端 axios stream，实现 token 级别的实时流式输出

---

# 第六章：简历与面试

## 6.1 简历项目描述（3 种版本）

### 版本 A：突出技术栈（适合 Java 后端岗）

> **小智医疗 — 基于 LangChain4j 的 AI 智能导诊系统**
>
> 基于 Spring Boot 3.2 + LangChain4j 框架构建的 AI 医疗智能体。集成阿里通义千问大模型 (Qwen3.7-plus)，通过声明式 @AiService 接口实现 LLM 调用、Function Calling 工具调用、RAG 检索增强生成三大 AI 能力。采用 MyBatis-Plus 操作 MySQL 存储预约数据，MongoDB 实现分布式会话记忆持久化，Pinecone 向量数据库存储医疗知识库。前端基于 Vue 3 + Element Plus 构建流式聊天 UI，后端使用 WebFlux 实现 token 级流式响应。
>
> **技术栈**：Java 17, Spring Boot 3.2, LangChain4j 1.0, MyBatis-Plus 3.5, MySQL 8.0, MongoDB, Pinecone(向量数据库), Vue 3, Element Plus, WebFlux, Swagger/Knife4j

### 版本 B：突出业务价值（适合简历空间充裕）

> **小智医疗 — AI 医疗智能导诊与预约系统**
>
> - 设计并实现基于 LangChain4j 的声明式 AI Agent 架构，将 LLM 调用、对话记忆、工具调用、RAG 检索统一为一个 @AiService 接口，代码量从传统方式的 300+ 行降低到 20 行
> - 实现 Function Calling 机制打通 AI 与业务数据库：LLM 自主解析用户意图并调用预约/取消/查号源等 Tool，通过 MyBatis-Plus 操作 MySQL 完成真实业务闭环
> - 构建 RAG 知识库：使用 DashScope Embedding 模型对协和医院文档向量化后存入 Pinecone，实现语义检索增强回答准确性
> - 实现分布式会话管理：基于 MongoDB 实现 ChatMemory 持久化，滑动窗口控制 Token 成本，支持多用户多会话上下文隔离
> - 实现端到端流式对话：WebFlux + text/stream + axios stream 实现 token 级实时输出，提升用户体验

### 版本 C：精简版（适合空间紧张）

> **小智医疗 — 基于 LangChain4j + 通义千问的 AI 医疗智能体** | Java 17, Spring Boot 3.2, LangChain4j, MySQL, MongoDB, Pinecone, Vue 3
>
> - 使用 LangChain4j @AiService 声明式构建 AI Agent，集成 LLM 调用、Function Calling、RAG 检索、对话记忆四大能力
> - 实现 Function Calling 调用 MyBatis-Plus 操作 MySQL，LLM 自主决策调用预约/取消/查询工具
> - 基于 Pinecone 向量库 + DashScope Embedding 构建医疗知识 RAG，配置检索阈值和 Top-K 过滤
> - 基于 MongoDB 实现 ChatMemory 持久化 + 滑动窗口，WebFlux 实现 token 级流式输出

## 6.2 面试高频问答

### Q1：这个项目是做什么的？

> 这是一个 AI 医疗智能导诊系统。用户通过网页聊天窗口与 AI 客服"小智"对话，可以咨询医疗问题、查询科室信息、预约挂号、取消预约。系统后端基于 Spring Boot + LangChain4j，集成了阿里通义千问大模型，通过 Function Calling 机制让 AI 能够操作 MySQL 数据库完成真实的预约业务，通过 RAG 机制从 Pinecone 向量库检索医院知识来增强回答准确性。

### Q2：LangChain4j 是什么？和直接调 OpenAI API 有什么区别？

> LangChain4j 是 Java 生态的 LLM 集成框架，类似 Python 的 LangChain。它把 AI 应用开发中的常见模式——Prompt 管理、对话记忆、工具调用（Function Calling）、RAG 检索——抽象成了注解和接口。
>
> 如果直接调 API，我需要手动完成：加载历史对话、拼接系统提示词、构建 HTTP 请求、解析 JSON 响应、处理工具调用循环、保存对话记录。使用 LangChain4j，我只需定义一个 `@AiService` 接口，用注解声明用什么模型、什么记忆策略、什么工具、什么检索器，框架在运行时通过动态代理自动生成所有实现。代码量从几百行降到几十行。

### Q3：什么是 Function Calling？你项目中怎么用的？

> Function Calling 是让 LLM 能够调用外部函数/API 的机制。LLM 不只是生成文本回复，它可以在需要时返回一个 tool_call 请求，包含函数名和参数 JSON，框架截获后反射调用对应的 Java 方法，把执行结果返回给 LLM，LLM 再基于结果生成最终回复。
>
> 在我的项目中，我在 `AppointmentTools` 类中用 `@Tool` 注解定义了三个工具：预约挂号、取消预约、查询号源。当用户在对话中表达挂号意图时，LLM 会自动调用对应的工具方法，方法通过 MyBatis-Plus 操作 MySQL 数据库。

### Q4：什么是 RAG？你项目中怎么用的？

> RAG 是 Retrieval-Augmented Generation，检索增强生成。核心思路是：在 LLM 回答之前，先从外部知识库中检索相关信息，把检索结果作为"参考资料"一起给 LLM，让 LLM 基于真实数据回答，从而减少幻觉、提高准确性。
>
> 在我的项目中，我用 DashScope 的 text-embedding-v3 模型把协和医院的科室信息、专家介绍等文档向量化后存入 Pinecone 向量数据库。用户提问时，系统先把问题向量化，在 Pinecone 中做相似度搜索（配置了 minScore=0.5 和 maxResults=10），把 Top-K 相关文档片段注入 Prompt 上下文，LLM 基于这些真实资料生成回答。

### Q5：对话记忆是怎么管理的？为什么用 MongoDB？

> 我实现了 LangChain4j 的 `ChatMemoryStore` 接口，用 MongoDB 做持久化存储。每个会话有一个唯一的 memoryId（来自前端的 UUID），对话记录序列化为 JSON 存到 MongoDB 的 `chat_messages` 集合中。
>
> 选择 MongoDB 不是 MySQL 的原因：对话记忆是非结构化的 JSON 数据，MongoDB 的文档模型天然适合；upsert 操作可以让每个会话只保留一条记录，读写高效。另外用了 `MessageWindowChatMemory` 的滑动窗口机制，只保留最近 20 条消息，旧的自动丢弃——这是为了控制 Token 消耗。

### Q6：流式输出是怎么实现的？

> 三层配合实现：
> 1. **LLM 层**：Qwen3.7-plus 的 API 支持 `stream: true` 参数，开启后 LLM 逐个 token 返回 SSE 流
> 2. **后端层**：Spring WebFlux 的 `Flux<String>` 作为返回类型，配合 Controller 上的 `produces = "text/stream"`，每个 token 到达就立刻推送到 HTTP 响应
> 3. **前端层**：axios 设置 `responseType: 'stream'`，在 `onDownloadProgress` 回调中增量获取新文本，通过 `marked` 库实时渲染 Markdown 到页面上
>
> 效果就是像 ChatGPT 一样的打字机输出体验。

### Q7：如果请求量很大，怎么扩展？

> 1. **水平扩展**：Spring Boot 应用本身无状态（会话状态在 MongoDB），可以直接增加实例 + Nginx 负载均衡
> 2. **MongoDB**：可以升级为 MongoDB 副本集或分片集群
> 3. **LLM 调用**：可以引入缓存层，或者对 LLM API 做限流
> 4. **Pinecone**：本身就是云服务，自动伸缩

### Q8：项目还有什么可以改进的地方？

> 1. **queryDepartment 目前是 stub**：实际应该对接医院的 HIS（医院信息系统）排班接口
> 2. **安全性**：API Key 硬编码需要改为环境变量或配置中心（该问题已在后续提交中修复）
> 3. **用户认证**：目前没有登录机制，真正的医疗系统需要患者身份认证
> 4. **消息队列**：高并发场景可以用消息队列缓冲 LLM 请求
> 5. **监控和告警**：接入 Prometheus + Grafana 监控 LLM 调用延迟和成功率
> 6. **AOP 切面**：可以加切面记录每次 LLM 调用的耗时和 Token 消耗

### Q9：做这个项目时遇到的最大的挑战是什么？

> 最大的挑战是理解 LangChain4j 的设计理念。刚开始我试图用传统方式写代码——手动拼接 Prompt、手动解析 JSON、手动管理对话历史，写了上百行代码后发现非常难维护。后来理解了 `@AiService` 的声明式设计后，才意识到框架已经把所有这些模式都抽象好了。最大的转变是从"我怎么实现"转向"我需要声明什么"——这是 AI 时代编程的一个核心范式转变。

### Q10：你怎么测试 LLM 的输出质量？

> 1. **提示词迭代**：写好提示词后反复测试，看回复是否准确、是否遵守规则
> 2. **单元测试**：Tools 方法的逻辑用 JUnit 测试（因为它们是纯 Java 方法）
> 3. **集成测试**：LangChain4j 支持用测试配置替换真实 LLM，Mock 掉 LLM 调用来验证整体流程
> 4. **人工评估**：对于对话质量，最终还是需要人工评估回复的准确性、友好度、规则遵守情况

## 6.3 技术问题速答卡

| 问题 | 一句话答案 |
|------|-----------|
| 用什么框架集成 LLM？ | LangChain4j，Java 版 LangChain |
| 用哪个大模型？ | 阿里通义千问 Qwen3.7-plus（流式对话）+ text-embedding-v3（嵌入） |
| 怎么连接阿里云的 API？ | 通过 OpenAI 兼容接口，base-url 指向 DashScope |
| Function Calling 怎么实现？ | `@Tool` 注解 + LangChain4j 自动反射调用 |
| 知识库怎么存的？ | DashScope 向量化 → Pinecone 向量库存储 |
| RAG 检索怎么控制质量？ | `minScore(0.5)` 过滤低相似度 + `maxResults(10)` 限制数量 |
| 对话记忆存哪？ | MongoDB，实现了 ChatMemoryStore 接口 |
| 流式输出怎么实现？ | WebFlux Flux\<String\> + text/stream |
| 前后端怎么通信？ | axios 流式 POST + Vite 代理解决跨域 |
| 数据库 ORM？ | MyBatis-Plus，配合 LambdaQueryWrapper |
| API 文档？ | Knife4j（Swagger 增强） |
| 为什么用三个数据库？ | MySQL(结构化业务数据) + MongoDB(非结构化对话) + Pinecone(向量检索) |

## 6.4 写在简历上的"项目职责"建议

```
✗ 差: 参与小智医疗项目的后端开发
✗ 差: 负责数据库设计和接口开发

✓ 好: 设计并实现基于 LangChain4j @AiService 的声明式 AI Agent 架构，
      集成 LLM 调用、Function Calling、RAG 检索、对话记忆四大能力
      
✓ 好: 实现 Function Calling 机制，LLM 自主决策调用预约/取消/查询等 Tool
      方法，通过 MyBatis-Plus 操作 MySQL 完成真实业务闭环
      
✓ 好: 构建基于 Pinecone 向量库的 RAG 知识库，使用 DashScope Embedding 
      模型向量化医疗文档，提升回答准确性
```

**核心原则**：用动词 + 技术 + 结果的方式描述，让面试官一眼看到你做了什么、用了什么技术。

---

## 🧠 记住这 3 句话，面试就够了

> **1.** 这个项目是一个 **AI 医疗智能导诊系统**，用户通过聊天窗口和 AI 客服"小智"对话，可以咨询医疗问题、查询科室、预约挂号。

> **2.** 技术核心是 **LangChain4j 的 @AiService 声明式 Agent**，把一个需要手写几百行 LLM 调用、记忆管理、工具调用的过程，简化成一个接口 + 几个注解。

> **3.** 项目集成了 **RAG**（从 Pinecone 向量库检索知识增强回答）+ **Function Calling**（LLM 自主决策调用 Java 方法操作 MySQL 完成预约）+ **ChatMemory**（MongoDB 持久化多轮对话）。

---

> 📁 本文档对应项目源码路径：`f:\rjgc-code\XiaoZhiMed-Langchain4j\XiaoZhiMed\`
