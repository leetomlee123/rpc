package com.lx.consume;

import org.junit.Test;

public class ConsumeMainTest {
    @Test
    public void main() {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> ConsumeMain.main(null)).run();
            System.out.println(i);
        }
    }
}