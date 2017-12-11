package com.yxf.doodle;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String path = "/sdcard/notes/20160101085845write.png";
        String[] strArray = path.split("\\.");
        if (strArray.length > 0) {
            System.out.println(true);
        } else {
            System.out.println((int) (path.charAt(path.length() - 4)));
            System.out.println((int)".".charAt(0));
        }




        assertEquals(4, 2 + 2);
    }
}