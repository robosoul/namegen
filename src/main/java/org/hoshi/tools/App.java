package org.hoshi.tools;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        NameGenerator gn = new NameGenerator(args[0]);

        int i = 0;
        while ((i++) < 50) {
            System.out.println(gn.compose(2));
        }
    }
}
