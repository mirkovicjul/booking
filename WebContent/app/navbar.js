Vue.component("navbar", {
	data: function () {
		    return {
		      role: localStorage.getItem("role"),
		      loggedIn: localStorage.getItem("jwt") ? true : false
		    }
	},
	template: ` 
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <a class="navbar-brand" href="#/">Navbar</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item active">
        <a class="nav-link" href="#/">Home</a>
      </li>
      <li class="nav-item active">
        <a class="nav-link" href="#/apartments">Apartments</a>
      </li>
      <li v-if="role=='ADMIN'" class="nav-item active">
        <a class="nav-link" href="#/amenities">Amenities</a>
      </li>
      <li v-if="role=='HOST'" class="nav-item active">
        <a class="nav-link" href="#/new-apartment">Add new apartment</a>
      </li>
    </ul>

	<span v-if="!loggedIn" class="navbar-text">
      <a class="nav-link" href="#/login">Login</a>
    </span>
    <span v-if="!loggedIn" class="navbar-text">
      <a class="nav-link" href="#/register">Register</a>
    </span>
    <span v-if="loggedIn" class="navbar-text">
      <a class="nav-link" href="#/account"">Account</a>
    </span>
    <span v-if="loggedIn" class="navbar-text">
      <a class="nav-link" href="#/login" v-on:click="logout()">Log out</a>
    </span>
    
  </div>
</nav>
`
	, 
	methods : {
		logout: function() {
			localStorage.removeItem('jwt');
			localStorage.removeItem('role');
			localStorage.removeItem('user');
			this.loggedIn = false;
			this.role = "";
		}
	},
	mounted() {

    },
    created(){
    	
    }
});
