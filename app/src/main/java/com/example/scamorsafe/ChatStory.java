package com.example.scamorsafe;

import java.util.HashMap;
import java.util.Map;

public class ChatStory {

    public static class Choice {
        public final String text;   // what you reply
        public final String next;   // id of the next node

        public Choice(String text, String next) {
            this.text = text;
            this.next = next;
        }
    }

    public static class Node {
        public final String[] messages;   // what the scammer sends
        public final Choice[] choices;
        public final int ending;          // 0 = story goes on, 1 = safe ending, 2 = scammed ending
        public final String lesson;

        private Node(String[] messages, Choice[] choices, int ending, String lesson) {
            this.messages = messages;
            this.choices = choices;
            this.ending = ending;
            this.lesson = lesson;
        }

        public static Node talk(String[] messages, Choice... choices) {
            return new Node(messages, choices, 0, "");
        }

        public static Node safe(String[] messages, String lesson) {
            return new Node(messages, new Choice[0], 1, lesson);
        }

        public static Node scammed(String[] messages, String lesson) {
            return new Node(messages, new Choice[0], 2, lesson);
        }
    }

    public final String title;
    public final String emoji;
    public final String contact;
    public final String start;
    public final Map<String, Node> nodes = new HashMap<>();

    public ChatStory(String title, String emoji, String contact, String start) {
        this.title = title;
        this.emoji = emoji;
        this.contact = contact;
        this.start = start;
    }

    public ChatStory add(String id, Node node) {
        nodes.put(id, node);
        return this;
    }
}