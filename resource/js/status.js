var placeArmyStatusFlag = 'place-army-status-flag';
var selectFromCountryStatusFlag = 'select-from-country-status-flag';
var selectToCountryStatusFlag = 'select-to-country-status-flag';

var currentStatus = null;
var orderType;
var fromCountry;
var toCountry;
var currentChoices;
var myIdentity = {};
var countryClickResponder = null;
var playerStatuses;

function displayOrderEvent(orderEvent){
    var orderEventDiv = document.getElementById('order-event-div');
    orderEventDiv.innerHTML = orderEvent.simple_text;
}

function isMyColor(color){
    return color.toUpperCase() == myIdentity.color.toUpperCase();
}

function initializeClient(name, address){
    draw_map();
    myIdentity.uniqueKey = Math.floor(Math.random() * 1000000);
    openWebSocketConnection(myIdentity.uniqueKey);
    myIdentity.name = name;
    myIdentity.address = address;
    myIdentity.color = "";
    playerStatuses = new PlayerStatuses();
}

function updatePlayerInfoAfterGameJoined(joinedObject){
    var color = joinedObject.color;
    var clientKey = joinedObject.client_key;
    console.log("Client with " + clientKey + " has color " + color);
    console.log("My client key is " + myIdentity.uniqueKey);
    if( clientKey == myIdentity.uniqueKey ){
        console.log("I am " + clientKey + " and my color is " + color);
        myIdentity.color = color;
    }
    if( joinedObject.first_player ){
        console.log("First player to join!")
        addButton("start");
    }
}

function updatePlayerStats(playerStatus){
    playerStatuses.update(playerStatus);
}

function updateOrderConsole(color, choicesObject){
    currentChoices = choicesObject;
    console.log("Choices for " + color + " are " + choicesObject);
    document.getElementById("active-player-field").textContent = "Active player is " + color;
    clearOrderConsole();
    if (isMyColor(color)) {
        var choices = [];
        for (var key in choicesObject) {
            console.log("Adding key " + key);
            choices.push(key);
        }
        console.log("Have " + choices.length + " choices");
        if (choices.length == 1) {
            buttonClicked(choices[0]);
        } else {
            for (var key in choicesObject) {
                addButton(key);
            }
        }
    }
}

function clearOrderConsole(){
    console.log("Clearing order console");
    var orderConsoleDiv = document.getElementById("order-console-div");
    var children = orderConsoleDiv.childNodes;
    var limit = children.length;
    console.log("Order console has " + children.length + " nodes");
    var removeList = [];
    for(var idx=0; idx<limit; idx++){
        var element = children[idx];
        console.log(idx + " Studying element " + element);
        if( element == null){
            console.log(idx + " was null!!?")
        } else {
            console.log("adding to remove list " + element.id);
            removeList.push(element);
        }
    }
    for(var idx=0; idx<removeList.length; idx++ ){
        var element = removeList[idx];
        orderConsoleDiv.removeChild(element);
        console.log("removed " + element + " with id " + element.id);
    }
}

function clearElementChildren(element){
    var children = element.childNodes;
    for(var idx=0; idx<children.length; idx++){
        element.removeChild(children[idx]);
    }
}

function buttonClicked(orderType){
    console.log("Selected " + orderType);
    if( orderType == "start" ) {
        startGame();
    } else if( orderType == "PlaceArmy"){
        placeArmySelected();
    } else if( orderType == "Attack"){
        fromToOrderSelected("Attack");
    } else if (orderType == "EndAttacks"){
        sendEndAttacksMessage();
    } else if (orderType == "AttackUntilVictoryOrDeath"){
        fromToOrderSelected("AttackUntilVictoryOrDeath");
    } else if (orderType == "Occupy") {
        occupySelected();
    } else if (orderType == "DrawCard" ) {
        sendDrawCardMessage();
    } else if (orderType == "ClaimArmies") {
        sendClaimArmiesMessage();
    } else if (orderType == "Fortify") {
        fromToOrderSelected("Fortify");
    } else if (orderType == "EndTurn") {
        endTurnSelected();
    } else if (orderType == "ExchangeCardSet" ) {
        sendExchangeCardSetMessage();
    } else {
        console.log("Selection unknown " + orderType);
    }
}

function startGame(){
    var msg = {};
    msg.messageType = "StartGame";
    var s = JSON.stringify(msg);
    sendMessage(s);
}

function sendExchangeCardSetMessage(){
    var order = newOrder("ExchangeCardSet");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function endTurnSelected(){
    var order = newOrder("EndTurn");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function occupySelected(){
    console.log("Occupy selected");
    clearOrderConsole();
    console.log("Current choices " + currentChoices);
    var occupation = new Occupation(currentChoices.Occupy);
    occupation.showOccupationControls(document.getElementById("order-console-div"));
}

function addNumericSelect(id,min,max){
    var selector = document.createElement("SELECT");
    selector.id = id;
    for(var idx=max; idx>=min; idx--){
        var option = new Option(idx,idx);
        selector.add(option);
    }
    var orderConsoleDiv = document.getElementById("order-console-div");
    orderConsoleDiv.appendChild(selector);
}

function addToOrderConsole(element){
    var orderConsoleDiv = document.getElementById("order-console-div");
    orderConsoleDiv.appendChild(element);
}

function placeArmySelected(){
    console.log("placeArmySelected");
    clearOrderConsole();

    countryClickResponder = new PlaceArmy(currentChoices.PlaceArmy);
    countryClickResponder.initialize();

    console.log("placeArmySelected - done");
}

function doStatusDependentCountryClickedWork(country){
    if (currentStatus == selectFromCountryStatusFlag){
        fromCountryClick(country);
    } else if (currentStatus == selectToCountryStatusFlag){
        toCountryClick(country);
    }

    if( countryClickResponder != null){
        var keepAlive = countryClickResponder.countryClicked(country);
        if( ! keepAlive ){
            countryClickResponder = null;
        }
    }
}

function fromCountryClick(country){
    console.log("fromCountryClick " + country);
    var orderSpecificRestrictions = currentChoices[orderType];
    console.log("orderSpecificRestrictions " + orderSpecificRestrictions);
    var countryMap = orderSpecificRestrictions["destinations"];
    console.log("Country map is " + countryMap);
    if( Object.keys(countryMap).indexOf(country.wire_name()) >= 0){

        currentStatus = selectToCountryStatusFlag;
        fromCountry = country;
        var fromCountryTextDiv = document.getElementById("from-text-div");
        var newHtml = "From: " + country.wire_name();
        fromCountryTextDiv.innerHTML = newHtml;

        // possible counts select
        console.log("currentChoices " + currentChoices);
        var counts = orderSpecificRestrictions["counts"];
        console.log("counts " + counts);
        var maxToMove = counts[country.wire_name()];
        console.log("army count " + maxToMove);
        addNumericSelect("army-count-select", 1 , maxToMove);
    } else {
        console.log("Invalid from selection " + country.wire_name());
    }
}

function toCountryClick(country){
    console.log("toCountryClick " + country);
    var orderSpecificRestrictions = currentChoices[orderType];
    console.log("orderSpecificRestrictions " + orderSpecificRestrictions);
    var countryMap = orderSpecificRestrictions["destinations"];
    console.log("Country map is " + countryMap);
    var possibleToList = countryMap[fromCountry.wire_name()];
    console.log("Possible list " + possibleToList);

    if( possibleToList.indexOf(country.wire_name()) >= 0){
        currentStatus = null;
        var toTextDiv = document.getElementById("to-text-div");
        var newHtml = "To: " + country.wire_name();
        toTextDiv.innerHTML = newHtml;

        var order = newOrder(orderType);
        order.from = fromCountry.wire_name();
        order.to = country.wire_name();
        order.army_count = document.getElementById("army-count-select").value;
        var jsonOrder = JSON.stringify(order);
        sendMessage(jsonOrder);
    } else {
        console.log("Invalid to selection " + country.wire_name());
    }
}

function sendEndAttacksMessage(){
    var order = newOrder("EndAttacks");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function sendDrawCardMessage(){
    var order = newOrder("DrawCard");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function sendClaimArmiesMessage(){
    var order = newOrder("ClaimArmies");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function newOrder(orderType){
    var order = {};
    order.messageType = "Order";
    order.orderType = orderType;
    return order;
}

function addButton(label){
    console.log("Adding button for " + label);
    var button = document.createElement("BUTTON");
    var text = document.createTextNode(label);
    button.id = "Button." + label;
    button.onclick = function(){
        buttonClicked(label);
    };
    button.appendChild(text);
    var orderConsoleDiv = document.getElementById("order-console-div");
    orderConsoleDiv.appendChild(button);
}

function fromToOrderSelected(orderTypeFlag){
    console.log("From-to order selected: " + orderTypeFlag);
    orderType = orderTypeFlag;
    currentStatus = selectFromCountryStatusFlag;

    clearOrderConsole();

    var fromToDiv = document.createElement("div");
    fromToDiv.id = "from-to-div";

    // from text console
    var fromDiv = document.createElement("div");
    fromDiv.id = "from-text-div";
    var fromContent = document.createTextNode("From: ");
    fromDiv.appendChild(fromContent);
    fromToDiv.appendChild(fromDiv);

    // to text console
    var toDiv = document.createElement("div");
    toDiv.id = "to-text-div";
    var toContent = document.createTextNode("To: ");
    toDiv.appendChild(toContent);
    fromToDiv.appendChild(toDiv);

    var orderConsoleDiv = document.getElementById("order-console-div");
    orderConsoleDiv.appendChild(fromToDiv);
}
