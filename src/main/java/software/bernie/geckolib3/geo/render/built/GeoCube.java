package software.bernie.geckolib3.geo.render.built;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import software.bernie.geckolib3.geo.raw.pojo.*;
import software.bernie.geckolib3.util.VectorUtils;

public class GeoCube {
	public final GeoQuad[] quads = new GeoQuad[6];
	public final Vector3f pivot;
	public final Vector3f rotation;
	public final Vector3f size = new Vector3f();

	public GeoCube(Cube cubeIn, ModelProperties properties, Double boneInflate, Boolean mirror) {
		UvUnion uvUnion = cubeIn.getUv();
		boolean cubeMirror = cubeIn.getMirror() != null && cubeIn.getMirror();

		float texHeight = properties.getTextureHeight().floatValue();
		float texWidth = properties.getTextureWidth().floatValue();

		Vec3 size = VectorUtils.fromArray(cubeIn.getSize());
		Vec3 origin = VectorUtils.fromArray(cubeIn.getOrigin());
		origin = new Vec3(-(origin.x + size.x) / 16, origin.y / 16, origin.z / 16);

		size = size.multiply(0.0625f, 0.0625, 0.0625f);

		Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getRotation()));
		rotation.mul(-1, -1, 1);

		rotation.setX((float) Math.toRadians(rotation.x()));
		rotation.setY((float) Math.toRadians(rotation.y()));
		rotation.setZ((float) Math.toRadians(rotation.z()));

		Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(cubeIn.getPivot()));
		pivot.mul(-1, 1, 1);

		this.pivot = pivot;
		this.rotation = rotation;

		//               P7                       P8
		//               - - - - - - - - - - - - -
		//               | \                     | \
		//               |   \                   |   \
		//               |     \                 |     \
		//               |       \               |       \
		//           Y   |         \             |         \
		//               |           \           |           \
		//               |             \ P3      |             \  P4
		//               |               - - - - - - - - - - - - -
		//               |               |       |               |
		//               |               |       |               |
		//               |               |       |               |
		//            P5 - - - - - - - - | - - - - P6            |
		//                 \             |         \             |
		//                   \           |           \           |
		//                     \         |             \         |
		//                  X    \       |               \       |
		//                         \     |                 \     |
		//                           \   |                   \   |
		//                             \ |                     \ |
		//                               - - - - - - - - - - - - -
		//                              P1                        P2
		//                                          Z
		//  this drawing corresponds to the points declared below
		//

		// Making all 8 points of the cube using the origin (where the Z, X, and Y
		// values are smallest) and offseting each point by the right size values
		double inflate = getInflation(cubeIn, boneInflate);
		double x0 = origin.x - inflate;
		double x1 = origin.x + size.x + inflate;
		double y0 = origin.y - inflate;
		double y1 = origin.y + size.y + inflate;
		double z0 = origin.z - inflate;
		double z1 = origin.z + size.z + inflate;

		GeoVertex P1 = new GeoVertex(x0, y0, z0);
		GeoVertex P2 = new GeoVertex(x0, y0, z1);
		GeoVertex P3 = new GeoVertex(x0, y1, z0);
		GeoVertex P4 = new GeoVertex(x0, y1, z1);
		GeoVertex P5 = new GeoVertex(x1, y0, z0);
		GeoVertex P6 = new GeoVertex(x1, y0, z1);
		GeoVertex P7 = new GeoVertex(x1, y1, z0);
		GeoVertex P8 = new GeoVertex(x1, y1, z1);

		GeoQuad quadWest;
		GeoQuad quadEast;
		GeoQuad quadNorth;
		GeoQuad quadSouth;
		GeoQuad quadUp;
		GeoQuad quadDown;

		if (!uvUnion.isBoxUV) {
			UvFaces faces = uvUnion.faceUV;
			FaceUv west = faces.getWest();
			FaceUv east = faces.getEast();
			FaceUv north = faces.getNorth();
			FaceUv south = faces.getSouth();
			FaceUv up = faces.getUp();
			FaceUv down = faces.getDown();
			// Pass in vertices starting from the top right corner, then going
			// counter-clockwise
			quadNorth = getQuad(P5, P1, P7, P3, cubeMirror, texWidth, texHeight, north, Direction.NORTH);
			quadSouth = getQuad(P2, P6, P4, P8, cubeMirror, texWidth, texHeight, south, Direction.SOUTH);
			quadUp = getQuad(P7, P3, P8, P4, cubeMirror, texWidth, texHeight, up, Direction.UP);
			quadDown = getQuad(P6, P2, P5, P1, cubeMirror, texWidth, texHeight, down, Direction.DOWN);

			if (cubeMirror || mirror == Boolean.TRUE) {
				quadWest = getQuad(P6, P5, P8, P7, cubeMirror, texWidth, texHeight, west, Direction.WEST);
				quadEast = getQuad(P1, P2, P3, P4, cubeMirror, texWidth, texHeight, east, Direction.EAST);
			} else {
				quadWest = getQuad(P1, P2, P3, P4, false, texWidth, texHeight, west, Direction.WEST);
				quadEast = getQuad(P6, P5, P8, P7, false, texWidth, texHeight, east, Direction.EAST);
			}
		} else {
			double[] UV = cubeIn.getUv().boxUVCoords;
			Vec3 UVSize = VectorUtils.fromArray(cubeIn.getSize());
			UVSize = new Vec3(Math.floor(UVSize.x), Math.floor(UVSize.y), Math.floor(UVSize.z));

			quadNorth = new GeoQuad(new GeoVertex[] { P3, P7, P5, P1 },
					new double[] { UV[0] + UVSize.z, UV[1] + UVSize.z }, new double[] { UVSize.x, UVSize.y },
					texWidth, texHeight, cubeMirror, Direction.NORTH);
			quadSouth = new GeoQuad(new GeoVertex[] { P8, P4, P2, P6 },
					new double[] { UV[0] + UVSize.z + UVSize.x + UVSize.z, UV[1] + UVSize.z },
					new double[] { UVSize.x, UVSize.y }, texWidth, texHeight, cubeMirror, Direction.SOUTH);
			quadUp = new GeoQuad(new GeoVertex[] { P4, P8, P7, P3 }, new double[] { UV[0] + UVSize.z, UV[1] },
					new double[] { UVSize.x, UVSize.z }, texWidth, texHeight, cubeMirror, Direction.UP);
			quadDown = new GeoQuad(new GeoVertex[] { P1, P5, P6, P2 },
					new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
					new double[] { UVSize.x, -UVSize.z }, texWidth, texHeight, cubeMirror, Direction.DOWN);

			if (cubeMirror == Boolean.TRUE || mirror == Boolean.TRUE) {
				quadWest = new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 },
						new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, texWidth, texHeight, cubeMirror, Direction.WEST);
				quadEast = new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 }, new double[] { UV[0], UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, texWidth, texHeight, cubeMirror, Direction.EAST);
			} else {
				quadWest = new GeoQuad(new GeoVertex[] { P4, P3, P1, P2 },
						new double[] { UV[0] + UVSize.z + UVSize.x, UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, texWidth, texHeight, false, Direction.WEST);
				quadEast = new GeoQuad(new GeoVertex[] { P7, P8, P6, P5 }, new double[] { UV[0], UV[1] + UVSize.z },
						new double[] { UVSize.z, UVSize.y }, texWidth, texHeight, false, Direction.EAST);
			}
		}

		quads[0] = quadWest;
		quads[1] = quadEast;
		quads[2] = quadNorth;
		quads[3] = quadSouth;
		quads[4] = quadUp;
		quads[5] = quadDown;
	}

	private static GeoQuad getQuad(GeoVertex v0, GeoVertex v1, GeoVertex v2, GeoVertex v3, boolean mirror,
			float texWidth, float texHeight, FaceUv face, Direction dir) {
		if (face == null) {
			return null;
		} else {
			return new GeoQuad(new GeoVertex[]{v0, v1, v2, v3}, face.getUv(), face.getUvSize(), texWidth, texHeight,
					mirror, dir);
		}
	}

	private static double getInflation(Cube cubeIn, Double boneInflate) {
		if (cubeIn.getInflate() == null) {
			if (boneInflate == null) return 0;
			return boneInflate;
		} else {
			return cubeIn.getInflate() / 16;
		}
	}
}
