package software.bernie.geckolib3.geo.render.built;

import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

public class GeoModel {
	public final ImmutableList<GeoBone> topLevelBones;
	public final ModelProperties properties;

	public GeoModel(ImmutableList<GeoBone> topLevelBones, ModelProperties properties) {
		this.topLevelBones = topLevelBones;
		this.properties = properties;
	}

	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : topLevelBones) {
			GeoBone optionalBone = getBoneRecursively(name, bone);
			if (optionalBone != null) {
				return Optional.of(optionalBone);
			}
		}
		return Optional.empty();
	}

	public List<GeoBone> getBones() {
		ImmutableList.Builder<GeoBone> bones = ImmutableList.builder();
		for (GeoBone bone : topLevelBones) {
			getBonesRecursively(bone, bones);
		}
		return bones.build();
	}

	private void getBonesRecursively(GeoBone bone, ImmutableList.Builder<GeoBone> bones) {
		bones.add(bone);
		for (GeoBone child : bone.childBones) {
			getBonesRecursively(child, bones);
		}
	}

	private GeoBone getBoneRecursively(String name, GeoBone bone) {
		if (bone.name.equals(name)) {
			return bone;
		}
		for (GeoBone childBone : bone.childBones) {
			if (childBone.name.equals(name)) {
				return childBone;
			}
			GeoBone optionalBone = getBoneRecursively(name, childBone);
			if (optionalBone != null) {
				return optionalBone;
			}
		}
		return null;
	}
}
