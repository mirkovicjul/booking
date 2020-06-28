Vue.component("reservations", {
	data: function () {
		    return {
		    	role: localStorage.getItem("role"),
		    	reservations: [
		    		{
		    			id: null,
		    			apartment: {
		    				name: ""
		    			}
		    		}
		    	],
		    	apartments: []
		    }
	},
	template: `
<div class="containerbody">
    <div class="container auth">
		<table class="table table-hover">
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
		    </tr>
		  </tbody>
		</table>
	</div>
</div>
`
	, 
	methods : {
		updateStatus: function(newStatus, reservationId){
			var payload = {"reservationId":reservationId, "user": localStorage.getItem("user"), "status": newStatus};
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
		setReservations: function(reservations){
			console.log(reservations);
			this.reservations = reservations;
		}
	},
	mounted() {
		axios
        .get('rest/reservation/all')
        .then(response => (this.setReservations(response.data)));
		axios
        .get('rest/apartment/all')
        .then(response => (this.apartments = response.data));
		
    },
    created() {
    	
    },
    filters: {
		moment: function (date) {
		    return moment(date).format('MMMM Do YYYY');
		}
    }
});
