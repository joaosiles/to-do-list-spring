<template>
  <q-page class="q-pa-md bg-grey-1">
    <div class="row items-center justify-between q-mb-md">
      <div class="text-h4 text-weight-bold text-dark">{{ translate('taskList.title') }}</div>
      <q-btn color="primary" icon="add" :label="translate('taskList.newTask')" to="/tasks/new" />
    </div>

    <div v-if="loading" class="flex flex-center q-pa-lg">
      <q-spinner size="3em" color="primary" />
    </div>

    <div v-else-if="tasks.length === 0" class="text-center q-pa-lg text-grey">
      {{ translate('taskList.noTasks') }}
    </div>

    <div v-else class="row q-col-gutter-md">
      <div v-for="task in filteredTasks" :key="task.id" class="col-12 col-md-6 col-lg-4">
        <TaskComponent :task="task" @edit="editTask" @delete="deleteTask" />
      </div>
    </div>
  </q-page>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import TaskComponent from 'components/TaskComponent/index.vue';

const quasar = useQuasar();
const router = useRouter();
const { t: translate } = useI18n();
const tasks = ref<any[]>([]);
const loading = ref(true);

const fetchTasks = async () => {
  loading.value = true;
  try {
    const response = await api.get('/tasks/');
    tasks.value = response.data;
  } catch (error) {
    console.error(error);
    quasar.notify({
      color: 'negative',
      message: translate('taskList.fetchFailed'),
      icon: 'report_problem'
    });
  } finally {
    loading.value = false;
  }
};

const filteredTasks = computed(() => {
  const now = new Date();
  return tasks.value.filter(task => {
    // "não listar as tasks que estão vencidas"
    const endDate = new Date(task.endAt);
    return endDate >= now;
  });
});

const editTask = (task: any) => {
  router.push(`/tasks/${task.id}`);
};

const deleteTask = async (id: string | number) => {
  try {
    quasar.dialog({
        title: translate('taskList.complete'),
        message: translate('taskList.confirmCompleted'),
        cancel: true,
        persistent: true
    }).onOk(async () => {
         await api.delete(`/tasks/${id}`);
         quasar.notify({
          color: 'positive',
          message: translate('taskList.completedSuccess'),
          icon: 'check'
        });
        await fetchTasks();
    });
  } catch (error) {
    console.error(error);
     quasar.notify({
      color: 'negative',
      message: translate('taskList.deleteFailed'),
      icon: 'report_problem'
    });
  }
};

onMounted(() => {
  fetchTasks();
});
</script>
