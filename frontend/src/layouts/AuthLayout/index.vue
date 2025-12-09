<template>
  <q-layout view="lHh Lpr fff">
    <q-header class="bg-transparent text-primary" flat>
        <q-toolbar>
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
            />
        </q-toolbar>
    </q-header>
    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<style src="./index.scss"></style>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
const { locale } = useI18n();

const localeOptions = [
  { value: 'en-US', label: 'English' },
  { value: 'pt-BR', label: 'Português' }
];
</script>
