var connection;
var queuedUpdates = {}

function openWebSocketConnection(name, address, uniqueKey, demoFlag, gameId, joinAttempt){
    connection = new WebSocket(web_socket_protocol + "://" + window.location.host + "/sockets/");
    connection.onmessage = function(event){
        //console.log("message from server: " + event.data);
        var datum = JSON.parse(event.data);
        if (datum.message_type == 'map_update'){
          queueMapUpdate(datum);
          //update_country(datum.country, datum.color, datum.count);
        } else if (datum.message_type == 'player_update'){
          updatePlayerStats(datum);
        } else if (datum.message_type == 'possible_order_types'){
          updateOrderConsole(datum.color, datum.turn_number, datum.order_types);
        } else if (datum.message_type == 'game_joined'){
          updatePlayerInfoAfterGameJoined(datum);
        } else if (datum.message_type == 'game_event') {
          queueGameEvent(datum);
        } else {
          console.log("BAD MESSAGE ON WEB SOCKET: " + event.data);
        }
    };
    queuedUpdates.mapUpdates = {};
    queuedUpdates.gameEvents = [];
    // need this to happen after connection created
    connection.onopen = function(event) {
        sendJoinMessage(name, address, uniqueKey, demoFlag, gameId, joinAttempt);
        var heartbeater = new Heartbeater();
        heartbeater.startHeartbeats();
    };
}

function processUpdates(){
  for (var property in queuedUpdates.mapUpdates) {
    if (queuedUpdates.mapUpdates.hasOwnProperty(property)) {
        var countryUpdate = queuedUpdates.mapUpdates[property];
        update_country(countryUpdate.country, countryUpdate.color, countryUpdate.count);
        delete queuedUpdates.mapUpdates[property];
    }
  }
  displayGameEvents(queuedUpdates.gameEvents);
  queuedUpdates.gameEvents = [];
}

function queueMapUpdate(updateObject){
    queuedUpdates.mapUpdates[updateObject.country] = updateObject;
}

function queueGameEvent(gameEvent){
  queuedUpdates.gameEvents.push(gameEvent);
}

function sendJoinMessage(name, address, uniqueKey, demoFlag, gameId, joinAttempt){
    var obj = {};
    obj.messageType = "ClientJoined";
    obj.uniqueKey = "" + uniqueKey;
    obj.displayName = name;
    obj.address = address;
    obj.demo = demoFlag;
    obj.gameId = gameId;
    obj.joinAttempt = joinAttempt;
    var msg = JSON.stringify(obj);
    sendMessage(msg);
}

function sendMessage(msg){
    //console.log("Sending to server " + msg);
    connection.send(msg);
}
