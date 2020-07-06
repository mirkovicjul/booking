Vue.component("reservations", {
	data: function () {
		    return {
				loggedIn: localStorage.getItem("jwt") ? true : false,
		    	role: localStorage.getItem("role"),
		    	user: localStorage.getItem("user"),
		    	reservations: [
		    		{
		    			id: null,
		    			apartment: {
		    				name: ""
		    			}
		    		}
		    	],
		    	apartments: [],
		    	comment: "",
		    	rating: null,
		    	leaveCommentResponse: {},
		    	searchUrl: "search?",
		    	reservationStatuses: [],
		    	selectedStatuses: [],
		    	searchUrlStatuses: "",
		    	searchResults: true,
		    	allReservations: true,
		    	showSearchForm: false,
		    	username: null,
		    	searchUrlUsername: ""
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
	    <br>
	    <br>
	    <br>
	    <h4>Reservations</h4>
	    <div v-if="reservations.length==0 && allReservations"><small class="form-text text-danger">
			You haven't made any reservations yet!
		</small></div>		
		<br>
		<div v-if="role=='ADMIN' || role=='HOST'"class="form-group">
            <label class="asearch-label" for="selectbasic" v-on:click="showSearch()">Search
                <img src='images/triangle_down.png' height="21" width="21" v-if="!showSearchForm" />
                <img src='images/triangle_up.png' height="21" width="21" v-if="showSearchForm" />
            </label>
		</div>
		
		<div v-if="showSearchForm && (role=='ADMIN' || role=='HOST')">
			<div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Username:</label>
                <div class="">
                    <input name="username" class="form-control" type="text" v-model="username">
                </div>
            </div>
            
            <div class="form-group">
                <div class="">
             		<button class="btn btn-info" v-on:click="search(username)">Search</button>
        		</div>
            </div>
		</div>
		<br>
		<div class="form-row">
			<div v-if="role=='ADMIN' || role=='HOST'" class="col-2">							
			       <div class="col-lg-12">
					     <div class="button-group">
					        <button type="button" class="btn btn-info  dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-cog"></span> <span class="caret">Status</span></button>
							<ul class="dropdown-menu">
							  <li v-for="status in reservationStatuses"  data-value="status"><input name="checkboxes" id="checkboxes-0" :value="status" v-model="selectedStatuses" type="checkbox" />&nbsp;{{status}}</li>
							  <li><button  @click="filterByStatus()" class="btn btn-info">Filter</button></li>
							</ul>			
					  	 </div>
					</div>			 	
			 </div>
			 			 
		 	<div v-if="role=='ADMIN' || role=='HOST' || role=='GUEST'"class="col-2">
				<div class="dropdown">
				  <button class="btn btn-info dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				    Sort by
				  </button>
				  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
				    <a class="dropdown-item" v-on:click="orderBy('price', 'ascending')">Price (ascending)</a>
				    <a class="dropdown-item" v-on:click="orderBy('price', 'descending')">Price (descending)</a>
				  </div>
				</div>
			</div>
		</div>
		

			
	 	<br>
	 	<div v-if="!searchResults"><small class="form-text text-danger">
			No matching results found.
		</small></div>
		
		<br>
		<table v-if="reservations.length!=0" class="table table-hover">
          <thead>
		    <tr>
		      <th scope="col">Apartment</th>
		      <th scope="col">Guest</th>
		      <th scope="col">Check-in date</th>
		      <th scope="col">Check-out date</th>
		      <th scope="col">Price</th>
		      <th scope="col">Message</th>
		      <th scope="col">Status</th>
		      <th scope="col" colspan="2"></th>
		    </tr>
		  </thead>
		  <tbody>
		    <tr v-for="reservation in reservations">	    	
			      <td v-for="apartment in apartments" v-if="apartment.id==reservation.apartmentId">{{apartment.name}}</td>
			      <td>{{reservation.guest}}</td> 
			      <td>{{reservation.startDate | moment}}</td> 
			      <td>{{reservation.endDate | moment}}</td>
			      <td>{{reservation.price}}</td> 
			      <td>{{reservation.message}}</td>
			      <td>{{reservation.status}}</td>
			      <div v-if="role=='HOST'">
				      <td v-if="reservation.status=='CREATED'"><button class="btn btn-info" v-on:click="updateStatus('ACCEPTED', reservation.id)" >Accept</button></td> 
			    	  <td v-if="(reservation.status=='CREATED' || reservation.status=='ACCEPTED') && !reservationEnded(reservation.endDate)"><button class="btn btn-info" v-on:click="updateStatus('DECLINED', reservation.id)">Decline</button></td>
					  <td v-if="reservation.status=='ACCEPTED' && reservationEnded(reservation.endDate)"><button class="btn btn-info" v-on:click="updateStatus('FINISHED', reservation.id)">Finish</button></td>
		    	  </div>
		    	  <div v-if="role=='GUEST'">
				      <td v-if="reservation.status=='CREATED' || reservation.status=='ACCEPTED'"><button class="btn btn-info" v-on:click="updateStatus('CANCELLED', reservation.id)" >Cancel</button></td> 
		    	  </div>
		    	  <div v-if="role=='GUEST' && reservationEnded(reservation.endDate) && (reservation.status=='FINISHED' || reservation.status=='DECLINED')">
				      <td><button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModalCenter">Leave a comment</button></td> 
		    	  </div>
		    	  <div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
					  <div class="modal-dialog modal-dialog-centered" role="document">
					    <div class="modal-content">
						    <div id="big-form" class="well auth-box">
						      <div class="modal-header">
						        <h5 class="modal-title" id="exampleModalLongTitle">Leave a review</h5>
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true">&times;</span>
						        </button>
						      </div>
						      <div class="modal-body">
						        <textarea v-model="comment" class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
						        <br>
						        <div class="form-group row">
							           <label class="col-sm-2 col-form-label" for="textinput">Rating:</label>
							           <div class="">
						                   <select id="selectbasic" v-model="rating" class="form-control">
				                                <option value="1">1</option>
				                                <option value="2">2</option>
				                                <option value="3">3</option>
				                                <option value="4">4</option>
				                                <option value="5">5</option>
			                            	</select>
					                	</div>
				                </div>
				                <div v-if="leaveCommentResponse.success"><small class="form-text text-success">
										{{leaveCommentResponse.message}}
									</small></div>
									<div v-if="!leaveCommentResponse.success"><small class="form-text text-danger">
										{{leaveCommentResponse.message}}
									</small>
								</div>
						      </div>
						      <div class="modal-footer">
						        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
						        <button type="button" class="btn btn-primary" v-on:click="leaveComment(reservation)" v-bind:disabled="!validate()">OK</button>
						      </div>
						     </div>
					    </div>
					  </div>
				  </div>
		    </tr>
		  </tbody>
		</table>
	</div>
</div>
`
	, 
	methods : {
		updateStatus: function(newStatus, reservationId){
			var payload = {"reservationId":reservationId, "user":localStorage.getItem("user"), "status":newStatus};
			console.log(payload);
			axios
	        .post('rest/reservation/update', payload)
	        .then(response => (this.checkUpdateStatusResponse(response.data)));
		},
		checkUpdateStatusResponse: function(response){
			if(response.success){
				axios
		        .get('rest/reservation/all')
		        .then(response => (this.reservations = response.data));
			}
		},
		reservationEnded: function(endDate){
			var now = Date.now();
			return now > endDate;
		},
		leaveComment: function(reservation){
			var payload = {"apartmentId":reservation.apartmentId, "commentator":{"username": this.user}, "comment":this.comment, "rating":this.rating};
			axios
	        .post('rest/apartment/comment', payload)
	        .then(response => (this.checkLeaveCommentResponse(response.data)));
		},
		checkLeaveCommentResponse: function(response){
			this.leaveCommentResponse = response;
			this.comment = "";
			this.rating = null;
		},
		validate: function(){
			if(this.comment.trim()=="")
				return false;
			else if(this.rating==null)
				return false;
			return true;
		},
		filterByStatus: function(){
			if(this.selectedStatuses.length!=0){
				this.searchUrlStatuses = '&status=';
				for(var i = 0; i < this.selectedStatuses.length; i++){
					console.log(this.selectedStatuses[i])
					this.searchUrlStatuses+=this.selectedStatuses[i] + ','
				}
				this.searchUrlStatuses = this.searchUrlStatuses.substring(0, this.searchUrlStatuses.length-1);
			} else {
				this.searchUrlStatuses = "";
			}
			console.log(this.searchUrl+this.searchUrlStatuses+this.searchUrlUsername)
			axios
		       .get('rest/reservation/'+this.searchUrl+this.searchUrlStatuses+this.searchUrlUsername)
		       .then(response => (this.checkSearchResults(response.data)));
		},
		search: function() {
			if(this.username != null && this.username.trim() != "")
				this.searchUrlUsername="&username="+this.username;
			else
				this.searchUrlUsername=""
			axios
		       .get('rest/reservation/'+this.searchUrl+this.searchUrlStatuses+this.searchUrlUsername)
		       .then(response => (this.checkSearchResults(response.data)));
		},
		orderBy: function(param, order){			
			if(param=="price" && order=="ascending"){
				this.reservations = this.reservations.sort(function(a, b) {
				    return parseFloat(a.price) - parseFloat(b.price);
				});
			} else if(param=="price" && order=="descending"){
				this.reservations = this.reservations.sort(function(a, b) {
				    return parseFloat(b.price) - parseFloat(a.price);
				});
			}
		},
		checkSearchResults: function(response){
			this.reservations = response;
			if(this.reservations.length==0){
				this.searchResults = false;
			} else {
				this.searchResults = true;
			}
			this.allReservations = false;
		},

		showSearch: function(){
			this.showSearchForm = !this.showSearchForm;
		}
	},
	mounted() {
		if(!this.loggedIn) {
			this.$router.push({ name: 'search' });
		} else {		
			axios
	        .get('rest/reservation/all')
	        .then(response => (this.reservations = response.data));
			axios
	        .get('rest/apartment/all')
	        .then(response => (this.apartments = response.data));
			axios
	        .get('rest/reservation/status/all')
	        .then(response => (this.reservationStatuses = response.data));
			this.allReservations = true;
		}
    },
    created() {
    	
    },
    filters: {
		moment: function (date) {
		    return moment(date).format('MMMM Do YYYY');
		}
    }
});
