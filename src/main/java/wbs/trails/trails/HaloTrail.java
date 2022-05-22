package wbs.trails.trails;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.trails.trails.options.DoubleOption;
import wbs.utils.util.WbsMath;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.particles.RingParticleEffect;

public class HaloTrail extends Trail<HaloTrail> {
	private static final double DEFAULT_RADIUS = 0.4;
	private static final double DEFAULT_HEIGHT = 0.1;

	@SuppressWarnings("unused") // Invoked reflectively
	public static void registerOptions(RegisteredTrail<HaloTrail> registration) {
		registration.registerOption(
				new DoubleOption<>("radius", DEFAULT_RADIUS, 0.2, 0.8, HaloTrail::setRadius, HaloTrail::getRadius)
		);
		registration.registerOption(
				new DoubleOption<>("height", DEFAULT_HEIGHT, DEFAULT_HEIGHT, 0.6, HaloTrail::setHeight, HaloTrail::getHeight)
		);
	}

	public HaloTrail(RegisteredTrail<HaloTrail> registration, Player player) {
		super(registration, player);
	}
	
	private double radius = DEFAULT_RADIUS;
	private double height = DEFAULT_HEIGHT;
	
	private final RingParticleEffect effect = (RingParticleEffect) new RingParticleEffect()
											.setRadius(radius)
											.setAmount(12);

	@Override
	public void tick() {
		Vector about = WbsEntityUtil.getLocalUp(player);
		about = WbsMath.scaleVector(about, 0.7 + height); // 0.7 is top of head
		
		effect.setAbout(about);
		effect.build();
		
		effect.play(particle, WbsPlayerUtil.getNeckPosition(player).add(about));
	}

	public void setRadius(double radius) {
		this.radius = radius;
		effect.setRadius(radius);
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getRadius() {
		return radius;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public RingParticleEffect getEffect() {
		return effect;
	}

    @Override
	public String getDescription() {
		return "A ring of particles that hover above your head";
	}

	@Override
	protected HaloTrail getThis() {
		return this;
	}
}
