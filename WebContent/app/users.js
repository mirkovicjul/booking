Vue.component("users", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	users: [],
		    	genders: [],
		    	roles: [],
		    	showSearchForm: false,
		    	gender: null,
		    	username: null,
		    	userRole: null
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
    <br>
    <br>
    <br>
    
     <div class="form-group">
            <label class="asearch-label" for="selectbasic" v-on:click="showSearch()">Search
                <img src='images/triangle_down.png' height="21" width="21" v-if="!showSearchForm" />
                <img src='images/triangle_up.png' height="21" width="21" v-if="showSearchForm" />
            </label>
     </div>
       
    <div v-if="showSearchForm && (role=='ADMIN' || role=='HOST')">
			<div v-if="role=='ADMIN'" class="form-group row">
	            <label class="col-sm-2 col-form-label" for="selectbasic">Role:</label>
	                <div class="">
			            <select class="form-control" v-model="userRole">
						  <option disabled value="">Please select one</option>
						  <option value="">All</option>
						  <option v-for="role in roles" :key="role">{{role}}</option>
						</select>
					</div>		
            </div>
			<div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control" type="text" v-model="username">
                </div>
            </div>
			<div class="form-group row">
	            <label class="col-sm-2 col-form-label" for="selectbasic">Gender:</label>
	                <div class="">
			            <select class="form-control" v-model="gender">
						  <option disabled value="">Please select one</option>
						  <option value="">All</option>
						  <option v-for="gender in genders" :key="gender">{{gender}}</option>
						</select>
					</div>		
            </div>
            
            <div class="form-group">
                <div class="">
             		<button class="btn btn-info" v-on:click="search(username, userRole, gender)">Search</button>
        		</div>
            </div>
    </div>
    <h4>Users</h4>
    <br>
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
		showSearch: function(){
			this.showSearchForm = !this.showSearchForm;
		},
		search: function(username, userRole, gender){
			var searchUrl = "?";
			if(username != null && username.trim() != "")
				searchUrl+="username="+username;
			if(userRole != null && userRole.trim() != "")
				searchUrl+="&role="+userRole;
			if(gender != null && gender != "")
				searchUrl+="&gender="+gender;
			axios
	        .get('rest/user/search'+searchUrl)
	        .then(response => (this.users=response.data));
		}
	},
	mounted() {
		if(this.role != "ADMIN" && this.role != "HOST") {
    		this.$router.push({ name: 'search' });
		} else {
			axios
	        .get('rest/user/all')
	        .then(response => (this.users=response.data));
			axios
	        .get('rest/user/genders')
	        .then(response => (this.genders=response.data));
			axios
	        .get('rest/user/roles')
	        .then(response => (this.roles=response.data));
		}
    },
    created() {
    	
    }

});
