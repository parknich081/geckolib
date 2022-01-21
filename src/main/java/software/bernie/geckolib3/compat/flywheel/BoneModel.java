package software.bernie.geckolib3.compat.flywheel;

import java.nio.ByteBuffer;

import com.jozufozu.flywheel.api.vertex.VertexList;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.core.Formats;
import com.jozufozu.flywheel.core.model.Model;
import com.jozufozu.flywheel.core.vertex.PosTexNormalVertex;
import com.jozufozu.flywheel.core.vertex.PosTexNormalVertexListUnsafe;
import com.jozufozu.flywheel.core.vertex.PosTexNormalWriterUnsafe;
import com.jozufozu.flywheel.util.RenderMath;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.util.RenderUtils;

public class BoneModel implements Model {

	public final GeoBone bone;

	private final int vertexCount;

	private final VertexList vertexList;

	public BoneModel(GeoBone bone) {
		this.bone = bone;

		vertexCount = bone.childCubes.size() * 6 * 4; // 6 quads per cube, 4 vertices per quad

		ByteBuffer byteBuffer = MemoryTracker.create(vertexCount * Formats.POS_TEX_NORMAL.getStride());

		var writer = Formats.POS_TEX_NORMAL.createWriter(byteBuffer);

		for (GeoCube cube : bone.childCubes) {
			bufferCube(writer, cube);
		}

		vertexList = writer.intoReader();
	}

	@Override
	public String name() {
		return bone.name;
	}

	@Override
	public VertexList getReader() {
		return vertexList;
	}

	@Override
	public int vertexCount() {
		return vertexCount;
	}

	public void bufferCube(PosTexNormalWriterUnsafe writer, GeoCube cube) {

		PoseStack stack = new PoseStack();

		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.last().normal();
		Matrix4f matrix4f = stack.last().pose();

		for (GeoQuad quad : cube.quads) {
			if (quad == null) {
				continue;
			}
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);

			/*
			 * Fix shading dark shading for flat cubes
			 */
			if ((cube.size.y() == 0 || cube.size.z() == 0) && normal.x() < 0) {
				normal.mul(-1, 1, 1);
			}
			if ((cube.size.x() == 0 || cube.size.z() == 0) && normal.y() < 0) {
				normal.mul(1, -1, 1);
			}
			if ((cube.size.x() == 0 || cube.size.y() == 0) && normal.z() < 0) {
				normal.mul(1, 1, -1);
			}

			for (GeoVertex vertex : quad.vertices) {
				Vector4f pos = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(),
						1.0F);
				pos.transform(matrix4f);

				writer.putVertex(pos.x(),
						pos.y(),
						pos.z(),
						normal.x(),
						normal.y(),
						normal.z(),
						vertex.textureU,
						vertex.textureV);
			}
		}
	}
}
