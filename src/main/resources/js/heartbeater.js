function Heartbeater(){

    this.publishInterval =  5000;

    this.startHeartbeats = function() {
        console.log("Starting heartbeats");
        setInterval( this.sendHeartbeat , this.publishInterval);
    }

    this.sendHeartbeat = function() {
        var heartbeat = {};
        heartbeat.messageType = "heartbeat";
        var msg = JSON.stringify(heartbeat)
        sendMessage(msg);
    }

}