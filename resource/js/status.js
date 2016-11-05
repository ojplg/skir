var statuses = ['select-attack-country','select-defense-country'];
var current_status = null;

function get_current_status(){
    return current_status;
}

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
        var orderConsoleDiv = document.getElementById("order-console");
        for(var idx=0; idx< choices.length; idx++){
            addButton(orderConsoleDiv, choices[idx]);
        }
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
    } else {
        console.log("Selection unknown " + orderType);
    }
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
    var attackDiv = document.createElement("div");
    var attackFromDiv = document.createElement("div");
    attackFromDiv.id = "attack-from-div";
    var attackContent = document.createTextNode("Attack from: ");

    attackFromDiv.appendChild(attackContent);

    attackDiv.appendChild(attackFromDiv);

    var playetStatusDiv = document.getElementById("player-status-div");
    document.body.insertBefore(attackDiv, playetStatusDiv);
}

function set_attack_country(country_name){
    console.log("attack country selected " + country_name);
    var attackCountrySpan = document.getElementById("attack-country-span");
    attackCountrySpan.textContent = country_name;
    current_status = 'select-defense-country';
}

function set_defense_country(country_name){
    console.log("defense country selected " + country_name);
    var defenseCountrySpan = document.getElementById("defense-country-span");
    defenseCountrySpan.textContent = country_name;
    current_status = null;
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
