Vue.component("registration", {
	data: function () {
		    return {
		    	user: {
		    		username: "",
		    		firstName: "",
		    		lastName: "",
		    		gender: null,
		    		password: "",
		    		passwordConfirmation: ""
		    	},
		    	genders: null,
		    	response: {
		    		failed: null,
		    		message: ""
		    	},
		    	usernameFieldFocus: null,
		    	firstNameFieldFocus: null,
		    	lastNameFieldFocus: null,
		    	genderFieldFocus: null,
			    passwordFieldFocus: null,
			    passwordConfirmationFieldFocus: null
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control" type="text" v-model="user.username" v-on:focusout="setFieldFocus('username')" required>
                	<div v-if="usernameFieldFocus && user.username.length==0">
	                   	<small class="form-text text-danger">
							Username is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">First name:</label>
                <div class="">
                    <input name="firstName" class="form-control" type="text" v-model="user.firstName" v-on:focusout="setFieldFocus('firstName')" required>
                	<div v-if="firstNameFieldFocus && user.firstName.length==0">
	                   	<small class="form-text text-danger">
							First name is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Last name:</label>
                <div class="">
                    <input name="lastName" class="form-control" type="text" v-model="user.lastName" v-on:focusout="setFieldFocus('lastName')" required>
                	<div v-if="lastNameFieldFocus && user.lastName.length==0">
	                   	<small class="form-text text-danger">
							Last name is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
            <label class="col-sm-2 col-form-label" for="selectbasic">Gender:</label>
                <div class="">
		            <select class="form-control" v-model="user.gender" v-on:focusout="setFieldFocus('gender')">
					  <option disabled value="">Please select one</option>
					  <option v-for="gender in genders" :key="gender">{{gender}}</option>
					</select>
					<div v-if="genderFieldFocus && user.gender==null">
	                   	<small class="form-text text-danger">
							Gender is required.
						</small>
            		</div>
				</div>
            </div>
           
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Password:</label>
                <div class="">
                    <input name="password" class="form-control" type="password" v-model="user.password" v-on:focusout="setFieldFocus('password')" required>
                	<div v-if="passwordFieldFocus && user.password.length==0">
	                   	<small class="form-text text-danger">
							Password is required.
						</small>
            		</div>
                </div>
            </div>
            
             <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Confirm password:</label>
                <div class="">
                    <input name="passwordConfirmation" class="form-control" type="password" v-model="user.passwordConfirmation" v-on:focusin="setFieldFocus('passwordConfirmation')" required>
                	<div v-if="passwordConfirmationFieldFocus && (user.passwordConfirmation.length==0 || user.password!=user.passwordConfirmation)">
                   	<small class="form-text text-danger">
						Passwords need to match.
					</small>
            </div>
                </div>
            </div>
           
            <div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="register(user)" v-bind:disabled="!validateForm()">Sign up</button>
                </div>
            </div>
            <div v-if="response.failed" class="alert alert-danger">
				{{response.message}}
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
		setFieldFocus: function(field) {
			switch(field){
			case "username":
				this.usernameFieldFocus = true;
				break;
			case "firstName":
				this.firstNameFieldFocus = true;
				break;
			case "lastName":
				this.lastNameFieldFocus = true;
				break;
			case "gender":
				this.genderFieldFocus = true;
				break;
			case "password":
				this.passwordFieldFocus = true;
				break;
			case "passwordConfirmation":
				this.passwordConfirmationFieldFocus = true;
				break;
			}
		},
		validateForm: function(){
			if(this.user.username.length == 0)
				return false;
			else if(this.user.firstName.length == 0)
				return false;
			else if(this.user.lastName.length == 0)
				return false;
			else if(this.user.gender == null)
				return false;
			else if(this.user.password.length == 0)
				return false;
			else if(this.user.passwordConfirmation.length == 0)
				return false;
			else if(this.user.password != this.user.passwordConfirmation)
				return false;
			else
				return true;
		},
		register: function(user) {
			axios
	          .post('rest/user/register', {"username":user.username, "password":user.password, "firstName":user.firstName, "lastName":user.lastName, "gender":user.gender})
	          .then(response => (this.checkResponse(response)));
		},
		login: function(user) {
			axios
	          .post('rest/login', {"username":user.username, "password":user.password})
	          .then(function (response) {
	        	  localStorage.setItem('jwt', response.data.jwt);
	        	  localStorage.setItem('role', response.data.role);
				  localStorage.setItem('user', response.data.username);
				  window.location.reload();
				  this.$router.push({ name: 'home' });
				}
	          )
		},
		checkResponse: function(response) {
			console.log(response.data);
			if(response.data.failed){
				this.response.failed = true;
				this.response.message = response.data.message;
			} else {
				this.login(response.data);
			}
		}
	},
	mounted() {
		axios
        .get('rest/user/genders')
        .then(response => (this.genders = response.data));
    },
    created() {
    	if(this.loggdIn)
    		this.$router.push({ name: 'home' });
    }
});
