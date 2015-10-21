package net.avensome.dev.gnupolpot.plotter;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.plotter.shapes.PlotPoint;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PointImporter {
    public static List<PlotPoint> fromStream(InputStream dataStream) throws DataFormatException {
        List<PlotPoint> points = new ArrayList<>();
        Scanner scanner = new Scanner(dataStream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();

            Pattern doublePattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
            Pattern colorPattern = Pattern.compile("#[0-9a-f]{6}|#[0-9a-f]{3}", Pattern.CASE_INSENSITIVE);

            if (line.matches("#.*") || line.isEmpty()) {
                continue;   // It's a comment or empty line, ignore it
            }

            String[] parts = line.split("\\s+");
            if (parts.length < 2 || parts.length > 3) {
                throw new DataFormatException(line);
            } else if (!doublePattern.matcher(parts[0]).matches() || !doublePattern.matcher(parts[1]).matches()) {
                throw new DataFormatException(line);
            } else if (parts.length == 3 && !colorPattern.matcher(parts[2]).matches()) {
                throw new DataFormatException(line);
            }

            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            Color color = (parts.length == 2) ? Color.BLACK : Color.web(parts[2]);

            points.add(new PlotPoint(x, y, color));
        }

        return points;
    }
}
