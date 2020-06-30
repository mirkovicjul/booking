Vue.component("navbar", {
	data: function () {
		    return {
		      role: localStorage.getItem("role"),
		      loggedIn: localStorage.getItem("jwt") ? true : false
		    }
	},
	template: ` 
<nav class="navbar navbar-expand-lg  navbar-dark bg-primary">
  <a class="navbar-brand" href="#/">
    <img src="images/logo.png" width="30" height="30" alt="">
  </a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
    <ul class="navbar-nav mr-auto">
      <li v-if="role!='HOST' && role!='ADMIN'"class="nav-item active">
        <a class="nav-link" href="#/apartments">Apartments</a>
      </li>   
      <li v-if="role=='HOST' || role=='ADMIN'" class="nav-item dropdown" >
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          Apartments
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="#/apartments">All apartments</a>
          <a v-if="role=='HOST'" class="dropdown-item" href="#/new-apartment">Add new apartment</a>
          <a v-if="role=='ADMIN'" class="dropdown-item" href="#/amenities">Amenities</a>
        </div>
      </li>   
      
      <li v-if="role=='HOST' || role=='ADMIN'" class="nav-item dropdown" >
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          Users
        </a>  
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="#/users">All users</a>
          <a v-if="role=='ADMIN'" class="dropdown-item" href="#/new-host">New host</a>
        </div>       
      </li>  
      
      <li v-if="loggedIn" class="nav-item active">
        <a class="nav-link" href="#/reservations">Reservations</a>
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
