Vue.component("users", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	users: []
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
		<table class="table table-hover">
          <thead>
		    <tr>
		      <th scope="col">Username</th>
		      <th scope="col">First name</th>
		      <th scope="col">Last name</th>
		      <th scope="col">Gender</th>
		      <th scope="col" v-if="role=='ADMIN'">Role</th>
		    </tr>
		  </thead>
		  <tbody>
		    <tr v-for="user in users">	    	
			      <td>{{user.username}}</td>
			      <td>{{user.firstName}}</td> 
			      <td>{{user.lastName}}</td> 
			      <td>{{user.gender}}</td>
			      <td v-if="role=='ADMIN'">{{user.role}}</td>
		    </tr>
		  </tbody>
		</table>
	</div>
</div>
`
	, 
	methods : {

	},
	mounted() {
		if(this.role != "ADMIN" && this.role != "HOST") {
    		this.$router.push({ name: 'home' });
		} else {
			axios
	        .get('rest/user/all')
	        .then(response => (this.users=response.data));
		}
    },
    created() {
    	
    }

});
