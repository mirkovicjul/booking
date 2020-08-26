Vue.component("apartments", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	apartments: [],
		    	myInactiveApartments: [],
		    	deleteApartmentId: null,
		    	apartmentNotDeleted: null
		    }
	},
	template: `
<div class="containerbody">
	<div class="container auth">
		<br>
		<br>
		  <small class="form-text text-danger" v-if="apartmentNotDeleted">
				Could not delete apartment. Something went wrong.
		  </small>
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
				          	<a  title=""  style="cursor: pointer;" :href="'#/edit-apartment/'+apartment.id">Edit </a>|
				          	<a  title=""  style="cursor: pointer;" :href="''" @click="setDeleteApartmentId(apartment.id)" data-toggle="modal" data-target="#exampleModalCenter"> Delete</a> 
				         <div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
							  <div class="modal-dialog modal-dialog-centered" role="document">
							    <div class="modal-content">
								    <div id="big-form" class="well auth-box">
								      <div class="modal-header">
								        <h6 class="modal-title" id="exampleModalLongTitle">This action cannot be undone. Are you sure you want to delete this apartment?</h6>
								        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
								          <span aria-hidden="true">&times;</span>
								        </button>
								      </div>
								
								      <div class="modal-footer">
								        <button type="button" class="btn btn-primary" @click="setDeleteApartmentId()" data-dismiss="modal">No</button>
								        <button type="button" class="btn btn-secondary" @click="deleteApartment(this.deleteApartmentId)" data-dismiss="modal">Yes</button>
								      </div>
								     </div>
							    </div>
							  </div>
						  </div>  
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
					          <a  title=""  style="cursor: pointer;" :href="'#/edit-apartment/'+apartment.id">Edit</a> | 
					          <a  title=""  style="cursor: pointer;" :href="''" @click="setDeleteApartmentId(apartment.id)" data-toggle="modal" data-target="#exampleModalCenter"> Delete</a> 
						      <div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
									  <div class="modal-dialog modal-dialog-centered" role="document">
									    <div class="modal-content">
										    <div id="big-form" class="well auth-box">
										      <div class="modal-header">
										        <h6 class="modal-title" id="exampleModalLongTitle">This action cannot be undone. Are you sure you want to delete this apartment?</h6>
										        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
										          <span aria-hidden="true">&times;</span>
										        </button>
										      </div>
										
										      <div class="modal-footer">
										        <button type="button" class="btn btn-primary" @click="setDeleteApartmentId()" data-dismiss="modal">No</button>
										        <button type="button" class="btn btn-secondary" @click="deleteApartment(this.deleteApartmentId)" data-dismiss="modal">Yes</button>
										      </div>
										     </div>
									    </div>
									  </div>
								  </div>
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
		},
		setDeleteApartmentId: function(apartmentId){
			this.deleteApartmentId = apartmentId;
		},
		deleteApartment: function() {
			axios
			  .post('rest/apartment/'+this.deleteApartmentId+"/delete")
			      .then(response => (this.checkDeleteApartmentResponse(response.data)));
		},
		checkDeleteApartmentResponse: function(response){
			console.log(response)
			if(response.success)
				window.location.reload();
			else{
				this.apartmentNotDeleted = true;
			}
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
