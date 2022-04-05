package com.example.pocfx.color;

public class ColorYUV {
    private float Y;
    private float U;
    private float V;

    public ColorYUV() {
    }

    public ColorYUV(float y, float u, float v) {
        Y = y;
        U = u;
        V = v;
    }

    public void RGBtoYUV(int r, int g, int b) {
        this.Y = 0.299f * r + 0.587f * g + 0.114f * b;
        this.U = 0.493f * (b - this.Y);
        this.V = 0.877f * (r - this.Y);
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getU() {
        return U;
    }

    public void setU(float u) {
        U = u;
    }

    public float getV() {
        return V;
    }

    public void setV(float v) {
        V = v;
    }
}
