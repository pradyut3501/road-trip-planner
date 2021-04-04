import './App.css';
//import './Location.js';
//import MapContainer from './Map.js';
//import {GoogleMapReact, LocationPin} from 'google-maps-react';
//import GooglePlacesAutocomplete from './Map.js'
import './index.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { DirectionsRenderer, DirectionsService, TravelMode, DirectionsStatus } from "react-google-maps";
import {useState, useEffect} from 'react'
import { AwesomeButton } from "react-awesome-button";
import "react-awesome-button/dist/styles.css";
import TextBox from './TextBox.js';
import axios from 'axios';

/*global google*/

let origin = null
let dest = null
let direction_service = null
let costPreference = 0
let steps = []
let shortestRouteDist = ""
//let shortestRouteTime = null
let distanceMessage = ["", ""]

//const google = window.google;

function App() {

  const [shortestRouteTime, setShortestRouteTime] = useState("");

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

  function setDollar1(){
    costPreference = 1
  }

  function setDollar2(){
    costPreference = 2
  }

  function setDollar3(){
    costPreference = 3
  }

  function setStops(){

  }

  function setDest(newDest){
    dest = newDest
    console.log(dest.value.place_id)
    //let directions = new google.maps.DirectionsService()
    console.log(dest.value)
    //let directionsRenderer = new google.maps.DirectionsRenderer()

    getRouteInfo();
  }
    //console.log(dest.value.formatted_address)
    //directions.origin = origin.location;
    //directions.destination = dest.location;

    function getRouteInfo(){

    let directions = new google.maps.DirectionsService()
    let directionsRenderer = new google.maps.DirectionsRenderer()


    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
          console.log(result)

      directionsRenderer.setDirections(result);

      shortestRouteDist = result.routes[0].legs[0].distance.text
      setShortestRouteTime(result.routes[0].legs[0].duration.text)


      let directionsData = result.routes[0].legs[0];

      steps = [];

      for (var i = 0; i < directionsData.steps.length; i++) {
        let currStep = directionsData.steps[i]
        let startLat = currStep.start_location.lat()
        let startLon = currStep.start_location.lng()
        let endLat = currStep.end_location.lat()
        let endLon = currStep.end_location.lng()
        steps.push([startLat, startLon, endLat, endLon])

       }
       console.log(steps)
        } else {
          console.error(`error fetching directions ${result}`);
        }
      });

  }

    // make axios post request to the backend
    function requestTrip(){

      // the source and destination of our desired route
      const toSend = {
        costPref: costPreference,
        route: steps

      };

      let config = {
        headers: {
          "Content-Type": "application/json",
          'Access-Control-Allow-Origin': '*',
        }
      }

      axios.post(
          "http://localhost:4567/route",
          toSend,
          config
        )
        .then(response => {
          console.log(response.data);
        })

        .catch(function(error) {
          console.log(error);

        });

    }

    useEffect(()=> {
      console.log("in use effect")
      console.log(shortestRouteDist)
      console.log(shortestRouteTime)
      distanceMessage[0] = "The quickest route for this origin and destination is "
      distanceMessage[1] = "and"
    }, [shortestRouteTime])

  return (

    <div>
    &nbsp;
    <h1> Welcome to the RhodeTrip Planner!</h1>
    <div>
    &nbsp;

    Where do you want to start?
    <GooglePlacesAutocomplete id = "origin" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          origin,
          onChange: setOrigin,
        }}/>

    <br></br>
    <br></br>
    &nbsp;
    Where do you want to go?
    <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          dest,
          onChange: setDest,
        }}/>

      <br></br>

      <div>
      <p>{distanceMessage[0]} {shortestRouteDist} {distanceMessage[1]} {shortestRouteTime}</p>
      </div>
      <br></br>
      Based on this, how much long would you like to spend on the road?
      &nbsp;
      <br></br>
      <TextBox label = {"Maximum time (hours): "} change = {setStops} />
      <TextBox label = {"Maximum distance (miles): "} change = {setStops} />

      <br></br>
      &nbsp;
      What's your budget like?
      <br></br>

      &nbsp;
      <AwesomeButton type = "primary" onPress = {setDollar1} > $ < /AwesomeButton>
      &nbsp;
      <AwesomeButton type = "primary" onPress = {setDollar2} > $$ < /AwesomeButton>
      &nbsp;
      <AwesomeButton type = "primary" onPress = {setDollar3} > $$$ < /AwesomeButton>

      <br></br>
      <br></br>

      <TextBox label = {"Maximum # of stops: "}
      change = {setStops} />

      <br></br>
      Any stops you need to make on the way?
      <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
      selectProps={{
            dest,
            onChange: setDest,
          }}/>


      <br></br>
      <br></br>
      &nbsp;
      <AwesomeButton type = "primary" onPress = {requestTrip} > Get my trip! < /AwesomeButton>



    </div>
    </div>


  );

}

export default App;
