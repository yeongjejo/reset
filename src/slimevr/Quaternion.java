package slimevr;

public class Quaternion {
    public static final Quaternion NULL = new Quaternion(0f, 0f, 0f, 0f);
    public static final Quaternion IDENTITY = new Quaternion(1f, 0f, 0f, 0f);
    public static final Quaternion I = new Quaternion(0f, 1f, 0f, 0f);
    public static final Quaternion J = new Quaternion(0f, 0f, 1f, 0f);
    public static final Quaternion K = new Quaternion(0f, 0f, 0f, 1f);

    private float w;
    private float x;
    private float y;
    private float z;

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(float w, Vector3 xyz) {
        this(w, xyz.getX(), xyz.getY(), xyz.getZ());
    }

    public Quaternion() {

    }

    public float[] getXyz() {
        return new float[]{x, y, z};
    }

    public Quaternion getRe() {
        return new Quaternion(w, 0f, 0f, 0f);
    }

    public Quaternion getIm() {
        return new Quaternion(0f, x, y, z);
    }

    public Quaternion negate() {
        return new Quaternion(-w, -x, -y, -z);
    }

    public Quaternion add(Quaternion that) {
        return new Quaternion(this.w + that.w, this.x + that.x, this.y + that.y, this.z + that.z);
    }

    public Quaternion subtract(Quaternion that) {
        return new Quaternion(this.w - that.w, this.x - that.x, this.y - that.y, this.z - that.z);
    }

    public float dot(Quaternion that) {
        return this.w * that.w + this.x * that.x + this.y * that.y + this.z * that.z;
    }

    public float lenSq() {
        return w * w + x * x + y * y + z * z;
    }

    public float len() {
        return (float) Math.sqrt(lenSq());
    }

    public Quaternion unit() {
        float m = len();
        return (m == 0f) ? NULL : this.divide(m);
    }

    public Vector3 sandwich(Vector3 that) {
        return this.multiply(new Quaternion(0f, that))
                .divide(this)
                .toVector3();
    }

    public Quaternion multiply(float scalar) {
        return new Quaternion(this.w * scalar, this.x * scalar, this.y * scalar, this.z * scalar);
    }


    public Quaternion multiply(Quaternion that) {
        return new Quaternion(
                this.w * that.w - this.x * that.x - this.y * that.y - this.z * that.z,
                this.x * that.w + this.w * that.x - this.z * that.y + this.y * that.z,
                this.y * that.w + this.z * that.x + this.w * that.y - this.x * that.z,
                this.z * that.w - this.y * that.x + this.x * that.y + this.w * that.z

        );

    }

    public Vector3 xyz(){
        return new Vector3(x, y, z);
    }

    public Quaternion project(Vector3 v) {
        // dot() 메소드는 Vector3 클래스에서 정의되어 있어야 합니다.
        float dotProduct = xyz().dot(v);
        float lenSquared = v.lenSq();
        Vector3 projectedVector = new Vector3(dotProduct / lenSquared * v.getX(),
                dotProduct / lenSquared * v.getY(),
                dotProduct / lenSquared * v.getZ());

        return new Quaternion(w, projectedVector);
    }

    public Quaternion fromRotationVector(Vector3 v) {

        return new Quaternion(0f, v.divide(2f)).exp();
    }

    public Quaternion inv() {
        float lenSq = lenSq();
        return new Quaternion(w / lenSq, -x / lenSq, -y / lenSq, -z / lenSq);
    }

    public Quaternion divide(float scalar) {
        return this.multiply(1f / scalar);
    }

    public Quaternion divide(Quaternion that) {
        return this.multiply(that.inv());
    }

    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    public Quaternion log() {
        float co = w;
        float si = len();
        float len = len();

        if (si == 0f) {
            return new Quaternion((float) Math.log(len), getXyz()[0] / w, getXyz()[1] / w, getXyz()[2] / w);
        }

        float ang = (float) Math.atan2(si, co);
        return new Quaternion((float) Math.log(len), ang / si * getXyz()[0], ang / si * getXyz()[1], ang / si * getXyz()[2]);
    }

    public Quaternion exp() {
        float ang = len();
        float len = (float) Math.exp(w);

        if (ang == 0f) {
            return new Quaternion(len, len * getXyz()[0], len * getXyz()[1], len * getXyz()[2]);
        }

        float co = (float) Math.cos(ang);
        float si = (float) Math.sin(ang);
        return new Quaternion(len * co, len * si / ang * getXyz()[0], len * si / ang * getXyz()[1], len * si / ang * getXyz()[2]);
    }

    public Quaternion pow(float t) {
        return log().multiply(t).exp();
    }

    public Quaternion twinNearest(Quaternion that) {
        return (this.dot(that) < 0f) ? this.negate() : this;
    }

    public Quaternion interpQ(Quaternion that, float t) {
        if (t == 0f) {
            return this;
        } else if (t == 1f) {
            return that;
        } else if (t < 0.5f) {
            return that.divide(this).pow(t).multiply(this);
        } else {
            return this.divide(that).pow(1f - t).multiply(that);
        }
    }

    public Quaternion interpR(Quaternion that, float t) {
        return this.interpQ(that.twinNearest(this), t);
    }

    public float angleAboutQ(float[] u) {
        float si = u[0] * x + u[1] * y + u[2] * z; // Dot product
        float co = (float) Math.sqrt(u[0] * u[0] + u[1] * u[1] + u[2] * u[2]) * w;
        return (float) Math.atan2(si, co);
    }

    public float[] toRotationVector() {
        Quaternion nearest = twinNearest(IDENTITY);
        float[] log = nearest.log().getXyz();
        return new float[]{2f * log[0], 2f * log[1], 2f * log[2]};
    }

    public Vector3 toVector3() {
        return new Vector3(x, y, z); // 쿼터니언에서 벡터로 변환
    }


    public Matrix3 toMatrix() {
        float d = lenSq();

        return new Matrix3(
                (w * w + x * x - y * y - z * z) / d,
                2f * (x * y - w * z) / d,
                2f * (w * y + x * z) / d,
                2f * (x * y + w * z) / d,
                (w * w - x * x + y * y - z * z) / d,
                2f * (y * z - w * x) / d,
                2f * (x * z - w * y) / d,
                2f * (w * x + y * z) / d,
                (w * w - x * x - y * y + z * z) / d
        );
    }


    public EulerAngles toEulerAngles(EulerOrders order) {
        return this.toMatrix().toEulerAnglesAssumingOrthonormal(order);
    }

    public float getW() {
        return w;
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

    @Override
    public String toString() {
        return "Quaternion{" +
                "w=" + w +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }


    // Add additional methods as needed
}
