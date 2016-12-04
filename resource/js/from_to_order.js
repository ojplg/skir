function FromToOrder(orderTypeFlag, restrictions){
    this.self = this;
    self.orderType = orderTypeFlag;
    self.orderRestrictions = restrictions;
    self.fromCountry = null;
    self.toCountry = null;

    this.initialize = function(orderConsoleDiv){
        console.log("Initializing the FromToOrder");
        var fromToDiv = document.createElement("div");
        fromToDiv.id = "from-to-div";

        // from text console
        self.fromDiv = document.createElement("div");
        var fromContent = document.createTextNode("From: ");
        fromDiv.appendChild(fromContent);
        fromToDiv.appendChild(fromDiv);

        // to text console
        self.toDiv = document.createElement("div");
        var toContent = document.createTextNode("To: ");
        toDiv.appendChild(toContent);
        fromToDiv.appendChild(toDiv);

        orderConsoleDiv.appendChild(fromToDiv);
    }

    this.countryClicked = function(country){
        console.log("FromToOrder.countryClicked " + country);
        if( self.fromCountry == null ){
            return this.fromCountryClicked(country);
        } else if (self.toCountry == null){
            return this.toCountryClicked(country);
        }
    }

    this.toCountryClicked = function(country){
        console.log("toCountryClick " + country);
        var countryMap = self.orderRestrictions["destinations"];
        console.log("Country map is " + countryMap);
        var possibleToList = countryMap[self.fromCountry.wire_name()];
        console.log("Possible list " + possibleToList);

        if( possibleToList.indexOf(country.wire_name()) >= 0){
            var newHtml = "To: " + country.wire_name();
            self.toDiv.innerHTML = newHtml;

            var order = newOrder(orderType);
            order.from = fromCountry.wire_name();
            order.to = country.wire_name();
            order.army_count = document.getElementById("army-count-select").value;
            var jsonOrder = JSON.stringify(order);
            sendMessage(jsonOrder);
        } else {
            console.log("Invalid to selection " + country.wire_name());
        }
        return false;
    }

    this.fromCountryClicked = function(country){
        console.log("fromCountryClick " + country);
        var countryMap = self.orderRestrictions["destinations"];
        if( Object.keys(countryMap).indexOf(country.wire_name()) >= 0){

            fromCountry = country;
            var newHtml = "From: " + country.wire_name();
            self.fromDiv.innerHTML = newHtml;

            // possible counts select
            console.log("currentChoices " + currentChoices);
            var counts = self.orderRestrictions["counts"];
            console.log("counts " + counts);
            var maxToMove = counts[country.wire_name()];
            console.log("army count " + maxToMove);
            // hmmm, what to do about this?
            addNumericSelect("army-count-select", 1 , maxToMove);
        } else {
            console.log("Invalid from selection " + country.wire_name());
        }
        console.log("Returning true!");
        return true;
    }
}