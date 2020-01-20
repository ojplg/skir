
function Heartbeater(connection){
    this.connection = connection;

    this.sendHeartbeat = function(con){
        var heartbeat = {};
        heartbeat.messageType = "heartbeat";
        var msg = JSON.stringify(heartbeat)
        con.sendMessage(msg);
    }

    this.startHeartbeats = function(){
        var con = this.connection;
        var sendBeat = function() {
//            console.log("lub");
            var heartbeat = {};
            heartbeat.messageType = "heartbeat";
            var msg = JSON.stringify(heartbeat)
            con.sendMessage(msg);
//            console.log("DUB");
        }
        setInterval(sendBeat, HEARTBEAT_INTERVAL);
    }
}