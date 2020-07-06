Vue.component("apartments", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	apartments: [],
		    	myInactiveApartments: []
		    }
	},
	template: `
<div class="containerbody">
	<div class="container auth">
		<br>
		<br>
		  <h3 v-if="role=='HOST'">My active apartments</h3>
		  <div v-if="apartments.length==0 && role=='HOST'">
		       	<small class="form-text text-danger">
					You don't have any active apartments.
				</small>
		  </div>
		  <section class="col-xs-12 col-sm-6 col-md-12">
		    <div v-for="apartment in apartments">
		      <article class="search-result row">
				  <div v-if="apartment.images.length==0" class="col-xs-12 col-sm-12 col-md-3">				  
					     <img src="images/hotel.png" />				
				  </div>
				  <div v-if="apartment.images.length!=0" class="col-xs-12 col-sm-12 col-md-3">			  	
					     <img :src="getImgUrl(apartment)" class="preview-image"/>				
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
		  <br>
		  <br>
		  <div v-if="role=='HOST'">
			  <h3>My inactive apartments</h3>
			  <div v-if="myInactiveApartments.length==0">
		       	<small class="form-text text-danger">
					You don't have any inactive apartments.
				</small>
			  </div>
			  <br>
			  <section class="col-xs-12 col-sm-6 col-md-12">
			    <div v-for="apartment in myInactiveApartments">
			      <article class="search-result row">
					  <div v-if="apartment.images.length==0" class="col-xs-12 col-sm-12 col-md-3">				  
					     <img src="images/hotel.png" />				
				  	  </div>
					  <div v-if="apartment.images.length!=0" class="col-xs-12 col-sm-12 col-md-3">			  	
						     <img :src="getImgUrl(apartment)" class="preview-image"/>				
					  </div>
					  <div class="col-xs-12 col-sm-12 col-md-4 ">
					          <h2><a title=""  style="cursor: pointer;" :href="'#/apartment/'+apartment.id">{{apartment.name}}</a></h2>
					          <a  title=""  style="cursor: pointer;" :href="'#/edit-apartment/'+apartment.id">Edit</a>      
					          <p>{{apartment.apartmentType}}</p>
					          <p>Max guests: {{apartment.capacity}}</p>
					          <p>Number of rooms: {{apartment.numberOfRooms}}</p>
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
</div>
`
	, 
	methods : {
		getImgUrl: function(apartment){
			return apartment.images[0]
		}
	},
	mounted() {
		axios
        .get('rest/apartment/all')
        .then(response => (this.apartments = response.data));
		if(this.role=="HOST"){
			axios
	        .get('rest/apartment/inactive')
	        .then(response => (this.myInactiveApartments = response.data));
		}
    },
    created() {
    	
    }
});
