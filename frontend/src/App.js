import './App.css';
//import './Location.js';
//import MapContainer from './Map.js';
//import {GoogleMapReact, LocationPin} from 'google-maps-react';
//import GooglePlacesAutocomplete from './Map.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { DirectionsRenderer, DirectionsService, TravelMode, DirectionsStatus } from "react-google-maps";
import {useState} from 'react'

let origin = null
let dest = null
let direction_service = null


function App() {

  //const [value, setValue] = useState(null);

  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"></script>

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
    let directions = DirectionsService;
    directions.origin = origin;
    directions.destination = dest;

    directions.route({
        origin: origin,
        destination: dest,
        travelMode: TravelMode.DRIVING,
      }, (result, status) => {
        if (status === DirectionsStatus.OK) {
          this.setState({
            directions: result,
          });
        } else {
          console.error(`error fetching directions ${result}`);
        }
      });
    //let directions =
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
