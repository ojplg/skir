function update_player_stats(color, armies, countries){
    armiesSpan = document.getElementById(color + "-armies");
    armiesSpan.textContent = armies;
    countriesSpan = document.getElementById(color+ "-countries");
    countriesSpan.textContent = countries;
}