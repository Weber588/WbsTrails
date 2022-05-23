package wbs.trails.trails;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import wbs.trails.trails.options.DoubleOption;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.particles.RingParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class HelixTrail extends Trail<HelixTrail> {
	private static final double DEFAULT_RADIUS = 1;
	private static final double DEFAULT_SPEED = 1;
	private static final double DEFAULT_ROTATIONS = 1;
	private static final int DEFAULT_AMOUNT = 3;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<HelixTrail> registration) {
		registration.registerOption(
				new IntegerOption<>("amount", DEFAULT_AMOUNT, 1, 5, HelixTrail::setAmount, HelixTrail::getAmount)
		);
		registration.registerOption(
				new DoubleOption<>("radius", DEFAULT_RADIUS, 1, 2, HelixTrail::setRadius, HelixTrail::getRadius)
		);
		registration.registerOption(
				new DoubleOption<>("speed", DEFAULT_SPEED, 0.1, 10, HelixTrail::setSpeed, HelixTrail::getSpeed)
		);
		registration.registerOption(
				new DoubleOption<>("rotations", DEFAULT_ROTATIONS, 0.1, 5, HelixTrail::setRotations, HelixTrail::getRotations)
		);
	}

	public HelixTrail(RegisteredTrail<HelixTrail> registration, Player player) {
		super(registration, player);
	}

	private double radius = DEFAULT_RADIUS;
	private int amount = DEFAULT_AMOUNT;
	private double speed = DEFAULT_SPEED;
	private double rotationsPerCycle = DEFAULT_ROTATIONS;

	private final Vector upVec = new Vector(0, 1, 0);
	private final RingParticleEffect effect =
			(RingParticleEffect) new RingParticleEffect()
					.setRadius(radius)
					.setAbout(upVec)
					.setAmount(amount);
	
	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}

	/**
	 * Range 0-1, 0 = bottom, 1 = top
	 */
	private double progress;

	@Override
	public void tick() {
		progress += (settings.getRefreshRate() * speed) / 100;
		progress %= 1;

		double height = progress * player.getHeight();

		double rotation = progress * 360 * rotationsPerCycle;
		rotation %= 360;

		effect.setRotation(rotation);
		effect.buildAndPlay(particle, player.getLocation().add(0, height, 0));
	}

	public void setAmount(int amount) {
		this.amount = amount;
		effect.setAmount(amount);
	}

	public void setRadius(double radius) {
		this.radius = radius;
		effect.setRadius(radius);
	}

	public void setRotations(double rotations) {
		this.rotationsPerCycle = rotations;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getRadius() {
		return radius;
	}

	public int getAmount() {
		return amount;
	}

	public double getSpeed() {
		return speed;
	}

	public double getRotations() {
		return rotationsPerCycle;
	}

	@Override
	public String getDescription() {
		return "Two orbs circle you while moving up and down, causing a spiral around your body";
	}

	@Override
	protected HelixTrail getThis() {
		return this;
	}
}
