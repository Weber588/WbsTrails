package wbs.trails.trails.data;

public class ParticleDataNotSupportedException extends Exception {
    public ParticleDataNotSupportedException() {}

    public ParticleDataNotSupportedException(String message) {
        super(message);
    }

    public ParticleDataNotSupportedException(Class<?> dataClass, String message) {
        super("Data class not supported: " + dataClass.getCanonicalName() + ". Error: " + message);
    }

    public ParticleDataNotSupportedException(Class<?> dataClass) {
        super("Data class not supported: " + dataClass.getCanonicalName());
    }
}
