package com.nhnacademy.modbus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class READ_INPUT_REGISTERS_4 {
    /**
     * 0x04 request
     * 
     * @param address
     * @param quantity
     * @return
     */
    public static byte[] makeReadInputRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];

        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        b.putInt(address);

        // PDU의 function code
        frame[0] = 0x04;

        // PDU의 data 부분
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(quantity);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        return frame;
    }

    /**
     * 0x04 response
     * 
     * @param address
     * @param registers
     * @return
     */
    public static byte[] makeReadInputRegistersResponse(int address, int[] registers) {
        byte[] frame = new byte[1 + 1 + registers.length * 2];

        // PDU의 Function code
        frame[0] = 0x04;

        // Length
        frame[1] = (byte) (registers.length * 2);

        for (int i = 0; i < registers.length; i++) {
            frame[2 + i * 2] = (byte) ((registers[i] >> 8) & 0xFF);
            frame[2 + i * 2 + 1] = (byte) ((registers[i]) & 0xFF);
        }
        return frame;
    }
}
