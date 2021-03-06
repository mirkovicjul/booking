const Home = { template: '<home></home>' }
const Login = { template: '<login></login>' }
const Navbar = { template: '<navbar></navbar>'}
const Registration = { template: '<registration></registration>'}
const Account = { template: '<account></account>'}
const Apartments = { template: '<apartments></apartments>'}
const Apartment = { template: '<apartment></apartment>'}
const Amenities = { template: '<amenities></amenities>'}
const NewApartment = { template: '<new-apartment></new-apartment>'}
const EditApartment = { template: '<edit-apartment></edit-apartment>'}
const Reservations = { template: '<reservations></reservations>'}
const Users = { template: '<users></users>'}
const NewHost = { template: '<new-host></new-host>'}
const Search = { template: '<search></search>'}

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/search', name: 'search', component: Search},
	    { path: '/', name: 'search', component: Search},
	    { path: '/login', name: 'login', component: Login},
	    { path: '/register', name: 'registration', component: Registration},
	    { path: '/account', name: 'account', component: Account},
	    { path: '/apartments', name: 'apartments', component: Apartments},
	    { path: '/apartment/:id', name: 'apartment', component: Apartment},
	    { path: '/amenities', name: 'amenities', component: Amenities},
	    { path: '/new-apartment', name: 'new-apartment', component: NewApartment},
	    { path: '/edit-apartment/:id', name: 'edit-apartment', component: EditApartment},
	    { path: '/reservations', name: 'reservations', component: Reservations},
	    { path: '/users', name: 'users', component: Users},
	    { path: '/new-host', name: 'new-host', component: NewHost}
	  ]
});

if(localStorage.getItem("jwt")!=null)
	axios.defaults.headers.common['Authorization'] = 'Bearer ' + localStorage.getItem("jwt")


var app = new Vue({
	router,
	el:'#home'
});
 