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

    this.updateTable = function(playerStatus){
        // TODO: this utility function should have a home
        clearElementChildren(this.table);
        var items;
        if ( playerStatus['countries'] > 0){
            items = ['name', 'armies','countries','continents','card_count',
                         'expected_armies',
                         'attack_luck_factor','armies_lost_attacking','armies_killed_attacking',
                         'defense_luck_factor', 'armies_lost_defending', 'armies_killed_defending'];
        } else {
            items = ['name',
                          'attack_luck_factor','armies_lost_attacking','armies_killed_attacking',
                          'defense_luck_factor', 'armies_lost_defending', 'armies_killed_defending'];
        }
        for(var idx=0; idx<items.length; idx++){
            var item = items[idx];
            var row = this.table.insertRow(idx);
            var nameCell = row.insertCell(0);
            nameCell.innerHTML = item;
            var valueCell = row.insertCell(1);
            valueCell.innerHTML = playerStatus[item];
        }
        if( isMyColor(playerStatus.color) ){
            var cards = playerStatus.cards;
            this.updateCards(playerStatus.color, cards);
        }
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
