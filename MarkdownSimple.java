package ru.adideas.alpha.MarkdownSimple;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkdownSimple {
    String markdown;
    public MarkdownSimple(String markdown) {
        this.markdown = markdown.replaceAll("  ", " ");
    }

    private byte[] stob(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private String btos(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }

    private byte[] shift(byte[] b, int shift) {
        return Arrays.copyOfRange(b, shift, b.length);
    }

    private byte[] slice(byte[] b, int start, int end) {
        return Arrays.copyOfRange(b, start, end);
    }

    public String parse() {
        if(markdown.contains("\n")) {
            ArrayList<String> lines = new ArrayList<>(List.of(this.markdown.split("\n")));
            for (int i = 0; i < lines.size(); i++) {
                withoutCH = false;
                lines.set(i, this.parse(stob(lines.get(i))));
            }
            lines.replaceAll(s -> this.parse(stob(s)));
            return String.join("\n", new ParseLine().syncLines(lines));
        } else {
            return this.parse(stob(this.markdown));
        }
    }

    private boolean withoutH = false;
    private boolean withoutS = false;
    private boolean withoutCH = false;

    public String parse(byte[] md) {
        if (md.length < 1) {
            return "";
        }
        if (md[0] == ' ' && !withoutS) {
            return parse(shift(md, 1));
        }

        if (md.length >= 2) {
            if (md[0] >= 48 && md[0] <= 57) {
                for(int i = 1; i < 4 && i < md.length - 2; i++) {
                    if((md[i] == ')' || md[i] == '.') && md[i + 1] == ' ') {
                        String start = btos(slice(md,0, i));
                        String name = parse(shift(md, i+2));
                        return "<ol start=\"" + start + "\"><li>" + name + "</li></ol>";
                    }
                    if (!((md[i] >= 48 && md[i] <= 57) || (md[i] == ')' || md[i] == '.'))) {
                        break;
                    }
                }
            }
            if (md[1] == ' ') {
                if (!withoutCH && (md[0] == '*' || md[0] == '-')) {
                    withoutS = true;
                    String include = "";

                    if (md.length >= 7 && md[2] == '[' && md[4] == ']' && md[5] == ' ') {
                        withoutCH = true;
                        String checked = md[3] == 'x' || md[3] == 'X' ? " checked=\"true\"" : "";
                        include = "<input type=\"checkbox\"" + checked + "> " + parse(shift(md, 6));
                    } else {
                        include = parse(shift(md, 2));
                    }

                    return "<ul><li>" + include + "</li></ul>";
                }
                if (md[0] == '>') {
                    withoutS = true;
                    return "<blockquote>" + parse(shift(md, 2)) + "</blockquote>";
                }
            }
            if (md[0] == '#' && !withoutH) {
                for (int i = 1; i < 7; i++) {
                    if (md[i] == ' ') {withoutH = withoutS = true; return "<h"+i+">" + parse(shift(md, i+1)) + "</h"+i+">";}
                    if (md[i] != '#') break;
                }
            }
        }
        if (md.length >= 3) {
            if (md.length == 3 && md[0] == '*' && md[1] == '*' && md[2] == '*') {
                return "<hr>";
            }
            if (md[0] == '-' && md[1] == '-' && md[2] == '-') {
                withoutH = withoutS = true;
                return md.length == 3 ? "<hr>" : "—" + parse(shift(md, 3));
            }
        }

        if (md.length >= 5) {
            if (md[0] == '[') {
                int[] param = getLink(md);
                if (param != null) {
                    return makeLinkOrImage(slice(md, 0, param[3]), param, true) + parse(shift(md, param[3]+1));
                }
            }
        }
        if (md.length >= 6) {
            if (md[0] == '!' && md[1] == '[') {
                int[] param = getLink(shift(md, 1));
                if (param != null) {
                    return makeLinkOrImage(slice(md, 1, param[3]+1), param, false) + parse(shift(md, param[3]+2));
                }
            }
        }

        return btos(md);
    }

    private int[] getLink(byte[] md) {
        int skip = 0;
        if (md[0] == '[') {
            int[] param = new int[4];
            param[0] = 0;
            for (int i = 1; i < md.length; i++) {
                if(md[i] == '[') {skip++;}
                if (md[i] == ']') {
                    if (skip > 0) {skip--;continue;}
                    param[1] = i;
                    for (;i < md.length; i++) {
                        if (md[i] == '(') {
                            param[2] = i;
                            for (;i < md.length; i++) {
                                if (md[i] == ')') {
                                    param[3] = i;
                                    return param;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String makeLinkOrImage(byte[] mb, int[] param, boolean isLink) {
        String title = parse(slice(mb, param[0]+1, param[1]));
        String link = btos(slice(mb, param[2]+1, param[3]));
        if (isLink) {
            return "<a href=\"" + link + "\">" + title + "</a>";
        } else {
            return "<img src=\"" + link + "\" alt=\"" + title + "\">";
        }
    }
}

class ParseLine {
    public ArrayList<String> syncLines(ArrayList<String> lines) {
        lines.add("");
        for (int i = 0; i < lines.size() - 1; i++) {
            String[] line = syncLines(lines.get(i), lines.get(i+1));
            if(line[1].equals("")) {
                lines.set(i, line[0]);
                lines.remove(i+1);
                i = i -1;
            } else {
                lines.set(i, line[0]);
                lines.set(i+1, line[1]);
            }
        }
        return lines;
    }

    public String[] syncLines(String up, String down) {
        if (down.length() < 1) {
            if (up.contains("<table")) {
                return new String[]{up, "</table>"};
            }
        }

        if ((up.contains("<ol") || up.contains("<ul")) && down.contains("<li>")) {
            up = up.substring(0, up.length() - 5) + down.substring(down.indexOf("<li>"));
            return new String[] {up, ""};
        }
        boolean h1 = down.contains("=");
        if (h1|down.contains("-")) {
            if (down.substring(down.length() - 1).equals(h1 ? "=" : "-")) {
                up = (h1 ? "<h1>" : "<h2>") + up + (h1 ? "</h1>" : "</h2>");
                return new String[] {up, ""};
            }
        }

        return new String[] {up, down};
    }
}
