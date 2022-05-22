package wbs.trails.trails;

import org.bukkit.entity.Player;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class StandardTrail extends Trail<StandardTrail> {
	private static final int DEFAULT_AMOUNT = 5;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<StandardTrail> registration) {
		registration.registerOption(
				new IntegerOption<>("amount", DEFAULT_AMOUNT, 1, 5, StandardTrail::setAmount, StandardTrail::getAmount)
		);
	}

	public StandardTrail(RegisteredTrail<StandardTrail> registration, Player player) {
		super(registration, player);
	}
	
	private int amount = DEFAULT_AMOUNT;
	
	private final NormalParticleEffect effect = (NormalParticleEffect) new NormalParticleEffect()
														.setXYZ(0.1)
														.setAmount(amount);

	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}

	@Override
	public void tick() {
		// buildAndPlay to refresh providers if used
		effect.buildAndPlay(particle, player.getLocation());
	}

	public void setAmount(int amount) {
		this.amount = amount;
		effect.setAmount(amount);
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String getDescription() {
		return "A small sphere of particles that follows you as you move";
	}

	@Override
	protected StandardTrail getThis() {
		return this;
	}

}
