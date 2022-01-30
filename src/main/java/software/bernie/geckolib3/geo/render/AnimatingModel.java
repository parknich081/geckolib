package software.bernie.geckolib3.geo.render;

import java.util.*;

import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class AnimatingModel implements BoneTree<AnimatingBone> {

	private final GeoModel model;

	private final List<AnimatingBone> topLevelBones = new ArrayList<>();

	private final List<AnimatingBone> allBones = new ArrayList<>();

	private final Map<String, AnimatingBone> boneLookup = new HashMap<>();

	public AnimatingModel(GeoModel model) {
		this.model = model;

		for (GeoBone topLevelBone : model.topLevelBones) {
			topLevelBones.add(new AnimatingBone(this, topLevelBone));
		}

		for (AnimatingBone bone : topLevelBones) {
			bone.walkDepthFirst(e -> {
				allBones.add(e);
				boneLookup.put(e.getName(), e);
			});
		}
	}

	@Override
	public List<AnimatingBone> getTopLevelBones() {
		return Collections.unmodifiableList(topLevelBones);
	}

	@Override
	public List<AnimatingBone> getAllBones() {
		return Collections.unmodifiableList(allBones);
	}

	@Override
	public AnimatingBone getBoneByName(String name) {
		return boneLookup.get(name);
	}
}
