package slimevr;

/**
 * <code>NanoTimer</code>는 <code>Timer</code>의 System.nanoTime 구현입니다.
 * 주로 서버에서 실행되는 헤드리스 애플리케이션에 유용합니다.
 */
public class NanoTimer {

    private static final long TIMER_RESOLUTION = 1000000000L; // 1초를 나노초로 표현
    private static final float INVERSE_TIMER_RESOLUTION = 1f / TIMER_RESOLUTION; // 초당 나노초 변환

    private long startTime; // 타이머 시작 시간
    private long previousTime; // 이전 시간
    private float tpf; // 프레임당 시간
    private long currentTime; // 현재 시간

    public NanoTimer() {
        startTime = System.nanoTime(); // 타이머 시작
    }

    /**
     * 현재 시간을 초 단위로 반환합니다. 타이머는 0.0초에서 시작합니다.
     *
     * @return 현재 시간 (초 단위)
     */
    protected long getTimeInternal() {
        return System.nanoTime() - startTime; // 시작 시간부터 현재 시간까지의 차이
    }

    public long getTime() {
        return currentTime; // 현재 시간 반환
    }

    public float getTpf() {
        return tpf;
    }

    public void update() {
        currentTime = getTimeInternal(); // 현재 시간 갱신
        tpf = (currentTime - previousTime) * (1.0f / TIMER_RESOLUTION); // 프레임당 시간 계산
        previousTime = getTime(); // 이전 시간 갱신
    }


}
