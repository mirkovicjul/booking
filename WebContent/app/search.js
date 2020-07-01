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
	    		searchResults: false,
	    		apartments: []
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
			          <div v-if="role=='ADMIN'"><p v-if="apartment.active">Active</p>
			          <p v-if="!apartment.active">Not active</p></div>
			  </div>
			  
			  <div class="col-xs-12 col-sm-12 col-md-4">	          
			            <span>{{apartment.location.address.street}}, {{apartment.location.address.city}} {{apartment.location.address.postalCode}}, {{apartment.location.address.country}}</span>
						<small class="form-text">
							{{apartment.location.latitude}} {{apartment.location.longitude}}
						</small>		                 
			  </div>
	      </article>
	      <br>
	      <br>
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
		        .get('rest/apartment/'+searchUrl)
		        .then(response => (this.checkSearchResults(response.data)));
		},
		checkSearchResults: function(response){
			console.log(response)
			this.searchResults = true;
			this.apartments = response;
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
		});
		var searchUrl = "search?";
		var params = false;
		if(this.$route.query.location!=null){
			searchUrl = searchUrl + '&location=' + this.$route.query.location;
			params = true;
		}
		if(this.$route.query.checkIn!=null){
			searchUrl = searchUrl + '&checkIn=' + this.$route.query.checkIn;
			params = true;
		}
		if(this.$route.query.checkOut!=null){
			searchUrl = searchUrl + '&checkOut=' + this.$route.query.checkOut;
			params = true;
		}
		if(this.$route.query.priceMin!=null){
			searchUrl = searchUrl + '&priceMin=' + this.$route.query.priceMin;
			params = true;
		}
		if(this.$route.query.priceMax!=null){
			searchUrl = searchUrl + '&priceMax=' + this.$route.query.priceMax;
			params = true;
		}
		if(this.$route.query.guests!=null){
			searchUrl = searchUrl + '&guests=' + this.$route.query.guests;
			params = true;
		}
		if(this.$route.query.rooms!=null){
			searchUrl = searchUrl + '&rooms=' + this.$route.query.rooms;
			params = true;
		}		
		if(params){
			axios
	        .get('rest/apartment/'+searchUrl)
	        .then(response => (this.checkSearchResults(response.data)));
		} 
    },
    created() {
    	
    },
    components: {
    	vuejsDatepicker
    }
});
