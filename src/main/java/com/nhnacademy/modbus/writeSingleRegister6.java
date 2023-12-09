package com.nhnacademy.modbus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class writeSingleRegister6 {


    /**
     * 기록하는 레지스터.
     * <p>
     * 0x06 request code
     * 
     * @param address : 기록할 Register의 주소. 0부터 주소가 지정.
     * @param value : 기록할 값.
     * @return request에 대한 echo. 레지스터의 내용이 작성된 후에 반환.
     */
    public static byte[] makeWriteSingleRegisterRequest(int address, int value) {
        byte[] frame = new byte[5];
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN); // Big_endian 처리

        frame[0] = 0x06; // 헥사코드, PDU function code

        b.putInt(address); // PDU data code
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear(); // PDU quantity data
        b.putInt(value);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        return frame;
    }

    /**
     * 0x06 response code
     * <p>
     * echo
     * 
     * @param address
     * @param value
     * @return
     */
    public static byte[] makeWriteSingleRegisterResponse(int address, int value) {
        return makeWriteSingleRegisterRequest(address, value);
    }

}
