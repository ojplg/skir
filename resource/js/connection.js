var connection = new WebSocket('ws://localhost:8080');

connection.onmessage = function(event){
  console.log("from server:" + event.data);
  var datum = JSON.parse(event.data);
  if (datum.message_type == 'map_update'){
    update_country(datum.country, datum.color, datum.count);
  } else if (datum.message_type = 'player_update'){
    update_player_stats(datum.color, datum.armies, datum.countries);
  }
}