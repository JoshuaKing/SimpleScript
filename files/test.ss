/* Simple Script */
package x.y.z

class MyClass {
    private int a = 4; // Test Comment 1
    private int y = 3;
    // Test Comment 2

    void x(int b) {
        x(y);
        x(1 + x.y.z.MyClass.a);
        if (a ==b) {
            return a < 20.2;
        }
    }
}