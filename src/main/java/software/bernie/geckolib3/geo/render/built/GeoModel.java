package software.bernie.geckolib3.geo.render.built;

import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

public class GeoModel {
	public List<GeoBone> topLevelBones = new ArrayList<>();
	public ModelProperties properties;

	private GeoModel original = this;

	public Optional<GeoBone> getBone(String name) {
		for (GeoBone bone : topLevelBones) {
			GeoBone optionalBone = getBoneRecursively(name, bone);
			if (optionalBone != null) {
				return Optional.of(optionalBone);
			}
		}
		return Optional.empty();
	}

	public List<IBone> getBones() {
		ImmutableList.Builder<IBone> bones = ImmutableList.builder();
		for (GeoBone bone : topLevelBones) {
			getBonesRecursively(bone, bones);
		}
		return bones.build();
	}

	private void getBonesRecursively(GeoBone bone, ImmutableList.Builder<IBone> bones) {
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

	public GeoModel copy() {
		GeoModel out = new GeoModel();
		out.original = this.original;
		out.properties = this.properties;

		for (GeoBone bone : topLevelBones) {
			out.topLevelBones.add(bone.copy(null));
		}

		return out;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeoModel geoModel = (GeoModel) o;
		return geoModel.original == this.original;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(original);
	}
}
