function ArmyCountGraph(){

    var blackLine = new TimeSeries();
    var blueLine = new TimeSeries();
    var redLine = new TimeSeries();
    var greenLine = new TimeSeries();
    var whiteLine = new TimeSeries();
    var pinkLine = new TimeSeries();

    var counts = {
        'black': 21,
        'blue': 21,
        'red': 21,
        'green': 21,
        'white': 21,
        'pink': 21
    };

    this.updateCount = function(color, cnt){
        counts[color] = cnt;
    }

    setInterval(function() {
        var x = new Date().getTime();
        blackLine.append(x, counts['black']);
        blueLine.append(x, counts['blue']);
        redLine.append(x, counts['red']);
        greenLine.append(x, counts['green']);
        whiteLine.append(x, counts['white']);
        pinkLine.append(x, counts['pink']);

    }, SMOOTHIE_INTERVAL);

    var smoothie = new SmoothieChart(
        {
            grid: { fillStyle:'rgb(60, 60, 60)' }
            ,millisPerPixel:SMOOTHIE_MILLIS_PER_PIXEL
        });

    smoothie.addTimeSeries(blackLine, { strokeStyle:'rgb(0, 0, 0)' });
    smoothie.addTimeSeries(blueLine, { strokeStyle:'rgb(0, 0, 255)' });
    smoothie.addTimeSeries(redLine, { strokeStyle:'rgb(255, 0, 0)' });
    smoothie.addTimeSeries(greenLine, { strokeStyle:'rgb(0, 255, 0)' });
    smoothie.addTimeSeries(whiteLine, { strokeStyle:'rgb(255, 255, 255)' });
    smoothie.addTimeSeries(pinkLine, { strokeStyle:'rgb(250, 175, 190)' });

    smoothie.streamTo(document.getElementById("army-count-graph"), SMOOTHIE_INTERVAL);

}
