Vue.component("amenities", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	amenities: [],
		    	selectedAmenity: null,
		    	mode: "",
		    	updateAmenityResponse: {},
		    	newAmenityResponse: {},
		    	deleteAmenityResponse: {},
		    	newAmenityName: ""
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
			<h4>Amenities</h4>
        	<br>
	        <div v-for="amenity in amenities" class="form row">
	        	<br>            
	            <div class="col">
	            	{{amenity.name}}
	            </div>
	            <div class="col">
	                <button class="btn btn-info" v-on:click="setMode(amenity, 'EDIT')">Edit</button>
	            </div>
	            <br>
	            <br>
	         </div>
	         <div v-if="deleteAmenityResponse.success"><small class="form-text text-success">
				{{deleteAmenityResponse.message}}
			</small></div>
			<div v-if="!deleteAmenityResponse.success"><small class="form-text text-danger">
				{{deleteAmenityResponse.message}}
			</small></div>
			<br>     
        </div>
        
        <div class="form row">
        	<div class="col">
	                <button class="btn btn-info" v-on:click="setMode(null, 'CREATE')">Create new amenity</button>
	        </div>
        </div>
        <br>
        <br>
        
        <div id="big-form" class="well auth-box" v-if="mode=='EDIT'">
        	<h4>Edit amenity</h4>
        	<div v-if="updateAmenityResponse.success"><small class="form-text text-success">
				{{updateAmenityResponse.message}}
			</small></div>
			<div v-if="!updateAmenityResponse.success"><small class="form-text text-danger">
				{{updateAmenityResponse.message}}
			</small></div>
			<br>
			
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">ID:</label>
                <div class="">
                    <input class="form-control" type="text" v-model="selectedAmenity.id" disabled>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Name:</label>
                <div class="">
                    <input class="form-control" type="text" v-model="selectedAmenity.name">
                </div>
            </div>
            <div class="form-group row">
              <div class="col-sm-2">
                     <button class="btn btn-info" v-on:click="updateAmenity(selectedAmenity)" v-bind:disabled="selectedAmenity.name.length==0" >Update</button>
              </div>
              <div class="col-sm-2">   
	                <button class="btn btn-info" v-on:click="deleteAmenity(selectedAmenity)">Delete</button>
	          </div>  
            </div>
        </div>
        
        <div id="big-form" class="well auth-box" v-if="mode=='CREATE'">
        	<h4>Create new amenity</h4>
			<div v-if="newAmenityResponse.success"><small class="form-text text-success">
				{{newAmenityResponse.message}}
			</small></div>
			<div v-if="!newAmenityResponse.success"><small class="form-text text-danger">
				{{newAmenityResponse.message}}
			</small></div>
			<br>		
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Name:</label>
                <div class="">
                    <input class="form-control" type="text" v-model="newAmenityName">
                </div>
            </div>
            <div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="createNewAmenity(newAmenityName)" v-bind:disabled="newAmenityName.length==0" >Create</button>
                </div>
            </div>
        </div> 
    </div>
 </div>
`
	, 
	methods : {
		setMode: function(amenity, mode){
			this.mode = mode;
			this.updateAmenityResponse = {};
			this.newAmenityResponse = {};
			this.deleteAmenityResponse = {};
			if(amenity)
				this.selectedAmenity = {id: amenity.id, name: amenity.name};
			else
				this.newAmenityName = "";
		},
		updateAmenity: function(amenity){
			axios
	        .post('rest/amenity/update', {"id":amenity.id, "name":amenity.name})
	        .then(response => (this.updateAmenityCheckResponse(response.data)));
		
		},
		updateAmenityCheckResponse: function(response){
			this.updateAmenityResponse = response;
			if(this.updateAmenityResponse.success){
				axios
		        .get('rest/amenity/all')
		        .then(response => (this.amenities = response.data));
			}
		},
		createNewAmenity: function(name){
			axios
	        .post('rest/amenity/create', {"name":name})
	        .then(response => (this.createNewAmenityCheckResponse(response.data)));
		},
		createNewAmenityCheckResponse: function(response){
			this.newAmenityResponse = response;
			if(this.newAmenityResponse.success){
				axios
		        .get('rest/amenity/all')
		        .then(response => (this.amenities = response.data));
			}
		},
		deleteAmenity: function(amenity){
			axios
	        .post('rest/amenity/delete', {"id":amenity.id, "name":amenity.name})
	        .then(response => (this.deleteAmenityCheckResponse(response.data)));	
		},
		deleteAmenityCheckResponse: function(response){
			this.deleteAmenityResponse = response.data;
			if(this.deleteAmenityResponse.success){
				axios
		        .get('rest/amenity/all')
		        .then(response => (this.amenities = response.data));
				this.updateAmenityResponse = "";
				this.mode = "BROWSE";
				this.selectedAmenity = null;
			}
		}
	},
	mounted() {
		if(this.role != "ADMIN") {
    		this.$router.push({ name: 'home' });
		} else {
			axios
	        .get('rest/amenity/all')
	        .then(response => (this.amenities = response.data));
		}
    },
    created() {
    	
    }
});
