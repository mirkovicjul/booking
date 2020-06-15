const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }
const Navbar = { template: '<navbar></navbar>'}
const Registration = { template: '<registration></registration>'}
const Account = { template: '<account></account>'}

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', name: 'home', component: Home},
	    { path: '/login', name: 'login', component: Login},
	    { path: '/register', name: 'registration', component: Registration},
	    { path: '/account', name: 'account', component: Account}
	  ]
});


axios.defaults.headers.common['Authorization'] = 'Bearer ' + localStorage.getItem("jwt")

var app = new Vue({
	router,
	el:'#home',
	data:{
		loggdIn: null
	}
});
 