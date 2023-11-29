package com.nhnacademy.node;

public abstract class ActiveNode extends Node implements Runnable {
    // Active 노드에 필요한 추가 필드 (스레드, running, interval)
    Thread thread;
    boolean running;
    long interval = 1000;

    // 생성하면 부모클래스 상속, thread 생성, 작동안함ss
    ActiveNode() {
        super();
        thread = new Thread(this, getId());
        running = false;
    }

    // 이름을 지정해주는 생성자
    ActiveNode(String name) {
        this();
        setName(name);
    }

    // 이름 getter setter
    @Override
    public String getName() {
        return thread.getName();
    }

    @Override
    public void setName(String name) {
        thread.setName(name);
    }

    // Interval getter setter
    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    // start 메서드
    public void start() {
        thread.start();
    }

    // stop 메서드
    public void stop() {
        running = false;
        thread.interrupt();
    }

    // 전처리, 처리 , 후처리 메서드 (일단 빈 메서드로 구현)
    void preprocess() {
        //
    }

    void process() {
        //
    }

    void postprocess() {
        //
    }

    // run 메서드
    @Override
    public void run() {
        // 전처리
        preprocess();

        // 처리 (runningFlag 지정)
        running = true;

        long startTime = System.currentTimeMillis();
        long previousTime = startTime;

        while (running) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;

            if (elapsedTime < interval) {
                try {
                    process();
                    Thread.sleep(interval - elapsedTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            previousTime =
                    startTime + (System.currentTimeMillis() - startTime) / interval * interval;
        }
        // 후처리
        postprocess();
    }
}
