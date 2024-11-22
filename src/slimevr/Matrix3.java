package slimevr;


import java.io.Serializable;

public class Matrix3 implements Serializable {
    private final float xx, yx, zx;
    private final float xy, yy, zy;
    private final float xz, yz, zz;

    public static final Matrix3 NULL = new Matrix3(
            0f, 0f, 0f,
            0f, 0f, 0f,
            0f, 0f, 0f
    );

    public static final Matrix3 IDENTITY = new Matrix3(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
    );

    public Matrix3(float xx, float yx, float zx, float xy, float yy, float zy, float xz, float yz, float zz) {
        this.xx = xx; this.yx = yx; this.zx = zx;
        this.xy = xy; this.yy = yy; this.zy = zy;
        this.xz = xz; this.yz = yz; this.zz = zz;
    }

    public Matrix3(Vector3 x, Vector3 y, Vector3 z) {
        this(x.getX(), y.getX(), z.getX(), x.getY(), y.getY(), z.getY(), x.getZ(), y.getZ(), z.getZ());
    }

    // Column getters
    public Vector3 getX() { return new Vector3(xx, xy, xz); }
    public Vector3 getY() { return new Vector3(yx, yy, yz); }
    public Vector3 getZ() { return new Vector3(zx, zy, zz); }

    // Row getters
    public Vector3 getXRow() { return new Vector3(xx, yx, zx); }
    public Vector3 getYRow() { return new Vector3(xy, yy, zy); }
    public Vector3 getZRow() { return new Vector3(xz, yz, zz); }

    public Matrix3 unaryMinus() {
        return new Matrix3(
                -xx, -yx, -zx,
                -xy, -yy, -zy,
                -xz, -yz, -zz
        );
    }

    public Matrix3 add(Matrix3 that) {
        return new Matrix3(
                this.xx + that.xx, this.yx + that.yx, this.zx + that.zx,
                this.xy + that.xy, this.yy + that.yy, this.zy + that.zy,
                this.xz + that.xz, this.yz + that.yz, this.zz + that.zz
        );
    }

    public Matrix3 subtract(Matrix3 that) {
        return new Matrix3(
                this.xx - that.xx, this.yx - that.yx, this.zx - that.zx,
                this.xy - that.xy, this.yy - that.yy, this.zy - that.zy,
                this.xz - that.xz, this.yz - that.yz, this.zz - that.zz
        );
    }

    public Matrix3 multiply(float scalar) {
        return new Matrix3(
                this.xx * scalar, this.yx * scalar, this.zx * scalar,
                this.xy * scalar, this.yy * scalar, this.zy * scalar,
                this.xz * scalar, this.yz * scalar, this.zz * scalar
        );
    }

    public Vector3 multiply(Vector3 vector) {
        return new Vector3(
                this.xx * vector.getX() + this.yx * vector.getY() + this.zx * vector.getZ(),
                this.xy * vector.getX() + this.yy * vector.getY() + this.zy * vector.getZ(),
                this.xz * vector.getX() + this.yz * vector.getY() + this.zz * vector.getZ()
        );
    }

    public Matrix3 multiply(Matrix3 that) {
        return new Matrix3(
                this.xx * that.xx + this.yx * that.xy + this.zx * that.xz,
                this.xx * that.yx + this.yx * that.yy + this.zx * that.yz,
                this.xx * that.zx + this.yx * that.zy + this.zx * that.zz,
                this.xy * that.xx + this.yy * that.xy + this.zy * that.xz,
                this.xy * that.yx + this.yy * that.yy + this.zy * that.yz,
                this.xy * that.zx + this.yy * that.zy + this.zy * that.zz,
                this.xz * that.xx + this.yz * that.xy + this.zz * that.xz,
                this.xz * that.yx + this.yz * that.yy + this.zz * that.yz,
                this.xz * that.zx + this.yz * that.zy + this.zz * that.zz
        );
    }

    public float normSq() {
        return xx * xx + yx * yx + zx * zx +
                xy * xy + yy * yy + zy * zy +
                xz * xz + yz * yz + zz * zz;
    }

    public float norm() {
        return (float) Math.sqrt(normSq());
    }

    public float det() {
        return (xz * yx - xx * yz) * zy +
                (xx * yy - xy * yx) * zz +
                (xy * yz - xz * yy) * zx;
    }

    public float trace() {
        return xx + yy + zz;
    }

    public Matrix3 transpose() {
        return new Matrix3(
                xx, xy, xz,
                yx, yy, yz,
                zx, zy, zz
        );
    }

    public Matrix3 inv() {
        float determinant = det();
        return new Matrix3(
                (yy * zz - yz * zy) / determinant, (yz * zx - yx * zz) / determinant, (yx * zy - yy * zx) / determinant,
                (xz * zy - xy * zz) / determinant, (xx * zz - xz * zx) / determinant, (xy * zx - xx * zy) / determinant,
                (xy * yz - xz * yy) / determinant, (xz * yx - xx * yz) / determinant, (xx * yy - xy * yx) / determinant
        );
    }

    public Matrix3 divide(float scalar) {
        return this.multiply(1f / scalar);
    }

    public Matrix3 divide(Matrix3 that) {
        return this.multiply(that.inv());
    }

    public Matrix3 invTranspose() {
        float determinant = det();
        return new Matrix3(
                (yy * zz - yz * zy) / determinant, (xz * zy - xy * zz) / determinant, (xy * yz - xz * yy) / determinant,
                (yz * zx - yx * zz) / determinant, (xx * zz - xz * zx) / determinant, (xz * yx - xx * yz) / determinant,
                (yx * zy - yy * zx) / determinant, (xy * zx - xx * zy) / determinant, (xx * yy - xy * yx) / determinant
        );
    }

    public Matrix3 orthonormalize() {
        if (this.det() <= 0f) {
            throw new IllegalArgumentException("Attempt to convert non-positive determinant matrix to rotation matrix");
        }

        Matrix3 curMat = this;
        float curDet = Float.POSITIVE_INFINITY;

        for (int i = 0; i < 100; i++) {
            Matrix3 newMat = curMat.add(curMat.invTranspose()).divide(2f);
            float newDet = Math.abs(newMat.det());
            if (newDet >= curDet) return curMat;
            if (newDet <= 1.0000001f) return newMat;
            curMat = newMat;
            curDet = newDet;
        }

        return curMat;
    }

    public Matrix3 average(Matrix3... others) {
        float count = 1f;
        Matrix3 sum = this;
        for (Matrix3 other : others) {
            count += 1f;
            sum = sum.add(other);
        }
        return sum.divide(count).orthonormalize();
    }

    public Matrix3 lerp(Matrix3 that, float t) {
        return this.multiply(1f - t).add(that.multiply(t));
    }

    public Quaternion toQuaternionAssumingOrthonormal() {
        if (yy > -zz && zz > -xx && xx > -yy) {
            return new Quaternion(1f + xx + yy + zz, yz - zy, zx - xz, xy - yx).unit();
        } else if (xx > yy && xx > zz) {
            return new Quaternion(yz - zy, 1f + xx - yy - zz, xy + yx, xz + zx).unit();
        } else if (yy > zz) {
            return new Quaternion(zx - xz, xy + yx, 1f - xx + yy - zz, yz + zy).unit();
        } else {
            return new Quaternion(xy - yx, xz + zx, yz + zy, 1f - xx - yy + zz).unit();
        }
    }

    public Quaternion toQuaternion() {
        return orthonormalize().toQuaternionAssumingOrthonormal();
    }

    public EulerAngles toEulerAnglesAssumingOrthonormal(EulerOrders order) {
        final float ETA = 1.5707964f;
        switch (order) {
            case XYZ: {
                float kc = (float) Math.sqrt(zy * zy + zz * zz);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.XYZ,
                            (float) Math.atan2(yz, yy),
                            sign(ETA, zx),
                            0f
                    );
                }

                return new EulerAngles(
                        EulerOrders.XYZ,
                        (float) Math.atan2(-zy, zz),
                        (float) Math.atan2(zx, kc),
                        (float) Math.atan2(xy * zz - xz * zy, yy * zz - yz * zy)
                );
            }
            case YZX: {
                float kc = (float) Math.sqrt(xx * xx + xz * xz);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.YZX,
                            0f,
                            (float) Math.atan2(zx, zz),
                            sign(ETA, xy)
                    );
                }

                return new EulerAngles(
                        EulerOrders.YZX,
                        (float) Math.atan2(xx * yz - xz * yx, xx * zz - xz * zx),
                        (float) Math.atan2(-xz, xx),
                        (float) Math.atan2(xy, kc)
                );
            }
            case ZXY: {
                float kc = (float) Math.sqrt(yy * yy + yx * yx);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.ZXY,
                            sign(ETA, yz),
                            0f,
                            (float) Math.atan2(xy, xx)
                    );
                }

                return new EulerAngles(
                        EulerOrders.ZXY,
                        (float) Math.atan2(yz, kc),
                        (float) Math.atan2(yy * zx - yx * zy, yy * xx - yx * xy),
                        (float) Math.atan2(-yx, yy)
                );
            }
            case ZYX: {
                float kc = (float) Math.sqrt(xy * xy + xx * xx);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.ZYX,
                            0f,
                            sign(ETA, -xz),
                            (float) Math.atan2(-yx, yy)
                    );
                }

                return new EulerAngles(
                        EulerOrders.ZYX,
                        (float) Math.atan2(zx * xy - zy * xx, yy * xx - yx * xy),
                        (float) Math.atan2(-xz, kc),
                        (float) Math.atan2(xy, xx)
                );
            }
            case YXZ: {
                float kc = (float) Math.sqrt(zx * zx + zz * zz);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.YXZ,
                            sign(ETA, -zy),
                            (float) Math.atan2(-xz, xx),
                            0f
                    );
                }

                return new EulerAngles(
                        EulerOrders.YXZ,
                        (float) Math.atan2(-zy, kc),
                        (float) Math.atan2(zx, zz),
                        (float) Math.atan2(yz * zx - yx * zz, xx * zz - xz * zx)
                );
            }
            case XZY: {
                float kc = (float) Math.sqrt(yz * yz + yy * yy);
                if (kc < 1e-7f) {
                    return new EulerAngles(
                            EulerOrders.XZY,
                            (float) Math.atan2(-zy, zz),
                            0f,
                            sign(ETA, -yx)
                    );
                }

                return new EulerAngles(
                        EulerOrders.XZY,
                        (float) Math.atan2(yz, yy),
                        (float) Math.atan2(xy * yz - xz * yy, zz * yy - zy * yz),
                        (float) Math.atan2(-yx, kc)
                );
            }
            default:
                throw new IllegalArgumentException("Unsupported Euler Order: " + order);
        }
    }

    // Orthonormalizes the matrix, then returns the euler angles
    public EulerAngles toEulerAngles(EulerOrders order) {
        return orthonormalize().toEulerAnglesAssumingOrthonormal(order);
    }


    private float sign(float value, float signValue) {
        return (signValue >= 0) ? Math.abs(value) : -Math.abs(value);
    }

}

