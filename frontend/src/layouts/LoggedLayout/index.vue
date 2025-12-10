<template>
  <q-layout view="lHh Lpr fff">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn flat round dense icon="home" to="/tasks" class="q-mr-sm" />
        <div class="text-h6">
          {{ formattedUsername }}
        </div>
        <q-space />
        <q-select
           v-model="locale"
           :options="localeOptions"
           dense
           borderless
           emit-value
           map-options
           style="min-width: 100px"
           class="text-white"
           popup-content-class="bg-primary text-white"
           dark
        >
          <template v-slot:option="scope">
            <q-item
              v-bind="scope.itemProps"
              :class="scope.selected ? 'bg-blue-6 text-yellow text-weight-bold' : 'text-white'"
            >
              <q-item-section>
                <q-item-label>{{ scope.opt.label }}</q-item-label>
              </q-item-section>
            </q-item>
          </template>
        </q-select>
        <q-btn flat round dense icon="logout" @click="logout" :label="translate('layout.logout')" style="margin-left: 20px" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { useI18n } from 'vue-i18n';

const router = useRouter();
const quasar = useQuasar();
const { t: translate, locale } = useI18n();
const username = ref('User');
const localeOptions = [
  { value: 'en-US', label: 'English' },
  { value: 'pt-BR', label: 'Português' }
];

const formattedUsername = computed(() => {
    if (!username.value) return '';
    return username.value.charAt(0).toUpperCase() + username.value.slice(1);
});

onMounted(() => {
  const storedUser = localStorage.getItem('username');
  if (storedUser) {
    username.value = storedUser;
  }
});

const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  quasar.notify({
    message: translate('layout.loggedOut'),
    color: 'info',
    icon: 'info'
  });
  router.push('/login');
};
</script>
