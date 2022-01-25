package software.bernie.geckolib3.geo.raw.tree;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.geo.raw.pojo.MinecraftGeometry;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;

import java.util.*;

public class RawGeometryTree {
	private final List<RawBoneGroup> topLevelBones;
	public final ModelProperties properties;

	public RawGeometryTree(List<RawBoneGroup> topLevelBones, ModelProperties properties) {
		this.topLevelBones = topLevelBones;
		this.properties = properties;
	}

	public Collection<RawBoneGroup> getTopLevelBones() {
		return topLevelBones;
	}

	public static RawGeometryTree parseHierarchy(ResourceLocation location, RawGeoModel model) {

		MinecraftGeometry geometry = model.getMinecraftGeometry()[0];

		Bone[] allBones = geometry.getBones();

		// create bone groups ahead of time
		Map<String, RawBoneGroup> boneGroups = RawBoneGroup.createBoneGroups(allBones);
		List<RawBoneGroup> topLevelBones = new ArrayList<>();

		for (Bone bone : allBones) {
			if (!hasParent(bone)) {
				// if this bone has no parent, it is a top level bone
				topLevelBones.add(boneGroups.get(bone.getName()));
			} else {
				// otherwise, find the parent bone group and add this bone to its children
				RawBoneGroup parentBone = boneGroups.get(bone.getParent());

				if (parentBone == null) {
					// but if the parent bone doesn't exist, throw an exception
					throw new GeckoLibException(location, "Bone '" + bone.getName() + "' refers to parent '" + bone.getParent() + "' which does not exist.");
				}

				parentBone.addChild(boneGroups.get(bone.getName()));
			}
		}
		return new RawGeometryTree(topLevelBones, geometry.getProperties());
	}

	public static boolean hasParent(Bone bone) {
		return bone.getParent() != null;
	}

}
