package wbs.trails.trails;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.trails.trails.options.DoubleOption;
import wbs.utils.util.particles.DiscParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class CloudTrail extends Trail<CloudTrail> {
	private static final int DEFAULT_RADIUS = 1;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<CloudTrail> registration) {
		registration.registerOption(
				new DoubleOption<>("radius", DEFAULT_RADIUS, 0.2, 1.2, CloudTrail::setRadius, CloudTrail::getRadius)
		);
	}
	
	public CloudTrail(RegisteredTrail<CloudTrail> registration, Player player) {
		super(registration, player);
	}
	
	private double radius = DEFAULT_RADIUS;
	
	private final Vector upVec = new Vector(0, 1, 0);
	private final DiscParticleEffect effect = (DiscParticleEffect) new DiscParticleEffect()
											.setRandom(true)
											.setAbout(upVec)
											.setRadius(radius)
											.setAmount((int) (radius * 30));

	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}

	@Override
	public void tick() {
		effect.setRotation(Math.random() * 360);
		effect.buildAndPlay(particle, player.getLocation());
	}

	public void setRadius(double radius) {
		this.radius = radius;
		effect.setRadius(radius);
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public String getDescription() {
		return "A wide disc of particles that goes around your feet";
	}

	@Override
	protected CloudTrail getThis() {
		return this;
	}
}
