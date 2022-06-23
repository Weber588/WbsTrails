package wbs.trails.trails;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.TrailsSettings;
import wbs.trails.WbsTrails;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.plugin.WbsMessenger;

import java.util.Objects;

public abstract class Trail<T extends Trail<T>> extends WbsMessenger {

	private static WbsTrails plugin = null;
	protected static TrailsSettings settings;

	public static void setPlugin(WbsTrails plugin) {
		Trail.plugin = plugin;
		settings = plugin.settings;
	}

	private final RegisteredTrail<T> registration;
	protected Player player;
	protected Particle particle;
	@Nullable
	protected DataProducer<?, ?> data;

	private boolean active = false;
	private boolean locked = false;
	
	Trail(RegisteredTrail<T> registration, Player player) {
		super(plugin);
		this.player = player;

		this.registration = registration;

		settings = plugin.settings;
	}
	public abstract void tick();
	
	public void enable() {
		Objects.requireNonNull(particle);

		active = true;

		build();
	}

	public void build() {
		if (data != null) {
			Object options = data.produce();

			if (!particle.getDataType().isAssignableFrom(options.getClass())) {
				throw new IllegalStateException("Invalid data type for particle \""
						+ particle + "\": " + options.getClass().getCanonicalName());
			}

			getEffect().setOptions(options);
		} else {
			getEffect().setOptions(null);
		}

		getEffect().build();
	}
	
	public void disable() {
		active = false;
	}

	public abstract WbsParticleEffect getEffect();
	
	public T setParticle(Particle particle) {
		this.particle = particle;
		return getThis();
	}
	
	public T setData(DataProducer<?, ?> data) {
		this.data = data;
		return getThis();
	}

	public boolean isActive() {
		return active;
	}
	
	public Player getPlayer() {
		return player;
	}

	public abstract String getDescription();

	protected abstract T getThis();

	public RegisteredTrail<T> getRegistration() {
		return registration;
	}

	public PresetTrail<T> toPreset() {
		return new PresetTrail<>(getThis());
	}

	public Particle getParticle() {
		return particle;
	}

	@Nullable
	public DataProducer<?, ?> getData() {
		return data;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}