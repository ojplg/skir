function Occupation(constraint, connection){
    let self = this;
    this.minimumToMove = constraint.minimum_occupation_force;
    this.maximumToMove = constraint.maximum_occupation_force;
    self.connection = connection;

    this.showOccupationControls = function(parentDiv){
        var selector = document.createElement("SELECT");
        for(var idx=this.maximumToMove; idx>=this.minimumToMove; idx--){
            var option = new Option(idx,idx);
            selector.add(option);
        }
        parentDiv.appendChild(selector);

        var button = document.createElement("BUTTON");
        var text = document.createTextNode("Occupy");
        button.onclick = function() {
            console.log("Sending occupation message");
            var order = newOrder("Occupy");
            order.occupationForce = selector.value;
            var jsonOrder = JSON.stringify(order);
            self.connection.sendMessage(jsonOrder);
        };
        button.appendChild(text);
        parentDiv.appendChild(button);
    }
}