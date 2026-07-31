<template>
  <div class="chat-page">
    <div class="chat-card">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="chat-header-left">
          <div class="bot-avatar">
            <el-icon :size="22" color="#fff"><Service /></el-icon>
          </div>
          <div>
            <div class="bot-name">可控医疗 AI 助手</div>
            <div class="bot-status"><span class="status-dot"></span>在线 · 支持流式问答</div>
          </div>
        </div>
        <el-button type="primary" plain round size="small" @click="newChat">
          <el-icon style="margin-right: 4px"><Plus /></el-icon>新会话
        </el-button>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messaggListRef">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['msg-row', message.isUser ? 'row-user' : 'row-bot']"
        >
          <div class="msg-avatar" :class="message.isUser ? 'avatar-user' : 'avatar-bot'">
            <el-icon :size="18" color="#fff">
              <User v-if="message.isUser" />
              <Service v-else />
            </el-icon>
          </div>
          <div class="msg-bubble" :class="message.isUser ? 'bubble-user' : 'bubble-bot'">
            <span v-if="message.isUser" v-html="message.content"></span>
            <span v-else v-html="markdownToHtml(message.content)"></span>
            <span class="loading-dots" v-if="message.isThinking || message.isTyping">
              <span class="dot"></span>
              <span class="dot"></span>
            </span>
          </div>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          resize="none"
          placeholder="请描述您的症状或健康问题，回车发送…"
          @keyup.enter.exact="sendMessage"
        ></el-input>
        <el-button
          @click="sendMessage"
          :disabled="isSending || !inputMessage.trim()"
          type="primary"
          class="send-button"
        >
          <el-icon style="margin-right: 4px"><Promotion /></el-icon>发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'
import { marked } from 'marked'

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])

onMounted(() => {
  initUUID()
  watch(messages, () => scrollToBottom(), { deep: true })
  hello()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  sendRequest('你好')
}

const markdownToHtml = (content) => {
  if (!content) return ''
  marked.setOptions({ breaks: true, gfm: true, sanitize: false })
  try {
    return marked.parse(content)
  } catch (error) {
    console.error('Markdown 解析错误:', error)
    return content
  }
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = { isUser: true, content: message, isTyping: false, isThinking: false }
  // 第一条默认发送的用户消息"你好"不放入会话列表
  if (messages.value.length > 0) {
    messages.value.push(userMsg)
  }

  const botMsg = { isUser: false, content: '', isTyping: true, isThinking: false }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()

  axios
    .post(
      '/api/xiaozhi/chat',
      { memoryId: uuid.value, message },
      {
        responseType: 'stream',
        onDownloadProgress: (e) => {
          const fullText = e.event.target.responseText
          let newText = fullText.substring(lastMsg.content.length)
          lastMsg.content += newText
          scrollToBottom()
        },
      }
    )
    .then(() => {
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
    .catch((error) => {
      console.error('流式错误:', error)
      messages.value.at(-1).content = '请求失败，请重试'
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
}

const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

const newChat = () => {
  localStorage.removeItem('user_uuid')
  window.location.reload()
}
</script>

<style scoped>
.chat-page {
  height: 100%;
  display: flex;
}

.chat-card {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(90deg, #ecfeff 0%, #f0fdfa 100%);
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bot-avatar {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #06b6d4, #0e7490);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(6, 182, 212, 0.35);
}

.bot-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.bot-status {
  font-size: 12px;
  color: #10b981;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 2px;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #10b981;
  display: inline-block;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8fafc;
}

.msg-row {
  display: flex;
  margin-bottom: 16px;
  gap: 10px;
}

.row-user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-bot {
  background: linear-gradient(135deg, #06b6d4, #0e7490);
}

.avatar-user {
  background: linear-gradient(135deg, #818cf8, #6366f1);
}

.msg-bubble {
  max-width: 72%;
  padding: 12px 16px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}

.bubble-bot {
  background: #fff;
  color: #1e293b;
  border: 1px solid #e2e8f0;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.05);
}

.bubble-user {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(6, 182, 212, 0.3);
}

.msg-bubble :deep(p) {
  margin: 0 0 8px;
}
.msg-bubble :deep(p:last-child) {
  margin-bottom: 0;
}

.loading-dots {
  padding-left: 5px;
}

.dot {
  display: inline-block;
  margin-left: 5px;
  width: 8px;
  height: 8px;
  background-color: #0e7490;
  border-radius: 50%;
  animation: pulse 1.2s infinite ease-in-out both;
}

.dot:nth-child(2) {
  animation-delay: -0.6s;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(0.6);
    opacity: 0.4;
  }
  50% {
    transform: scale(1);
    opacity: 1;
  }
}

.input-area {
  display: flex;
  gap: 12px;
  padding: 14px 20px;
  border-top: 1px solid #f1f5f9;
  background: #fff;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}

.send-button {
  height: 56px;
  padding: 0 26px;
  border-radius: 10px;
  background: linear-gradient(135deg, #06b6d4, #0e7490);
  border: none;
  font-size: 15px;
}

.send-button:hover {
  opacity: 0.9;
}
</style>
