package wbs.trails.trails;

import org.bukkit.entity.Player;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class AuraTrail extends Trail<AuraTrail> {
	private static final int DEFAULT_AMOUNT = 5;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<AuraTrail> registration) {
		registration.registerOption(
				new IntegerOption<>("amount", DEFAULT_AMOUNT, 1, 5, AuraTrail::setAmount, AuraTrail::getAmount)
		);
	}

	public AuraTrail(RegisteredTrail<AuraTrail> registration, Player player) {
		super(registration, player);
	}

	private int amount = DEFAULT_AMOUNT;
	
	private final NormalParticleEffect effect = (NormalParticleEffect) new NormalParticleEffect()
														.setX(0.2)
														.setY(0.5)
														.setZ(0.2)
														.setAmount(amount * settings.getRefreshRate());
	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}

	@Override
	public void tick() {
		// buildAndPlay to refresh providers if used
		effect.buildAndPlay(particle, WbsEntityUtil.getMiddleLocation(player));
	}

	public void setAmount(int amount) {
		this.amount = amount;
		effect.setAmount(amount * settings.getRefreshRate());
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String getDescription() {
		return "Simply a slow spawning trail of particles that surround you";
	}

	@Override
	protected AuraTrail getThis() {
		return this;
	}
}
