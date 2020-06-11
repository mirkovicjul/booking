const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }
const Navbar = { template: '<navbar></navbar>'}
const Registration = { template: '<registration></registration>'}

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', name: 'home', component: Home},
	    { path: '/login', name: 'login', component: Login},
	    { path: '/register', name: 'registration', component: Registration}
	  ]
});

var app = new Vue({
	router,
	el:'#home'
});
 