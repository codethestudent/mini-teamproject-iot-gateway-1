package com.nhnacademy.node;

import com.nhnacademy.exception.AlreadyExistsException;
import com.nhnacademy.exception.InvalidArgumentException;
import com.nhnacademy.exception.OutOfBoundsException;
import com.nhnacademy.wire.Wire;

public abstract class OutputNode extends ActiveNode {
    Wire[] inputWires;

    public OutputNode(String id, int wireCount) {
        super(id);
        if (wireCount <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new Wire[wireCount];
    }

    public OutputNode(int wireCount) {
        super();
        if (wireCount <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new Wire[wireCount];
    }

    public void connectInputWire(int wireIndex, Wire wire) {
        if (inputWires.length <= wireIndex) {
            throw new OutOfBoundsException();
        }

        if (inputWires[wireIndex] != null) {
            throw new AlreadyExistsException();
        }

        inputWires[wireIndex] = wire;
    }

    public int getInputWireCount() {
        return inputWires.length;
    }

    public Wire getInputWire(int wireIndex) {
        if (wireIndex < 0 || inputWires.length <= wireIndex) {
            throw new OutOfBoundsException();
        }

        return inputWires[wireIndex];
    }

}
