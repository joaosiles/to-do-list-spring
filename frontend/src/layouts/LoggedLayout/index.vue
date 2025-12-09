<template>
  <q-layout view="lHh Lpr fff">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-avatar icon="account_circle" />
        <q-toolbar-title>
          {{ username }}
        </q-toolbar-title>
        <q-space />
        <q-select
           v-model="locale"
           :options="localeOptions"
           dense
           borderless
           emit-value
           map-options
           options-dense
           style="min-width: 100px"
           class="q-mr-md"
        />
        <q-btn flat round dense icon="logout" @click="logout" :label="translate('layout.logout')" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
