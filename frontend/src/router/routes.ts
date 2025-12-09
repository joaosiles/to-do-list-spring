import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/AuthLayout/index.vue'),
    redirect: '/login', // Redirecionar raiz para login
    children: [
      { path: 'login', component: () => import('pages/LoginPage/index.vue') },
      { path: 'register', component: () => import('pages/RegisterPage/index.vue') }
    ]
  },
  {
    path: '/tasks',
    component: () => import('layouts/LoggedLayout/index.vue'),
    children: [
      { path: '', component: () => import('pages/TaskListPage/index.vue') },
      { path: 'new', component: () => import('pages/TaskEditPage/index.vue') },
      { path: ':id', component: () => import('pages/TaskEditPage/index.vue') }
    ]
  },

  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
];

export default routes;
