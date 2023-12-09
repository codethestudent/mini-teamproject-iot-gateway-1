package com.nhnacademy.modbus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nhnacademy.exception.InvalidArgumentException;

public class FunctionCode16 {
    // Write Multiple Registers 16

    public byte[] makeWriteMultipleRegistersRequest(int address, int quantity, byte[] pdu) {
        // fc, address, address, quantity, quantity, byte count, Register values(Nbytes)
        // address : 10101 (0010 0111 0111 0101)-> {16, 39, 117, x, x, x, x ...}

        byte[] frame = new byte[6 + pdu.length];

        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        frame[0] = 0x10;

        b.putInt(address);
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();
        b.putInt(quantity);
        frame[3] = b.get(2);
        frame[4] = b.get(3);

        frame[5] = (byte) (quantity < 127 || quantity > 0 ? quantity * 2 : quantity);

        if (quantity * 2 != pdu.length) {
            throw new IllegalArgumentException();
        }

        System.arraycopy(pdu, 0, frame, 6, pdu.length);

        return frame;
    }

    public static void main(String[] args) {
        FunctionCode16 code = new FunctionCode16();
        byte[] b = new byte[] {1,2,3,4,5,6};
        

        byte[] frame = code.makeWriteMultipleRegistersRequest(10101, 3, b);

        for(byte a : frame){
            System.out.println(a);
        }
    }
}
