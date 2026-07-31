<template>
  <div class="page">
    <div class="page-card">
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索科室名称 / 简介"
          clearable
          class="search-input"
          @keyup.enter="loadData(1)"
          @clear="loadData(1)"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="loadData(1)">
          <el-icon style="margin-right: 4px"><Search /></el-icon>查询
        </el-button>
        <el-button type="success" @click="openDialog()">
          <el-icon style="margin-right: 4px"><Plus /></el-icon>新增科室
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="科室名称" width="180">
          <template #default="{ row }">
            <el-tag effect="plain" type="primary">{{ row.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="科室简介" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm title="确定删除该科室吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger"><el-icon><Delete /></el-icon>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData()"
          @size-change="loadData(1)"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑科室' : '新增科室'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入科室简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ id: null, name: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }],
}

const loadData = async (toFirst) => {
  if (toFirst) query.pageNum = 1
  loading.value = true
  try {
    const res = await request.get('/department/page', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, name: '', description: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) {
      await request.put('/department', form)
    } else {
      await request.post('/department', form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await request.delete(`/department/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.page {
  height: 100%;
  display: flex;
}

.page-card {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(15, 23, 42, 0.06);
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  width: 280px;
}

.data-table {
  flex: 1;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
