package wbs.trails.trails;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.trails.trails.options.BooleanOption;
import wbs.trails.trails.options.DoubleOption;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.particles.RingParticleEffect;

public class OrbiterTrail extends Trail<OrbiterTrail> {
	private static final double DEFAULT_RADIUS = 1;
	private static final double DEFAULT_HEIGHT = 1;
	private static final double DEFAULT_SPEED = 1;
	private static final int DEFAULT_AMOUNT = 5;
	private static final boolean DEFAULT_BOUNCE = true;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<OrbiterTrail> registration) {
		registration.registerOption(
				new IntegerOption<>("amount", DEFAULT_AMOUNT, 1, 5, OrbiterTrail::setAmount, OrbiterTrail::getAmount)
		);
		registration.registerOption(
				new DoubleOption<>("speed", DEFAULT_SPEED, 0, 2, OrbiterTrail::setSpeed, OrbiterTrail::getSpeed)
		);
		registration.registerOption(
				new BooleanOption<>("bounce", DEFAULT_BOUNCE, OrbiterTrail::setBounce, OrbiterTrail::isBounce)
		);
		registration.registerOption(
				new DoubleOption<>("radius", DEFAULT_RADIUS, 0.2, 1.2, OrbiterTrail::setRadius, OrbiterTrail::getRadius)
		);
		registration.registerOption(
				new DoubleOption<>("height", DEFAULT_HEIGHT, 0, 2, OrbiterTrail::setHeight, OrbiterTrail::getHeight)
		);
	}

	public OrbiterTrail(RegisteredTrail<OrbiterTrail> registration, Player player) {
		super(registration, player);
	}

	private double radius = DEFAULT_RADIUS;
	private int amount = DEFAULT_AMOUNT;
	private boolean bounce = DEFAULT_BOUNCE;
	private double initialHeight = DEFAULT_HEIGHT;
	private double speed = DEFAULT_SPEED;
	
	private final Vector upVec = new Vector(0, 1, 0);
	private final RingParticleEffect effect = (RingParticleEffect) new RingParticleEffect()
											.setRadius(radius)
											.setAbout(upVec)
											.setAmount(amount);

	@Override
	public RingParticleEffect getEffect() {
		return effect;
	}

	private double age = 0;
	private int rotation = 0;

	private double height = 0;

	@Override
	public void tick() {
		rotation += settings.getRefreshRate() * speed;
		effect.setRotation(rotation);

		if (bounce) {
			age += settings.getRefreshRate();
			height = Math.sin(age / 3) * 0.1;
		}
		
		effect.buildAndPlay(particle, player.getLocation().add(0, initialHeight + height, 0));
	}

	public void setAmount(int amount) {
		this.amount = amount;
		effect.setAmount(amount);
	}

	public void setRadius(double radius) {
		this.radius = radius;
		effect.setRadius(radius);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setHeight(double height) {
		this.initialHeight = height;
	}

	public void setBounce(boolean bounce) {
		this.bounce = bounce;
	}

	public double getRadius() {
		return radius;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isBounce() {
		return bounce;
	}

	public double getHeight() {
		return initialHeight;
	}

	public double getSpeed() {
		return speed;
	}

	@Override
	public String getDescription() {
		return "A group of particles that circles around you";
	}

	@Override
	protected OrbiterTrail getThis() {
		return this;
	}

}
