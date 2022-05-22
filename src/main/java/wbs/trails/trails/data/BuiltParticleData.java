package wbs.trails.trails.data;

public class BuiltParticleData<T> {
    private final T data;
    private final int argsUsed;

    public BuiltParticleData(T data, int argsUsed) {
        this.data = data;
        this.argsUsed = argsUsed;
    }

    public T getData() {
        return data;
    }

    public int getArgsUsed() {
        return argsUsed;
    }
}
