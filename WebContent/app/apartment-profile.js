Vue.component("apartment", {
	data: function () {
		    return {
		    	apartment: {
		    		location: {
		    			address: {}
		    		}
		    	},
		    	disabledDates: {
		    		ranges: []
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
                <label class="col-sm-2 col-form-label" for="textinput">Check-in </label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.checkIn}}</label>
            </div>
            
            <div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Check-out </label>
                <label class="col-sm-2 col-form-label" for="textinput">{{apartment.checkOut}}</label>
            </div>
        </div>
        <br>
        <br>
        <table class="table  table-hover">
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
		<br>
		<br>
		<div class="form-group row">
                <label class="col-sm-2 col-form-label" for="textinput">Availability</label>
                <div>
                	<vuejsDatepicker placeholder="Click to see the calendar" :disabled-dates="disabledDates"></vuejsDatepicker>
                </div>
        </div>
        <br>
        <br>
        <table class="table table-hover">
          <thead>
		    <tr>
		      <th scope="col">User</th>
		      <th scope="col">Comment</th>
		      <th scope="col">Rating</th>
		      <th scope="col">Approved</th>
		    </tr>
		  </thead>
		  <tbody>
		    <tr v-for="comment in apartment.comments">
		      <td>{{comment.commentator.username}}</td>
		      <td>{{comment.comment}}</td> 
		      <td>{{comment.rating}}</td> 
		      <td v-if="comment.approved">Yes</td>
		      <td v-if="!comment.approved">No</td>         
		    </tr>
		  </tbody>
		</table>
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
			if(params.disabledDates != null) {
				for(i=0; i<params.disabledDates.length;i++){
					var start = new Date(params.disabledDates[i].startDate);
					var end = new Date(params.disabledDates[i].endDate);
					var d = {
							from: start,
							to: end
					}
					dates.push(d);
				}
				this.disabledDates.ranges = dates;
			}
		}
	},
	mounted() {
		axios
        .get('rest/apartment/'+this.$route.params.id)
        .then(response => (this.setParameters(response.data)));
    },
    created() {
    	
    },
    components: {
    	vuejsDatepicker
    }
});
