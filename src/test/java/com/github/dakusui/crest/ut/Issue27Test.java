package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.ExecutionFailure;
import com.github.dakusui.crest.core.Session;
import com.github.dakusui.crest.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static com.github.dakusui.crest.utils.printable.Predicates.matchesRegex;

public class Issue27Test extends TestBase {
  @Test
  public void addException() {
    Assert.assertFalse(Session.create().addException(new Throwable()).report().exceptions().isEmpty());
  }

  @Ignore
  @Test
  public void example() {
    Crest.assertThat(
        new StringBuilder(),
        asObject(
            call("append", "hello")
                .andThen("append", "world")
                .$())
            .check(
                call("append", "!").andThen("append", "!").andThen("toString").$(),
                equalTo("HELLOWORLD!")
            )
            .$()
    );
  }

  @Test(expected = IOException.class)
  public void stateful() throws IOException {
    try {
      Crest.assertThat(
          new StringBuilder(),
          asObject(
              call("append", "hello")
                  .andThen("append", "world")
                  .$())
              .check(
                  call("append", "!").andThen("append", "!").andThen("toString").$(),
                  equalTo("HELLOWORLD!")
              )
              .$()
      );
    } catch (ComparisonFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("x=<helloworld!!> [did not satisfy\n" +
              "(y=x.append(\"hello\").append(\"world\")).append(\"!\").append(\"!\").toString() equalTo[\"HELLOWORLD!\"] was not met\n" +
              "  y=x.append(\"hello\").append(\"world\")\n" +
              "                    |               |\n" +
              "                    |               +-<helloworld>:StringBuilder\n" +
              "                    |\n" +
              "                    +-----------------<hello>:StringBuilder\n" +
              "  y.append(\"!\").append(\"!\").toString() equalTo[\"HELLOWORLD!\"]\n" +
              "              |           |          |\n" +
              "              |           |          +-\"helloworld!!\"\n" +
              "              |           |\n" +
              "              |           +------------<helloworld!!>:StringBuilder\n" +
              "              |\n" +
              "              +------------------------<helloworld!>:StringBuilder]"
          )
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void stateful2() throws IOException {
    try {
      Crest.assertThat(
          new StringBuilder(),
          asString(
              call("append", "hello")
                  .andThen("append", "world")
                  .andThen("toString")
                  .$())
              .equalTo("HelloWorld").$()
      );
    } catch (ComparisonFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("x=<helloworld> [satisfies\n" +
              "x.append(\"hello\").append(\"world\").toString() equalTo[\"HelloWorld\"]]> but was:<x=<helloworld> [did not satisfy\n" +
              "x.append(\"hello\").append(\"world\").toString() equalTo[\"HelloWorld\"] was not met\n" +
              "                |               |          |\n" +
              "                |               |          +-\"helloworld\"\n" +
              "                |               |\n" +
              "                |               +------------<helloworld>:StringBuilder\n" +
              "                |\n" +
              "                +----------------------------<hello>:StringBuilder]")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void simplest() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          asString("toLowerCase").check(
              call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
              equalTo('z')
          ).$()
      );
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL   FROM CREST:" + e.getActual());
      e.printStackTrace();
      System.out.println("EXPECTED FROM ACTUAL: " + e.getExpected());
      System.out.println("MESSAGE: " + e.getMessage());
      Assert.assertEquals(
          e.getMessage(),
          "expected:<x=\"WORLD\" [satisfies\n" +
              "(y=x.toLowerCase()).toUpperCase().substring(2).charAt(1) equalTo['z']]> but was:<x=\"WORLD\" [did not satisfy\n" +
              "(y=x.toLowerCase()).toUpperCase().substring(2).charAt(1) equalTo['z'] was not met\n" +
              "  y=x.toLowerCase()\n" +
              "    x.toLowerCase()=\"world\"\n" +
              "  y.toUpperCase().substring(2).charAt(1) equalTo['z']\n" +
              "                |            |         |\n" +
              "                |            |         +-\"L\":Character\n" +
              "                |            |\n" +
              "                |            +-----------\"RLD\"\n" +
              "                |\n" +
              "                +------------------------\"WORLD\"]>"
      );
      throw new IOException();
    }
  }


  @Test(expected = IOException.class)
  public void simpler() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          allOf(
              asString("toLowerCase").check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('z')
              ).$())
      );
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("was not met\n"
              + "    y=x.toLowerCase()\n"
              + "      x.toLowerCase()=\"world\"\n"
              + "    y.toUpperCase().substring(2).charAt(1) equalTo['z']\n"
              + "                  |            |         |\n"
              + "                  |            |         +-\"L\":Character\n"
              + "                  |            |\n"
              + "                  |            +-----------\"RLD\"\n"
              + "                  |\n"
              + "                  +------------------------\"WORLD\""
          )
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void simple() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          allOf(
              asString("toLowerCase").check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('z')
              ).$())
      );
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("was not met\n"
              + "    y=x.toLowerCase()\n"
              + "      x.toLowerCase()=\"world\"\n"
              + "    y.toUpperCase().substring(2).charAt(1) equalTo['z']\n"
              + "                  |            |         |\n"
              + "                  |            |         +-\"L\":Character\n"
              + "                  |            |\n"
              + "                  |            +-----------\"RLD\"\n"
              + "                  |\n"
              + "                  +------------------------\"WORLD\"\n"
              + "]->false")
      );
      throw new IOException();
    }

  }

  @Ignore
  @Test
  public void lessSimpleExample() {
    Crest.assertThat("hello",
        "WORLD",
        allOf(
            asString(call("toLowerCase").andThen("substring", 1).$())
                .check(
                    call("replaceAll", "d", "DDD").andThen("concat", "XYZ").$(),
                    matchesRegex("xyz"))
                .check(
                    call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                    equalTo('D'))
                .$(),
            asInteger("length").equalTo(5).$()
        )
    );
  }

  @Test(expected = IOException.class)
  public void lessSimple() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", 1).$())
              .check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('D'))
              .check(
                  call("replaceAll", "d", "DDD").andThen("concat", "XYZ").$(),
                  matchesRegex("xyz"))
              .$());
    } catch (ComparisonFailure e) {
      System.out.println("MESSAGE: " + e.getMessage());
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("did not satisfy\n" +
              "and:[\n" +
              "  (y=x.toLowerCase().substring(1)).toUpperCase().substring(2).charAt(1) equalTo['D']\n" +
              "  (y=x.toLowerCase().substring(1)).replaceAll(\"d\",\"DDD\").concat(\"XYZ\") matchesRegex[\"xyz\"] was not met\n" +
              "    y=x.toLowerCase().substring(1)\n" +
              "                    |            |\n" +
              "                    |            +-\"orld\"\n" +
              "                    |\n" +
              "                    +--------------\"world\"\n" +
              "    y.replaceAll(\"d\",\"DDD\").concat(\"XYZ\") matchesRegex[\"xyz\"]\n" +
              "                          |             |\n" +
              "                          |             +-\"orlDDDXYZ\"\n" +
              "                          |\n" +
              "                          +---------------\"orlDDD\"\n" +
              "]->false")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void givenErrorCausingCheck$whenAssertThat$thenMessageContainsIntendedString() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", 1).$())
              .check(
                  call("toUpperCase").andThen("substring", -2).andThen("charAt", 1).$(),
                  equalTo('z'))
              .$());
    } catch (ExecutionFailure e) {
      System.out.println("MESSAGE: " + e.getMessage());
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("" +
              "x=\"WORLD\" [did not satisfy\n" +
              "(y=x.toLowerCase().substring(1)).toUpperCase().substring(-2).charAt(1) equalTo['z'] failed with java.lang.StringIndexOutOfBoundsException(String index out of range: -2)\n" +
              "  y=x.toLowerCase().substring(1)\n" +
              "                  |            |\n" +
              "                  |            +-\"orld\"\n" +
              "                  |\n" +
              "                  +--------------\"world\"\n" +
              "  y.toUpperCase().substring(-2).charAt(1) equalTo['z']\n" +
              "                |             |         |\n" +
              "                |             |         +-java.lang.StringIndexOutOfBoundsException(String index out of range: -2)\n" +
              "                |             |\n" +
              "                |             +-----------java.lang.StringIndexOutOfBoundsException(String index out of range: -2)\n" +
              "                |\n" +
              "                +-------------------------\"ORLD\"\n" +
              "FAILED]>"
          )
      );
      throw new IOException(e);
    }
  }


  @Test(expected = IOException.class)
  public void givenErrorCausingMatcher$whenAssertThat$thenMessageContainsIntendedString() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", -1).$())
              .check(call("toUpperCase")
                      .andThen("substring", 1)
                      .andThen("charAt", 1).$(),
                  equalTo('z')
              ).$());
    } catch (ExecutionFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString(""
              + "but was:<x=\"WORLD\" [did not satisfy\n" +
              "(y=x.toLowerCase().substring(-1)).toUpperCase().substring(1).charAt(1) equalTo['z'] failed with java.lang.StringIndexOutOfBoundsException(String index out of range: -1)\n" +
              "              |             |\n" +
              "              |             +-java.lang.StringIndexOutOfBoundsException(String index out of range: -1)\n" +
              "              |\n" +
              "              +---------------\"world\"\n" +
              "FAILED]")
      );
      throw new IOException(e);
    }
  }

  @Test(expected = IOException.class)
  public void partial() throws IOException {
    try {
      Crest.assertThat(
          "HELLO",
          Crest.not(
              asString().containsString("HELLO").$()
          )
      );
    } catch (ComparisonFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("x=\"HELLO\" [satisfies\n" +
              "not:[\n" +
              "  x containsString[\"HELLO\"]\n" +
              "]]> but was:<x=\"HELLO\" [did not satisfy\n" +
              "not:[\n" +
              "  x containsString[\"HELLO\"]\n" +
              "]->false]")
      );
      throw new IOException(e);
    }
  }
}
