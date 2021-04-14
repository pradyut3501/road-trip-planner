import './App.css';
import './index.js'
import GooglePlacesAutocomplete from 'react-google-places-autocomplete';
import { GoogleMap, DirectionsRenderer, DirectionsService, TravelMode, DirectionsStatus } from "react-google-maps";
import { Map, Marker, GoogleApiWrapper, InfoWindow } from "google-maps-react"

import {useState, useEffect} from 'react'
import { AwesomeButton } from "react-awesome-button";
import TextBox from './TextBox.js';
import "react-awesome-button/dist/themes/theme-eric.css";
import axios from 'axios';

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import fork from './fork.png'
import park from './park.png'
import museum from './museum.png'
import shop from './shop.png'


//import { Link } from 'react-router-dom';

/*global google*/

let origin = null
let dest = null
let middle = null
let destText = ""
let direction_service = null
let costPreference = 1
let steps = []
let shortestRouteDist = ""
//let shortestRouteTime = null
let distanceMessage = ["", ""]
let logo = "https://i.ibb.co/drqk6c8/logo.png";
let restaurantLogo = "fork.png"
let response_message = ""
let originCoords = []
let destCoords = []
let stops = 0
let time = 0
let dist = 0
let directions = null
let directionsRenderer = null
let map = null
let markerList = []

//const google = window.google;

function App() {

  const [shortestRouteTime, setShortestRouteTime] = useState("");
  const [submitted, setSubmitted] = useState(0)
  const [attractions, setAttractions] = useState([])
  const [originText, setOriginText] = useState("")
  const [destText, setDestText] = useState("")

  const [restaurant, setRestaurant] = useState(30);
  const [museum, setMuseum] = useState(30);
  const [park, setPark] = useState(30);
  const [shop, setShop] = useState(30);

  const handleChangeRestaurant = (event, newValue) => {
    setRestaurant(newValue);
  };

  const handleChangeMuseum = (event, newValue) => {
    setMuseum(newValue);
  };

  const handleChangePark = (event, newValue) => {
    setPark(newValue);
  };

  const handleChangeShop = (event, newValue) => {
    setShop(newValue);
  };

  const useStyles = makeStyles({
    root: {
      width: 100,
    },
  });

  const classes = useStyles();


  //const [value, setValue] = useState(null);

  //<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"></script>

  const script = document.createElement("script");
  script.async = true;
  script.defer = true;
  script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c";

  function setOrigin(newOrigin){
    origin = newOrigin
  }

  function setMiddle(newMiddle){
    middle = newMiddle
    console.log(middle.geometry)
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

  function setStops(value){
    stops = value
    console.log(stops)
  }

  function setDist(value){
    dist = value
  }

  function setTime(value){
    time = value
  }

  function setDest(newDest){
    dest = newDest
    console.log(dest.value.place_id)
    //let directions = new google.maps.DirectionsService()
    console.log(dest.value)
    //let directionsRenderer = new google.maps.DirectionsRenderer()

    getRouteInfo();
  }

    function initMap(){
      //var directionsService = new google.maps.DirectionsService();
      //var directionsRenderer = new google.maps.DirectionsRenderer();
      let initialView = new google.maps.LatLng(41.850033, -87.6500523);
      var mapOptions = {
        zoom:7,
        center: initialView
      }
      map = new google.maps.Map(document.getElementById('map'), mapOptions);
      directionsRenderer.setMap(map);
      directionsRenderer.setPanel(document.getElementById('directionsPanel'));
    }

  function getRouteInfo(){

    directions = new google.maps.DirectionsService()
    directionsRenderer = new google.maps.DirectionsRenderer()


    directions.route({
        origin: origin.value.description,
        destination: dest.value.description,
        travelMode: google.maps.TravelMode.DRIVING,
      }, (result, status) => {
        if (status === google.maps.DirectionsStatus.OK) {
          console.log(result)

      directionsRenderer.setDirections(result);
      initMap()

      shortestRouteDist = result.routes[0].legs[0].distance.text
      setShortestRouteTime(result.routes[0].legs[0].duration.text)


      let directionsData = result.routes[0].legs[0];

      steps = [];
      let tripLength = directionsData.steps.length
      for (var i = 0; i < tripLength; i++) {
        let currStep = directionsData.steps[i]
        let startLat = currStep.start_location.lat()
        let startLon = currStep.start_location.lng()
        let endLat = currStep.end_location.lat()
        let endLon = currStep.end_location.lng()
        steps.push([startLat, startLon, endLat, endLon])

       }
       console.log(steps)

       originCoords = [steps[0][0], steps[0][1]]
       destCoords = [steps[tripLength-1][0], steps[tripLength-1][1]]

       //when axios is ready, move this into requestTrip

        } else {
          console.error(`error fetching directions ${result}`);
        }
      });

  }

    // make axios post request to the backend
    const requestTrip = () => {

      console.log(costPreference)
      console.log(steps)
      console.log(restaurant)
      console.log(museum)
      console.log(park)
      console.log(originCoords)
      console.log(destCoords)

      // the source and destination of our desired route
      const toSend = {
        costPref: costPreference,
        route: steps,
        restValue: restaurant,
        musValue: museum,
        parkValue: park,
        shopValue: shop,
        stopPref: stops,
        originLat: originCoords[0],
        originLon:originCoords[1],
        destLat: destCoords[0],
        destLon: destCoords[1],
        maxTime: time,
        maxDist: dist,
        middleLat: middle

      }

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

        /*  let stop1 = {
            id: "tnhfDv5Il8EaGSXZGiuQGg",
            name: "Garaje",
            location: ["475 3rd St", "San Francisco", "CA", "94107"],
            coordinates: [37.7817529521, -122.39612197],
            price: 1.0,
            rating: 4.5
          }

          let stop2 = {
            id: "tnhfDv5Il8EaGSXZGiuQGh",
            name: "Garaje",
            location: ["475 3rd St", "San Francisco", "CA", "94107"],
            coordinates: [37.7817529521, -122.39612197],
            price: 1.0,
            rating: 4.5
          } */

           // add the start and the end to the list of attractions
           let originNode = {
             id: "",
             name: originText,
             location:  "",
             coordinates: [originCoords[0], originCoords[1]]
           }

           let destNode = {
             id: "",
             name: destText,
             location:  "",
             coordinates: [destCoords[0], destCoords[1]]
           }
           markerList = []
          let newAttractions = response.data["route"]
          setAttractions(response.data["route"])
          for (let i = 0; i < newAttractions.length; i++){
            let marker = new google.maps.Marker({
              position: {lat: newAttractions[i].coordinates[0], lng:newAttractions[i].coordinates[1] },
              map: map,
              icon: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'
            })
          let infoWindow = new google.maps.InfoWindow({
            content: '<div> <h3>' + newAttractions[i].name + '</h3>' + newAttractions[i].location[1] + ", " + newAttractions[i].location[2] + '</div>'
          })
            marker.addListener('click', function(){
            infoWindow.open(map, marker)
            })
            markerList.push(marker)

          }
          console.log("in axios")
          console.log(response.data["route"])


          setOriginText("Start: " + origin.label)
          setDestText("End: " + dest.label)

          setSubmitted(1);


        })

        .catch(function(error) {
          console.log(error);

        });

    }


    /*const requestTrip2 = () => {

      let stop1 = {
        id: "tnhfDv5Il8EaGSXZGiuQGg",
        name: "Garaje",
        location: ["475 3rd St", "San Francisco", "CA", "94107"],
        coordinates: [37.7817529521, -122.39612197],
        price: 1.0,
        rating: 4.5
      }

      let stop2 = {
        id: "tnhfDv5Il8EaGSXZGiuQGh",
        name: "Plant City",
        location: ["475 3rd St", "Providence", "RI", "94107"],
        coordinates: [41.8207975,-71.406356],
        price: 1.0,
        rating: 4.5
      }

      setAttractions([stop1, stop2])

    }*/

    useEffect(()=> {
      console.log("in use effect")
      console.log(shortestRouteDist)
      console.log(shortestRouteTime)
      distanceMessage[0] = "The quickest route for this origin and destination is "
      distanceMessage[1] = "and"
    }, [shortestRouteTime])

    useEffect(()=> {
      console.log("changing message")
      response_message = "Here's your route!"

    }, [submitted])

    useEffect(()=> {
      console.log("changing message")
      console.log(attractions)

    }, [attractions])

    useEffect(()=> {
      console.log("adding origin")
      console.log(originText)

    }, [originText])





  return (

    <div>
    &nbsp;
      <div className={"center"}><img src={logo} alt={"RhodeTrip logo"} style={{width: '500px'}}></img></div>
    <div>
    &nbsp;

    Where do you want to start?
    <GooglePlacesAutocomplete id = "origin" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
    selectProps={{
          origin,
          onChange: setOrigin,
        }}
      style = {{width: '66%'}}/>

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
      <TextBox label = {"Maximum time (hours): "} change = {setTime} />
      <TextBox label = {"Maximum distance (miles): "} change = {setDist} />

      <br></br>
      &nbsp;
      What's your budget like?
      <br></br>

      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar1} > $ < /AwesomeButton>*/}
      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar2} > $$ < /AwesomeButton>*/}
      {/*&nbsp;*/}
      {/*<AwesomeButton type = "primary" onPress = {setDollar3} > $$$ < /AwesomeButton>*/}
        <div class="item1">
          <input type="radio" value="$" name="budget" onChange={setDollar1} checked/> $
          <input type="radio" value="$$" name="budget" onChange={setDollar2}/> $$
          <input type="radio" value="$$$" name="budget" onChange={setDollar3}/> $$$
        </div>

      <br></br>
      <br></br>

      <TextBox label = {"Preferred # of stops: "}
      change = {setStops} />

      <br></br>
      Any stops you need to make on the way?
      <GooglePlacesAutocomplete id = "destination" apiKey="AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c"
      selectProps={{
            middle,
            onChange: setMiddle,
          }}/>

      <br></br>

      How much do you prefer the following types of stops?

          <div className={classes.root}>
            <Typography id="continuous-slider" gutterBottom>
              Restaurants
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={restaurant} onChange={handleChangeRestaurant} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Museums
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={museum} onChange={handleChangeMuseum} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Parks
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={park} onChange={handleChangePark} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Shops
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs>
                <Slider value={shop} onChange={handleChangeShop} aria-labelledby="continuous-slider" />
              </Grid>
            </Grid>

        </div>
        <AwesomeButton type = "secondary" onPress = {requestTrip} > Get my trip! < /AwesomeButton>
        <div><p>{response_message}</p></div>
        <div>
        {originText} </div>
        <div>

          {attractions.map(function (x,i, elements){
          if (x.nodeType == "restaurant"){
            return (<p><img src="museum.png" alt={"shop icon"} style={{width: '50px'}}/></p>)
          }
          if(i < elements.length - 1 ){
            return (<p><img src={fork} alt={"restaurant"} style={{width: '100px'}}/>
            <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">{x.name}</a>
          <br></br> {x.rating} stars
          <br></br> {x.location[1]}, {x.location[2]}
          <br></br> <a href={"https://www.google.com/maps/dir/" +
          x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]}
          target="_blank">take me to stop {i + 2}!</a>
        </p>)}})}

          </div>
          <div>
        <p>{destText} </p>
        </div>

        <div id="map" style={{float: "center", width: 600, height: 400}}></div>



    </div>
    </div>


  );

}

export default App;
