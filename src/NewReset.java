import slimevr.EulerAngles;
import slimevr.EulerOrders;
import slimevr.Quaternion;
import slimevr.Vector3;

import java.util.HashMap;
import java.util.Map;

public class NewReset {
    final private Quaternion HalfHorizontal = new EulerAngles(EulerOrders.YZX, 0f, (float) Math.PI, 0f).toQuaternion();
    private Quaternion gyroFix = Quaternion.IDENTITY;
    private Quaternion attachmentFix = Quaternion.IDENTITY;
    private Quaternion mountRotFix = Quaternion.IDENTITY;
    private Quaternion yawFix = Quaternion.IDENTITY;
//    private Quaternion yawFixSmoothIncremental = Quaternion.IDENTITY;
    private Quaternion tposeDownFix = Quaternion.IDENTITY;
//    private Quaternion lastResetQuaternion;
    private Quaternion  mountingOrientation = HalfHorizontal;
//    private Quaternion attachmentFixNoMounting = Quaternion.IDENTITY;


    private float yawResetSmoothTimeRemain = 0.0f;


    // Qt = 현재 로우 쿼터니언 각도
    // Qt.inv() * Q(t+1) = reset값
    // 근데 요놈이 비툴게 센서를 장착했을때 문제가 있었음
    public void resetFull(SensorData sensor) {
        Quaternion referenceRotation = Quaternion.IDENTITY;

//        if (sensor.isLeftArmTracker()) {
//            tposeDownFix = new EulerAngles(EulerOrders.YZX, 0f, 0f, (float) -(0.5f * Math.PI)).toQuaternion();
//        } else if (sensor.isRightArmTracker()) {
//            tposeDownFix = new EulerAngles(EulerOrders.YZX, 0f, 0f, (float) -(0.5f * Math.PI)).toQuaternion();
//        } else {
//            tposeDownFix = Quaternion.IDENTITY;
//        }


        // Adjust raw rotation to mountingOrientation
        Quaternion mountingAdjustedRotation = sensor.getRawRotation().multiply(mountingOrientation);

        // Gyrofix
        gyroFix = fixGyroscope(mountingAdjustedRotation.multiply(tposeDownFix));

        //.../ 여기 나중에 확인

        // Attachment fix
        attachmentFix = fixAttachment(mountingAdjustedRotation);


        // Rotate attachmentFix by 180 degrees as a workaround for t-pose (down)
        if (tposeDownFix != Quaternion.IDENTITY) {
            attachmentFix = attachmentFix.multiply(HalfHorizontal);
        }

//        makeIdentityAdjustmentQuatsFull(sensor);

        yawFix = fixYaw(mountingAdjustedRotation, referenceRotation);
//        yawResetSmoothTimeRemain = 0.0f;

//        calculateDrift(oldRot);


    }


    public void resetMouning(SensorData sensor) {
        if (sensor.getSensorPart() == SensorPart.LEFT_FOOT || sensor.getSensorPart() == SensorPart.RIGHT_FOOT) {
            return;
        }

        Quaternion referenceRotation = Quaternion.IDENTITY;

        Quaternion rotBuf = sensor.getRawRotation().multiply(mountingOrientation);
        rotBuf = gyroFix.multiply(rotBuf);
        rotBuf = rotBuf.multiply(attachmentFix);
        rotBuf = yawFix.multiply(rotBuf);

        rotBuf = referenceRotation.project(Vector3.POS_Y).inv().unit().multiply(rotBuf); // 검즘

        Vector3 rotVector = rotBuf.sandwich(Vector3.POS_Y); // 검증

        double yawAngle = Math.atan2(rotVector.getX(), rotVector.getZ());

        boolean isLowerArmBack = sensor.isLeftLowerArmTracker() || sensor.isRightLowerArmTracker();

        if(!sensor.isThighTracker() && !isLowerArmBack) {
            yawAngle -= (float) Math.PI;
        }

        mountRotFix = new EulerAngles(EulerOrders.YZX, (float) 0, (float) yawAngle, (float) 0).toQuaternion();


    }




    public Quaternion adjustToReference(Quaternion rotation) {
        rotation = rotation.multiply(mountingOrientation);
        rotation = gyroFix.multiply(rotation);
        rotation = rotation.multiply(attachmentFix);
        rotation = mountRotFix.inv().multiply(rotation.multiply(mountRotFix));
        rotation = rotation.multiply(tposeDownFix);
        rotation = yawFix.multiply(rotation);

        return rotation;
    }



    private Quaternion fixGyroscope(Quaternion sensorRotation) {
        return getYawQuaternion(sensorRotation).inv();
    }

    private Quaternion getYawQuaternion(Quaternion rotation) {
        return new EulerAngles(EulerOrders.YZX, 0f, rotation.toEulerAngles(EulerOrders.YZX).getY(), 0f).toQuaternion();
    }

    private Quaternion fixAttachment(Quaternion sensorRotation) {
        return gyroFix.multiply(sensorRotation).inv();
    }

    private Quaternion fixYaw(Quaternion sensorRotation, Quaternion reference) {
        Quaternion rot = gyroFix.multiply(sensorRotation);
        rot = rot.multiply(attachmentFix);
        rot = mountRotFix.inv().multiply(rot.multiply(mountRotFix));
        rot = getYawQuaternion(rot);
        return rot.inv().multiply(reference.project(Vector3.POS_Y).unit());
    }



}
