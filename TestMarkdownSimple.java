package ru.adideas.alpha.MarkdownSimple;

import org.junit.Test;
import ru.adideas.alpha.MarkdownSimple.MarkdownSimple;

import static org.junit.Assert.*;

public class TestMarkdownSimple {

    private void run(String markdown, String html) {
        assertEquals(
                markdown + " => " + html,
                new MarkdownSimple(markdown).parse(),
                html
        );
    }

    @Test
    public void test2() {
        run(
                "* test\n* test2\n* test3",
                "<ul><li>test</li><li>test2</li><li>test3</li></ul>"
        );
        run(
                "- test\n- test2\n- test3",
                "<ul><li>test</li><li>test2</li><li>test3</li></ul>"
        );
        run(
                "* test1\n* * test1-2\n* test2",
                "<ul><li>test1</li><li><ul><li>test1-2</li></ul></li><li>test2</li></ul>"
        );
        run(
                "- test1\n- - test1-2\n- test2",
                "<ul><li>test1</li><li><ul><li>test1-2</li></ul></li><li>test2</li></ul>"
        );

        run("Test\n==\n", "<h1>Test</h1>");
        run("Test\n=", "<h1>Test</h1>");
        run("Test\n--\n", "<h2>Test</h2>");
        run("Test\n-", "<h2>Test</h2>");

        run("1. test1\n2. test2", "<ol start=\"1\"><li>test1</li><li>test2</li></ol>");
        run("1) test1\n2) test2", "<ol start=\"1\"><li>test1</li><li>test2</li></ol>");

        run("- [ ] test\n-[ ] test2", "<ul><li><input type=\"checkbox\"> test</li></ul>\n" +
                "-[ ] test2");

        run("- [ ] test\n- [ ] test2", "<ul><li><input type=\"checkbox\"> test</li><li><input type=\"checkbox\"> test2</li></ul>");
        run(
                "- [ ] - [ ] test\n- [ ] - [ ] test2",
                "<ul><li><input type=\"checkbox\"> - [ ] test</li><li><input type=\"checkbox\"> - [ ] test2</li></ul>");

    }

    @Test
    public void test1() {
        run("* test", "<ul><li>test</li></ul>");
        run("1.1234", "1.1234");
        run("1)1234", "1)1234");
        run("- test", "<ul><li>test</li></ul>");
        run("--- test", "— test");
        run("---", "<hr>");
        run("***", "<hr>");
        run("> test", "<blockquote>test</blockquote>");
        run("## test", "<h2>test</h2>");
        run("# test", "<h1>test</h1>");
        run("####### test", "####### test");
        run("###### test", "<h6>test</h6>");
        run("# 1 # 1", "<h1>1 # 1</h1>");
        run("# --- test", "<h1>— test</h1>");
        run("*** test", "*** test");
        run("* --- test", "<ul><li>— test</li></ul>");
        run("--- # test", "— # test");
        run("---# test", "—# test");
        run("* # test", "<ul><li><h1>test</h1></li></ul>");
        run("*# test", "*# test");
        run("*  # test", "<ul><li><h1>test</h1></li></ul>");
        run("*  #test", "<ul><li>#test</li></ul>");
        run("* test*", "<ul><li>test*</li></ul>");
        run("- test*", "<ul><li>test*</li></ul>");
        run("    - test*", "<ul><li>test*</li></ul>");
        run("[Test](http://a.com)", "<a href=\"http://a.com\">Test</a>");
        run("![Test](http://url/a.png)", "<img src=\"http://url/a.png\" alt=\"Test\">");

        run("[Test](http://a.com)![Test](http://url/a.png)",
                "<a href=\"http://a.com\">Test</a><img src=\"http://url/a.png\" alt=\"Test\">");

        run("[![Test](http://url/a.png)](http://a.com)",
                "<a href=\"http://a.com\"><img src=\"http://url/a.png\" alt=\"Test\"></a>");

        run("[# Test](http://a.com)",
                "<a href=\"http://a.com\"><h1>Test</h1></a>");

        run("- [ ] test",
                "<ul><li><input type=\"checkbox\"> test</li></ul>");

        run("- [ ] * test",
                "<ul><li><input type=\"checkbox\"> * test</li></ul>");

        run("- [ ] * * test",
                "<ul><li><input type=\"checkbox\"> * * test</li></ul>");

        run("* * test",
                "<ul><li><ul><li>test</li></ul></li></ul>");

        run("- [ ] - [ ]",
                "<ul><li><input type=\"checkbox\"> - [ ]</li></ul>");

        run("* [ ] - [ ]",
                "<ul><li><input type=\"checkbox\"> - [ ]</li></ul>");

        run("* * [ ] fg", "<ul><li><ul><li><input type=\"checkbox\"> fg</li></ul></li></ul>");
        run("* * * [ ] fg", "<ul><li><ul><li><ul><li><input type=\"checkbox\"> fg</li></ul></li></ul></li></ul>");

        run("- [X] test",
                "<ul><li><input type=\"checkbox\" checked=\"true\"> test</li></ul>");

        run("- [x] test",
                "<ul><li><input type=\"checkbox\" checked=\"true\"> test</li></ul>");

        run("2) test", "<ol start=\"2\"><li>test</li></ol>");
        run("2. test", "<ol start=\"2\"><li>test</li></ol>");
        run("2) # test",
                "<ol start=\"2\"><li><h1>test</h1></li></ol>");
        run("2. # test",
                "<ol start=\"2\"><li><h1>test</h1></li></ol>");
        run("123) * 1",
                "<ol start=\"123\"><li><ul><li>1</li></ul></li></ol>");
        run("123. * 1",
                "<ol start=\"123\"><li><ul><li>1</li></ul></li></ol>");
        run("123) - [ ] test",
                "<ol start=\"123\"><li><ul><li><input type=\"checkbox\"> test</li></ul></li></ol>");
        run("123. - [ ] test",
                "<ol start=\"123\"><li><ul><li><input type=\"checkbox\"> test</li></ul></li></ol>");

        run("123.  123", "<ol start=\"123\"><li>123</li></ol>");
        run("1234.  123", "1234. 123");
    }
}
