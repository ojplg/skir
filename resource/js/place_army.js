function PlaceArmy(constraint){
    this.placementConstraint = constraint;

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

    this.countryClicked = function placeArmyCountryClick(country){
        console.log("place army div country clicked " + country);
        var countries = this.placementConstraint.possible_countries;
        if( countries.indexOf(country.wire_name()) >= 0 ){
            // TODO: This should be a function on an object.
            var order = newOrder("PlaceArmy");
            order.country = country.wire_name();
            order.number_armies = this.selector.value;
            var jsonOrder = JSON.stringify(order);
            // TODO: This should be a function on an object.
            sendMessage(jsonOrder);
            // TODO: This should not be needed
            currentStatus = null;
        } else {
            console.log("Cannot place an army in " + country.wire_name());
        }
    }
}
