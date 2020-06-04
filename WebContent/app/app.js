const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }
const Navbar = { template: '<navbar></navbar>'}

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', name: 'home', component: Home},
	    { path: '/login', name: 'login', component: Login} 
	  ]
});

var app = new Vue({
	router,
	el:'#home'
});
 