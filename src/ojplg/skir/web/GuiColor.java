package ojplg.skir.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiColor {

    public static GuiColor BLACK = new GuiColor("Black", "#000000", "#FFFFFF");
    public static GuiColor BLUE = new GuiColor("Blue", "#0000FF", "#FFFFFF");
    public static GuiColor RED = new GuiColor("Red", "red", "#000000");
    public static GuiColor GREEN = new GuiColor("Green", "green", "#FFFFFF");
    public static GuiColor WHITE = new GuiColor("White", "#FFFFFF", "#000000");
    public static GuiColor PINK = new GuiColor("Pink", "pink", "#000000");

    public static List<GuiColor> ALL_COLORS = Collections.unmodifiableList(
            Arrays.asList( new GuiColor[]{ BLACK, BLUE, RED, GREEN, WHITE, PINK} ));

    private final String _color;
    private final String _background;
    private final String _foreground;

    private GuiColor(String color, String background, String foreground){
        this._color = color;
        this._background = background;
        this._foreground = foreground;
    }

    public String getColor() {
        return _color;
    }

    public String getLowerCaseColor(){
        return _color.toLowerCase();
    }

    public String getBackground() {
        return _background;
    }

    public String getForeground() {
        return _foreground;
    }
}
