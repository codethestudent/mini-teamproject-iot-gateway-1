package com.nhnacademy.node;

import com.nhnacademy.exception.AlreadyExistsException;
import com.nhnacademy.exception.InvalidArgumentException;
import com.nhnacademy.exception.NotExistsException;
import com.nhnacademy.exception.OutOfBoundsException;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

public abstract class InputNode extends ActiveNode {
    Wire[][] outputWires;

    public InputNode(String id, int outCount, int outWireCount) {
        super(id);

        if (outCount <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new Wire[outCount][outWireCount];
    }

    public InputNode(int outCount, int outWireCount) {
        super();

        if (outCount <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new Wire[outCount][outWireCount];
    }

    public void connectOutputWire(int index, int outWireIndex, Wire wire) {
        if (getOutputCount() <= index || index < 0 || getOutputWireCount() <= outWireIndex || outWireIndex < 0) {
            throw new OutOfBoundsException();
        }
        if (outputWires[index][outWireIndex] != null) {
            throw new AlreadyExistsException();
        }

        outputWires[index][outWireIndex] = wire;
    }

    public void disconnectOutputWire(int index, int outWireIndex) {
        if (getOutputCount() <= index || index < 0 || getOutputWireCount() <= outWireIndex || outWireIndex < 0) {
            throw new OutOfBoundsException();
        }
        if (outputWires[index][outWireIndex] == null)
            throw new NotExistsException();

        outputWires[index][outWireIndex] = null;
    }

    public int getOutputCount() {
        return outputWires.length;
    }

    public int getOutputWireCount() {
        if (outputWires.length == 0)
            return 0;
        return outputWires[0].length;
    }

    public Wire getoutputWire(int index, int outWireCount) {
        if (index < 0 || outputWires.length <= index) {
            throw new OutOfBoundsException();
        }
        return outputWires[index][outWireCount];
    }

    void output(Message message, int index) {
        for (Wire wire : outputWires[index]) {
            if (wire != null) {
                wire.put(message);
            }
        }
    }

}
