Vue.component("new-apartment", {
	data: function () {
		    return {
		    	loggedIn: localStorage.getItem("jwt") ? true : false,
    			cin: {
	    			time: 2,
	    			period: "PM"
	    		},
	    		cout: {
	    			time: 11,
	    			period: "AM"
	    		},
		    	apartment: {
		    		name: "",
		    		apartmentType: "",
		    		numberOfRooms: null,
		    		capacity: null,
		    		price: null,
		    		checkIn: "",
		    		checkOut: "", 
		    		location: {
		    			latitude: "",
		    			longitude: "",
		    			address: {
		    				street: "",
		    				city: "",
		    				postalCode: "",
		    				country: ""
		    			}
		    		},
		    		amenities: []
		    	},
		    	apartmentTypes: [],
		    	amenities: [],
		    	fieldFocus: {
		    		name: false,
		    		type: false,
		    		rooms: false,
		    		capacity: false,
		    		price: false,
		    		street: false,
		    		city: false,
		    		postalCode: false,
		    		country: false,
		    		latitude: false,
		    		longitude: false
		    		
		    	},
		    	newApartmentResponse: {}
		 
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
        	<h4>Create new apartment</h4>
        	<div v-if="newApartmentResponse.success"><small class="form-text text-success">
					{{newApartmentResponse.message}}
				</small></div>
				<div v-if="!newApartmentResponse.success"><small class="form-text text-danger">
					{{newApartmentResponse.message}}
				</small>
			</div>
			<br>
			
            <h5>Basic info</h5>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Name:</label>
                <div class="">
                    <input class="form-control" type="text" v-model="apartment.name" v-on:focusout="setFieldFocus('name')">
                	<div v-if="fieldFocus.name && apartment.name.length==0">
	                   	<small class="form-text text-danger">
							Apartment name is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
               <label class="col-sm-2 col-form-label" for="textinput">Apartment type:</label>
                <div class="">
                    <select id="selectbasic" name="selectbasic" class="form-control" v-model="apartment.apartmentType" v-on:focusout="setFieldFocus('type')">
                        <option v-for="type in apartmentTypes" :key="type">{{type}}</option>
                    </select>
                    <div v-if="fieldFocus.type && apartment.apartmentType.length==0">
	                   	<small class="form-text text-danger">
							Apartment type is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Number of rooms:</label>
                <div class="">
                    <input id="textinput" v-model="apartment.numberOfRooms" class="form-control input-md" type="number"  min = "1" v-on:focusout="setFieldFocus('rooms')">     
                	<div v-if="fieldFocus.rooms && apartment.numberOfRooms==null">
	                   	<small class="form-text text-danger">
							Number of rooms is required.
						</small>
            		</div>
                </div>
            </div>
            
           	<div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Max guests:</label>
                <div class="">
                    <input id="textinput" v-model="apartment.capacity" class="form-control input-md" type="number"  min = "1" v-on:focusout="setFieldFocus('capacity')">              	
                	<div v-if="fieldFocus.capacity && apartment.capacity==null">
	                   	<small class="form-text text-danger">
							Number of guests is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Price:</label>
                <div class="">
                    <input id="textinput" v-model="apartment.price" class="form-control input-md" type="number"  min = "1" v-on:focusout="setFieldFocus('price')">              	
                	<div v-if="fieldFocus.price && apartment.price==null">
	                   	<small class="form-text text-danger">
							Price per night is required.
						</small>
            		</div>
                </div>
            </div>
            
            <div class="form-row">
                <label class="col-sm-2 col-form-label" for="textinput">Check-in:</label>
            	<div class="col-1">
                	<input id="textinput" v-model="cin.time" class="form-control input-md" type="number"  min = "1" max="12">              	
            	</div>
            	<div class="col-1">
                	<select id="selectbasic" name="selectbasic" class="form-control" v-model="cin.period">
                        <option value="AM">AM</option>
                        <option value="PM">PM</option>
                    </select>
                </div>               
            </div>
            <br>
            
            <div class="form-row">
                <label class="col-sm-2 col-form-label" for="textinput">Check-out:</label>
                <div class="col-1">
                    <input id="textinput" v-model="cout.time" class="form-control input-md" type="number"  min = "1" max="12">              	
				</div>
				<div class="col-1">
					<select id="selectbasic" name="selectbasic" class="form-control" v-model="cout.period">
                        <option value="AM">AM</option>
                        <option value="PM">PM</option>
                    </select>
                </div>
            </div>            
            <br>
            
           	<h5>Location</h5>
           	<div class="form-row">				
			    <div class="col-4">
			      <input type="text" class="form-control" placeholder="Street" v-model="apartment.location.address.street" v-on:focusout="setFieldFocus('street')">
			      <div v-if="fieldFocus.street && apartment.location.address.street.length==0">
	                   	<small class="form-text text-danger">
							Street is required.
						</small>
				  </div>
			    </div>
			    <div class="col">
			      <input type="text" class="form-control" placeholder="City" v-model="apartment.location.address.city" v-on:focusout="setFieldFocus('city')">
			       <div v-if="fieldFocus.city && apartment.location.address.city.length==0">
	                   	<small class="form-text text-danger">
							City is required.
						</small>
				  </div>
			    </div>
			    <div class="col">
			      <input type="text" class="form-control" placeholder="Postal code" v-model="apartment.location.address.postalCode" v-on:focusout="setFieldFocus('postalCode')">
			       <div v-if="fieldFocus.postalCode && apartment.location.address.postalCode.length==0">
	                   	<small class="form-text text-danger">
							Postal code is required.
						</small>
				  </div>
			    </div>
			    <div class="col">
			      <input type="text" class="form-control" placeholder="Country" v-model="apartment.location.address.country" v-on:focusout="setFieldFocus('country')">
			       <div v-if="fieldFocus.country && apartment.location.address.country.length==0">
	                   	<small class="form-text text-danger">
							Country is required.
						</small>
				  </div>
			    </div>		   
		   	</div>
		   	<br>

		   	<div class="form-group row">
                <div class="col-3">
			      <input type="text" class="form-control" placeholder="Latitude" v-model="apartment.location.latitude" v-on:focusout="setFieldFocus('latitude')">
			       <div v-if="fieldFocus.latitude && apartment.location.latitude.length==0">
	                   	<small class="form-text text-danger">
							Latitude is required.
						</small>
				  </div>
			    </div>
			    <div class="col-3">
			      <input type="text" class="form-control" placeholder="Longitude" v-model="apartment.location.longitude" v-on:focusout="setFieldFocus('longitude')">
			       <div v-if="fieldFocus.longitude && apartment.location.longitude.length==0">
	                   	<small class="form-text text-danger">
							Longitude is required.
						</small>
				  </div>
			    </div>	
            </div>		  	
		  	<br>
		  	
		  	<h5>Amenities</h5>
		  	<div class="form-group">
                <div>
               		<div class="" v-for="amenity in amenities">
	                    <div class="checkbox">
	                        <label for="checkboxes-0">
	                            <input name="checkboxes" id="checkboxes-0" :value="amenity" v-model="apartment.amenities" type="checkbox"> {{amenity.name}}
	                        </label>
	                    </div>
                </div>
            </div>
            </div>

		   	<div class="form-group">
                <div class="">
                     <button class="btn btn-info" v-on:click="create(apartment)" v-bind:disabled="!validateForm()">Create</button>
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
		setFieldFocus: function(field) {
			switch(field){
			case "name":
				this.fieldFocus.name = true;
				break;
			case "type":
				this.fieldFocus.type = true;
				break;
			case "rooms":
				this.fieldFocus.rooms = true;
				break;
			case "capacity":
				this.fieldFocus.capacity = true;
				break;
			case "price":
				this.fieldFocus.price = true;
				break;
			case "street":
				this.fieldFocus.street = true;
				break;
			case "city":
				this.fieldFocus.city = true;
				break;
			case "postalCode":
				this.fieldFocus.postalCode = true;
				break;
			case "country":
				this.fieldFocus.country = true;
				break;
			case "latitude":
				this.fieldFocus.latitude = true;
				break;
			case "longitude":
				this.fieldFocus.longitude = true;
				break;
			}
		},
		validateForm: function(){
			if(this.apartment.name.length == 0)
				return false;
			else if(this.apartment.apartmentType.length == 0)
				return false;
			else if(this.apartment.numberOfRooms == null)
				return false;
			else if(this.apartment.capacity == null)
				return false;
			else if(this.apartment.price == null)
				return false;
			else if(this.apartment.location.address.street.length == 0)
				return false;
			else if(this.apartment.location.address.city.length == 0)
				return false;
			else if(this.apartment.location.address.postalCode.length == 0)
				return false;
			else if(this.apartment.location.address.country.length == 0)
				return false;
			else if(this.apartment.location.latitude.length == 0)
				return false;
			else if(this.apartment.location.longitude.length == 0)
				return false;
			else
				return true;
		},
		create: function(apartment) {
			console.log(apartment);
			apartment.checkIn = this.cin.time+this.cin.period;
			apartment.checkOut = this.cout.time+this.cout.period;
			apartment.capacity = parseInt(apartment.capacity);
			apartment.numberOfRooms = parseInt(apartment.numberOfRooms);
			apartment.price = parseInt(apartment.price);
			axios
	          .post('rest/apartment/add', apartment)
	          .then(response => (this.newApartmentCheckResponse(response.data)));
		},
		newApartmentCheckResponse: function(response) {
			this.newApartmentResponse = response;
			if(this.newApartmentResponse.success){
				this.apartment = {
		    		name: "",
		    		apartmentType: "",
		    		numberOfRooms: null,
		    		capacity: null,
		    		price: null,
		    		checkIn: "",
		    		checkOut: "", 
		    		location: {
		    			latitude: "",
		    			longitude: "",
		    			address: {
		    				street: "",
		    				city: "",
		    				postalCode: "",
		    				country: ""
		    			}
		    		},
		    		amenities: []
		    	};
				this.fieldFocus = {
		    		name: false,
		    		type: false,
		    		rooms: false,
		    		capacity: false,
		    		price: false,
		    		street: false,
		    		city: false,
		    		postalCode: false,
		    		country: false,
		    		latitude: false,
		    		longitude: false
		    		
		    	}
			}
		}
	},
	mounted() {
		axios
        .get('rest/apartment/types')
        .then(response => (this.apartmentTypes = response.data));
		axios
        .get('rest/amenity/all')
        .then(response => (this.amenities = response.data));
    },
    created() {
    
    }
});
