package slimevr;

import com.google.gson.annotations.SerializedName;
import slimevr.EulerOrders;
import slimevr.Matrix3;
import slimevr.Quaternion;

import java.io.Serializable;
import static java.lang.Math.cos;
import static java.lang.Math.sin;



public class EulerAngles implements Serializable {
    @SerializedName("order")
    private final EulerOrders order;

    @SerializedName("x")
    private final float x;

    @SerializedName("y")
    private final float y;

    @SerializedName("z")
    private final float z;

    public EulerAngles(EulerOrders order, float x, float y, float z) {
        this.order = order;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EulerOrders getOrder() {
        return order;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    /**
     * creates a quaternion which represents the same rotation as this EulerAngles
     *
     * @return the quaternion
     */
    public Quaternion toQuaternion() {
        float cX = (float) cos(x / 2f);
        float cY = (float) cos(y / 2f);
        float cZ = (float) cos(z / 2f);
        float sX = (float) sin(x / 2f);
        float sY = (float) sin(y / 2f);
        float sZ = (float) sin(z / 2f);

        switch (order) {
            case XYZ:
                return new Quaternion(
                        cX * cY * cZ - sX * sY * sZ,
                        cY * cZ * sX + cX * sY * sZ,
                        cX * cZ * sY - cY * sX * sZ,
                        cZ * sX * sY + cX * cY * sZ
                );

            case YZX:
                return new Quaternion(
                        cX * cY * cZ - sX * sY * sZ,
                        cY * cZ * sX + cX * sY * sZ,
                        cX * cZ * sY + cY * sX * sZ,
                        cX * cY * sZ - cZ * sX * sY
                );

            case ZXY:
                return new Quaternion(
                        cX * cY * cZ - sX * sY * sZ,
                        cY * cZ * sX - cX * sY * sZ,
                        cX * cZ * sY + cY * sX * sZ,
                        cZ * sX * sY + cX * cY * sZ
                );

            case ZYX:
                return new Quaternion(
                        cX * cY * cZ + sX * sY * sZ,
                        cY * cZ * sX - cX * sY * sZ,
                        cX * cZ * sY + cY * sX * sZ,
                        cX * cY * sZ - cZ * sX * sY
                );

            case YXZ:
                return new Quaternion(
                        cX * cY * cZ + sX * sY * sZ,
                        cY * cZ * sX + cX * sY * sZ,
                        cX * cZ * sY - cY * sX * sZ,
                        cX * cY * sZ - cZ * sX * sY
                );

            case XZY:
                return new Quaternion(
                        cX * cY * cZ + sX * sY * sZ,
                        cY * cZ * sX - cX * sY * sZ,
                        cX * cZ * sY - cY * sX * sZ,
                        cZ * sX * sY + cX * cY * sZ
                );

            default:
                throw new IllegalArgumentException("Unknown Euler order: " + order);
        }
    }

    /**
     * creates a matrix which represents the same rotation as this EulerAngles
     *
     * @return the matrix
     */
    public Matrix3 toMatrix() {
        float cX = (float) cos(x);
        float cY = (float) cos(y);
        float cZ = (float) cos(z);
        float sX = (float) sin(x);
        float sY = (float) sin(y);
        float sZ = (float) sin(z);

        switch (order) {
            case XYZ:
                return new Matrix3(
                        cY * cZ, -cY * sZ, sY,
                        cZ * sX * sY + cX * sZ, cX * cZ - sX * sY * sZ, -cY * sX,
                        sX * sZ - cX * cZ * sY, cZ * sX + cX * sY * sZ, cX * cY
                );

            case YZX:
                return new Matrix3(
                        cY * cZ, sX * sY - cX * cY * sZ, cX * sY + cY * sX * sZ,
                        sZ, cX * cZ, -cZ * sX,
                        -cZ * sY, cY * sX + cX * sY * sZ, cX * cY - sX * sY * sZ
                );

            case ZXY:
                return new Matrix3(
                        cY * cZ - sX * sY * sZ, -cX * sZ, cZ * sY + cY * sX * sZ,
                        cZ * sX * sY + cY * sZ, cX * cZ, sY * sZ - cY * cZ * sX,
                        -cX * sY, sX, cX * cY
                );

            case ZYX:
                return new Matrix3(
                        cY * cZ, cZ * sX * sY - cX * sZ, cX * cZ * sY + sX * sZ,
                        cY * sZ, cX * cZ + sX * sY * sZ, cX * sY * sZ - cZ * sX,
                        -sY, cY * sX, cX * cY
                );

            case YXZ:
                return new Matrix3(
                        cY * cZ + sX * sY * sZ, cZ * sX * sY - cY * sZ, cX * sY,
                        cX * sZ, cX * cZ, -sX,
                        cY * sX * sZ - cZ * sY, cY * cZ * sX + sY * sZ, cX * cY
                );

            case XZY:
                return new Matrix3(
                        cY * cZ, -sZ, cZ * sY,
                        sX * sY + cX * cY * sZ, cX * cZ, cX * sY * sZ - cY * sX,
                        cY * sX * sZ - cX * sY, cZ * sX, cX * cY + sX * sY * sZ
                );

            default:
                throw new IllegalArgumentException("Unknown Euler order: " + order);
        }
    }

    @Override
    public String toString() {
        return "EulerAngles{" +
                "order=" + order +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}