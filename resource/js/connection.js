var connection;

function openWebSocketConnection(uniqueKey){
    connection = new WebSocket("ws://" + window.location.host + "/sockets/");
    connection.onmessage = function(event){
        console.log("message from server: " + event.data);
        var datum = JSON.parse(event.data);
        if (datum.message_type == 'map_update'){
          update_country(datum.country, datum.color, datum.count);
        } else if (datum.message_type == 'player_update'){
          updatePlayerStats(datum);
        } else if (datum.message_type == 'possible_order_types'){
          updateOrderConsole(datum.color, datum.order_types);
        } else if (datum.message_type == 'game_joined'){
          updatePlayerInfoAfterGameJoined(datum);
        } else {
          console.log("BAD MESSAGE ON WEB SOCKET: " + event.data);
        }
    };
    // need this to happen after connection created
    connection.onopen = function(event) { sendJoinMessage(uniqueKey); };
}

function sendJoinMessage(uniqueKey){
    var obj = {};
    obj.messageType = "ClientJoined";
    obj.uniqueKey = "" + uniqueKey;
    var msg = JSON.stringify(obj);
    sendMessage(msg);
}

function sendMessage(msg){
    console.log("Sending to server " + msg);
    connection.send(msg);
}
