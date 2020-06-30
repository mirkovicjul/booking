Vue.component("new-host", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	user: {
		    		username: "",
		    		firstName: "",
		    		lastName: "",
		    		gender: null,
		    		password: "",
		    		passwordConfirmation: ""
		    	},
		    	genders: null,
		    	newHostResponse: {
		    		success: null,
		    		message: ""
		    	},
		    	fieldFocus: {
		    		username: null,
		    		firstName: null,
		    		lastName: null,
		    		gender: null,
		    		password: null,
		    		passwordConfirmation: null
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
        <h4>Add new host</h4>
        <br>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control" type="text" v-model="user.username" v-on:focusout="setFieldFocus('username')" required>
                	<div v-if="fieldFocus.username && user.username.length==0">
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
                	<div v-if="fieldFocus.firstName && user.firstName.length==0">
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
                	<div v-if="fieldFocus.lastName && user.lastName.length==0">
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
					<div v-if="fieldFocus.gender && user.gender==null">
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
                	<div v-if="fieldFocus.password && user.password.length==0">
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
                	<div v-if="fieldFocus.passwordConfirmation && (user.passwordConfirmation.length==0 || user.password!=user.passwordConfirmation)">
                   	<small class="form-text text-danger">
						Passwords need to match.
					</small>
            </div>
                </div>
            </div>
           
            <div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="addNewHost(user)" v-bind:disabled="!validateForm()">Add</button>
                </div>
            </div>
            <div v-if="newHostResponse.success"><small class="form-text text-success">
				{{newHostResponse.message}}
			</small></div>
            <div v-if="!newHostResponse.success"><small class="form-text text-danger">
					{{newHostResponse.message}}
			</small></div>
        
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
				this.fieldFocus.username = true;
				break;
			case "firstName":
				this.fieldFocus.firstName = true;
				break;
			case "lastName":
				this.fieldFocus.lastName = true;
				break;
			case "gender":
				this.fieldFocus.gender = true;
				break;
			case "password":
				this.fieldFocus.password = true;
				break;
			case "passwordConfirmation":
				this.fieldFocus.passwordConfirmation = true;
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
		addNewHost: function(user) {
			axios
	          .post('rest/user/register', {"username":user.username, "password":user.password, "firstName":user.firstName, "lastName":user.lastName, "gender":user.gender, "role":"HOST"})
	          .then(response => (this.checkAddNewHostResponse(response.data)));
		},
		checkAddNewHostResponse: function(response){
			console.log(response)
			this.newHostResponse = response;
			if(response.success){
				this.user = {
		    		username: "",
		    		firstName: "",
		    		lastName: "",
		    		gender: null,
		    		password: "",
		    		passwordConfirmation: ""
		    	}
				this.fieldFocus = {
		    		username: null,
		    		firstName: null,
		    		lastName: null,
		    		gender: null,
		    		password: null,
		    		passwordConfirmation: null
		    	}
			}
		}
	},
	mounted() {
		if(this.role != "ADMIN") {
    		this.$router.push({ name: 'home' });
		} else {
			axios
	        .get('rest/user/genders')
	        .then(response => (this.genders = response.data));
		}
    },
    created() {
    	
    }

});
