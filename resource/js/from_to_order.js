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
        var countryWireName = country.wire_name();
        console.log("toCountryClick on" + countryWireName);
        var countryMap = self.orderRestrictions["destinations"];
        console.log("Country map is " + countryMap);
        var possibleToList = countryMap[self.fromCountry.wire_name()];
        console.log("Possible list " + possibleToList);

        if( possibleToList.indexOf(countryWireName) >= 0){
            var newHtml = "To: " + countryWireName;
            self.toDiv.innerHTML = newHtml;

            var order = newOrder(orderType);
            order.from = fromCountry.wire_name();
            order.to = countryWireName;
            order.army_count = document.getElementById("army-count-select").value;
            var jsonOrder = JSON.stringify(order);
            sendMessage(jsonOrder);
            return false;
        } else {
            console.log("Invalid to selection " + countryWireName + " returning true");
            return true;
        }
    }

    this.fromCountryClicked = function(country){
        var countryWireName = country.wire_name();
        console.log("fromCountryClick " + countryWireName);
        var countryMap = self.orderRestrictions["destinations"];
        if( Object.keys(countryMap).indexOf(countryWireName) >= 0){

            fromCountry = country;
            var newHtml = "From: " + countryWireName;
            self.fromDiv.innerHTML = newHtml;

            // possible counts select
            console.log("currentChoices " + currentChoices);
            var counts = self.orderRestrictions["counts"];
            console.log("counts " + counts);
            var maxToMove = counts[countryWireName];
            console.log("army count " + maxToMove);
            // hmmm, what to do about this?
            addNumericSelect("army-count-select", 1 , maxToMove);
        } else {
            console.log("Invalid from selection " + countryWireName);
        }
        console.log("Returning true!");
        return true;
    }
}