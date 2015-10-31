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
    private static final Pattern SHAPE_PATTERN = Pattern.compile("\\$([a-z0-9_-]+)", Pattern.CASE_INSENSITIVE);

    public static PlotData fromStream(InputStream dataStream) throws DataFormatException {

        List<PlotPoint> points = new ArrayList<>();

        Map<String, List<PlotPoint>> shapePoints = new HashMap<>();
        Map<String, ShapeStub> shapeStubs = new HashMap<>();

        Scanner scanner = new Scanner(dataStream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();

            if (line.matches("#.*") || line.isEmpty()) {
                //noinspection UnnecessaryContinue
                continue;   // It's a comment or empty line, ignore it
            } else if (line.matches("\\$.+")) {
                // There's a chance it's a shape color spec
                ShapeStub shapeStub = parseShapeColor(line);
                shapeStubs.put(shapeStub.getName(), shapeStub);
            } else {
                Pair<PlotPoint, Set<String>> dataPoint = parsePoint(line);
                PlotPoint point = dataPoint.getFirst();
                Set<String> shapeNames = dataPoint.getSecond();

                points.add(point);

                for (String shapeName : shapeNames) {
                    if (!shapePoints.containsKey(shapeName)) {
                        shapePoints.put(shapeName, new ArrayList<>());
                    }
                    shapePoints.get(shapeName).add(point);
                }
            }
        }

        List<Shape> shapes = shapePoints.entrySet().stream()
                .map(entry -> {
                    String shapeName = entry.getKey();
                    ShapeStub shapeStub = shapeStubs.get(shapeName);
                    if (shapeStub == null) {
                        shapeStub = new ShapeStub(shapeName, null, Shape.Type.FILLED);
                    }
                    return shapeStub.toShape(entry.getValue());
                })
                .collect(Collectors.toList());

        return new PlotData(points, shapes);
    }

    private static ShapeStub parseShapeColor(String line) throws DataFormatException {
        String[] parts = line.split("\\s+");
        if (parts.length < 2 || parts.length > 3) {
            throw new DataFormatException(line);
        }

        String name;
        String color;
        Shape.Type type = Shape.Type.FILLED;

        Matcher nameMatcher = SHAPE_PATTERN.matcher(parts[0]);
        Matcher strokeColorMatcher = COLOR_PATTERN.matcher(parts[1]);
        if (!nameMatcher.matches() || !strokeColorMatcher.matches()) {
            throw new DataFormatException(line);
        }
        name = nameMatcher.group(1);
        color = parts[1];

        if (parts.length == 3) {
            String typeString = parts[2].toLowerCase();
            switch (typeString) {
                case "filled":
                    type = Shape.Type.FILLED;
                    break;
                case "empty":
                    type = Shape.Type.EMPTY;
                    break;
                case "line":
                    type = Shape.Type.LINE;
                    break;
                default:
                    throw new DataFormatException(line);
            }
        }

        return new ShapeStub(name, color, type);
    }

    private static Pair<PlotPoint, Set<String>> parsePoint(String line) throws DataFormatException {
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            throw new DataFormatException(line);
        } else if (!DOUBLE_PATTERN.matcher(parts[0]).matches() || !DOUBLE_PATTERN.matcher(parts[1]).matches()) {
            throw new DataFormatException(line);
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);

        Color color = Color.BLACK;
        Set<String> shapeNames = new HashSet<>();

        if (parts.length >= 3) {
            int startIndex;
            if (COLOR_PATTERN.matcher(parts[2]).matches()) {
                color = Color.web(parts[2]);
                startIndex = 3;
            } else {
                startIndex = 2;
            }

            for (int i = startIndex; i < parts.length; i++) {
                Matcher matcher = SHAPE_PATTERN.matcher(parts[i]);
                if (!matcher.matches()) {
                    throw new DataFormatException(line);
                }
                shapeNames.add(matcher.group(1));
            }
        }

        PlotPoint point = new PlotPoint(x, y, color);
        return new Pair<>(point, shapeNames);
    }

    private static class ShapeStub {
        private final String name;
        private final Color color;
        private final Shape.Type type;

        public ShapeStub(String name, String color, Shape.Type type) {
            this.name = name;
            this.color = (color != null) ? Color.web(color) : null;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Shape toShape(List<PlotPoint> value) {
            return new Shape(value, color, type);
        }
    }
}
