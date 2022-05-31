package wbs.trails.trails;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.utils.util.VersionUtil;
import wbs.utils.util.WbsMath;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.particles.CustomParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HornsTrail extends Trail<HornsTrail> {
	private static final Vector[] customPoints = {

			new Vector(0.2, 0.45, 0.2),
			new Vector(-0.2, 0.45, 0.2),

			new Vector(0.25, 0.5, 0.2),
			new Vector(-0.25, 0.5, 0.2),

			new Vector(0.3, 0.55, 0.2),
			new Vector(-0.3, 0.55, 0.2),

			new Vector(0.3, 0.6, 0.2),
			new Vector(-0.3, 0.6, 0.2),

			new Vector(0.3, 0.65, 0.2),
			new Vector(-0.3, 0.65, 0.2)

	};

	static {
		// 1.16 changed model positions slightly. This corrects that.
		if (VersionUtil.getVersion() >= 16) {
			Vector offsetVec1_16 = new Vector(0, -0.05, 0.1);
			for (Vector point : customPoints) {
				point.add(offsetVec1_16);
			}
		}
	}

	public HornsTrail(RegisteredTrail<HornsTrail> registration, Player player) {
		super(registration, player);
	}
	
	private final CustomParticleEffect effect = new CustomParticleEffect() {



		@Override
		public CustomParticleEffect play(Particle particle, Location loc) {
			refreshProviders();

			World world = loc.getWorld();
			assert world != null;
			ArrayList<Location> locations = getLocations(loc);
			
			if (options == null) {
				for (Location point : locations) {
					world.spawnParticle(particle, point, 1, 0, 0, 0, 0, null, true);
				}
			} else {
				for (Location point : locations) {
					world.spawnParticle(particle, point, 1, 0, 0, 0, 0, particle.getDataType().cast(options), true);
				}
			}
			return this;
		}

		@Override
		public WbsParticleEffect play(Particle particle, Location location, Player player) {
			refreshProviders();

			ArrayList<Location> locations = getLocations(location);

			if (options == null) {
				for (Location point : locations) {
					player.spawnParticle(particle, point, 1, 0, 0, 0, 0, null);
				}
			} else {
				for (Location point : locations) {
					player.spawnParticle(particle, point, 1, 0, 0, 0, 0, particle.getDataType().cast(options));
				}
			}
			return this;
		}
	};

	private final static Vector upVector = new Vector(0, 1, 0);
	
	@Override
	public void tick() {
		Vector localUp = WbsEntityUtil.getLocalUp(player);
		
		List<Vector> newPoints = WbsMath.rotateFrom(Arrays.asList(customPoints),
				localUp,
				upVector);
		
		newPoints = WbsMath.rotateVectors(newPoints, localUp, 0 - player.getLocation().getYaw());
		
		effect.setPoints(newPoints);

		// buildAndPlay to refresh providers if used
		effect.buildAndPlay(particle, WbsPlayerUtil.getNeckPosition(player));
	}

	@Override
	public CustomParticleEffect getEffect() {
		return effect;
	}

	@Override
	public String getDescription() {
		return "Particle horns!";
	}

	@Override
	protected HornsTrail getThis() {
		return this;
	}
}
