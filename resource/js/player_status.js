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
    console.log("Table name is " + tableName);
    this.table = document.getElementById(tableName);
    console.log("This table is " + this.table);

    this.updateTable = function(playerStatus){
        // TODO: this utility function should have a home
        clearElementChildren(this.table);
        var items = ['name', 'armies','countries','continents','card_count',
                     'expected_armies', 'attack_luck_factor', 'defense_luck_factor'];
        for(var idx=0; idx<items.length; idx++){
            var item = items[idx];
            console.log("adding item " + item + " at index " + idx);
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
            console.log("found card " + card);
            txt = txt.concat(idx);
            txt = txt.concat(": ");
            txt = txt.concat(card.country);
            txt = txt.concat("-");
            txt = txt.concat(card.symbol);
            txt = txt.concat("<br/>");
        }
        var cardsSpan = document.getElementById("player-cards-" + color + "-span");
        cardsSpan.innerHTML = txt;
    }
}
