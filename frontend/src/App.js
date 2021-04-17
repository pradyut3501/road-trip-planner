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
import store from './store.png'
import start from './start.png'
import finish from './finish.png'
import road from './road.png'
import park2 from './park2.png'
import {Container, Row, Col} from "react-bootstrap"
import 'bootstrap/dist/css/bootstrap.min.css';

//import 'semantic-ui-css/semantic.min.css';
import { Icon } from 'semantic-ui-react';

import ThumbUpAltIcon from '@material-ui/icons/ThumbUpAlt';
import ThumbDownAltIcon from '@material-ui/icons/ThumbDownAlt';

/*global google*/

let origin = null
let originPlace = null
let originName = ""
let originWebsite = ""
let originMapURL = ""
let originPhone = ""
let originIcon = ""
let error_message = ""
let dest = null
let destPlace = null
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
let trip_message = ""
let route_message = ""
let summary_text = ""
let loading_message = ""
let originCoords = []
let destCoords = []
let middleCoords = []
let stops = 0
let time = 0
let dist = 0
let directions = null
let directionsRenderer = null
let map = null
let markerList = []
let middlePhotoURL = ""
let tripResourceLink = ""
let resource_name = ""
let destName = ""
let destMapURL = ""
let destWebsite = ""
let destPhone = ""
let destIcon = ""

//const google = window.google;

function App() {

  const [shortestRouteTime, setShortestRouteTime] = useState("");
  const [submitted, setSubmitted] = useState(0)
  const [loading, setLoading] = useState(0)
  const [attractions, setAttractions] = useState([])
  const [originText, setOriginText] = useState("")
  const [destText, setDestText] = useState("")
  //const [destIcon, setDestIcon] = useState("")

  const [restaurant, setRestaurant] = useState(50);
  const [museum, setMuseum] = useState(50);
  const [park, setPark] = useState(50);
  const [shop, setShop] = useState(50);

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
      width: 150,
    },
  });

  const classes = useStyles();

  const script = document.createElement("script");
  script.async = true;
  script.defer = true;
  script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c";

  function setOrigin(newOrigin){
    origin = newOrigin

    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    service.getDetails({
        placeId: origin.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          originPlace = result

        }
      })
    }


  function setMiddle(newMiddle){
    middle = newMiddle
    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    service.getDetails({
        placeId: middle.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {

          console.log(result)
          console.log(result.geometry.location.lat())
          middleCoords = [result.geometry.location.lat(), result.geometry.location.lng()]
          middlePhotoURL = result.photos[0].getUrl()
        }
      })
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

  function setDollar4(){
    costPreference = 4
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

    let service = new google.maps.places.PlacesService(document.getElementById('map'));

    service.getDetails({
        placeId: dest.value.place_id
      }, (result, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          destPlace = result
          console.log(originPlace)
        }
      })

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

      console.log(middle)
      loading_message = "Calculating the perfect trip..."
      setLoading(1)
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
        middleLat: middleCoords[0],
        middleLon: middleCoords[1]

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
          loading_message = ""

          if(response.data["error_message"] != ""){
            error_message = response.data["error_message"] + ": please adjust your preferences"
            setSubmitted(1);
          } else {

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
          // add start/end points to the attraction list
          newAttractions.unshift(originNode)
          newAttractions.push(destNode)
          initMap()

          setAttractions(newAttractions)
          for (let i = 1; i < newAttractions.length - 1; i++){
            console.log("in loop")
            let marker = new google.maps.Marker({
              position: {lat: newAttractions[i].coordinates[0], lng:newAttractions[i].coordinates[1] },
              map: map,
              icon: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'
            })

            console.log(marker.position.lat())
          let infoWindow = new google.maps.InfoWindow({
            content: '<div> <h3>' + newAttractions[i].name + '</h3>' + newAttractions[i].location[1] + ", " + newAttractions[i].location[2] + '</div>'
          })
            marker.addListener('click', function(){
            infoWindow.open(map, marker)
            })
            markerList.push(marker)

          }
          console.log(response.data["route"])
          response_message = "Trip Itinerary"
          trip_message = "Trip Details"
          route_message = "Route Map"

          setOriginText(origin.value.structured_formatting)
          setDestText(dest.value.structured_formatting)


          //initMap()
          tripResourceLink = "https://www.tripsavvy.com/planning-a-road-trip-complete-guide-4845956"
          resource_name = "Planning a Road Trip: The Complete Guide"
          originName = originPlace.name
          originMapURL = originPlace.url
          originWebsite = originPlace.website
          originPhone = originPlace.formatted_phone_number
          originIcon = originPlace.icon

          destName = destPlace.name
          destMapURL = destPlace.url
          destWebsite = destPlace.website
          destPhone = destPlace.formatted_phone_number
          destIcon = destPlace.icon
          //setDestIcon(destIcon)
          console.log(destName)
          console.log(destMapURL)
          console.log(destWebsite)
          console.log(destPhone)
          console.log(destIcon)
          setSubmitted(1);

        }
          //specialDisplay()


        })

        .catch(function(error) {
          console.log(error);

        });

    }

    function specialDisplay(){
      tripResourceLink = "https://www.tripsavvy.com/planning-a-road-trip-complete-guide-4845956"
      originName = originPlace.name
      originMapURL = originPlace.url
      originWebsite = originPlace.website
      originPhone = originPlace.formatted_phone_number
      originIcon = originPlace.icon

      destName = destPlace.name
      destMapURL = destPlace.url
      destWebsite = destPlace.website
      destPhone = destPlace.formatted_phone_number
      destIcon = destPlace.icon

    }


    useEffect(()=> {
      console.log("in use effect")
      console.log(shortestRouteDist)
      console.log(shortestRouteTime)
      distanceMessage[0] = "The quickest route for this origin and destination is "
      distanceMessage[1] = "and"
    }, [shortestRouteTime])

    useEffect(()=> {
      console.log("changing message")

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
  <div className = "App">
    <Container >
    <div >
    <Row >
    <Col>
    </Col>
    <Col sm = {8}>

    &nbsp;
      <div ><img src={logo} alt={"RhodeTrip logo"} style={{width: '500px'}}></img></div>

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


      <p>{distanceMessage[0]} {shortestRouteDist} {distanceMessage[1]} {shortestRouteTime}</p>

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
          <input type="radio" value="$$$$" name="budget" onChange={setDollar4}/> $$$$
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

      <div >
            <Typography id="continuous-slider" gutterBottom>
              Restaurants
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={restaurant} onChange={handleChangeRestaurant} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Museums
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={museum} onChange={handleChangeMuseum} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Parks
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={park} onChange={handleChangePark} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

            <Typography id="continuous-slider" gutterBottom>
              Shops
            </Typography>
            <Grid container spacing={1}>
              <Grid item>
                <ThumbDownAltIcon />
              </Grid>
              <Grid item xs>
                <Slider value={shop} onChange={handleChangeShop} aria-labelledby="continuous-slider" />
              </Grid>
              <Grid item>
                <ThumbUpAltIcon />
              </Grid>
            </Grid>

        </div>

        <AwesomeButton type = "secondary" onPress = {requestTrip} > Get my trip! </AwesomeButton>
        <div><br></br>
        <h1>{loading_message}</h1>
        <h2>{error_message}</h2>
        </div>
        </Col>
        <Col></Col>

        </Row>
        </div>

        <Row>
        <Col>
        <h1>{response_message}</h1>

          {attractions.map(function (x,i, elements){

          //displaying the start
          if (i == 0){
            return (<div><img class = "left" src={start} alt={"start icon"} style={{width: '100px'}}/>
          <h2 class = "text" class = "pad"> {originText.main_text} </h2> <h4> {originText.secondary_text}</h4>
          &nbsp;
          <a href={"https://www.google.com/maps/dir/" +
          x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
          <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}}/></a>

        </div>)
          }
          //displaying the destination
          if (i == elements.length - 1){
            return (<div><br></br><img class = "left" src={finish} alt={"finish icon"} style={{width: '100px'}}/>
            <h2 class = "text"> {destText.main_text} </h2> <h4> {destText.secondary_text}</h4>
            </div>)

          }
          if (x.nodeType == "restaurant"){
            return (<div><div class = "rectangle"><img class = "left" src={fork} alt={"restaurant"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else if (x.nodeType == "museum"){
            return (<div><div class = "rectangle"><img class = "left" src={museum} alt={"museum"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else if(x.nodeType == "shop"){
            return (<div><div class = "rectangle"><img class = "left" src={store} alt={"shop"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <p class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></p></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          } else {
            return (<div><div class = "rectangle"><img class = "left" src={park2} alt={"park"} style={{width: '100px'}}/>
            <br></br>
          <p class = "pad"><h2> {x.name}</h2>
          <div class = "description"> {x.location[1]}, {x.location[2]}
          <br></br> {x.rating} stars
          <br></br>
          <a href={"https://www.yelp.com/biz/" + x.id} target="_blank">Learn more</a></div></p>
        </div>
        <a href={"https://www.google.com/maps/dir/" +
        x.coordinates[0]+ "," + x.coordinates[1] + "/" + elements[i+1].coordinates[0] + "," + elements[i+1].coordinates[1]} target="_blank">
        <img class = "center" src={road} alt={"road icon"} style={{width: '100px'}} /></a>
        </div>)
          }})}


        </Col>

        <Col>

        <h1>{route_message}</h1>
        <div id="map" class = "rounded" style={{float: "center", width: 600, height: 400}}></div>
        <h1>{trip_message}</h1>
        <p>
        {summary_text}
        <a href={tripResourceLink} target="_blank">{resource_name}</a>
        <br></br>
        <h3>Origin</h3>
        <img src = {originIcon} style={{width: '100px'}}/>
        <p>
        <a href={originWebsite} target="_blank">{originName}</a>
        <br></br>
        Contact: {originPhone}
        <br></br>
        <a href={originMapURL} target="_blank">View in Google Maps</a>
        </p>


        <h3>Destination</h3>
        <img src = {destIcon} style={{width: '100px'}}/>
        <p>
        <a href={destWebsite} target="_blank">{destName}</a>
        <br></br>
        Contact: {destPhone}
        <br></br>
        <a href={destMapURL} target="_blank">View in Google Maps</a>
        </p>


        </p>
        <img src = {middlePhotoURL} style={{width: '250px'}}></img>

        </Col>
        </Row>
        </Container>
      </div>


  );

}

export default App;
