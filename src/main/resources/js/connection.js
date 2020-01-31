function Connection(){
    let self = this;

    this.socket = new WebSocket(web_socket_protocol + "://" + window.location.host + "/sockets/");
    this.queuedUpdates = {};

    this.queueGameEvent = function (gameEvent){
        self.queuedUpdates.gameEvents.push(gameEvent);
    };

    this.queueMapUpdate = function queueMapUpdate(updateObject){
        self.queuedUpdates.mapUpdates[updateObject.country] = updateObject;
    };

    this.processUpdates = function(){
        for (var property in self.queuedUpdates.mapUpdates) {
            if (self.queuedUpdates.mapUpdates.hasOwnProperty(property)) {
                var countryUpdate = self.queuedUpdates.mapUpdates[property];
                update_country(countryUpdate.country, countryUpdate.color, countryUpdate.count);
                delete self.queuedUpdates.mapUpdates[property];
            }
        }
        displayGameEvents(self.queuedUpdates.gameEvents);
        self.queuedUpdates.gameEvents = [];
    };

    this.joinMessage = function (name, address, uniqueKey, demoFlag, gameId, joinAttempt){
        var obj = {};
        obj.messageType = "ClientJoined";
        obj.uniqueKey = "" + uniqueKey;
        obj.displayName = name;
        obj.address = address;
        obj.demo = demoFlag;
        obj.gameId = gameId;
        obj.joinAttempt = joinAttempt;
        var msg = JSON.stringify(obj);
        return msg;
    };

    this.open = function(name, address, uniqueKey, demoFlag, gameId, joinAttempt){
        console.log("Opening connection" + this);
        self.queuedUpdates.mapUpdates = {};
        self.queuedUpdates.gameEvents = [];

        self.socket.onmessage = function(event){
            //console.log("message from server: " + event.data);
            var datum = JSON.parse(event.data);
            if (datum.message_type == 'map_update'){
                self.queueMapUpdate(datum);
            } else if (datum.message_type == 'player_update'){
                updatePlayerStats(datum);
            } else if (datum.message_type == 'possible_order_types'){
                updateOrderConsole(datum.color, datum.turn_number, datum.order_types);
            } else if (datum.message_type == 'game_joined'){
                updatePlayerInfoAfterGameJoined(datum);
            } else if (datum.message_type == 'game_event') {
                self.queueGameEvent(datum);
            } else {
                console.log("BAD MESSAGE ON WEB SOCKET: " + event.data);
            }
        };
        // need this to happen after connection created
        self.socket.onopen = function(event) {
            console.log("working on onopen! " + self);
            console.log("working more " + self.socket);
            var msg = self.joinMessage(name, address, uniqueKey, demoFlag, gameId, joinAttempt);
            self.sendMessage(msg);
            var heartbeater = new Heartbeater(self);
            heartbeater.startHeartbeats();
        };
        console.log("Connection opened " + this);
    };
;

    this.sendMessage = function(msg){
        this.socket.send(msg);
    };
}
