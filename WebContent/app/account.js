Vue.component("account", {
	data: function () {
		    return {
			  loggedIn: localStorage.getItem("jwt") ? true : false,
		      username: localStorage.getItem("user"),
		      info: {
		    	  firstName: "",
		    	  lastName: "",
		    	  gender: ""
		      },
		      genders: [],
		      newPassword: "",
		      passwordConfirmation:"",	     
		      passwordConfirmationFieldFocus: null,
		      userInfoUpdateResponse: {
		    	  success: null,
		    	  message: ""
		      },
		      passwordUpdateResponse: {
		    	  success: null,
		    	  message: ""
		      }
		    }
	},
	template: ` 
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
        	<h4>Personal info</h4>
        	<div v-if="userInfoUpdateResponse.success"><small class="form-text text-success">
				{{userInfoUpdateResponse.message}}
			</small></div>
			<div v-if="!userInfoUpdateResponse.success"><small class="form-text text-danger">
				{{userInfoUpdateResponse.message}}
			</small></div>
			<br>
			
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control" type="text" v-model="username" disabled>
                </div>
            </div>
           
            <div class="form-group row">
            <label class="col-sm-2 col-form-label" for="selectbasic">Gender:</label>
                <div class="">
		            <select class="form-control" v-model="info.gender">
					  <option disabled value="">Please select one</option>
					  <option v-for="gender in genders" :key="gender">{{gender}}</option>
					</select>
				</div>		
            </div>
           
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">First name:</label>
                <div class="">
                    <input name="firstName" class="form-control" type="text" v-model="info.firstName">
                </div>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Last name:</label>
                <div class="">
                    <input name="lastName" class="form-control" type="text" v-model="info.lastName">                
                </div>
            </div>
                   	
            <div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="updateUserInfo(info)">Save changes</button>
                </div>
        	</div>
            <br><br>
			
			<h4>Change Password</h4>		
			<div v-if="passwordUpdateResponse.success"><small class="form-text text-success">
				{{passwordUpdateResponse.message}}
			</small></div>
			<div v-if="!passwordUpdateResponse.success"><small class="form-text text-danger">
				{{passwordUpdateResponse.message}}
			</small></div>
			<br>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">New password:</label>
                <div class="">
                    <input name="password" class="form-control" type="password" v-model="newPassword">
                </div>
            </div>
            
             <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Confirm password:</label>
                <div class="">
                    <input name="passwordConfirmation" class="form-control" type="password" v-model="passwordConfirmation" v-on:focusin="setFocus()" >
                   	<div v-if="passwordConfirmationFieldFocus && (newPassword!=passwordConfirmation)"><small class="form-text text-danger">
						Passwords need to match.
					</small></div>
                </div>
            </div>
            
            <div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="updatePassword(newPassword)" v-bind:disabled="newPassword.length==0 || newPassword!=passwordConfirmation">Change password</button>
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
		getUserInfo: function(data) {
    		this.info.firstName = data.firstName;
    		this.info.lastName = data.lastName;
    		this.info.gender = data.gender;
		},
		setFocus: function() {
			this.passwordConfirmationFieldFocus = true;
		},
		updateUserInfo: function(info) {
			axios
	          .post('rest/user/update', {"username":localStorage.getItem("user"), "firstName": info.firstName, "lastName":info.lastName, "gender":info.gender})
	          .then(response => (this.updateUserInfoResponse(response.data)));
		},
		updateUserInfoResponse: function(data) {
			this.passwordUpdateResponse.success = null;
			this.passwordUpdateResponse.message = "";
			this.userInfoUpdateResponse = data;
			axios
	        .get('rest/user/account/'+this.username)
	        .then(response => this.getUserInfo(response.data));
		},
		updatePassword: function(password) {
			axios
	          .post('rest/user/updatePassword', {"username":localStorage.getItem("user"), "password":password})
	          .then(response => (this.updatePasswordResponse(response.data)));
		},
		updatePasswordResponse: function(data) {
			this.userInfoUpdateResponse.success = null;
			this.userInfoUpdateResponse.message = "";
			this.passwordUpdateResponse = data;
			this.newPassword = "";
			this.passwordConfirmation = "";
			this.passwordConfirmationFieldFocus = null;
		}
	},
	mounted() {
		if(!this.loggedIn)
    		this.$router.push({ name: 'login' })
    	else
			axios
	        .get('rest/user/account/'+this.username)
	        .then(response => this.getUserInfo(response.data));
			axios
	        .get('rest/user/genders')
	        .then(response => (this.genders = response.data));
    },
    created() {
    	
    }
});
