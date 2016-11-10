var placeArmyStatusFlag = 'place-army-status-flag';
var selectAttackCountryStatusFlag = 'select-attack-country-status-flag';
var selectDefenseCountryStatusFlag = 'select-defense-country-status-flag';

var currentStatus = null;
var attackCountry;
var defenseCountry;

function updatePlayerStats(color, armies, countries, cardCount){
    console.log("Updating player " + color);
    var armiesSpan = document.getElementById(color + "-armies");
    armiesSpan.textContent = armies;
    var countriesSpan = document.getElementById(color+ "-countries");
    countriesSpan.textContent = countries;
    var cardSpan = document.getElementById(color + "-cardCount");
    cardSpan.textContent = cardCount;
}

function updateOrderConsole(color, choices){
    console.log("Choices for " + color + " are " + choices);
    document.getElementById("active-player-field").textContent = "Active player is " + color;
    if( choices.length == 1 ){
        buttonClicked(choices[0]);
    } else {
        clearOrderConsole();
        for(var idx=0; idx< choices.length; idx++){
            addButton(choices[idx]);
        }
    }
}

function clearOrderConsole(){
    console.log("Clearing order console");
    var orderConsoleDiv = document.getElementById("order-console-div");
    var children = orderConsoleDiv.childNodes;
    for(var idx=0; idx<children.length; idx++){
        var element = children[idx];
        if(element.id != "order-console-end-div"){
            console.log("removing " + element.id);
            orderConsoleDiv.removeChild(element);
        }
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
    if( orderType == "PlaceArmy"){
        placeArmySelected();
    } else if( orderType == "Attack"){
        attackSelected();
    } else if (orderType == "EndAttacks"){
        sendEndAttacksMessage();
    } else if (orderType == "AttackUntilVictoryOrDeath"){
    } else if (orderType == "Occupy") {
        occupySelected();
    } else if (orderType == "DoOccupation") {
        sendDoOccupationMessage();
    } else if (orderType == "DrawCard" ) {
        sendDrawCardMessage();
    } else if (orderTYpe = "ClaimArmies") {
        sendClaimArmiesMessage();
    }else {
        console.log("Selection unknown " + orderType);
    }
}

function occupySelected(){
    console.log("Occupy selected");
    clearOrderConsole();
    var orderConsoleDiv = document.getElementById("order-console-div");
    addButton("DoOccupation");
}

function addToOrderConsole(element){
    var orderConsoleDiv = document.getElementById("order-console-div");
    var orderConsoleEndDiv = document.getElementById("order-console-end-div");
    orderConsoleDiv.insertBefore(element, orderConsoleEndDiv);
}

function placeArmySelected(){
    console.log("placeArmySelected");
    clearOrderConsole();
    var placementDiv = document.createElement("div");
    placementDiv.id = "place-army-text-div";
    var placementContent = document.createTextNode("Click a country to place an army");
    placementDiv.appendChild(placementContent);
    addToOrderConsole(placementDiv);
    currentStatus = placeArmyStatusFlag;
    console.log("placeArmySelected - done");
}

function doStatusDependentCountryClickedWork(country){
    if( currentStatus == placeArmyStatusFlag){
        placeArmyCountryClick(country);
    } else if (currentStatus == selectAttackCountryStatusFlag){
        attackCountryClick(country);
    } else if (currentStatus == selectDefenseCountryStatusFlag){
        defenseCountryClick(country);
    }
}

function attackCountryClick(country){
    console.log("attackCountryClick " + country);
    currentStatus = selectDefenseCountryStatusFlag;
    attackCountry = country;
    var attackCountryTextDiv = document.getElementById("attacker-text-div");
    var newHtml = "Attack From: " + country.wire_name();
    attackCountryTextDiv.innerHTML = newHtml;
    console.log("Reset innerHTML to " + newHtml);
}

function defenseCountryClick(country){
    console.log("defenseCountryClick " + country);
    currentStatus = null;
    var defenderTextDiv = document.getElementById("defender-text-div");
    var newHtml = "Defender: " + country.wire_name();
    defenderTextDiv.innerHTML = newHtml;
    console.log("Reset innerHTML to " + newHtml);
    var order = newOrder("Attack");
    order.attacker = attackCountry.wire_name();
    order.defender = country.wire_name();
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
}

function sendDoOccupationMessage(){
    var order = newOrder("DoOccupation");
    var jsonOrder = JSON.stringify(order);
    sendMessage(jsonOrder);
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

function placeArmyCountryClick(country){
    currentStatus = null;
    var order = newOrder("PlaceArmy");
    order.country = country.wire_name();
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

function get_order_button(order_type){
    return document.getElementById(order_type + "-Button");
}

function attackSelected(){
    console.log("Attack selected");
    clearOrderConsole();

    currentStatus = selectAttackCountryStatusFlag;
    var attackDiv = document.createElement("div");
    attackDiv.id = "attack-div";

    // invader text console
    var attackFromDiv = document.createElement("div");
    attackFromDiv.id = "attacker-text-div";
    var attackContent = document.createTextNode("Attack from: ");
    attackFromDiv.appendChild(attackContent);
    attackDiv.appendChild(attackFromDiv);

    // defender text console
    var defendFromDiv = document.createElement("div");
    defendFromDiv.id = "defender-text-div";
    var defendContent = document.createTextNode("Invade into: ");
    defendFromDiv.appendChild(defendContent);
    attackDiv.appendChild(defendFromDiv);

    var orderConsoleDiv = document.getElementById("order-console-div");
    var orderConsoleEndDiv = document.getElementById("order-console-end-div");
    orderConsoleDiv.insertBefore(attackDiv, orderConsoleEndDiv);
}

function set_attack_country(country_name){
    console.log("attack country selected " + country_name);
    var attackCountrySpan = document.getElementById("attack-country-span");
    attackCountrySpan.textContent = country_name;
    currentStatus = 'select-defense-country';
}

function set_defense_country(country_name){
    console.log("defense country selected " + country_name);
    var defenseCountrySpan = document.getElementById("defense-country-span");
    defenseCountrySpan.textContent = country_name;
    currentStatus = null;
}

function doAttack(all_out_flag){
    console.log("Doing attack " + all_out_flag);
    var attackCountrySpan = document.getElementById("attack-country-span");
    var attackCountry = attackCountrySpan.textContent;
    var defenseCountrySpan = document.getElementById("defense-country-span");
    var defenseCountry = defenseCountrySpan.textContent;

    var msg = '{"message-type":"attack-command",' +
      '"client-color":"' + color + '",' +
      '"attack-country":"' + attackCountry + '",' +
      '"defense-country":"' + defenseCountry + '",' +
      '"all-out-flag":"' + all_out_flag + '"}';

    sendMessage(msg);
}
