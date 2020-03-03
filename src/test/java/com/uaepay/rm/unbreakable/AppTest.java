package com.uaepay.rm.unbreakable;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */

public class AppTest {
    private static <T> void log(T message) {
        System.out.println(message.toString());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testOption() {
        Option<String> a = Option.lift("hello,");
        Option<String> b = Option.lift("world");
        Option<String> c = Option.map2(a, b, (x, y) -> x + y); // c = Some("hello,world");
        Option<Integer> d = Option.map3(a, b, c, (x, y, z) -> x.length() + y.length() + z.length());
        log(d);
        assertTrue("a should not be empty", a.isSome());
    }

    @Test
    public void testResult() {
        // print: "1 + 2 = 3"
        Result<String, String> a = Result.lift("1");
        Result<String, String> b = Result.lift("2A");
        Result<String, Integer> c = Result.flatten(Result.map2(a, b, (a1, b1) -> {
            return Result.trying(() -> Integer.parseInt(a1) + Integer.parseInt(b1)).mapLeft(x -> x.getMessage())
                    .recover(() -> 0);
        }));
        assertTrue("c should be 0", c.getOrElse(() -> {
            throw new RuntimeException("c'value is present");
        }) == 0);

        Result<String, String> expr = Result.map3(a, b, c, (a1, b1, c1) -> a1 + " + " + b1 + " = " + c1);
        log(expr);
        String sExpr = expr.getOrElse(() -> "Oooops");
        log(sExpr);

        Result<Exception, Integer> integer = Result.trying(() -> Integer.parseInt("1"));
        Result<Exception, String> sInteger = integer.map(i -> i.toString());

        log(sInteger);

        Result<Exception, String> ex = Result.liftLeft(new Exception("This is a new exception"));
        log(ex);
        Result<String, String> ex1 = ex.mapLeft(x -> x.getMessage());
        log(ex1);

        // trying a throws method

        Result<Throwable, String> ex2 = Result.trying(() -> {
            throw new Exception("exception can be thrown");            
        });

        log(ex2);

    }

    @Test
    public  void testNPE() {
        String s = null;
        Result<String, Boolean> result = Result.trying(() -> {
            char [] xs = s.toCharArray();
            return true;
        }).mapLeft(Throwable::getMessage);
        Result<String, String> result2 =  result.map(x -> "");
        Result<String, String> result3 = result2.recover(() -> "hello,world");

        System.out.println("Result of testNPE = " + result);
        System.out.println("Result2 of testNPE = " + result2);
        System.out.println("Result3 of testNPE = " + result3);
    }
}
