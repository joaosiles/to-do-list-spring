<template>
  <q-page class="flex flex-center bg-grey-2">
    <q-card class="q-pa-md shadow-2 my-card" style="width: 100%; max-width: 400px;">
      <q-card-section class="text-center">
        <div class="text-h5 text-weight-bold text-secondary">{{ translate('register.title') }}</div>
        <div class="text-subtitle2">{{ translate('register.subtitle') }}</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit="onSubmit" class="q-gutter-md">
          <q-input
            filled
            v-model="username"
            :label="translate('register.username')"
            lazy-rules
            :rules="[ val => val && val.length > 0 || translate('login.usernameRequired')]"
          />

          <q-input
            filled
            v-model="email"
            :label="translate('register.email')"
            lazy-rules
            :rules="[ val => val && val.length > 0 || translate('taskEdit.required')]"
          />

          <q-input
            filled
            type="password"
            v-model="password"
            :label="translate('register.password')"
            lazy-rules
            :rules="[ val => val && val.length > 0 || translate('login.passwordRequired')]"
          />

          <div class="q-mt-lg">
            <q-btn :label="translate('register.submit')" type="submit" color="secondary" class="full-width" unelevated size="lg" :loading="loading" />
            <q-btn :label="translate('register.backToLogin')" to="/login" color="primary" flat class="full-width q-mt-sm" />
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

const { t: translate } = useI18n();
const quasar = useQuasar();
const router = useRouter();

const username = ref('');
const email = ref('');
const password = ref('');
const loading = ref(false);

const onSubmit = async () => {
  loading.value = true;
  try {
    // Backend espera: username, name, password
    // UI tem apenas username e password. Usaremos username como name.
    await api.post('/users/', {
      username: username.value,
      email: email.value,
      name: username.value, 
      password: password.value
    });

    quasar.notify({
      color: 'positive',
      message: translate('register.success'),
      icon: 'check'
    });

    router.push('/login');
  } catch (error: any) {
    console.error(error);
    quasar.notify({
      color: 'negative',
      message: error.response?.data?.message || translate('register.failed'),
      icon: 'report_problem'
    });
  } finally {
    loading.value = false;
  }
};
</script>
