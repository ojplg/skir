// This object represents the div that is used
// in the order console when a player is in the
// place army phase.
// It knows how to respond to country clicks.
function PlaceArmy(constraint, connection){
    this.placementConstraint = constraint;
    this.connection = connection;

    this.initialize = function(){
        console.log("initializing place army selector")
        var placementDiv = document.createElement("div");
        this.selector = document.createElement("SELECT");
        var textNode = document.createTextNode("Choose number of armies to place ");
        placementDiv.appendChild(textNode);
        for(var idx=this.placementConstraint.maximum_armies; idx>=1; idx--){
            var option = new Option(idx,idx);
            this.selector.add(option);
        }
        placementDiv.appendChild(this.selector);
        // TODO: This should be a function on an object.
        addToOrderConsole(placementDiv);
        console.log("place army selector initialized");
    }

    this.countryClicked = function(country){
        console.log("place army div country clicked " + country);
        var countries = this.placementConstraint.possible_countries;
        if( countries.indexOf(country.wire_name) >= 0 ){
            // TODO: This should be a function on an object.
            var order = newOrder("PlaceArmy");
            order.country = country.wire_name;
            order.number_armies = this.selector.value;
            var jsonOrder = JSON.stringify(order);
            this.connection.sendMessage(jsonOrder);
            // TODO: This should not be needed
            currentStatus = null;
            return false;
        } else {
            console.log("Cannot place an army in " + country.wire_name);
            return true;
        }
    }
}
