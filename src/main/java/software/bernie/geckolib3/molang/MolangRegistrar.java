package software.bernie.geckolib3.molang;

import com.eliotlash.molang.math.Variable;
import com.eliotlash.molang.MolangParser;

public class MolangRegistrar {
	public static void registerVars(MolangParser parser) {
		parser.createVariable("query.anim_time");
		parser.createVariable("query.actor_count");
		parser.createVariable("query.health");
		parser.createVariable("query.max_health");
		parser.createVariable("query.distance_from_camera");
		parser.createVariable("query.yaw_speed");
		parser.createVariable("query.is_in_water_or_rain");
		parser.createVariable("query.is_in_water");
		parser.createVariable("query.is_on_ground");
		parser.createVariable("query.time_of_day");
		parser.createVariable("query.is_on_fire");
		parser.createVariable("query.ground_speed");
	}
}
