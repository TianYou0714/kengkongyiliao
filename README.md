# 可控医疗（KengKong Medical）

基于 **Spring Boot 3 + Langchain4j + Vue 3** 的智能医疗管理平台。
在原有"小智医疗"AI 问诊能力的基础上，新增了完整的业务管理模块，前端重构为左侧业务导航布局并全面更名、美化。

## 功能模块

| 模块 | 说明 |
| --- | --- |
| AI 智能问诊 | 基于通义千问（qwen-plus）的流式医疗问答，支持会话记忆（MongoDB）、RAG 知识库（Pinecone）、预约挂号工具调用 |
| 预约挂号 | 预约记录的增删改查、分页与关键字搜索 |
| 医生管理 | 医生信息维护，支持按科室筛选、职称标记 |
| 科室管理 | 科室信息维护 |
| 患者管理 | 患者档案维护 |

## 技术栈

- **后端**：Spring Boot 3.2、Langchain4j 1.0.0-beta3、MyBatis-Plus 3.5.11、MySQL、MongoDB、Knife4j
- **前端**：Vue 3、Vite 5、Element Plus、vue-router 4、axios、marked

## 快速开始

1. **初始化数据库**：执行 `XiaoZhiMed/sql/kengkongyiliao.sql`（建库 `xiaozhi` + 四张业务表 + 示例数据）
2. **配置密钥与数据源**：修改 `XiaoZhiMed/src/main/resources/application.properties` 中的通义千问 API Key、MySQL / MongoDB 连接信息
3. **启动后端**（需 JDK 17）：`cd XiaoZhiMed && mvnw spring-boot:run`
4. **启动前端**（开发模式）：`cd XiaoZhiMed/src/main/resources/xiaozhi-ui && npm install && npm run dev`
   - 前端已预构建到 `dist/`，也可直接由后端托管访问
5. 浏览器访问前端地址，左侧导航栏即可切换 **AI 智能问诊** 与各业务管理页面

## 接口文档

启动后端后访问 Knife4j：`http://localhost:8080/doc.html`

业务接口（统一 `Result` 响应封装）：

- `GET/POST/PUT/DELETE /department` 科室
- `GET/POST/PUT/DELETE /doctor` 医生
- `GET/POST/PUT/DELETE /patient` 患者
- `GET/POST/PUT/DELETE /appointment` 预约
- `POST /xiaozhi/chat` AI 流式对话

## 目录说明

- `XiaoZhiMed/` — 后端 Spring Boot 工程（前端工程位于 `src/main/resources/xiaozhi-ui`）
- `XiaoZhiMed项目完全吃透教程.md` — 原项目学习教程
