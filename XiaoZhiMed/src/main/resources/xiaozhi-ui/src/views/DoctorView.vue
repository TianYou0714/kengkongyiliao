<template>
  <div class="page">
    <div class="page-card">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索医生姓名 / 擅长领域"
          clearable
          class="search-input"
          @keyup.enter="loadData(1)"
          @clear="loadData(1)"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.departmentId" placeholder="按科室筛选" clearable style="width: 160px" @change="loadData(1)">
          <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
        <el-button type="primary" @click="loadData(1)">
          <el-icon style="margin-right: 4px"><Search /></el-icon>查询
        </el-button>
        <el-button type="success" @click="openDialog()">
          <el-icon style="margin-right: 4px"><Plus /></el-icon>新增医生
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="110" />
        <el-table-column prop="title" label="职称" width="120">
          <template #default="{ row }">
            <el-tag :type="titleTagType(row.title)" effect="light">{{ row.title || '—' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属科室" width="130">
          <template #default="{ row }">{{ departmentName(row.departmentId) }}</template>
        </el-table-column>
        <el-table-column prop="specialty" label="擅长领域" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm title="确定删除该医生吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger"><el-icon><Delete /></el-icon>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑医生' : '新增医生'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="住院医师" value="住院医师" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择科室" style="width: 100%">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="擅长领域" prop="specialty">
          <el-input v-model="form.specialty" placeholder="请输入擅长领域" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
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
const departments = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', departmentId: null })
const form = reactive({ id: null, name: '', title: '', departmentId: null, specialty: '', phone: '' })

const rules = {
  name: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
}

const titleTagType = (title) => {
  if (title === '主任医师') return 'danger'
  if (title === '副主任医师') return 'warning'
  return 'primary'
}

const departmentName = (id) => departments.value.find((d) => d.id === id)?.name || '—'

const loadDepartments = async () => {
  const res = await request.get('/department/list')
  departments.value = res.data
}

const loadData = async (toFirst) => {
  if (toFirst) query.pageNum = 1
  loading.value = true
  try {
    const res = await request.get('/doctor/page', { params: query })
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
    Object.assign(form, { id: null, name: '', title: '', departmentId: null, specialty: '', phone: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) {
      await request.put('/doctor', form)
    } else {
      await request.post('/doctor', form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await request.delete(`/doctor/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadDepartments()
  loadData()
})
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
  width: 260px;
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
