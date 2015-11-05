package net.avensome.dev.gnupolpot.core.plotter;

import javafx.scene.paint.Color;
import net.avensome.dev.gnupolpot.api.plotter.DataFormatException;
import net.avensome.dev.gnupolpot.api.plotter.PlotData;
import net.avensome.dev.gnupolpot.api.plotter.PlotPoint;
import net.avensome.dev.gnupolpot.api.plotter.Shape;
import net.avensome.dev.gnupolpot.core.plotter.util.Pair;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Importer {
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    private static final Pattern COLOR_PATTERN = Pattern.compile("#[0-9a-f]{6}|#[0-9a-f]{3}", Pattern.CASE_INSENSITIVE);
    private static final Pattern ID_PATTERN = Pattern.compile("\\*([a-z0-9_-]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TYPE_PATTERN = Pattern.compile("_([a-z]+)", Pattern.CASE_INSENSITIVE);

    public static PlotData fromStream(InputStream dataStream) throws DataFormatException {
        List<ShapeStub> shapeStubs = new LinkedList<>();
        Map<String, PlotPoint> points = new HashMap<>();

        Scanner scanner = new Scanner(dataStream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();

            if (line.matches("#.*") || line.isEmpty()) {
                //noinspection UnnecessaryContinue
                continue;   // It's a comment or empty line, ignore it
            } else if (line.matches("\\$\\s.+")) {
                // There's a chance it's a shape color spec
                ShapeStub shapeStub = parseShape(line);
                shapeStubs.add(shapeStub);
            } else {
                Pair<PlotPoint, String> dataPoint = parsePoint(line);
                PlotPoint point = dataPoint.getFirst();
                String pointId = dataPoint.getSecond();
                points.put(pointId, point);
            }
        }

        List<Shape> shapes = shapeStubs.stream()
                .map(stub -> stub.toShape(points))
                .collect(Collectors.toList());

        return new PlotData(points.values(), shapes);
    }

    private static Pair<PlotPoint, String> parsePoint(String line) throws DataFormatException {
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            throw new DataFormatException(line);
        } else if (!DOUBLE_PATTERN.matcher(parts[0]).matches() || !DOUBLE_PATTERN.matcher(parts[1]).matches()) {
            throw new DataFormatException(line);
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);

        Color color = Color.BLACK;
        String id = null;

        if (parts.length >= 3) {
            int idIndex;
            if (COLOR_PATTERN.matcher(parts[2]).matches()) {
                color = Color.web(parts[2]);
                idIndex = 3;
            } else {
                idIndex = 2;
            }

            if (parts.length > idIndex) {
                Matcher idMatcher = ID_PATTERN.matcher(parts[idIndex]);
                if (idMatcher.matches()) {
                    id = idMatcher.group(1);
                } else {
                    throw new DataFormatException(line);
                }
            }

            if (parts.length > 4) {
                throw new DataFormatException(line);
            }
        }

        PlotPoint point = new PlotPoint(x, y, color);
        return new Pair<>(point, id);
    }

    private static ShapeStub parseShape(String line) throws DataFormatException {
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            throw new DataFormatException(line);
        }


        int index = 1;

        String color = null;
        Matcher colorMatcher = COLOR_PATTERN.matcher(parts[index]);
        if (colorMatcher.matches()) {
            index++;
            color = parts[1];
        }

        Shape.Type type = Shape.Type.FILLED;
        Matcher typeMatcher = TYPE_PATTERN.matcher(parts[index]);
        if (typeMatcher.matches()) {
            index++;
            type = parseType(typeMatcher.group(1).toUpperCase());
        }

        List<String> partsList = Arrays.asList(parts);
        List<String> idParts = partsList.subList(index, partsList.size());
        for (String id : idParts) {
            if (!ID_PATTERN.matcher(id).matches()) {
                throw new DataFormatException(line);
            }
        }
        List<String> ids = idParts.stream()
                .map(part -> {
                    Matcher matcher = ID_PATTERN.matcher(part);
                    //noinspection ResultOfMethodCallIgnored
                    matcher.matches();
                    return matcher.group(1);
                })
                .collect(Collectors.toList());

        return new ShapeStub(color, type, ids);
    }

    private static Shape.Type parseType(String typeString) {
        try {
            return Shape.Type.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static class ShapeStub {
        private final Color color;
        private final Shape.Type type;
        private final List<String> expectedPointIds;

        public ShapeStub(String color, Shape.Type type, List<String> expectedPointIds) {
            this.color = (color != null) ? Color.web(color) : null;
            this.type = type;
            this.expectedPointIds = expectedPointIds;
        }

        public Shape toShape(Map<String, PlotPoint> pointIds) {
            List<PlotPoint> points = expectedPointIds.stream()
                    .map(pointIds::get)
                    .collect(Collectors.toList());
            return new Shape(points, color, type);
        }
    }
}
