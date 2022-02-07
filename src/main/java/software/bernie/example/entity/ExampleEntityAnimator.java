package software.bernie.example.entity;

import com.eliotlash.molang.MolangParser;

import software.bernie.example.client.model.entity.ExampleEntityModelType;
import software.bernie.geckolib3.core.bone.BoneTree;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ExampleEntityAnimator extends Animator<GeoExampleEntity> {

	public ExampleEntityAnimator(GeoExampleEntity entity, BoneTree model, ExampleEntityModelType modelType) {
		super(entity, model, modelType);
	}

	@Override
	public void tickAnimation(AnimationEvent<GeoExampleEntity> event, MolangParser parser, double renderTime) {
		super.tickAnimation(event, parser, renderTime);

		IBone head = getBone("head");

		EntityModelData extraData = event.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}
}
