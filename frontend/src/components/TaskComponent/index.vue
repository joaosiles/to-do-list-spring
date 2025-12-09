<template>
  <q-card class="my-card q-mb-md" flat bordered>
    <q-card-section>
      <div class="row items-center no-wrap">
        <div class="col">
          <div class="text-h6 text-primary text-weight-bold">{{ task.title }}</div>
          <div class="text-subtitle2 text-grey-7">{{ task.description }}</div>
        </div>
        <div class="col-auto">
          <q-btn color="grey-7" round flat icon="more_vert">
            <q-menu cover auto-close>
              <q-list>
                <q-item clickable @click="$emit('edit', task)">
                  <q-item-section>{{ translate('taskList.edit') }}</q-item-section>
                </q-item>
                <q-item clickable @click="$emit('delete', task.id)">
                  <q-item-section class="text-negative">{{ translate('taskList.complete') }}</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
        </div>
      </div>
    </q-card-section>

    <q-separator />

    <q-card-section class="q-pt-xs">
      <div class="row q-gutter-sm text-caption text-grey">
        <div class="col-auto">
          <q-icon name="event" /> {{ translate('taskList.start') }}: {{ formatDate(task.startAt) }}
        </div>
        <div class="col-auto">
          <q-icon name="event_busy" /> {{ translate('taskList.end') }}: {{ formatDate(task.endAt) }}
        </div>
        <div v-if="task.priority" class="col-auto text-weight-medium" :class="getPriorityColor(task.priority)">
          {{ translate('taskList.priority') }}: {{ task.priority }}
        </div>
      </div>
    </q-card-section>
    
    <q-card-actions align="right">
       <q-btn flat :label="translate('taskList.edit')" color="primary" icon="edit" @click="$emit('edit', task)" />
       <q-btn flat :label="translate('taskList.done')" color="positive" icon="check" @click="$emit('delete', task.id)" />
    </q-card-actions>
  </q-card>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { date } from 'quasar';
import { useI18n } from 'vue-i18n';

const { t: translate } = useI18n();

const props = defineProps<{
  task: {
    id: string | number;
    title: string;
    description: string;
    startAt: string;
    endAt: string;
    priority?: string;
  };
}>();

defineEmits(['edit', 'delete']);

const formatDate = (dateString: string) => {
  return date.formatDate(dateString, 'YYYY-MM-DD HH:mm');
};

const getPriorityColor = (priority: string) => {
    if (priority === 'Alta') return 'text-negative';
    if (priority === 'Media') return 'text-warning';
    return 'text-positive';
}
</script>
