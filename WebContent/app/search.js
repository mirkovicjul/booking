Vue.component("search", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	apartment: {
		    		rooms: null,
		    		guests: null,
		    		priceMin: null,
		    		priceMax: null,
		    		checkIn: null,
		    		checkOut: null, 
		    		location: ""
		    		},
		    	cin: {
	    			
	    		},
	    		cout: {
	    			
	    		},
	    		searchUrl: "search?",
	    		searchFilterUrl: "search?",
	    		searchResults: false,
	    		apartments: [],
	    		amenities: [],	    		
	    		noMatchingApartments: false,
	    		noMatchingApartmentsFilter: false,
	    		searchUrlAmenities: "",
	    		selectedAmenities: [],
	    		searchUrlType: "",
	    		typeSelected: null,
	    		filterTypeApartment: false,
	    		filterTypeRoom: false,
	    		searchUrlStatus: "",
	    		statusSelected: null,
	    		filterStatusActive: false,
	    		filterStatusInactive: false
		    }
	},
	template: `
<div class="containerbody">
	<br>
	<br>
    <div v-if="!searchResults" class="container auth">
        <h3 class="text-center">Find your perfect holiday destination
        </h3>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
            <form>
                <fieldset>
                	
                	<div class="form-group row">
		                <label class="col-sm-2 col-form-label" for="textinput">Location:</label>
		                <div class="">
		                    <input class="form-control" type="text" v-model="apartment.location">
		                </div>
            		</div>
                	
                    <div  class="form-group row">		        	
		                <label class="col-sm-2 col-form-label" for="textinput">Check-in date:</label>
		                <div class="">
		                	<vuejsDatepicker v-model="apartment.checkIn" placeholder="Click to see the calendar" ></vuejsDatepicker>
		                </div>		            
			        </div>
			        
			        <div  class="form-group row">		            
		                <label class="col-sm-2 col-form-label" for="textinput">Check-out date:</label>
		                <div class="">
		                	<vuejsDatepicker v-model="apartment.checkOut" placeholder="Click to see the calendar"></vuejsDatepicker>
		                </div>						
	        		</div> 

                    <div class="form-group row">
		                <label class="col-sm-2 col-form-label" for="textinput">Number of rooms:</label>
		                <div class="">
		                    <input id="textinput" v-model="apartment.rooms" class="form-control input-md" type="number"  min = "1">     
		                </div>
		            </div>
		            
		           	<div class="form-group row">
		                <label class="col-sm-2 col-form-label" for="textinput">Number of guests:</label>
		                <div class="">
		                    <input id="textinput" v-model="apartment.guests" class="form-control input-md" type="number"  min = "1">              	
		                </div>
		            </div>
		            <div class="form-group row">
		                <label class="col-sm-2 col-form-label" for="textinput">Price:</label>
		                <div class="">
		                    <input id="textinput" v-model="apartment.priceMin" placeholder="min" class="form-control input-md" type="number"  min = "1">
		                </div>
		                <div class="">
		                    <input id="textinput" v-model="apartment.priceMax" placeholder="max" class="form-control input-md" type="number"  min = "1">             	
		                </div>
		            </div>
		            
					<br>
                    <div class="form-group">
                        <div class="">
                     		<button class="btn btn-info" v-on:click="search(apartment)">Search</button>
                		</div>
                    </div>
                </fieldset>
            </form>
        </div>
        <div class="clearfix"></div>
    </div>
    <div v-if="searchResults" class="container">
		<div class="form-row">
			<div class="col-2">
				<div class="dropdown">
				  <button class="btn btn-info dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				    Sort by
				  </button>
				  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
				    <a class="dropdown-item" v-on:click="orderBy('price', 'ascending')">Price (ascending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('price', 'descending')">Price (descending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('rooms', 'ascending')">Number of rooms (ascending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('rooms', 'descending')">Number of rooms (descending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('capacity', 'ascending')">Max guests (ascending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('capacity', 'descending')">Max guests (descending)</a>
				  </div>
				</div>
			</div>
			
			<div v-if="role=='GUEST' || role=='ADMIN' || role=='HOST'" class="col-3">
					<div class="btn-group btn-group-toggle" >
					  <label class="btn btn-info shadow-none" v-bind:class="{active: this.filterTypeRoom==true}">
					    <input @click="filterByType('ROOM')" data-toggle="button" type="radio" >Room
					  </label>
					  <label class="btn btn-info shadow-none" v-bind:class="{active: this.filterTypeApartment==true}">
					    <input @click="filterByType('APARTMENT')" data-toggle="button" type="radio" >Apartment
					  </label>
					</div>										
			</div>
			
			<div v-if="role=='GUEST' || role=='ADMIN' || role=='HOST'" class="col-3">			
					<div class="btn-group btn-group-toggle" >
					  <label class="btn btn-info shadow-none" v-bind:class="{active: this.filterStatusActive==true}">
					    <input @click="filterByStatus('true')" data-toggle="button" type="radio" >Active
					  </label>
					  <label class="btn btn-info shadow-none" v-bind:class="{active: this.filterStatusInactive==true}">
					    <input @click="filterByStatus('false')" data-toggle="button" type="radio" >Inactive
					  </label>
					</div>								
			</div>
			<div v-if="role=='GUEST' || role=='ADMIN' || role=='HOST'" class="col-3">			
				<div class="row">
			       <div class="col-lg-12">
					     <div class="button-group">
					        <button type="button" class="btn btn-info  dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-cog"></span> <span class="caret">Amenities</span></button>
							<ul class="dropdown-menu">
							  <li v-for="amenity in amenities"  data-value="amenity.id"><input name="checkboxes" id="checkboxes-0" :value="amenity" v-model="selectedAmenities" type="checkbox"/>&nbsp;{{amenity.name}}</li>
							  <li><button  @click="filterByAmenities()" class="btn btn-info">Filter</button></li>
							</ul>			
					  </div>
					</div>
			 	</div>
			</div>
		</div>
		<br>
		<br>
	    <section class="col-xs-12 col-sm-6 col-md-12">
	    <div v-for="apartment in apartments">
	      <article class="search-result row">
			  <div class="col-xs-12 col-sm-12 col-md-3">
				     <img src="images/hotel.png" />
			  </div>
			  <div class="col-xs-12 col-sm-12 col-md-4 ">
			          <h2><a title=""  style="cursor: pointer;" :href="'#/apartment/'+apartment.id">{{apartment.name}}</a></h2>
			          <div v-if="role=='HOST' || role=='ADMIN'">
			          	<a  title=""  style="cursor: pointer;" :href="'#/edit-apartment/'+apartment.id">Edit</a>
			          </div>
			          <p>{{apartment.apartmentType}}</p>
			          <p>Max guests: {{apartment.capacity}}</p>
			          <p>Number of rooms: {{apartment.numberOfRooms}}</p>
			          <p>Price: €{{apartment.price}}/night</p>
			          <div v-if="role=='ADMIN'"><p v-if="apartment.active">Active</p>
			          <p v-if="!apartment.active">Not active</p></div>
			  </div>
			  
			  <div class="col-xs-12 col-sm-12 col-md-4">	          
			            <span>{{apartment.location.address.street}}, {{apartment.location.address.city}} {{apartment.location.address.postalCode}}, {{apartment.location.address.country}}</span>
						<small class="form-text">
							{{apartment.location.latitude}} {{apartment.location.longitude}}
						</small>		                 
			  </div>
			    <div v-if="noMatchingApartmentsFilter" class="container auth">
			       	<small class="form-text text-danger">
						No matching results found.
					</small>
				</div>
	      </article>
	      <br>
	      <br>
	    </div>
	     <div v-if="noMatchingApartments" class="container auth">
			       	<small class="form-text text-danger">
						No matching results found.
					</small>
		</div>
	  </section>
    </div>
  
</div>
`
	, 
	methods : {
		search: function(params){
			var searchUrl = "search?";
			var query = {};
			if(params.location.trim()!=""){
				searchUrl = searchUrl + '&location=' + params.location;
				query.location = params.location
			}
			if(params.checkIn!=null){
				var checkInTimestamp = params.checkIn.getTime();
				searchUrl = searchUrl + '&checkIn=' + checkInTimestamp;
				query.checkIn = checkInTimeStamp;
			}
			if(params.checkOut!=null){
				var checkOutTimestamp = params.checkOut.getTime();
				searchUrl = searchUrl + '&checkOut=' + checkOutTimestamp;
				query.checkOut = checkOutTimeStamp;
			}
			if(params.priceMin!=null){
				searchUrl = searchUrl + '&priceMin=' + params.priceMin;
				query.priceMin = params.priceMin;
			}
			if(params.priceMax!=null){
				searchUrl = searchUrl + '&priceMax=' + params.priceMax;
				query.priceMax = params.priceMax;
			}
			if(params.guests!=null){
				searchUrl = searchUrl + '&guests=' + params.guests;
				query.guests = params.guests;
			}
			if(params.rooms!=null){
				searchUrl = searchUrl + '&rooms=' + params.rooms;
				query.rooms = params.rooms;
			}					
			this.$router.push({ name: 'search', query });			
			axios
		        .get('rest/apartment/'+this.searchUrl)
		        .then(response => (this.checkSearchResults(response.data)));
		},
		checkSearchResults: function(response){
			console.log(response)
			this.searchResults = true;
			this.apartments = response;
			this.allApartments = response;
			if(this.apartments.length==0){
				this.noMatchingApartments = true;
				this.noMatchingApartmentsFilter = true;
			} else {
				this.noMatchingApartments = false;
				this.noMatchingApartmentsFilter = false;
			}
			axios
	        .get('rest/amenity/all')
	        .then(response => (this.amenities = response.data));
		},
		orderBy: function(param, order){			
			if(param=="price" && order=="ascending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(a.price) - parseFloat(b.price);
				});
			} else if(param=="price" && order=="descending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(b.price) - parseFloat(a.price);
				});
			} else if(param=="rooms" && order=="ascending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(a.numberOfRooms) - parseFloat(b.numberOfRooms);
				});
			} else if(param=="rooms" && order=="descending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(b.numberOfRooms) - parseFloat(a.numberOfRooms);
				});
			} else if(param=="capacity" && order=="ascending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(a.capacity) - parseFloat(b.capacity);
				});
			} else if(param=="capacity" && order=="descending"){
				this.apartments = this.apartments.sort(function(a, b) {
				    return parseFloat(b.capacity) - parseFloat(a.capacity);
				});
			}		
		},
		filterByType: function(type){			
			 if(type=="ROOM"){
				 if(this.filterTypeRoom){
					 this.typeSelected = null;
					 this.filterTypeRoom = false;
					 this.filterTypeApartment = false;
					 this.searchUrlType = "";
				 } else {
					 this.typeSelected = type;
					 this.filterTypeRoom = true;
					 this.filterTypeApartment = false;
					 this.searchUrlType = "&type="+type;
				 }
			 } else if(type=="APARTMENT"){
				 if(this.filterTypeApartment){
					 this.typeSelected = null;
					 this.filterTypeApartment = false;
					 this.filterTypeRoom = false;
					 this.searchUrlType = "";
				 } else {
					 this.typeSelected = type;
					 this.filterTypeApartment = true;
					 this.filterTypeRoom = false;
					 this.searchUrlType = "&type="+type;						 
				 }
			 }
			 axios
		        .get('rest/apartment/'+this.searchUrl+this.searchUrlType+this.searchUrlStatus+this.searchUrlAmenities)
		        .then(response => (this.checkSearchResults(response.data)));
		},
		filterByStatus: function(status) {
			 if(status=="true"){
				 if(this.filterStatusActive){
					 this.statusSelected = null;
					 this.filterStatusActive = false;
					 this.filterStatusInactive = false;
					 this.searchUrlStatus = "";
				 } else {
					 this.statusSelected = status;
					 this.filterStatusActive = true;
					 this.filterStatusInactive = false;
					 this.searchUrlStatus = "&status="+status;
				 }
			 } else if(status=="false"){
				 if(this.filterStatusInactive){
					 this.statusSelected = null;
					 this.filterStatusInactive = false;
					 this.filterStatusActive = false;
					 this.searchUrlStatus = "";
				 } else {
					 this.statusSelected = status;
					 this.filterStatusInactive = true;
					 this.filterStatusActive = false;
					 this.searchUrlStatus = "&status="+status;
				 }
			 }
			 console.log(this.searchUrlType+this.searchUrlStatus+this.searchUrlAmenities)
			 axios
		        .get('rest/apartment/'+this.searchUrl+this.searchUrlType+this.searchUrlStatus+this.searchUrlAmenities)
		        .then(response => (this.checkSearchResults(response.data)));
		},
		filterByAmenities: function(){
			console.log(this.selectedAmenities);
			if(this.selectedAmenities.length!=0){
				this.searchUrlAmenities = '&amenities=';
				for(var i = 0; i < this.selectedAmenities.length; i++){
					console.log(this.selectedAmenities[i].name)
					this.searchUrlAmenities+=this.selectedAmenities[i].id + ','
				}
				this.searchUrlAmenities = this.searchUrlAmenities.substring(0, this.searchUrlAmenities.length-1);
			} else {
				this.searchUrlAmenities = "";
			}
			axios
		       .get('rest/apartment/'+this.searchUrl+this.searchUrlType+this.searchUrlStatus+this.searchUrlAmenities)
		       .then(response => (this.checkSearchResults(response.data)));
		}
	},
	mounted() {
		this.$root.$on('restartParams', () => {
			this.searchResults = false;
			this.apartments = [];
			this.apartment = {
	    		rooms: null,
	    		guests: null,
	    		priceMin: null,
	    		priceMax: null,
	    		checkIn: null,
	    		checkOut: null, 
	    		location: ""
			}
			this.searchUrlType = "";
			this.searchUrlStatus = "";
			this.searchUrlAmenities = "";
			this.selectedAmenities = [],
    		this.typeSelected = null,
    		this.filterTypeApartment = false,
    		this.filterTypeRoom = false,
    		this.statusSelected = null,
    		this.filterStatusActive = false,
    		this.filterStatusInactive = false			
		});
		
		var params = false;
		if(this.$route.query.location!=null){
			this.searchUrl = this.searchUrl + '&location=' + this.$route.query.location;
			params = true;
		}
		if(this.$route.query.checkIn!=null){
			this.searchUrl = this.searchUrl + '&checkIn=' + this.$route.query.checkIn;
			params = true;
		}
		if(this.$route.query.checkOut!=null){
			this.searchUrl = this.searchUrl + '&checkOut=' + this.$route.query.checkOut;
			params = true;
		}
		if(this.$route.query.priceMin!=null){
			this.searchUrl = this.searchUrl + '&priceMin=' + this.$route.query.priceMin;
			params = true;
		}
		if(this.$route.query.priceMax!=null){
			this.searchUrl = this.searchUrl + '&priceMax=' + this.$route.query.priceMax;
			params = true;
		}
		if(this.$route.query.guests!=null){
			this.searchUrl = this.searchUrl + '&guests=' + this.$route.query.guests;
			params = true;
		}
		if(this.$route.query.rooms!=null){
			this.searchUrl = this.searchUrl + '&rooms=' + this.$route.query.rooms;
			params = true;
		}		
		if(params){
			axios
	        .get('rest/apartment/'+this.searchUrl)
	        .then(response => (this.checkSearchResults(response.data)));
		} 
    },
    created() {
    	
    },
    components: {
    	vuejsDatepicker
    }
});
