
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
var argentina = new_country('Argentina', 100, 460, 80, 40, '#189A88', 'white');

// europe
var iceland = new_country('Iceland',300,20,50,50,'#2C22F3','white');
var scandanavia = new_country('Scanda- navia',425,20,65,100,'#1A11C0','white');
var great_britain = new_country('Great Britain',320,100,60,80,'#262266','white');
var northern_europe = new_country('Northern Europe',410,120,80,80,'#5C55BD','white');
var southern_europe = new_country('Southern Europe',420,200,70,70,'#5570BD','white');
var western_europe = new_country('Western Europe',350,200,70,70,'#1947CB','white');
var ukraine = new_country('Ukraine',490,40,80,200,'#4D77F2','white');

// africa
var north_africa = new_country('North Africa',300,300,140,140,'#F2C04D','black');
var egypt = new_country('Egypt',440,300,50,70,'#EEA806','black');
var east_africa = new_country('East Africa',440,370,70,70,'#DAB86A','black');
var congo = new_country('Congo',410,440,50,50,'#BBA504','black');
var madagascar = new_country('Mada- gascar',550,410,50,80,'#E7D86B','black');
var south_africa = new_country('South Africa',460,440,70,70,'#FEE005','black');

// asia
var middle_east = new_country('Middle East',490,240,100,130,'green','white');
var afghanistan = new_country('Afghanistan',570,140,80,100,'#107229','white');
var india = new_country('India',590,240,100,100,'#0FAF38','white');
var ural = new_country('Ural',570,40,100,100,'#058D28','white');
var siberia = new_country('Siberia',670,40,60,100,'#76CF8D','white');
var yakutsk = new_country('Yakutsk', 730, 30, 70, 40, '#107A06', 'white');
var irkutsk = new_country('Irkutsk', 730, 70, 70, 40, '#3ca824', 'white');
var mongolia = new_country('Mongolia', 730, 110, 70, 30, '#52864D', 'white');
var china = new_country('China', 650, 140, 140, 100, '#18E304', 'white');
var siam = new_country('Siam',690,240,80,80,'#0F7E04','white');
var japan = new_country('Japan',880,100,45,125, '#2aab0e', 'white');
var kamchatka = new_country('Kam- chatka', 800,20,55,110, '#2CCF08', 'white');

// australia
var indonesia = new_country('Indonesia',720,350,70,50,'#A645D6','white');
var new_guinea = new_country('New Guinea',810,340, 50, 50,'#A111E8','white');
var western_australia = new_country('Western Australia',730,420,70,60,'#9B4CC1','white');
var eastern_australia = new_country('Eastern Australia',800,420,70,60,'#8E189A','white');

var countries = [ alaska, northwest_territory, alberta, quebec, ontario,
                   eastern_united_states, western_united_states, greenland, central_america,
                  venezuela, peru, brazil, argentina,
                  iceland, scandanavia, great_britain, northern_europe,
                    southern_europe, western_europe, ukraine,
                  north_africa, egypt, east_africa,
                    congo, madagascar, south_africa,
                  middle_east, afghanistan, india, ural, siberia,
                    yakutsk, irkutsk, china, siam,
                    japan, kamchatka, mongolia,
                   indonesia, new_guinea, eastern_australia, western_australia];

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
    context.fillText(word,country.left + 2, country.top + downoffset);
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

function update_country(countryName, playerColor, armyCount){
  console.log("Going to color country " + countryName + " with color " + playerColor);
  var canvas = document.getElementById ('canvas_map');
  var context = canvas.getContext ('2d');
  for(var idx=0; idx<countries.length; idx++){
    var country = countries[idx];
    if( country.name == countryName ){
      var border = 4;
      context.fillStyle = playerColor;
      context.fillRect(country.left + border, country.top + border,
        country.width - (border*2), country.height- (border*2));
    }
  }
}

var connection = new WebSocket('ws://localhost:8080');
connection.onmessage = function(event){
  console.log("from server:" + event.data);
  var datum = JSON.parse(event.data);
  update_country(datum.country, datum.color, datum.count);
}
