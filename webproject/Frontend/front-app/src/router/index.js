import { createRouter, createWebHashHistory } from 'vue-router';
import ChocolateFactoryDetail from '../views/ChocolateFactoryDetail.vue';
import ChocolateFactoryList from '../views/ChocolateFactoryList.vue';
import LoginRegister from '../views/LoginRegister.vue';
const routes = [
  {
    path: "/home",
    name: "home",
    component: ChocolateFactoryList,
  },
  {
    path: "/chocolates",
    name: "chocolates",
    component: () =>
      import("../views/Chocolates.vue"),
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: () =>
      import("../views/Dashboard.vue"),
  },
  {
    path: "/cart",
    name: "carts",
    component: () =>
      import("../views/ShoppingCart.vue"),
  },
  {
    path: '/factory/:id',
    name: 'ChocolateFactoryDetail',
    component: ChocolateFactoryDetail,
    props: true
  },
  {
    path: '/',
    name: 'LoginRegister',
    component: LoginRegister,
    props: true
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router