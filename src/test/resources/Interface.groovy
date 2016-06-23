interface B {
    int a;
    long b;
    double c;
    char d;
    short e;
    byte f;
    float g;
    boolean h;
    String i;
}

interface C {}

interface D {}

interface A extends B, C, D {}

class X {
    int a;
    long b;
    double c;
    char d;
    short e;
    byte f;
    float g;
    boolean h;
    String i;
}

trait Y {
    int a;
    long b;
    double c;
    char d;
    short e;
    byte f;
    float g;
    boolean h;
    String i;
}

enum Z {
    int a;
    long b;
    double c;
    char d;
    short e;
    byte f;
    float g;
    boolean h;
    String i;
}
