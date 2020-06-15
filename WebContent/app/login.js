Vue.component("login", {
	data: function () {
		    return {
		      user: {
		    	  username: "",
		    	  password: ""
		      },
		      loggedIn: localStorage.getItem("jwt") ? true : false,
		      loginFailed: null,
		      usernameFieldFocus: null,
		      passwordFieldFocus: null
		    }
	},
	template: ` 
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
            <div class="form-group">
                <label class=" control-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control input-md" type="text" v-model="user.username" v-on:focusout="setFieldFocus('username')" required>
                	<div v-if="usernameFieldFocus && user.username.length==0">
	                   	<small class="form-text text-danger">
							Username is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group">
                <label class=" control-label" for="textinput">Password:</label>
                <div class="">
                    <input name="password" class="form-control input-md" type="password" v-model="user.password" v-on:focusout="setFieldFocus('password')" required>
                	<div v-if="passwordFieldFocus && user.password.length==0">
	                   	<small class="form-text text-danger">
							Password is required.
						</small>
            		</div>
                </div>
            </div>
            
           
            <div class="form-group">
                <div class="">
                    <button class="btn btn-info" v-on:click="login(user)" v-bind:disabled="user.username.length==0 || user.password.length==0">Log in</button>
                	<div v-if="loginFailed">
	                    <small class="form-text text-danger">
							Invalid username or password.
						</small>
            		</div>
                </div>
            </div>
        </div>
        <br>
        <br>
        <div class="clearfix"></div>
    </div>
</div>
`
	, 
	methods : {
		checkLogin: function(data){
			if(data.success){
				localStorage.setItem('jwt', data.jwt);
				localStorage.setItem('role', data.role);
				localStorage.setItem('user', data.username);
				window.location.reload();
				this.loggedIn = true;
				this.loggdIn = true;
			} else {
				this.loginFailed = true;
			}
		},
		login: function(user) {
			axios
	          .post('rest/login', {"username":user.username, "password":user.password})
	          .then(response => (this.checkLogin(response.data)));
		},
		setFieldFocus: function(field) {
			switch(field){
			case "username":
				this.usernameFieldFocus = true;
				break;
			case "password":
				this.passwordFieldFocus = true;
				break;
			}
		}
	},
	mounted() {
      
    },
    created() {
    	if(this.loggedIn)
    		this.$router.push({ name: 'home' });
    }
});
