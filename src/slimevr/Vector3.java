package slimevr;

import java.io.Serializable;

public class Vector3 implements Serializable {
    public static final Vector3 NULL = new Vector3(0f, 0f, 0f);
    public static final Vector3 POS_X = new Vector3(1f, 0f, 0f);
    public static final Vector3 POS_Y = new Vector3(0f, 1f, 0f);
    public static final Vector3 POS_Z = new Vector3(0f, 0f, 1f);
    public static final Vector3 NEG_X = new Vector3(-1f, 0f, 0f);
    public static final Vector3 NEG_Y = new Vector3(0f, -1f, 0f);
    public static final Vector3 NEG_Z = new Vector3(0f, 0f, -1f);

    private final float x;
    private final float y;
    private final float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 add(Vector3 that) {
        return new Vector3(this.x + that.x, this.y + that.y, this.z + that.z);
    }

    public Vector3 subtract(Vector3 that) {
        return new Vector3(this.x - that.x, this.y - that.y, this.z - that.z);
    }

    public float dot(Vector3 that) {
        return this.x * that.x + this.y * that.y + this.z * that.z;
    }

    public float lenSq() {
        return x * x + y * y + z * z;
    }

    public Vector3 cross(Vector3 that) {
        return new Vector3(
                this.y * that.z - this.z * that.y,
                this.z * that.x - this.x * that.z,
                this.x * that.y - this.y * that.x
        );
    }

    public Vector3 hadamard(Vector3 that) {
        return new Vector3(
                this.x * that.x,
                this.y * that.y,
                this.z * that.z
        );
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public Vector3 unit() {
        float m = length();
        return (m == 0f) ? NULL : this.divide(m);
    }

    public Vector3 multiply(float scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 divide(float scalar) {
        return new Vector3(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public float angleTo(Vector3 that) {
        return (float) Math.atan2(this.cross(that).length(), this.dot(that));
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Vector3 scale(float scalar, Vector3 vector) {
        return vector.multiply(scalar);
    }

    public static Vector3 divide(float scalar, Vector3 vector) {
        return vector.divide(scalar);
    }
}