<template>
  <q-page class="q-pa-md bg-grey-1 flex flex-center">
    <q-card class="q-pa-md shadow-2" style="width: 100%; max-width: 600px;">
      <q-card-section>
        <div class="text-h5 text-weight-bold text-primary">{{ isEditing ? translate('taskEdit.editTitle') : translate('taskEdit.newTitle') }}</div>
      </q-card-section>

      <q-separator />

      <q-card-section class="q-gutter-md">
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            filled
            v-model="form.title"
            :label="translate('taskEdit.title')"
            :rules="[ val => val && val.length > 0 || translate('taskEdit.required')]"
          />

          <q-input
            filled
            v-model="form.description"
            :label="translate('taskEdit.description')"
            type="textarea"
          />

          <q-select
            filled
            v-model="form.priority"
            :options="['Baixa', 'Media', 'Alta']"
            :label="translate('taskEdit.priority')"
          />

          <div class="row q-col-gutter-sm">
            <div class="col-12 col-md-6">
              <q-input filled v-model="form.startAt" :label="translate('taskEdit.start')">
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-date v-model="form.startAt" mask="YYYY-MM-DD">
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup :label="translate('taskEdit.close')" color="primary" flat />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>
            </div>
            
            <div class="col-12 col-md-6">
               <q-input filled v-model="form.endAt" :label="translate('taskEdit.end')">
                <template v-slot:prepend>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-date v-model="form.endAt" mask="YYYY-MM-DD">
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup :label="translate('taskEdit.close')" color="primary" flat />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>
            </div>
          </div>

          <div class="row justify-end q-mt-lg">
             <q-btn :label="translate('taskEdit.cancel')" to="/tasks" color="grey" flat class="q-mr-sm" />
             <q-btn :label="translate('taskEdit.save')" type="submit" color="primary" unelevated :loading="loading" />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { api } from 'boot/axios';
import { useQuasar, date } from 'quasar';
import { useI18n } from 'vue-i18n';

const route = useRoute();
const router = useRouter();
const quasar = useQuasar();
const { t: translate } = useI18n();

const isEditing = computed(() => !!route.params.id && route.params.id !== 'new');
const loading = ref(false);

const form = ref({
  title: '',
  description: '',
  priority: 'Media',
  startAt: date.formatDate(Date.now(), 'YYYY-MM-DD'),
  endAt: date.formatDate(date.addToDate(Date.now(), { days: 1 }), 'YYYY-MM-DD')
});

onMounted(async () => {
  if (isEditing.value) {
    try {
        loading.value = true;
        const response = await api.get(`/tasks/${route.params.id}`);
        const task = response.data;
        
        if (task) {
            form.value = {
                title: task.title,
                description: task.description,
                priority: task.priority || 'Media',
                startAt: date.formatDate(task.startAt, 'YYYY-MM-DD'),
                endAt: date.formatDate(task.endAt, 'YYYY-MM-DD')
            };
        } else {             
            quasar.notify({ message: translate('taskEdit.notFound'), color: 'negative' });
            router.push('/tasks');
        }
    } catch (e) {
        console.error(e);
        quasar.notify({ message: translate('taskEdit.fetchError'), color: 'negative' });
    } finally {
        loading.value = false;
    }
  }
});

const onSubmit = async () => {
    loading.value = true;
    try {
        const payload = {
            ...form.value,
            startAt: form.value.startAt + 'T00:00:00',
            endAt: form.value.endAt + 'T00:00:00'
        };

        if (isEditing.value) {
            await api.put(`/tasks/${route.params.id}`, payload);
            quasar.notify({ message: translate('taskEdit.updated'), color: 'positive', icon: 'check' });
        } else {
            await api.post('/tasks/', payload);
            quasar.notify({ message: translate('taskEdit.created'), color: 'positive', icon: 'check' });
        }
        router.push('/tasks');
    } catch (error: any) {
        console.error(error);
        quasar.notify({ message: translate('taskEdit.failed'), color: 'negative', icon: 'report_problem' });
    } finally {
        loading.value = false;
    }
};
</script>
