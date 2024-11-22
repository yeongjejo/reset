import com.google.gson.Gson;
import slimevr.EulerOrders;
import slimevr.Quaternion;
import slimevr.QuaternionMovingAverage;

public class SensorData implements Cloneable {
    private int id;
    private double w;
    private double x;
    private double y;
    private double z;

    private double filterW = 1.0;
    private double filterX = 0.0;
    private double filterY = 0.0;
    private double filterZ = 0.0;

    private double eulerX;
    private double eulerY;
    private double eulerZ;
    
    private SensorPart sensorPart; // 센서 부착 위치

    private NewReset newReset;

    private QuaternionMovingAverage movingAverage = new QuaternionMovingAverage();

    public SensorData(int id, double w, double x, double y, double z, double eulerX, double eulerY, double eulerZ, SensorPart sensorPart, NewReset newReset) {
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        movingAverage.addQuaternion(Quaternion.IDENTITY, false);
        this.id = id;
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.eulerX = eulerX;
        this.eulerY = eulerY;
        this.eulerZ = eulerZ;
        this.sensorPart = sensorPart;
        this.newReset = newReset;
    }


    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getEulerX() {
        return eulerX;
    }

    public void setEulerX(double eulerX) {
        this.eulerX = eulerX;
    }

    public double getEulerY() {
        return eulerY;
    }

    public void setEulerY(double eulerY) {
        this.eulerY = eulerY;
    }

    public double getEulerZ() {
        return eulerZ;
    }

    public void setEulerZ(double eulerZ) {
        this.eulerZ = eulerZ;
    }

    public SensorPart getSensorPart() {
        return sensorPart;
    }

    public void SensorPart(SensorPart sensorPart) {
        this.sensorPart = sensorPart;
    }

    public void setSensorPart(SensorPart sensorPart) {
        this.sensorPart = sensorPart;
    }

    public QuaternionMovingAverage getMovingAverage() {
        return movingAverage;
    }

    public void setMovingAverage(QuaternionMovingAverage movingAverage) {
        this.movingAverage = movingAverage;
    }

    public Quaternion getRawRotation() {
        return new Quaternion((float) this.w, (float) this.x, (float) this.y, (float) this.z);
    }


    public Quaternion getRotation() {

//        if (this.sensorPart == SensorPart.RIGHT_UPPER_LEG) {
//            System.out.println("0차 : " + getRawRotation());
//        }
        Quaternion rotation = movingAverage.getFilteredQuaternion();
//        if (this.sensorPart == SensorPart.RIGHT_UPPER_LEG) {
//            System.out.println("1차 : " + rotation.toString());
//            System.out.println("1차 : " + (rotation.getW() - w) + ", " + (rotation.getX() - x) + ", " + (rotation.getY() - y) + ", " + (rotation.getZ() - z) + ", ");
//            System.out.println("-------------------------------");
//        }
        rotation = getReferenceAdjustedDriftRotationFrom(rotation);
//        Quaternion rotation = getReferenceAdjustedDriftRotationFrom(getRawRotation());
//        System.out.println("2차 : " + rotation.toString());



        filterW = rotation.getW();
        filterX = rotation.getX();
        filterY = rotation.getY();
        filterZ = rotation.getZ();



        rotation.toEulerAngles(EulerOrders.YZX);

        return rotation;
    }

    private Quaternion getReferenceAdjustedDriftRotationFrom(Quaternion rotation) {
        return newReset.adjustToReference(rotation);
    }


    public boolean isLeftArmTracker() {
        return switch (sensorPart) {
            case LEFT_UPPER_ARM, LEFT_LOWER_ARM, LEFT_HAND, LEFT_SHOULDER -> true;
            default -> false;
        };

    }


    public boolean isLeftLowerArmTracker() {
        return switch (sensorPart) {
            case LEFT_LOWER_ARM, LEFT_HAND -> true;
            default -> false;
        };

    }

    public boolean isRightArmTracker() {
        return switch (sensorPart) {
            case RIGHT_UPPER_ARM, RIGHT_LOWER_ARM, RIGHT_HAND, RIGHT_SHOULDER -> true;
            default -> false;
        };
    }


    public boolean isRightLowerArmTracker() {
        return switch (sensorPart) {
            case RIGHT_LOWER_ARM, RIGHT_HAND -> true;
            default -> false;
        };
    }


    public boolean isThighTracker() {
        return switch (sensorPart) {
            case LEFT_UPPER_LEG, RIGHT_UPPER_LEG -> true;
            default -> false;
        };
    }

    public NewReset getNewReset() {
        return newReset;
    }

    public void setNewReset(NewReset newReset) {
        this.newReset = newReset;
    }

    @Override
    public String toString() {
        try {
            // Gson = Json 형식으로 만들어주는 lib
            Gson gson = new Gson();
            return gson.toJson(this);
        } catch (IllegalArgumentException e) {
            System.err.println("NaN 예외 처리!!!!!!!!!!! : " + e);
            return null;
        }

    }

    // 깊은 복사 용도
    @Override
    public SensorData clone() {
        try {
            return (SensorData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
