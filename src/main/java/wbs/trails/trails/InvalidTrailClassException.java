package wbs.trails.trails;

public class InvalidTrailClassException extends RuntimeException {
    public <T extends Trail<T>> InvalidTrailClassException(Class<T> trailClass, String msg) {
        super("Invalid trail class \"" + trailClass.getCanonicalName() + "\": " + msg);
    }
}
