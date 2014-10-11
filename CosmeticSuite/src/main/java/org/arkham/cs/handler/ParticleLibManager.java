package org.arkham.cs.handler;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.utils.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.ArcLocationEffect;
import de.slikey.effectlib.effect.AtomLocationEffect;
import de.slikey.effectlib.effect.BleedEntityEffect;
import de.slikey.effectlib.effect.ConeLocationEffect;
import de.slikey.effectlib.effect.DnaLocationEffect;
import de.slikey.effectlib.effect.ExplodeLocationEffect;
import de.slikey.effectlib.effect.FlameEntityEffect;
import de.slikey.effectlib.effect.FountainLocationEffect;
import de.slikey.effectlib.effect.GridLocationEffect;
import de.slikey.effectlib.effect.HelixLocationEffect;
import de.slikey.effectlib.effect.LoveEntityEffect;
import de.slikey.effectlib.effect.MusicEntityEffect;
import de.slikey.effectlib.effect.ShieldEntityEffect;
import de.slikey.effectlib.effect.SmokeEntityEffect;
import de.slikey.effectlib.effect.StarLocationEffect;
import de.slikey.effectlib.effect.TraceEntityEffect;
import de.slikey.effectlib.effect.VortexLocationEffect;
import de.slikey.effectlib.effect.WarpEntityEffect;

public class ParticleLibManager {

	private static EffectManager effectManager;

	public ParticleLibManager() {
		EffectLib lib = EffectLib.instance();
		effectManager = new EffectManager(lib);
	}

	public enum FancyEffect {
		/**
		 * - Create architectual correct arc of particles
		 */
		ARCLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create the orbital-model of the atom
		 */
		ATOMLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Let the target entity bleed.
		 */
		BLEEDENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Cast a cone in all possible directions
		 */
		CONELOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * Create DNA molecule
		 */
		DNALOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a explosion at location.
		 */
		EXPLODELOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Let the target entity burn.
		 */
		FLAMEENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a foundtain for you well
		 */
		FOUNTAINLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Customizable grid for you signwall
		 */
		GRIDLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a customizable static helix.
		 */
		HELIXLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - The target entity is in love.
		 */
		LOVEENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Circle of notes appearers above the entity.
		 */
		MUSICENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Spherical Shield around an entity.
		 */
		SHIELDENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Let the target entity smoke.
		 */
		SMOKEENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create fully customizable 3D star
		 */
		STARLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a trace along an entitys path.
		 */
		TRACEENTITYEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a vortex of particles at location
		 */
		VORTEXLOCATIONEFFECT(ParticleClassType.LIB_EFFECT),
		/**
		 * - Create a warp-effect around an entity
		 */
		WARPENTITYEFFECT(ParticleClassType.LIB_EFFECT);

		private ParticleClassType type;

		private FancyEffect(ParticleClassType type) {
			this.type = type;
		}

		public ParticleClassType getType() {
			return type;
		}
		
		public void stop(Player player){
			Effect e = null;
			switch (this) {
			case ARCLOCATIONEFFECT:
				e = new ArcLocationEffect(effectManager, player.getEyeLocation(), player.getEyeLocation().getDirection().multiply(-3)
						.toLocation(player.getWorld()));
				break;
			case ATOMLOCATIONEFFECT:
				final AtomLocationEffect atom = new AtomLocationEffect(effectManager, player.getLocation().clone().add(0, 1, 0));
				atom.radius = 5;
				e = atom;
				break;
			case BLEEDENTITYEFFECT:
				e = new BleedEntityEffect(effectManager, player);
				break;
			case CONELOCATIONEFFECT:
				e = new ConeLocationEffect(effectManager, player.getEyeLocation());
				break;
			case DNALOCATIONEFFECT:
				e = new DnaLocationEffect(effectManager, player.getEyeLocation());
				break;
			case EXPLODELOCATIONEFFECT:
				e = new ExplodeLocationEffect(effectManager, player.getEyeLocation());
				break;
			case FLAMEENTITYEFFECT:
				e = new FlameEntityEffect(effectManager, player);
				break;
			case FOUNTAINLOCATIONEFFECT:
				e = new FountainLocationEffect(effectManager, player.getEyeLocation());
				break;
			case GRIDLOCATIONEFFECT:
				e = new GridLocationEffect(effectManager, player.getEyeLocation());
				break;
			case HELIXLOCATIONEFFECT:
				e = new HelixLocationEffect(effectManager, player.getEyeLocation());
				break;
			case LOVEENTITYEFFECT:
				e = new LoveEntityEffect(effectManager, player);
				break;
			case MUSICENTITYEFFECT:
				e = new MusicEntityEffect(effectManager, player);
				break;
			case SHIELDENTITYEFFECT:
				e = new ShieldEntityEffect(effectManager, player);
				e.type = EffectType.INSTANT;
				break;
			case SMOKEENTITYEFFECT:
				e = new SmokeEntityEffect(effectManager, player);
				break;
			case STARLOCATIONEFFECT:
				e = new StarLocationEffect(effectManager, player.getEyeLocation());
				break;
			case TRACEENTITYEFFECT:
				e = new TraceEntityEffect(effectManager, player);
				break;
			case VORTEXLOCATIONEFFECT:
				e = new VortexLocationEffect(effectManager, player.getEyeLocation());
				break;
			case WARPENTITYEFFECT:
				e = new WarpEntityEffect(effectManager, player);
				break;
			default:
				break;
			}
			ParticleLibManager.stop(e, player);
		}

		public void display(final Player player) {
			switch (this.type) {
			case LIB_EFFECT:
				Effect e = null;
				switch (this) {
				case ARCLOCATIONEFFECT:
					e = new ArcLocationEffect(effectManager, player.getEyeLocation(), player.getEyeLocation().getDirection().multiply(-3)
							.toLocation(player.getWorld()));
					break;
				case ATOMLOCATIONEFFECT:
					final AtomLocationEffect atom = new AtomLocationEffect(effectManager, player.getLocation().clone().add(0, 1, 0));
					atom.radius = 5;
					e = atom;
					break;
				case BLEEDENTITYEFFECT:
					e = new BleedEntityEffect(effectManager, player);
					break;
				case CONELOCATIONEFFECT:
					e = new ConeLocationEffect(effectManager, player.getEyeLocation());
					break;
				case DNALOCATIONEFFECT:
					e = new DnaLocationEffect(effectManager, player.getEyeLocation());
					break;
				case EXPLODELOCATIONEFFECT:
					e = new ExplodeLocationEffect(effectManager, player.getEyeLocation());
					break;
				case FLAMEENTITYEFFECT:
					e = new FlameEntityEffect(effectManager, player);
					break;
				case FOUNTAINLOCATIONEFFECT:
					e = new FountainLocationEffect(effectManager, player.getEyeLocation());
					break;
				case GRIDLOCATIONEFFECT:
					e = new GridLocationEffect(effectManager, player.getEyeLocation());
					break;
				case HELIXLOCATIONEFFECT:
					e = new HelixLocationEffect(effectManager, player.getEyeLocation());
					break;
				case LOVEENTITYEFFECT:
					e = new LoveEntityEffect(effectManager, player);
					break;
				case MUSICENTITYEFFECT:
					e = new MusicEntityEffect(effectManager, player);
					break;
				case SHIELDENTITYEFFECT:
					e = new ShieldEntityEffect(effectManager, player);
					e.type = EffectType.REPEATING;
					break;
				case SMOKEENTITYEFFECT:
					e = new SmokeEntityEffect(effectManager, player);
					break;
				case STARLOCATIONEFFECT:
					e = new StarLocationEffect(effectManager, player.getEyeLocation());
					break;
				case TRACEENTITYEFFECT:
					e = new TraceEntityEffect(effectManager, player);
					break;
				case VORTEXLOCATIONEFFECT:
					e = new VortexLocationEffect(effectManager, player.getEyeLocation());
					break;
				case WARPENTITYEFFECT:
					e = new WarpEntityEffect(effectManager, player);
					break;
				default:
					break;
				}
				run(e, player);
			case PARTICLE_EFFECT:
				break;
			}
		}

		public enum ParticleClassType {
			PARTICLE_EFFECT, LIB_EFFECT;
		}
	}

	public static void run(final Effect atom, final Player player){
		atom.start();
		new BukkitRunnable() {
			@Override
			public void run() {
				atom.cancel();
				player.removeMetadata("effected", CosmeticSuite.getInstance());
			}
		}.runTaskLaterAsynchronously(CosmeticSuite.getInstance(), 20L*20);;
	}
	
	public static void stop(Effect effect, Player player){
		effect.cancel();
		player.removeMetadata("effected", CosmeticSuite.getInstance());
	}

	public static String name(FancyEffect effect){
		String name = effect.name();
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.YELLOW + ChatColor.BOLD.toString());		
		if(name.contains("_")){
			String[] str = name.split("_");
			for(int i = 0; i < str.length; i++){
				String s = str[i];
				s = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
				builder.append(s + " ");
			}
		} else {
			name = name.replace("ENTITY", " Entity");
			name = name.replace("LOCATION", " Location");
			name = name.replace("EFFECT", " Effect");
			String[] str = name.split(" ");
			for(int i = 0; i < str.length; i++){
				String s = str[i];
				s = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
				builder.append(s + " ");
			}
		}
		return builder.toString();
	}

	public static Rank getRank(FancyEffect effect){
		switch(effect.getType()){
		case LIB_EFFECT:
			return Rank.SUPERHERO;
		case PARTICLE_EFFECT:
			return Rank.HERO;
		}
		return Rank.HERO;
	}
}
