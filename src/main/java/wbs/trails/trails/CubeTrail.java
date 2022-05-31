package wbs.trails.trails;

import org.bukkit.entity.Player;
import wbs.trails.trails.options.BooleanOption;
import wbs.trails.trails.options.DoubleOption;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.particles.CuboidParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class CubeTrail extends Trail<CubeTrail> {
	private static final int DEFAULT_AMOUNT = 5;
	private static final double DEFAULT_SIZE = 1;
	private static final double DEFAULT_SPEED = 1;
	private static final boolean DEFAULT_BOUNCE = true;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<CubeTrail> registration) {
		registration.registerOption(
				new IntegerOption<>("amount", DEFAULT_AMOUNT, 1, 10, CubeTrail::setAmount, CubeTrail::getAmount)
		);
		registration.registerOption(
				new DoubleOption<>("size", DEFAULT_SIZE, 0.4, 2, CubeTrail::setSize, CubeTrail::getSize)
		);
		registration.registerOption(
				new DoubleOption<>("speed", DEFAULT_SPEED, 0, 2, CubeTrail::setSpeed, CubeTrail::getSpeed)
		);
		registration.registerOption(
				new BooleanOption<>("bounce", DEFAULT_BOUNCE, CubeTrail::setBounce, CubeTrail::isBounce)
		);
	}

	private int amount = DEFAULT_AMOUNT;
	private double speed = DEFAULT_SPEED;
	private double size = DEFAULT_SIZE;
	private boolean bounce = DEFAULT_BOUNCE;
	
	private final CuboidParticleEffect effect = (CuboidParticleEffect) new CuboidParticleEffect().setXYZ(size)
			.setAmount(amount);
	
	public CubeTrail(RegisteredTrail<CubeTrail> registration, Player player) {
		super(registration, player);
	}

	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}
	
	private double rotation = 0;
	private double height = 0;
	
	private int age = 0;
	
	@Override
	public void tick() {
		age += settings.getRefreshRate();
		if (speed > 0) {
			rotation += settings.getRefreshRate() * speed;
			
			effect.setRotation(rotation);
		}
		
		if (bounce) {
			height = Math.sin(age * settings.getRefreshRate() * speed * 0.05) * (2 - size) / 2 + 0.1; // 0.1 is offset because particles are often not just a point
		}
		
		effect.buildAndPlay(particle, WbsEntityUtil.getMiddleLocation(player).add(0, height, 0));
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setSize(double size) {
		this.size = size;
		effect.setXYZ(size);
	}

	public void setAmount(int amount) {
		this.amount = amount;
		effect.setAmount(amount);
	}

	public void setBounce(boolean bounce) {
		this.bounce = bounce;
	}

	public int getAmount() {
		return amount;
	}

	public double getSpeed() {
		return speed;
	}

	public double getSize() {
		return size;
	}

	public boolean isBounce() {
		return bounce;
	}

	@Override
	public String getDescription() {
		return "Particles spawn in the outline of a cube around you.";
	}

	@Override
	protected CubeTrail getThis() {
		return this;
	}
}
