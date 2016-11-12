var connection = new WebSocket('ws://localhost:8080');

connection.onmessage = function(event){
  console.log("from server:" + event.data);
  var datum = JSON.parse(event.data);
  if (datum.message_type == 'map_update'){
    update_country(datum.country, datum.color, datum.count);
  } else if (datum.message_type == 'player_update'){
    updatePlayerStats(datum);
  } else if (datum.message_type == 'possible_order_types'){
    console.log('ORDER TYPES ' + datum.order_types);
    updateOrderConsole(datum.color, datum.order_types);
  } else {
    console.log("BAD MESSAGE ON WEB SOCKET: " + event.data);
  }
}

function sendMessage(msg){
    console.log("Sending to server " + msg);
    connection.send(msg);
}
