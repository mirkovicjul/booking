Vue.component("apartment", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	username: localStorage.getItem("user"),
		    	apartment: {
		    		location: {
		    			address: {}
		    		}
		    	},
		    	disabledDates: {
		    		ranges: [],
		    		to: null
		    	},
		    	disabledDatesCheckIn: {
		    		ranges: [],
		    		to: null,
		    		from: null
		    	},
		    	disabledDatesCheckOut: {
		    		ranges: [],
		    		to: null
		    	},
		    	reservation: {
		    		message: "",
		    		price: null,
		    	},
		    	createReservationResponse: {},
		    	addDisabledDateResponse: {},
		    	price: null,
		    	cin: null,
		    	cout: null,
		    	commentsShown: false
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
        <br>
        <br>
        <br>
        <div id="big-form" class="well auth-box">
            <div class="center-element">
                <h1 class="text-info">{{apartment.name}}</h1>
                  <label class="col-sm-2 col-form-label" for="textinput">
                	{{apartment.location.address.street}}, {{apartment.location.address.city}} 
                	{{apartment.location.address.postalCode}}, 
                	{{apartment.location.address.country}}
                	<small class="form-text">{{apartment.location.latitude}} {{apartment.location.longitude}}</small>
                </label>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Type</label>
                <label class="col-sm-2 col-form-label">{{apartment.apartmentType}}</label>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Max guests</label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.capacity}}</label>
            </div>
           
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Number of rooms</label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.numberOfRooms}}</label>
            </div>   
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Price per night</label>
                <label class="col-sm-2 col-form-label" for="textinput">€{{apartment.price}}</label>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Check-in</label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.checkIn}}</label>
            </div> 
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Check-out </label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.checkOut}}</label>
            </div>
        </div>
        <br>
        <br>
        <div>
	        <table  v-if="apartment.amenities != null" class="table  table-hover">
	          	<thead>
				    <tr>
				      <th scope="col">Amenities</th>
				    </tr>
			  	</thead>
			  
			  	<tbody>
				    <tr v-for="amenity in apartment.amenities">
				      <td>{{amenity.name}}</td>      
				    </tr>
				</tbody>
				
			</table>
			<div v-if="apartment.amenities==null">
				<h5>Amenities</h5>
		       	<small class="form-text text-danger">
					This apartment doesn't have any amenities.
				</small>
			</div>
		</div>
		<br>
		<br>
		<div v-if="role!='GUEST'" class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Availability</label>
                <div>
                	<vuejsDatepicker v-model="cin" placeholder="Click to see the calendar" :disabled-dates="disabledDates"></vuejsDatepicker>
                </div>
        </div>
        <div v-if="role=='GUEST'">
	        <div  class="form-group row">
	        	<div class="col">
	                <label for="textinput">Check-in date</label>
	                <div v-on:focusout="setDisabledDatesCheckOut()">
	                	<vuejsDatepicker v-model="cin" placeholder="Click to see the calendar" :disabled-dates="disabledDatesCheckIn" ></vuejsDatepicker>
	                </div>
	            </div>
	            <div class="col">
	                <label for="textinput">Check-out date</label>
	                <div v-on:focusout="setDisabledDatesCheckIn()">
	                	<vuejsDatepicker v-model="cout" placeholder="Click to see the calendar" :disabled-dates="disabledDatesCheckOut"></vuejsDatepicker>
	                </div>
	            </div>
	        </div> 
	        <br>
	        <div v-if="price!=null" class="form-group row">
	                <label class="col-sm-2 col-form-label" for="textinput"><h5>Total price</h5></label>
	                <label class="col-sm-2 col-form-label"><h5>€{{price}}</h5></label>
	        </div>
	        <br>    
	        <div class="form-group">
			    <label for="exampleFormControlTextarea1">Say hi to your host!</label>
			    <textarea v-model="reservation.message" class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
			</div>
	        <div class="form-group">
	                <div class="">
	                     <button class="btn btn-info" v-on:click="createReservation()" v-bind:disabled="!validateForm()">Book</button>
	                </div>
	                <div v-if="createReservationResponse.success"><small class="form-text text-success">
							{{createReservationResponse.message}}
						</small></div>
						<div v-if="!createReservationResponse.success"><small class="form-text text-danger">
							{{createReservationResponse.message}}
						</small>
					</div>
	        </div>
	    </div>       
        <div v-if="role=='HOST'">
        	<h5>Disable apartment</h5>
	        <div  class="form-group row">
	        
	        	<div class="col">
	                <label for="textinput">Start date</label>
	                <div v-on:focusout="setDisabledDatesCheckOut()">
	                	<vuejsDatepicker v-model="cin" placeholder="Click to see the calendar" :disabled-dates="disabledDatesCheckIn" ></vuejsDatepicker>
	                </div>
	            </div>
	            <div class="col">
	                <label for="textinput">End date</label>
	                <div v-on:focusout="setDisabledDatesCheckIn()">
	                	<vuejsDatepicker v-model="cout" placeholder="Click to see the calendar" :disabled-dates="disabledDatesCheckOut"></vuejsDatepicker>
	                </div>
	            </div>
	        </div> 

	        <br>    

	        <div class="form-group">
	                <div class="">
	                     <button class="btn btn-info" v-on:click="addDisabledDate()" v-bind:disabled="!validateForm()">Disable</button>
	                </div>
	                <div v-if="addDisabledDateResponse.success"><small class="form-text text-success">
							{{addDisabledDateResponse.message}}
						</small></div>
						<div v-if="!addDisabledDateResponse.success"><small class="form-text text-danger">
							{{addDisabledDateResponse.message}}
						</small>
					</div>
	        </div>
	    </div>
        
        <br>
        <div class="form-group">
            <label class="asearch-label" for="selectbasic" v-on:click="showComments()">Comments
                <img src='images/triangle_down.png' height="21" width="21" v-if="!commentsShown" />
                <img src='images/triangle_up.png' height="21" width="21" v-if="commentsShown" />
            </label>
       </div>

	    <div v-if="commentsShown">
	        <table class="table table-hover">
	          <thead>
			    <tr>
			      <th scope="col">User</th>
			      <th scope="col">Comment</th>
			      <th scope="col">Rating</th>
			      <span v-if="role=='HOST' || role=='ADMIN'">
			      	<th scope="col">Approved</th>
			      </span>
			    </tr>
			  </thead>
			  <tbody>
			    <tr v-for="comment in apartment.comments">	    	
				      <td>{{comment.commentator.username}}</td>
				      <td>{{comment.comment}}</td> 
				      <td>{{comment.rating}}</td> 
				    <span v-if="role=='HOST' || role=='ADMIN'">
				      <td v-if="comment.approved">Yes</td>
				      <td v-if="!comment.approved">No</td>
				    </span>         
			    </tr>
			  </tbody>
			</table>
		</div>
    </div>
</div>
`
	, 
	methods : {
		setParameters: function(params) {
			console.log(params);
			this.apartment = params;
			var i;
			var dates = [];
			var datesCheckOut = [];
			if(params.disabledDates != null) {
				console.log(params.disabledDates);
				for(i=0; i<params.disabledDates.length;i++){
					var start = new Date(params.disabledDates[i].startDate);
					var end = new Date(params.disabledDates[i].endDate);
					var endCheckIn = new Date(end.getFullYear(),end.getMonth(),end.getDate()+1);
					var endCheckOut = new Date(end.getFullYear(),end.getMonth(),end.getDate()+2);
					var d = {
							from: start,
							to: endCheckIn
					}
					var dCheckOut = {
							from: start,
							to: endCheckOut
					}
					dates.push(d);
					datesCheckOut.push(dCheckOut);				
				}
				this.disabledDates.ranges = dates;
				this.disabledDatesCheckIn.ranges = dates;
				this.disabledDatesCheckOut.ranges = datesCheckOut;
			}
			var to = new Date();
			this.disabledDates.to = to;
			this.disabledDatesCheckIn.to = to;
			this.disabledDatesCheckOut.to = to;
		},
		setDisabledDatesCheckOut: function(){
			if(this.cin != null){
				var nextDay = new Date(this.cin.getFullYear(),this.cin.getMonth(),this.cin.getDate()+1);
				this.disabledDatesCheckOut.to = nextDay;
				var endDates = this.disabledDatesCheckOut
				.ranges
				.filter(d => this.cin < d.from)
				.sort((a, b) => a < b);
						
				this.disabledDatesCheckOut.from = endDates[0].from;
			}
		},
		setDisabledDatesCheckIn: function(){
			if(this.cout != null){
				var checkOutDate = new Date(this.cout.getFullYear(),this.cout.getMonth(),this.cout.getDate());
				this.disabledDatesCheckIn.from = checkOutDate;
			}
		},
		validateForm: function(){
			if(this.cin==null)
				return false;
			else if(this.cout==null)
				return false;
			else if(this.role=="GUEST" && this.reservation.message.trim()=="")
				return false;
			else
				return true;
		},
		createReservation: function(){
			this.reservation.startDate = this.cin.getTime();
			this.reservation.endDate = this.cout.getTime(); 
			this.reservation.apartmentId = this.apartment.id;
			this.reservation.guest = this.username;
			this.reservation.price = this.price;
			var trimMessage = this.reservation.message.trim();
			this.reservation.message = trimMessage;
			console.log(this.reservation);
			axios
	          .post('rest/reservation/add', this.reservation)
	          .then(response => (this.createReservationResponse = response.data));
		},
		addDisabledDate: function(){
			var startDate = this.cin.getTime();
			var endDate = this.cout.getTime(); 
			axios
	          .post('rest/apartment/disable', {"apartmentId" : this.apartment.id, "startDate": startDate, "endDate": endDate})
	          .then(response => (this.addDisabledDateCheckResponse(response.data)));
		},
		addDisabledDateCheckResponse: function(response){
			this.addDisabledDateResponse = response;
			if(response.success){
				axios
		        .get('rest/apartment/'+this.$route.params.id)
		        .then(response => (this.setParameters(response.data)));
			}
		},
		showComments: function(){
			this.commentsShown = !this.commentsShown;
		}
	},
	mounted() {
		axios
        .get('rest/apartment/'+this.$route.params.id)
        .then(response => (this.setParameters(response.data)));
    },
    created() {
    	
    },
    watch : {
        cin: function(val) {
        	if(this.cout != null)
        		this.price = Math.round(Math.abs((this.cout - this.cin) / 86400000)) * this.apartment.price;
        },
        cout: function (val) {
        	if(this.cin != null)
        		this.price = Math.round(Math.abs((this.cout - this.cin) / 86400000)) * this.apartment.price;
        }
     },
    components: {
    	vuejsDatepicker
    }
});
