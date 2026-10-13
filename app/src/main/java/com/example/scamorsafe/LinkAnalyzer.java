package com.example.scamorsafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LinkAnalyzer {

    public static class Finding {
        public final String title;
        public final String detail;

        public Finding(String title, String detail) {
            this.title = title;
            this.detail = detail;
        }
    }

    public static class Result {
        public boolean valid = false;
        public String host = "";
        public String realDomain = "";
        public String protocol = "";      // "https", "http" or "" (not given)
        public boolean isIp = false;
        public boolean trusted = false;
        public int score = 0;
        public final List<Finding> flags = new ArrayList<>();
        public final List<Finding> goods = new ArrayList<>();

        private void flag(int points, String title, String detail) {
            score += points;
            flags.add(new Finding(title, detail));
        }

        public int risk() {
            return Math.min(score, 100);
        }

        /** 0 = looks safe, 1 = suspicious, 2 = high risk */
        public int level() {
            int r = risk();
            if (r >= 60) return 2;
            if (r >= 30) return 1;
            return 0;
        }
    }

    // domains like sbi.co.in have a two-part ending
    private static final Set<String> TWO_LEVEL = new HashSet<>(Arrays.asList(
            "co.in", "gov.in", "ac.in", "org.in", "net.in", "nic.in", "res.in", "edu.in",
            "co.uk", "org.uk", "gov.uk", "ac.uk", "com.au", "co.jp", "com.br", "co.za"));

    private static final Set<String> SHORTENERS = new HashSet<>(Arrays.asList(
            "bit.ly", "tinyurl.com", "goo.gl", "t.co", "cutt.ly", "is.gd", "rb.gy",
            "shorturl.at", "ow.ly", "buff.ly", "tiny.cc", "rebrand.ly"));

    private static final Set<String> SUSPICIOUS_TLDS = new HashSet<>(Arrays.asList(
            "xyz", "top", "info", "click", "buzz", "tk", "ml", "ga", "cf", "gq", "work",
            "loan", "vip", "icu", "cyou", "monster", "site", "online"));

    private static final String[] KEYWORDS = {
            "login", "signin", "verify", "update", "secure", "account", "bank", "free",
            "prize", "gift", "kyc", "confirm", "password", "wallet", "reward", "otp",
            "claim", "refund", "offer", "bonus", "lottery", "suspend", "unlock", "limited"
    };

    // {brand keyword, official domains...}
    private static final String[][] BRANDS = {
            {"sbi", "sbi.co.in", "onlinesbi.sbi", "sbicard.com", "sbilife.co.in"},
            {"hdfc", "hdfcbank.com", "hdfc.com", "hdfclife.com"},
            {"icici", "icicibank.com", "iciciprulife.com"},
            {"axis", "axisbank.com"},
            {"paytm", "paytm.com"},
            {"phonepe", "phonepe.com"},
            {"google", "google.com", "google.co.in", "gmail.com"},
            {"amazon", "amazon.in", "amazon.com"},
            {"flipkart", "flipkart.com"},
            {"paypal", "paypal.com"},
            {"netflix", "netflix.com"},
            {"irctc", "irctc.co.in"},
            {"uidai", "uidai.gov.in"},
            {"whatsapp", "whatsapp.com"},
            {"instagram", "instagram.com"},
            {"facebook", "facebook.com"},
            {"microsoft", "microsoft.com"},
            {"apple", "apple.com", "icloud.com"},
            {"jio", "jio.com", "jiosaavn.com"},
            {"airtel", "airtel.in"},
            {"indiapost", "indiapost.gov.in"},
            {"swiggy", "swiggy.com"},
            {"zomato", "zomato.com"}
    };

    private static final Set<String> TRUSTED = new HashSet<>();

    static {
        for (String[] b : BRANDS) {
            for (int i = 1; i < b.length; i++) TRUSTED.add(b[i]);
        }
        TRUSTED.addAll(Arrays.asList(
                "wikipedia.org", "github.com", "youtube.com", "linkedin.com", "cybercrime.gov.in"));
    }

    public static Result analyze(String input) {
        Result r = new Result();
        String lower = input.trim().toLowerCase(Locale.ROOT);
        if (lower.isEmpty() || lower.matches("(?s).*\\s.*")) return r;

        // ----- split the link into parts -----
        if (lower.startsWith("https://")) r.protocol = "https";
        else if (lower.startsWith("http://")) r.protocol = "http";

        String rest = lower.replaceFirst("^[a-z][a-z0-9+.\\-]*://", "");
        int cut = rest.length();
        for (char ch : new char[]{'/', '?', '#'}) {
            int i = rest.indexOf(ch);
            if (i != -1 && i < cut) cut = i;
        }
        String authority = rest.substring(0, cut);
        boolean hasAt = authority.contains("@");
        String hostPort = hasAt ? authority.substring(authority.lastIndexOf('@') + 1) : authority;

        String host = hostPort;
        String port = "";
        int colon = hostPort.lastIndexOf(':');
        if (colon != -1 && hostPort.substring(colon + 1).matches("\\d+")) {
            host = hostPort.substring(0, colon);
            port = hostPort.substring(colon + 1);
        }

        boolean isIp = host.matches("^\\d{1,3}(\\.\\d{1,3}){3}$");
        if (host.isEmpty() || (!isIp && !host.contains("."))) return r;   // not a link

        r.valid = true;
        r.host = host;
        r.isIp = isIp;

        // ----- find the real domain -----
        String[] labels = host.split("\\.");
        String real = host;
        int realLabels = labels.length;
        if (!isIp && labels.length >= 2) {
            String lastTwo = labels[labels.length - 2] + "." + labels[labels.length - 1];
            realLabels = (labels.length >= 3 && TWO_LEVEL.contains(lastTwo)) ? 3 : 2;
            real = joinLast(labels, realLabels);
        }
        r.realDomain = real;
        int subdomains = isIp ? 0 : labels.length - realLabels;
        r.trusted = !isIp && (TRUSTED.contains(real) || real.endsWith(".gov.in") || real.endsWith(".nic.in"));

        // ----- red flags -----
        if (hasAt) {
            r.flag(25, "Contains '@' in the address",
                    "The browser ignores everything before the '@', so it can hide the real destination.");
        }
        if (isIp) {
            r.flag(30, "Uses an IP address",
                    "Real companies use a name like sbi.co.in, not a string of numbers.");
        }
        if (r.protocol.equals("http")) {
            r.flag(20, "Not secure (HTTP)",
                    "Anything you type can be read by others. Real banks always use HTTPS.");
        }
        if (SHORTENERS.contains(real)) {
            r.flag(20, "Shortened link",
                    "A short link hides where it really goes. Never open short links from unknown senders.");
        }

        boolean hidden = false;
        if (!r.trusted && !isIp) {
            outer:
            for (String[] b : BRANDS) {
                for (int i = 1; i < b.length; i++) {
                    if (containsDomain(host, b[i]) && !real.equals(b[i])) {
                        r.flag(30, "Real website name hidden inside a fake address",
                                "The address contains \"" + b[i] + "\", but the website's real domain is "
                                        + real + ". Only the last part before the first '/' counts.");
                        hidden = true;
                        break outer;
                    }
                }
            }
        }

        if (!r.trusted && !isIp && !hidden) {
            String[] tokens = host.split("[.\\-]");
            boolean done = false;
            for (int t = 0; t < tokens.length - 1 && !done; t++) {   // last token is the ending (.com)
                String raw = tokens[t];
                String norm = unleet(raw);
                for (String[] b : BRANDS) {
                    if (norm.startsWith(b[0])) {
                        r.flag(30, "Pretends to be " + b[0].toUpperCase(Locale.ROOT),
                                "The name is used in the address, but the real domain is " + real
                                        + ", which is not an official " + b[0].toUpperCase(Locale.ROOT) + " website.");
                        if (!raw.equals(norm)) {
                            r.flag(15, "Look-alike spelling",
                                    "\"" + raw + "\" uses numbers instead of letters to copy \"" + b[0] + "\".");
                        }
                        done = true;
                        break;
                    }
                }
            }
        }

        boolean nonAscii = false;
        for (char ch : host.toCharArray()) {
            if (ch > 127) {
                nonAscii = true;
                break;
            }
        }
        if (nonAscii || host.contains("xn--")) {
            r.flag(20, "Look-alike characters",
                    "The address uses special characters that imitate normal letters.");
        }

        if (!isIp) {
            String tld = labels[labels.length - 1];
            if (SUSPICIOUS_TLDS.contains(tld)) {
                r.flag(15, "Suspicious ending (." + tld + ")",
                        "Cheap endings like this are very common in scam websites.");
            }
        }

        if (subdomains >= 3) {
            r.flag(10, "Too many subdomains (" + subdomains + ")",
                    "Scammers stack extra words in front of the real domain to look official.");
        }

        if (host.length() - host.replace("-", "").length() >= 3) {
            r.flag(10, "Many hyphens in the name",
                    "Names like secure-login-verify-account are a common phishing pattern.");
        }

        if (!port.isEmpty() && !port.equals("80") && !port.equals("443")) {
            r.flag(10, "Unusual port (:" + port + ")",
                    "Normal websites rarely use a custom port number.");
        }

        if (!r.trusted) {
            List<String> found = new ArrayList<>();
            for (String k : KEYWORDS) {
                if (lower.contains(k)) found.add(k);
            }
            if (!found.isEmpty()) {
                r.flag(Math.min(10 * found.size(), 30), "Pressure words in the link",
                        "Words like " + found + " are often used to trick you into entering details.");
            }
            if (lower.length() > 75) {
                r.flag(10, "Very long address (" + lower.length() + " characters)",
                        "Long links are used to hide the suspicious part.");
            }
        }

        // ----- good signs -----
        if (r.protocol.equals("https")) {
            r.goods.add(new Finding("Uses HTTPS",
                    "The connection is encrypted. This alone does not prove a site is safe, because scammers use HTTPS too."));
        }
        if (r.trusted) {
            r.goods.add(new Finding("Known trusted domain",
                    real + " matches an official or well-known website."));
        }

        return r;
    }

    private static String joinLast(String[] labels, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = labels.length - count; i < labels.length; i++) {
            if (sb.length() > 0) sb.append('.');
            sb.append(labels[i]);
        }
        return sb.toString();
    }

    private static boolean containsDomain(String host, String domain) {
        return host.equals(domain)
                || host.startsWith(domain + ".")
                || host.endsWith("." + domain)
                || host.contains("." + domain + ".");
    }

    /** amaz0n -> amazon, paypa1 -> paypal */
    private static String unleet(String s) {
        return s.replace('0', 'o').replace('1', 'l').replace('3', 'e')
                .replace('4', 'a').replace('5', 's').replace('7', 't');
    }
}