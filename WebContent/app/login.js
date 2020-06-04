Vue.component("login", {
	data: function () {
		    return {
		      user: {
		    	  username: "",
		    	  password: ""
		      },
		      loggedIn: localStorage.getItem("loggedIn"),
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
                </div>
            </div>
            <div v-if="usernameFieldFocus && user.username.length==0" class="alert alert-danger">
                   	Username is required.
            </div>
            <div class="form-group">
                <label class=" control-label" for="textinput">Password:</label>
                <div class="">
                    <input name="password" class="form-control input-md" type="password" v-model="user.password" v-on:focusout="setFieldFocus('password')" required>
                </div>
            </div>
            <div v-if="passwordFieldFocus && user.password.length==0" class="alert alert-danger">
                   	Password is required.
            </div>
            <div v-if="loginFailed" class="alert alert-danger">
                    Invalid username or password.
            </div>
            <div class="form-group">
                <div class="">
                     <button class="btn btn-success" v-on:click="login(user)" v-bind:disabled="user.username.length==0 || user.password.length==0">Log in</button>
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
				localStorage.setItem('loggedIn', true);
				window.location.reload();
			} else {
				this.loginFailed = true;
			}
		},
		login : function (user) {
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
	mounted () {
      
    },
    created(){
    	if(this.loggedIn)
    		this.$router.push({ name: 'home' });
    }
});
