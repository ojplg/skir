var placeArmyStatusFlag = 'place-army-status-flag';
var selectAttackCountryStatusFlag = 'select-attack-country-status-flag';
var selectDefenseCountryStatusFlag = 'select-defense-country-status-flag';

var currentStatus = null;
var attackCountry;
var defenseCountry;

function update_player_stats(color, armies, countries){
    console.log("Updating player " + color);
    armiesSpan = document.getElementById(color + "-armies");
    armiesSpan.textContent = armies;
    countriesSpan = document.getElementById(color+ "-countries");
    countriesSpan.textContent = countries;
}

function update_order_console(color, choices){
    document.getElementById("active-player-field").textContent = "Active player is " + color;
    if( choices.length == 1 ){
        orderTypeSelected(choices[0]);
    } else {
        clearOrderConsole();
        var orderButtonsDiv = document.getElementById("order-buttons-div");
        for(var idx=0; idx< choices.length; idx++){
            addButton(orderButtonsDiv, choices[idx]);
        }
    }
}

function clearOrderConsole(){
    var orderConsoleDiv = document.getElementById("order-buttons-div");
    clearElementChildren(orderConsoleDiv);
}

function clearElementChildren(element){
    var children = element.childNodes;
    for(var idx=0; idx<children.length; idx++){
        element.removeChild(children[idx]);
    }
}

function orderTypeSelected(orderType){
    console.log("Selected " + orderType);
    if( orderType == "PlaceArmy"){
        placeArmySelected();
    } else if( orderType == "Attack"){
        attackSelected();
    } else if (orderType == "EndAttacks"){
    } else if (orderType == "AttackUntilVictoryOrDeath"){
    } else if (orderType == "Occupy") {
        occupySelected();
    } else {
        console.log("Selection unknown " + orderType);
    }
}

function occupySelected(){
    console.log("Occupy selected");
    removeAttackDiv();
    var orderConsoleDiv = document.getElementById("order-console-div");
    addButton(orderConsoleDiv, "Occupy");
}

function placeArmySelected(){
    console.log("placeArmySelected");
    var placementDiv = document.createElement("div");
    currentStatus = placeArmyStatusFlag;
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
    currentStatus = selectDefenseCountryStatusFlag;
    attackCountry = country;
}

function defenseCountryClick(country){
    currentStatus = null;
    var order = newOrder("Attack");
    order.attacker = attackCountry.wire_name();
    order.defender = country.wire_name();
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

function addButton(element, label){
    var button = document.createElement("BUTTON");
    var text = document.createTextNode(label);
    button.onclick = function(){
        orderTypeSelected(label);
    };
    button.appendChild(text);
    element.appendChild(button);
}

function get_order_button(order_type){
    return document.getElementById(order_type + "-Button");
}

function attackSelected(){
    console.log("Attack selected");
    removeAttackDiv();

    currentStatus = selectAttackCountryStatusFlag;
    var attackDiv = document.createElement("div");
    attackDiv.id = "attack-div";

    // invader text console
    var attackFromDiv = document.createElement("div");
    attackFromDiv.id = "attack-from-div";
    var attackContent = document.createTextNode("Attack from: ");
    attackFromDiv.appendChild(attackContent);
    attackDiv.appendChild(attackFromDiv);

    // defender text console
    var defendFromDiv = document.createElement("div");
    defendFromDiv.id = "defend-from-div";
    var defendContent = document.createTextNode("Invade into: ");
    defendFromDiv.appendChild(defendContent);
    attackDiv.appendChild(defendFromDiv);

    var orderConsoleDiv = document.getElementById("order-console-div");
    var orderConsoleEndDiv = document.getElementById("order-console-end-div");
    orderConsoleDiv.insertBefore(attackDiv, orderConsoleEndDiv);
}

function removeAttackDiv(){
    var attackDiv = document.getElementById("attack-div");
    if( attackDiv != null ){
        clearElementChildren(attackDiv);
        var orderConsoleDiv = document.getElementById("order-console-div");
        orderConsoleDiv.removeChild(attackDiv);
    }
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
