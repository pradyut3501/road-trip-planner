import './App.css';
//import './Location.js';
//import MapContainer from './Map.js';
//import {GoogleMapReact, LocationPin} from 'google-maps-react';
//import GooglePlacesAutocomplete from './Map.js'
import './index.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { DirectionsRenderer, DirectionsService, TravelMode, DirectionsStatus } from "react-google-maps";
import {useState} from 'react'

/*global google*/

let origin = null
let dest = null
let direction_service = null
//const google = window.google;


function App() {

  //const [value, setValue] = useState(null);

  //<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"></script>

  const script = document.createElement("script");
  script.async = true;
  script.defer = true;
  script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c";

  function setOrigin(newOrigin){
    origin = newOrigin
    //direction_service = new DirectionsService
    //direction_service.origin = origin
    console.log(origin.value.place_id)

  }

  function setDest(newDest){
    dest = newDest
    //direction_service.destination = dest
    console.log(dest.value.place_id)
    //console.log(origin.value)
    //let directions = direction_service.route()
    //console.log(directions)
    //let directions = DirectionsService;
    let directions = new google.maps.DirectionsService()
    console.log(dest.value)
    let directionsRenderer = new google.maps.DirectionsRenderer()
    //console.log(dest.value.formatted_address)
    //directions.origin = origin.location;
    //directions.destination = dest.location;

    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
          console.log(result)

      directionsRenderer.setDirections(result);

      var directionsData = result.routes[0].legs[0];

      var steps = [];

      for (var i = 0; i < directionsData.steps.length; i++) {
        let currStep = directionsData.steps[i]
        let startLat = currStep.start_location.lat()//.Scopes[0].d
        let startLon = currStep.start_location.lng()//.Scopes[0].e
        let endLat = currStep.end_location.lat()//.Scopes[0].d
        let endLon = currStep.end_location.lng()//.Scopes[0].e
        steps.push([startLat, startLon, endLat, endLon])

       }

       console.log(steps)

          //this.setState({
          //  directions: result,
          //});
        } else {
          console.error(`error fetching directions ${result}`);
        }
      });
  
  }

  return (

    <div>
    <h1>     Welcome to the RhodeTrip Planner!</h1>
    <div>
    Where do you want to start?
    <GooglePlacesAutocomplete id = "origin" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          origin,
          onChange: setOrigin,
        }}/>

    <br></br>
    Where do you want to go?
    <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          dest,
          onChange: setDest,
        }}/>


    </div>
    </div>


  );

}

export default App;
