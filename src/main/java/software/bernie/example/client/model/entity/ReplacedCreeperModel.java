package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel<Creeper> {
	@Override
	public ResourceLocation getModelLocation(Creeper object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Creeper object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Creeper animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/creeper.animation.json");
	}
}
