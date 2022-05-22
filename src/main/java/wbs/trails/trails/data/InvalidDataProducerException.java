package wbs.trails.trails.data;

public class InvalidDataProducerException extends RuntimeException {
    public <T> InvalidDataProducerException(Class<T> trailClass, String msg) {
        super("Invalid trail class \"" + trailClass.getCanonicalName() + "\": " + msg);
    }
}
