/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JNIDemo;

/**
 *
 * @author tutyb
 */
public class JNIDemo {
    public static void main(String[] args) {
        new JNIDemo().nativePrint();
    }

    public native void nativePrint();
}
