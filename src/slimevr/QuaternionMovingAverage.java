package slimevr;

public class QuaternionMovingAverage {
    private Quaternion filteredQuaternion = Quaternion.IDENTITY;
    private Quaternion latestQuaternion = Quaternion.IDENTITY;
    private Quaternion smoothingQuaternion = Quaternion.IDENTITY;
    private float predictFactor = 13;
    private CircularArrayList rotBuffer = new CircularArrayList(6);
    private NanoTimer fpsTimer = new NanoTimer();


    public void update(Boolean test) {
        synchronized (rotBuffer) {
            if (rotBuffer.size() > 0) {
                Quaternion quatBuf = latestQuaternion; // 가장 최근의 raw 쿼터니언 데이터

                // Applies the past rotations to the current rotation
                for (Object q : rotBuffer) {
                    quatBuf = quatBuf.multiply((Quaternion) q); // 이전 회전 적용
                }

                // Calculate how much to slerp
                float amt = predictFactor * fpsTimer.getTpf(); // 13 * tpf

//            if (test) {
//                System.out.println(amt);
//            }

                // Slerps the target rotation to that predicted rotation by amt
//            if (test) {
//                System.out.println(quatBuf);
//            }

                filteredQuaternion = filteredQuaternion.interpR(quatBuf, amt);

//            if (test) {
//                System.out.println(filteredQuaternion.toString());
//            }
            }
        }

    }


    public void addQuaternion(Quaternion q, boolean test) {
        synchronized (rotBuffer) {
            // (이것도 마지막 데이터 삭제가 아니네)
            // 새로 추가될 데이터를 고려하고 rotBuffer의 사이즈를 6으로 고정하기 위해
            if (rotBuffer.size() == rotBuffer.capacity()) {
                rotBuffer.removeLast();
            }

//        if (test) {
//            System.out.println(q + " : " + latestQuaternion);
//            System.out.println(latestQuaternion.inv().multiply(q) + "");
//        }

            rotBuffer.add(latestQuaternion.inv().multiply(q)); // 이전 각도와의 차이?

            latestQuaternion = q;
        }

    }


    public NanoTimer getFpsTimer() {
        return fpsTimer;
    }


    public Quaternion getFilteredQuaternion() {
        return filteredQuaternion;
    }

    public Quaternion test () {
        return filteredQuaternion;
    }
}
