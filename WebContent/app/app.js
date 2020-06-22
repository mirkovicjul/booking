const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }
const Navbar = { template: '<navbar></navbar>'}
const Registration = { template: '<registration></registration>'}
const Account = { template: '<account></account>'}
const Apartments = { template: '<apartments></apartments>'}
const Apartment = { template: '<apartment></apartment>'}
const Amenities = { template: '<amenities></amenities>'}
const NewApartment = { template: '<new-apartment></new-apartment>'}

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', name: 'home', component: Home},
	    { path: '/login', name: 'login', component: Login},
	    { path: '/register', name: 'registration', component: Registration},
	    { path: '/account', name: 'account', component: Account},
	    { path: '/apartments', name: 'apartments', component: Apartments},
	    { path: '/apartment/:id', name: 'apartment', component: Apartment},
	    { path: '/amenities', name: 'amenities', component: Amenities},
	    { path: '/new-apartment', name: 'new-apartment', component: NewApartment}
	  ]
});


axios.defaults.headers.common['Authorization'] = 'Bearer ' + localStorage.getItem("jwt")

var app = new Vue({
	router,
	el:'#home'
});
 