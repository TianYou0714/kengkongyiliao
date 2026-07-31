<template>
  <div class="page">
    <div class="page-card">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索预约人 / 科室 / 医生"
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
          <el-icon style="margin-right: 4px"><Plus /></el-icon>新增预约
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="预约人" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="180" show-overflow-tooltip />
        <el-table-column prop="department" label="科室" width="110">
          <template #default="{ row }">
            <el-tag effect="plain" type="success">{{ row.department }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column prop="date" label="预约日期" width="120" />
        <el-table-column prop="time" label="预约时间" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm title="确定取消该预约吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger"><el-icon><Delete /></el-icon>取消</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑预约' : '新增预约'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="预约人" prop="username">
          <el-input v-model="form.username" placeholder="请输入预约人姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-select v-model="form.department" placeholder="请选择科室" style="width: 100%">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生" prop="doctorName">
          <el-select v-model="form.doctorName" placeholder="请选择医生" style="width: 100%" clearable>
            <el-option v-for="doc in doctors" :key="doc.id" :label="doc.name" :value="doc.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预约时间" prop="time">
          <el-time-picker
            v-model="form.time"
            format="HH:mm"
            value-format="HH:mm"
            placeholder="请选择时间"
            style="width: 100%"
          />
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
const doctors = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ id: null, username: '', idCard: '', department: '', doctorName: '', date: '', time: '' })

const rules = {
  username: [{ required: true, message: '请输入预约人姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  department: [{ required: true, message: '请选择科室', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间', trigger: 'change' }],
}

const loadOptions = async () => {
  const [deptRes, docRes] = await Promise.all([
    request.get('/department/list'),
    request.get('/doctor/list'),
  ])
  departments.value = deptRes.data
  doctors.value = docRes.data
}

const loadData = async (toFirst) => {
  if (toFirst) query.pageNum = 1
  loading.value = true
  try {
    const res = await request.get('/appointment/page', { params: query })
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
    Object.assign(form, { id: null, username: '', idCard: '', department: '', doctorName: '', date: '', time: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) {
      await request.put('/appointment', form)
    } else {
      await request.post('/appointment', form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await request.delete(`/appointment/${row.id}`)
  ElMessage.success('已取消预约')
  loadData()
}

onMounted(() => {
  loadOptions()
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
