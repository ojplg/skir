
const PLAYER_STATS = ['name', 'armies','countries','continents','card_count',
                         'expected_armies',
                         'attack_luck_factor','armies_lost_attacking','armies_killed_attacking',
                         'defense_luck_factor', 'armies_lost_defending', 'armies_killed_defending'];


function PlayerStatuses() {
    var colors = ["black","white","blue","red","pink","green"];
    for(var idx=0; idx<colors.length; idx++){
        var color = colors[idx];
        this[color] = new PlayerStatus(color);
    }

    this.update = function(playerStatus){
        this[playerStatus.color].updateTable(playerStatus);
    }
}

function PlayerStatus(playerColor){
    this.color = playerColor;
    var tableName = "player-status-" + this.color + "-table";
    this.table = document.getElementById(tableName);
    var nameRow = this.table.insertRow(0);
    var nameCell = nameRow.insertCell(0);
    nameCell.id = this.color + ".name";

    for(var idx = 1; idx< PLAYER_STATS.length; idx++ ){
        var row = this.table.insertRow(idx);
        var nameCell = row.insertCell(0);
        var caption = PLAYER_STATS[idx];
        nameCell.innerHTML = caption;
        var valueCell = row.insertCell(1);
        valueCell.id = this.color + "." + caption;
    }

    this.updateTable = function(playerStatus){
        var items;
        for(var idx=0; idx<PLAYER_STATS.length; idx++){
            var stat = PLAYER_STATS[idx];
            var cellId = this.color + "." + stat;
            var valueCell = document.getElementById(cellId);
            valueCell.innerHTML = playerStatus[stat];
        }
        if( isMyColor(playerStatus.color) ){
            var cards = playerStatus.cards;
            this.updateCards(playerStatus.color, cards);
        }

        var armyCount = playerStatus['armies'];
    }

    this.updateCards = function(color, cards){
        var txt = "Cards:<br/>";
        for( var idx=1; idx<=cards.length ; idx++ ){
            var card = cards[idx-1];
            txt = txt.concat(idx);
            txt = txt.concat(": ");
            if ( card.joker ) {
                txt = txt.concat("Joker");
            } else {
                txt = txt.concat(card.country);
                txt = txt.concat("-");
                txt = txt.concat(card.symbol);
            }
            txt = txt.concat("<br/>");
        }
        var cardsSpan = document.getElementById("player-cards-" + color + "-span");
        cardsSpan.innerHTML = txt;
    }
}
