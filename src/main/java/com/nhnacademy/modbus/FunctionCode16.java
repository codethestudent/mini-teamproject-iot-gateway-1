package com.nhnacademy.modbus;

import com.nhnacademy.exception.InvalidArgumentException;

public class FunctionCode16 {
    // Write Multiple Registers 16

    public void writeMultipleRegisters(byte[] pdu) {
        // fc, address, address, quantity, quantity, byte count, Register values(Nbytes)
        // address : 10101 (0010 0111 0111 0101)-> {16, 39, 117, x, x, x, x ...}

        byte[] tempDB = new byte[40000];

        int address = (pdu[1] << 8) | (pdu[2] & 0xFF);
        int quantity = (pdu[3] << 8) | (pdu[4] & 0xFF);
        int registersByte = (pdu[5] & 0xFF);
        byte[] registersValues = new byte[pdu.length - 6];

        System.arraycopy(pdu, 5, registersValues, 0, pdu.length - 5);

        if (registersByte != registersValues.length || registersByte / 2 != quantity) {
            throw new InvalidArgumentException();
        }
        int j = 0;
        for (int i = address; i < quantity; i++) {
            tempDB[i] = registersValues[j++];
        }

    }
}
