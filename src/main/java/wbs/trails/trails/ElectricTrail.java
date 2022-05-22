package wbs.trails.trails;

import org.bukkit.entity.Player;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.particles.ElectricParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

public class ElectricTrail extends Trail<ElectricTrail> {
	public ElectricTrail(RegisteredTrail<ElectricTrail> registration, Player player) {
		super(registration, player);
	}

	private final ElectricParticleEffect effect = (ElectricParticleEffect) new ElectricParticleEffect()
														.setTicks(1)
														.setArcLength(0.4)
														.setRadius(1)
														.setAmount(2);

	@Override
	public WbsParticleEffect getEffect() {
		return effect;
	}

	@Override
	public void tick() {
		// buildAndPlay to refresh providers if used
		effect.buildAndPlay(particle, WbsEntityUtil.getMiddleLocation(player));
	}

    @Override
	public String getDescription() {
		return "Tiny arcs appear around you as if you were sparking.";
	}

	@Override
	protected ElectricTrail getThis() {
		return this;
	}
}
