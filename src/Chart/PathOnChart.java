package Chart;

import java.util.ArrayList;

import android.graphics.PointF;

public class PathOnChart {

    PathAttributes attributes;

    ArrayList<PointF> points;

    public PathOnChart(ArrayList<PointF> points, PathAttributes pathAttributes) {

        this.attributes = pathAttributes;
        this.points = points;
    }
}