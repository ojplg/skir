var statuses = ['select-attack-country','select-defense-country'];
var current_status = null;

function get_current_status(){
    return current_status;
}

function update_player_stats(color, armies, countries){
    armiesSpan = document.getElementById(color + "-armies");
    armiesSpan.textContent = armies;
    countriesSpan = document.getElementById(color+ "-countries");
    countriesSpan.textContent = countries;
}

function update_order_console(color, choices){
    document.getElementById("active-player-field").textContent = "Active player is " + color;
    var order_console_div = document.getElementById("order-console");
    for(var idx=0; idx< choices.length; idx++){
        console.log("adding a choice");
        var choice = choices[idx];
        console.log(choice);
        var button = get_order_button(choice);
        button.visible = true;
    }
}

function get_order_button(order_type){
    return document.getElementById(order_type + "-Button");
}

function set_order_type_buttons_invisible(){
    var buttons = order_type_buttons();
    for(var idx=0; idx<buttons.length; idx++){
        var button = buttons[idx];
        button.visible = false;
    }
}

function order_type_buttons(){
    return [
        document.getElementById("Attack-Button"),
        document.getElementById("EndAttacks-Button"),
        document.getElementById("AttackUntilVictoryOrDeath-Button")
    ];
}

function order_type_selected(type){
    console.log("Selected: " + type);
}

function attack_selected(){
    console.log("Attack selected");
    set_order_type_buttons_invisible();
    var attackDiv = document.getElementById("attack-panel-div");
    attackDiv.visible = true;
    current_status = 'select-attack-country';
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
      '"all-out-flag":"' + all_out_flag + ''"}';

    sendMessage(msg);
}
