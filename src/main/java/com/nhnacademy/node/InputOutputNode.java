package com.nhnacademy.node;

import com.nhnacademy.exception.AlreadyExistsException;
import com.nhnacademy.exception.NotExistsException;
import com.nhnacademy.exception.OutOfBoundsException;
import com.nhnacademy.message.Message;
import com.nhnacademy.wire.Wire;

public abstract class InputOutputNode extends ActiveNode {
    Wire[] inputWires;
    Wire[][] outputWires;

    public InputOutputNode(String id, int inWireCount, int outCount, int outWireCount) {
        super(id);

        inputWires = new Wire[inWireCount];
        outputWires = new Wire[outCount][outWireCount];
    }

    public InputOutputNode(int inWireCount, int outCount, int outWireCount) {
        super();

        inputWires = new Wire[inWireCount];
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

    public synchronized Wire getOutputWire(int index, int outWireIndex) {
        if (index < 0 || outputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        return outputWires[index][outWireIndex];
    }

    public void connectInputWire(int wireIndex, Wire wire) {
        if (getInputWireCount() <= wireIndex || wireIndex < 0) {
            throw new OutOfBoundsException();
        }

        if(inputWires[wireIndex] != null){
            throw new AlreadyExistsException();
        }
        inputWires[wireIndex] = wire;
    }

    public void disconnectInputWire(int wireIndex) {
        if (getInputWireCount() <= wireIndex || wireIndex < 0) {
            throw new OutOfBoundsException();
        }
        if (inputWires[wireIndex] == null)
            throw new NotExistsException();

        inputWires[wireIndex] = null;
    }

    public int getInputWireCount() {
        return inputWires.length;
    }

    public synchronized Wire getInputWire(int wireIndex) {
        if (wireIndex < 0 || inputWires.length <= wireIndex) {
            throw new OutOfBoundsException();
        }
        return inputWires[wireIndex];
    }

    void output(int index, Message message) {
        for (Wire wire : outputWires[index]) {
            if (wire != null) {
                wire.put(message);
            }
        }
    }
}
