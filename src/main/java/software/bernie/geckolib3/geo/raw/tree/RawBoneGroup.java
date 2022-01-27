package software.bernie.geckolib3.geo.raw.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import software.bernie.geckolib3.geo.raw.pojo.Bone;

public class RawBoneGroup {
	private final List<RawBoneGroup> children = new ArrayList<>();
	public final Bone selfBone;

	private RawBoneGroup(Bone bone) {
		this.selfBone = bone;
	}

	static Map<String, RawBoneGroup> createBoneGroups(Bone[] bones) {
		ImmutableMap.Builder<String, RawBoneGroup> flatList = ImmutableMap.builder();
		for (Bone bone : bones) {
			flatList.put(bone.getName(), new RawBoneGroup(bone));
		}
		return flatList.build();
	}

	public void addChild(RawBoneGroup bone) {
		children.add(bone);
	}

	public Collection<RawBoneGroup> getChildren() {
		return children;
	}
}
