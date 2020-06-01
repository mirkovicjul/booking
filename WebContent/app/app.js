const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', component: Home},
	    { path: '/login', component: Login}
	  ]
});

var app = new Vue({
	router,
	el:'#home'
});
 