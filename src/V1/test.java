package V1;

import java.util.*;

public class test {
    public static void main(String[] args) {

        test: for (int i = 0; i < 5; i++) {
            if (true) {
                continue test;
            }
        }

    }

}