// asia
var irktusk = new_country('Irktusk', 780, 50, 100, 100, '#3ca824', 'white');
var japan = new_country('Japan',920,150,45,125, '#2aab0e', 'white');
var kamchatka = new_country('Kamchatka', 880,100,35,150, 'green', 'white');
var mongolia = new_country('Mongolia',820,255,85,50,'green', 'white');

// north america
var alaska = new_country('Alaska', 20, 20, 80, 80, '#ddcc00','black');
var northwest_territory = new_country('Northwest Territory', 100, 20, 80, 80, '#DAEA39','black');
var greenland = new_country('Greenland', 180, 20, 80, 80, '#CCD666','black');
var alberta = new_country('Alberta', 80, 100, 60, 60, '#E4F251','black');
var ontario = new_country('Ontario', 140, 100, 60, 60, '#F2F251','black');
var quebec = new_country('Quebec', 200, 100, 60, 60, '#EAEA10','black');
var western_united_states = new_country('Western United States', 100, 160, 80, 80, '#DCDC05','black');
var eastern_united_states = new_country('Eastern United States', 180, 160, 80, 80, '#DFDF5F','black');
var central_america = new_country('Central America', 120, 240, 80, 80, 'yellow','black');

// south america
var venezuela = new_country('Venezuela', 100, 320, 120, 40, '#5FCCDF', 'white');
var peru = new_country('Peru', 100, 360, 40, 100, '#5fdfcf', 'white');
var brazil = new_country('Brazil', 140, 360, 80, 100, '#19A1BD', 'white');
var argentina = new_country('Argentina', 100, 460, 80, 40, '#6CD2E7', 'white');

// europe
var iceland = new_country('Iceland',400,20,50,50,'#2C22F3','white');
var scandanavia = new_country('Scandanavia',500,20,50,100,'#1A11C0','white');
var great_britain = new_country('Great Britain',440,100,40,80,'#262266','white');
var northern_europe = new_country('Northern Europe',400,200,80,80,'#5C55BD','white');
var southern_europe = new_country('Southern Europe',480,280,80,80,'#5570BD','white');
var western_europe = new_country('Western Europe',400,280,80,80,'#1947CB','white');
var ukraine = new_country('Ukraine',550,40,80,200,'#4D77F2','white');

var countries = [ alaska, northwest_territory, alberta, quebec, ontario,
                   eastern_united_states, western_united_states, greenland, central_america,
                  venezuela, peru, brazil, argentina,
                  iceland, scandanavia, great_britain, northern_europe,
                    southern_europe, western_europe, ukraine,
                  irktusk, japan, kamchatka, mongolia ];

function draw_map(){
  var canvas = document.getElementById ('canvas_map');
  var context = canvas.getContext ('2d');
  context.fillStyle = '#A7B3FE';
  context.fillRect(0,0,1000,600);

  for(var idx=0; idx<countries.length; idx++){
    var country = countries[idx];
    console.log('drawing ' + country.name);
    draw_country(context,country);
  }

  canvas.addEventListener('click', function(e) {
    map_clicked(e);
  });
}

function draw_country(context,country){
  context.fillStyle = country.color;
  context.fillRect(country.left, country.top, country.width, country.height);
  context.fillStyle = country.text_color;
  context.font = '10pt Arial';
  var words = country.name.split(' ');
  for(var idx=0; idx<words.length; idx++){
    var word = words[idx];
    var downoffset = 20 + 20 * idx;
    context.fillText(word,country.left + 5, country.top + downoffset);
  }
}

function new_country(name, left, top, width, height, color, text_color){
  var that = {};
  that.name = name;
  that.top = top;
  that.left = left;
  that.height = height;
  that.width = width;
  that.color = color;
  that.text_color = text_color;
  return that;
}

function map_clicked(e){
  for(var idx=0; idx<countries.length; idx++){
    var country = countries[idx];
    if(country.top <= e.offsetY &&
       country.left <= e.offsetX &&
       country.top + country.height >= e.offsetY &&
       country.left + country.width >= e.offsetX ){
       console.log('Clicked on ' + country.name);
       connection.send('{\'country\':\'' + country.name + '\'}');
    }
  }
}

var connection = new WebSocket('ws://localhost:8080');
