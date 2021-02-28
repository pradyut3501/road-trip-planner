# cs0320 Term Project 2021

**Team Members:** Sara (ssyed7), Abby (adicther), Erika (ebussman), Pradyut (psekhsar)

**Team Strengths and Weaknesses:** 
<br />Sara: 
<br />Strengths:(Likes to make things look pretty, Communication, Planning)
<br />  Weaknesses: (Doesn’t like to work in a consistent schedule (doesn’t like do a little bit at a time), Not great at github, Not good at estimating how long something will take, Not a big javascript fan )
<br />Abby:
 <br /> Strengths: (Will crunch to get it done, can work efficiently, Problem solving, Unique ideas)
 <br /> Weaknesses: (Only likes coding in the morning, doesn’t stay up late, Can have erratic working schedule sometimes, Gets frustrated with finicky syntax)
<br />Erika: 
 <br /> Strengths: (Backend logic/data structures, Refactoring, Debugging)
 <br /> Weaknesses: (Like to do work in bursts (trying to improve consistency), Front end/design, Edge cases/small details, Prioritizing work I don’t want to) do/unsure about
<br />Pradyut:
<br />  Strengths: (Algorithms, data structures, Planning, Testing, finding edge cases etc.)
<br />  Weaknesses: (Cannot code in the morning, Javascript/html stuff, Github)


**Project Idea(s):** _Fill this in with three unique ideas! (Due by March 1)_
### Idea 1: The Road Trip Planner
Overview: The Road Trip planner would aim to solve the problem of planning a roadtrip that balances driving with sightseeing. A customizable planner would allow a user to choose where they would like to start and end their roadtrip, how much time they want to spend on the road, and what kind of attractions they want to see (natural scenery, national parks, restaurants, museums etc). The planner would generate a scenic and entertaining route specific to what each user is looking for in an ideal road trip.

<br />Requirements/Features:
<br />**Constraints: specify the user’s travel budget (restaurants, accomodations, tolls, etc), allowed time for the trip, maximum/minimum number of stops, stops for gas/ charging stations**
<br /> Why:
<br /> -People have many different objectives/limitations when planning a road trip, and for the 
sake of usability/inclusivity it’s important to acknowledge these different criteria when planning a trip
<br />Challenges:
<br /> -Optimizing for multiple constraints and identifying user preferences when these 
constraints conflict (eg. is time constraint more important than exactly meeting the budget?)
<br /> -Estimating how long certain stops (such as restaurant visits etc.) might take
<br /> -Estimating the time between stops for gas/charging
<br /> -Estimating how different stops will affect overall budget
<br />**Interests: specify the user’s preferences between categories (eg. restaurants, museums, 
etc) and within categories (eg. Italian restaurants)**
<br />Why:
<br /> -How enjoyable a trip is depends a lot on whether the user is able to explore the things 
they’re interested in, and allowing for preference specification improves the chances that the user and their travel party will be able to draw on past experiences while experiencing new things
<br />Challenges:
<br /> -Finding data about local attractions and scenic routes
<br /> -Deciding how much to weight/prioritize each category
<br />**Visualization/trip summary:allows the user to visualize the suggested trip plan in 
multiple formats (eg. map form, list form with stops and details)**
<br />Why:
<br /> -when on a road trip, it’s helpful to visualize the overall trip from a bird’s eye view 
(eg. to see if there’s other stops not on the list that user may be interested in visiting) as well as in a more detailed, informational form (eg. list of stops, times, addresses) so the user can follow the route in a step-by-step manner and avoid getting lost on the way
<br />Challenges:
<br /> -Choosing which details to present on a map vs. which to specify in the trip 
overview/directions
<br /> -Semi-automating the process of visualization for the map (eg. icons for stops, 
categorization of stop types, etc.)

### Idea 2: Recipe Generator
<br />Overview:
<br />A user could input whatever ingredients they have in their fridge, and this program would generate meal ideas for the user! This would help users who are lacking recipe ideas and want inspiration for a meal that will help prevent their food from going to waste!

Requirements/Features:
<br />**Input panel for users to add / remove ingredients they have, and the quantity of ingredients**
<br />Why:
<br />Recipe generator will not be useful for user without the ability to specify the ingredients they currently have, and in what quantity; needing to buy additional ingredients/quantities defeats the purpose of the application
<br />Challenges:
<br /> -Choosing input types (eg. text box, selection tool)
<br /> -Parsing inputs/dealing with errant inputs and assigning to relevant category
<br /> -Dealing with appropriate units for each ingredient (would have to be automated or 
specified by category)
<br /> -Sorting/displaying food based on the amount of additional ingredients needed
<br />**Any allergies/dietary restrictions the user may have**
<br />Why:
<br />As a a user it would be great if the recipes were pre-filtered to only include options that we would want to make; potential users highlighted that allergen constraints are critical for safety and accessibility
<br />Challenges:
<br />Automating the process of assigning different food groups/inputs to different allergens
<br />**The type of meal they are looking for (breakfast, lunch, dinner, a snack, etc.) and portion size**
<br />Why:
<br />This would be good for pre-filtering recipes based on what we are looking for; recipe 
suggestions will be very different based on the context of the meal; portion size preferences may eliminate certain recipes based on user’s quantities of existing ingredients
<br />Challenges:
<br /> -Automating the assignment of recipes in recipe bank to types of meals (breakfast, lunch, 
etc)
<br /> -Converting between amounts/quantities in different units and overall portion sizes (eg. 
small/medium/large, # of people it serves, calories, etc.)
<br />**Cook Time/Prep time & level of difficulty of the suggested recipe**
<br />Why:
<br />Again, this would be a good filtering strategy for recipes, based on the user’s needs


### Idea 3: Vacation Planner
Overview: The Vacation planner would generate a destination and itinerary for users unsure of where to travel next. The vacation planner would allow users to input their time frame and budget as well as what they look for in vacations and previous destinations that they liked/disliked. Given these inputs, the vacation planner would recommend several locations as well a sample itinerary.

Requirements/Features:
<br />**Constraints: specify the user’s travel budget (restaurants, accomodations, flights, etc),** 
allowed time for the trip, previous destinations, interests, places the user is required to visit/ merging with an existing itinerary.
<br />Why:
<br />Everyone has different preferences for their vacations when it comes to type of destination, content of trip, and budget. It is also valuable to learn what previous destinations were liked/disliked when recommending a new trip.
<br />Challenges:
<br /> -Optimizing for multiple constraints and identifying user preferences when these 
constraints conflict
<br /> -Not overgeneralizing the similarities between destinations when using previous travel 
information to recommend a new trip.
<br /> -Fitting in places required by the user (a particular meeting/dinner they have to go to, 
a particular hotel they have to stay at etc.)
<br />**Interests: specify the user’s preferences between categories (cities, beach, mountains)**
<br />Why:
<br /> -People tend to have strong opinions about what type of vacations they like to take, and 
it is important that a planner recommends vacations of the right type
<br />Challenges:
<br /> -A certain type of vacation may not be in the users budget, or it may conflict with their 
time frame/other specifications. These conflicts will have to be managed creatively.
<br />**Visualization: allows the user to visualize the suggested trip plan in multiple formats (eg. map form, list form with stops and details)**
<br />Why:
<br /> -This is the aspect that will really help make the planner user friendly, and 
differentiate our project from other trip planners.
<br />Challenges:
<br /> -A lot of information may be displayed, so it could be a challenge to keep the interface 
simple and intuitive.
<br /> -Overall there are a lot of different design choices that can be made

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 15)_

**4-Way Checkpoint:** _(Schedule for on or before April 5)_

**Adversary Checkpoint:** _(Schedule for on or before April 12 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
