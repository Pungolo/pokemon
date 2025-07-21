package utils;

import static javax.imageio.ImageIO.read;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SpriteLoader {

    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();

    private SpriteLoader() {
        // Utility class: non instanziabile
    }

    public static BufferedImage load(final String path) {
        return CACHE.computeIfAbsent(path, SpriteLoader::loadImage);
    }

    private static BufferedImage loadImage(final String path) {
        try (InputStream is = SpriteLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }
            return read(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite: " + path, e);
        }
    }
}
