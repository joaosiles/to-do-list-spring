<template>
  <q-page class="flex flex-center bg-grey-2">
    <q-card class="q-pa-md shadow-2 my-card" style="width: 100%; max-width: 400px;">
      <q-card-section class="text-center">
        <div class="text-h5 text-weight-bold text-primary">{{ $t('login.title') }}</div>
        <div class="text-subtitle2">{{ $t('login.subtitle') }}</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            filled
            v-model="username"
            :label="$t('login.username')"
            lazy-rules
            :rules="[ val => val && val.length > 0 || $t('login.usernameRequired')]"
          />

          <q-input
            filled
            type="password"
            v-model="password"
            :label="$t('login.password')"
            lazy-rules
            :rules="[ val => val && val.length > 0 || $t('login.passwordRequired')]"
          />

          <div class="q-mt-lg">
            <q-btn :label="$t('login.submit')" type="submit" color="primary" class="full-width" unelevated size="lg" :loading="loading" />
            <q-btn :label="$t('login.registerLink')" to="/register" color="secondary" flat class="full-width q-mt-sm" />
          </div>
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const $q = useQuasar();
const router = useRouter();

const username = ref('');
const password = ref('');
const loading = ref(false);

const onSubmit = async () => {
  loading.value = true;
  try {
    const response = await api.post('/users/login', {
      username: username.value,
      password: password.value
    });

    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token);
    }

    localStorage.setItem('username', username.value);

    $q.notify({
      color: 'positive',
      message: t('login.success'),
      icon: 'check'
    });

    router.push('/tasks');
  } catch (error: any) {
    console.error(error);
    $q.notify({
      color: 'negative',
      message: error.response?.data?.message || t('login.failed'),
      icon: 'report_problem'
    });
  } finally {
    loading.value = false;
  }
};
</script>