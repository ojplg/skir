function Country(name, left, top, width, height, color, text_color) {
    let self = this;

    self.name = name;
    self.top = top;
    self.left = left;
    self.height = height;
    self.width = width;
    self.color = color;
    self.text_color = text_color;
    self.current_color = null;

    self.wire_name =  name.replace("- ","");

    self.initialDraw = function(context){
        context.fillStyle = self.color;
        context.fillRect(self.left, self.top, self.width, self.height);
        self.paintCountryName(context,self.text_color);
    }

    self.occupiedTextColor = function(player_color){
        var name_color;
        if(player_color == 'Black' || player_color == 'Blue' || player_color == 'Green'){
            name_color = 'white';
        }
        if(player_color == 'Red' || player_color == 'White' || player_color == 'Pink'){
            name_color = 'black';
        }
        return name_color;
    }

    self.update = function (player_color, army_count){
        //console.log("Going to color country " + country_name + " with color " + player_color);
        var canvas = document.getElementById ('canvas_map');
        var context = canvas.getContext ('2d');
        //console.log("Found country " + country);
        var border = 4;
        var text_color = self.occupiedTextColor(player_color);
        if ( self.current_color == null || self.current_color !== player_color){
            context.fillStyle = player_color;
            context.fillRect(self.left + border, self.top + border,
                    self.width - (border*2), self.height- (border*2));
            self.paintCountryName(context, text_color);
        }
        self.updateCountryOccupationCount(context, army_count, text_color);
    }

    self.paintCountryName = function(context, name_color){
        context.fillStyle = name_color;
        context.font = '9pt Arial';
        var words = self.name.split(' ');
        for(var idx=0; idx<words.length; idx++){
            var word = words[idx];
            var downoffset = 20 + 20 * idx;
            context.fillText(word,self.left + 5, self.top + downoffset);
        }
    }

    self.updateCountryOccupationCount = function(context, army_count, number_color){
      context.fillStyle = number_color;
      context.font = '9pt Arial';
      var widthOffset = 14;
      if (army_count >= 1000 ){
        widthOffset = 34;
      } else if (army_count >= 100 ) {
        widthOffset = 26;
      }
      else if (army_count >= 10){
        widthOffset = 18;
      }
      context.fillText(army_count, self.left + self.width - widthOffset,
        self.top + self.height - 6 );
    }

}

function Map(){

    let self = this;

    // north america
    var alaska = new Country('Alaska', 20, 20, 80, 80, '#ddcc00','black');
    var northwest_territory = new Country('Northwest Territory', 100, 20, 80, 80, '#DAEA39','black');
    var greenland = new Country('Greenland', 180, 20, 80, 80, '#CCD666','black');
    var alberta = new Country('Alberta', 80, 100, 60, 60, '#E4F251','black');
    var ontario = new Country('Ontario', 140, 100, 60, 60, '#F2F251','black');
    var quebec = new Country('Quebec', 200, 100, 60, 60, '#EAEA10','black');
    var western_united_states = new Country('Western United States', 100, 160, 80, 80, '#DCDC05','black');
    var eastern_united_states = new Country('Eastern United States', 180, 160, 80, 80, '#DFDF5F','black');
    var central_america = new Country('Central America', 120, 240, 80, 80, 'yellow','black');

    // south america
    var venezuela = new Country('Venezuela', 100, 320, 120, 40, '#5FCCDF', 'white');
    var peru = new Country('Peru', 100, 360, 40, 100, '#5fdfcf', 'white');
    var brazil = new Country('Brazil', 140, 360, 80, 100, '#19A1BD', 'white');
    var argentina = new Country('Argentina', 100, 460, 80, 40, '#189A88', 'white');

    // europe
    var iceland = new Country('Iceland',300,20,50,50,'#2C22F3','white');
    var scandinavia = new Country('Scandi- navia',425,20,65,100,'#1A11C0','white');
    var great_britain = new Country('Great Britain',320,100,60,80,'#262266','white');
    var northern_europe = new Country('Northern Europe',410,120,80,80,'#5C55BD','white');
    var southern_europe = new Country('Southern Europe',420,200,70,70,'#5570BD','white');
    var western_europe = new Country('Western Europe',350,200,70,70,'#1947CB','white');
    var ukraine = new Country('Ukraine',490,40,80,200,'#4D77F2','white');

    // africa
    var north_africa = new Country('North Africa',300,300,140,140,'#F2C04D','black');
    var egypt = new Country('Egypt',440,300,50,70,'#EEA806','black');
    var east_africa = new Country('East Africa',440,370,70,70,'#DAB86A','black');
    var congo = new Country('Congo',410,440,50,50,'#BBA504','black');
    var madagascar = new Country('Mada- gascar',550,410,50,80,'#E7D86B','black');
    var south_africa = new Country('South Africa',460,440,70,70,'#FEE005','black');

    // asia
    var middle_east = new Country('Middle East',490,240,100,130,'green','white');
    var afghanistan = new Country('Afghanistan',570,140,80,100,'#107229','white');
    var india = new Country('India',590,240,100,100,'#0FAF38','white');
    var ural = new Country('Ural',570,40,100,100,'#058D28','white');
    var siberia = new Country('Siberia',670,40,60,100,'#76CF8D','white');
    var yakutsk = new Country('Yakutsk', 730, 20, 70, 40, '#107A06', 'white');
    var irkutsk = new Country('Irkutsk', 730, 60, 70, 40, '#3ca824', 'white');
    var mongolia = new Country('Mongolia', 730, 100, 70, 40, '#52864D', 'white');
    var china = new Country('China', 650, 140, 140, 100, '#18E304', 'white');
    var siam = new Country('Siam',690,240,80,80,'#0F7E04','white');
    var japan = new Country('Japan',880,100,45,125, '#2aab0e', 'white');
    var kamchatka = new Country('Kam- chatka', 800,20,55,110, '#2CCF08', 'white');

    // australia
    var indonesia = new Country('Indonesia',720,350,70,50,'#A645D6','white');
    var new_guinea = new Country('New Guinea',810,340, 60, 65,'#A111E8','white');
    var western_australia = new Country('Western Australia',730,420,70,60,'#9B4CC1','white');
    var eastern_australia = new Country('Eastern Australia',800,420,70,60,'#8E189A','white');

    self.countries = [ alaska, northwest_territory, alberta, quebec, ontario,
                   eastern_united_states, western_united_states, greenland, central_america,
                  venezuela, peru, brazil, argentina,
                  iceland, scandinavia, great_britain, northern_europe,
                    southern_europe, western_europe, ukraine,
                  north_africa, egypt, east_africa,
                    congo, madagascar, south_africa,
                  middle_east, afghanistan, india, ural, siberia,
                    yakutsk, irkutsk, china, siam,
                    japan, kamchatka, mongolia,
                   indonesia, new_guinea, eastern_australia, western_australia];

    self.countries_by_wire_name = {};

    self.countries.forEach(country => {
        self.countries_by_wire_name[country.wire_name] = country; });

    self.drawMap = function(){
        var canvas = document.getElementById ('canvas_map');
        var context = canvas.getContext ('2d');
        context.fillStyle = '#A7B3FE';
        context.fillRect(0,0,950,600);

        for(var idx=0; idx<self.countries.length; idx++){
        var country = self.countries[idx];
            //console.log('drawing ' + country.name);
            country.initialDraw(context);
        }

        self.drawOverseaConnectors();

        canvas.addEventListener('click', function(e) {
            self.mapClicked(e);
        });
    }

    self.drawOverseaConnectors = function(){
        // brazil to north africa
        self.drawOverseaConnector(222,400,298,400);
        // greenland to iceland
        self.drawOverseaConnector(262,50,300,50);
        // iceland to great britain
        self.drawOverseaConnector(335,72,335,100);
        // iceland to scandinavia
        self.drawOverseaConnector(355,50,430,50);
        // great britain to scandinavia
        self.drawOverseaConnector(380,110,430,110);
        // great britain to western europe
        self.drawOverseaConnector(355,182,355,202);
        // great britain to northern europe
        self.drawOverseaConnector(380,150,410,150);
        // western europe to north africa
        self.drawOverseaConnector(375,272,375,298);
        // southern europe to north africa
        self.drawOverseaConnector(445,272,395,298);
        // southern europe to egypt
        self.drawOverseaConnector(465,272,465,298);
        // east africa to madagascar
        self.drawOverseaConnector(512,420,548,420);
        // south africa to madagascar
        self.drawOverseaConnector(532,470,548,470);
        // kamchatka to japan
        self.drawOverseaConnector(858,110,877,110);
        // mongolia to japan
        self.drawOverseaConnector(802,136,877,136);
        // siam to indonesia
        self.drawOverseaConnector(735,322,735,348);
        // indonesia to western australia
        self.drawOverseaConnector(765,402,765,418);
        // indonesia to new guinea
        self.drawOverseaConnector(792,375,808,375);
        // new guinea to western australia
        self.drawOverseaConnector(785,420,810,405);
        // new guinea to eastern australia
        self.drawOverseaConnector(825,407,825,418);
        // border to alaska
        self.drawOverseaConnector(2,40,18,40);
        // kamchatka to border
        self.drawOverseaConnector(857,40,948,40);
    }

    self.drawOverseaConnector = function(xStart,yStart,xEnd,yEnd){
        var canvas = document.getElementById('canvas_map');
        var ctx = canvas.getContext("2d");

        ctx.setLineDash([5, 5]);
        ctx.lineWidth = 3;

        ctx.beginPath();
        ctx.moveTo(xStart,yStart);
        ctx.lineTo(xEnd,yEnd);
        ctx.stroke();
    }

    self.mapClicked = function(e){
        for(var idx=0; idx<self.countries.length; idx++){
            var country = self.countries[idx];
            if(country.top <= e.offsetY &&
                    country.left <= e.offsetX &&
                    country.top + country.height >= e.offsetY &&
                    country.left + country.width >= e.offsetX ){
                console.log('Clicked on ' + country.name);
                // TODO: This should not be a global function
                doStatusDependentCountryClickedWork(country);
            }
        }
    }

    self.updateCountry = function(country_name, player_color, army_count){
        var country = self.countries_by_wire_name[country_name];
        country.update(player_color, army_count);
    }
}
