package com.example.scamorsafe;

import java.util.ArrayList;
import java.util.List;

public class ChatStories {

    public static List<ChatStory> all() {
        List<ChatStory> list = new ArrayList<>();
        list.add(bankAgent());
        list.add(digitalArrest());
        list.add(fakeJob());
        return list;
    }

    // small helpers to keep the stories short
    private static String[] m(String... messages) {
        return messages;
    }

    private static ChatStory.Choice c(String text, String next) {
        return new ChatStory.Choice(text, next);
    }

    // ------------------------------------------------------------------
    private static ChatStory bankAgent() {
        return new ChatStory("Fake Bank Agent", "🏦", "SBI Customer Care", "start")

                .add("start", ChatStory.Node.talk(
                        m("Hello sir, I am calling from SBI Customer Care.",
                                "Your KYC has expired and your account will be BLOCKED in 30 minutes!",
                                "Please confirm, are you the account holder?"),
                        c("Yes! Please help me, what should I do?", "install"),
                        c("I'll call the number on my debit card to check", "verify"),
                        c("Ignore this and block the number", "block")))

                .add("install", ChatStory.Node.talk(
                        m("Good. To save your account, install the app 'QuickSupport' from the link I am sending.",
                                "Then tell me the code it shows on your screen."),
                        c("Install the app", "otp"),
                        c("This feels wrong. I'll stop here", "stop")))

                .add("otp", ChatStory.Node.talk(
                        m("Thank you sir, the app is connected ✅",
                                "Now an OTP has come to your phone. Please read it out to me quickly!"),
                        c("Read out the OTP", "lost"),
                        c("Banks never ask for an OTP. No!", "safeOtp")))

                .add("verify", ChatStory.Node.safe(
                        m("Sir, there is no time to call anyone! Your account will be blocked right now!!"),
                        "Great move! Scammers hate being checked, so they add pressure. Always call the number printed on your card or use the official app."))

                .add("block", ChatStory.Node.safe(
                        m("Hello? Sir? Please respond, your money is at risk!!"),
                        "Perfect. Blocking and ignoring unknown callers who threaten you is the safest response."))

                .add("stop", ChatStory.Node.safe(
                        m("Sir, you will lose all your money if you stop now!"),
                        "Good instinct. Real banks never ask you to install apps from links. Threats of blocking are a classic scare tactic."))

                .add("safeOtp", ChatStory.Node.safe(
                        m("Sir, it is only for verification, just tell me the six digits!"),
                        "You stopped the scam in time. But you installed a remote-access app earlier, so uninstall it now and change your banking passwords. Never install apps from links."))

                .add("lost", ChatStory.Node.scammed(
                        m("Thank you sir. Verification complete ✅",
                                "💸 SMS: Rs 47,500 debited from your account XX1234."),
                        "The remote-access app showed your screen to the scammer, and the OTP let them approve the payment. Banks never ask you to install apps or share OTPs. If this happens, call 1930 immediately."));
    }

    // ------------------------------------------------------------------
    private static ChatStory digitalArrest() {
        return new ChatStory("Digital Arrest Call", "👮", "Officer Rathore (CBI)", "start")

                .add("start", ChatStory.Node.talk(
                        m("This is CBI Cyber Cell. A parcel in your name was caught with illegal items.",
                                "A case is registered against you. You are now under DIGITAL ARREST.",
                                "Do not disconnect this call. Do not tell anyone, not even your family."),
                        c("Please sir, I'm scared. What should I do?", "scared"),
                        c("Digital arrest is not a real thing. Hanging up.", "hangup"),
                        c("Let me talk to my family first", "family")))

                .add("scared", ChatStory.Node.talk(
                        m("Cooperate and nothing will happen to you.",
                                "Show your Aadhaar and PAN card on video now.",
                                "Then transfer your savings to an 'RBI safe account' for verification. It will be returned after the investigation."),
                        c("Show the documents and transfer the money", "lostMoney"),
                        c("Share only my Aadhaar, not any money", "lostData"),
                        c("Refuse and call 1930", "report")))

                .add("hangup", ChatStory.Node.safe(
                        m("Do not hang up! You will be arrested!",
                                "(The call ends. The same caller keeps ringing from different numbers.)"),
                        "Correct! 'Digital arrest' does not exist. No police or agency arrests anyone over a call or video call. Block the numbers and report them."))

                .add("family", ChatStory.Node.safe(
                        m("NO! This is a secret national case! Telling anyone is a crime!!"),
                        "Excellent. Scammers want you isolated and scared. The demand for secrecy is a huge red flag. Talking to someone you trust breaks the spell."))

                .add("report", ChatStory.Node.safe(
                        m("You are making a big mistake! I will send officers!",
                                "(The caller disconnects.)"),
                        "That is the right call. Report such calls on 1930 or cybercrime.gov.in. No genuine officer asks you to move money to a 'safe account'."))

                .add("lostMoney", ChatStory.Node.scammed(
                        m("Transfer received. Thank you.",
                                "💸 Rs 2,10,000 transferred. The 'officer' disconnects and the number is switched off."),
                        "No agency asks you to transfer money for 'verification'. There is no RBI safe account. Money sent to such accounts is moved out within minutes, so call 1930 right away if it happens."))

                .add("lostData", ChatStory.Node.scammed(
                        m("Good. We have your details now. Wait for the next call.",
                                "(Later: a loan is taken in your name and fake accounts are opened with your Aadhaar.)"),
                        "Sharing Aadhaar or PAN also hurts. Scammers use identity documents for loans, SIM cards and fake accounts. Never share ID documents with a caller."));
    }

    // ------------------------------------------------------------------
    private static ChatStory fakeJob() {
        return new ChatStory("Fake Job Offer", "💼", "Priya HR (TalentHub)", "start")

                .add("start", ChatStory.Node.talk(
                        m("Hi! We saw your profile 🎉",
                                "Work from home and earn Rs 4,000 per day by liking YouTube videos.",
                                "No experience needed. Interested?"),
                        c("Sure, tell me more!", "tasks"),
                        c("Sounds too good to be true. No thanks.", "no")))

                .add("tasks", ChatStory.Node.talk(
                        m("Great! Do 3 small tasks first and you get Rs 150 for each.",
                                "💰 Rs 450 credited to your UPI!",
                                "See, it's real! Now join our VIP group. Deposit Rs 2,000 and get Rs 3,000 back in 10 minutes."),
                        c("Deposit Rs 2,000", "vip"),
                        c("Take the Rs 450 and stop here", "stop")))

                .add("vip", ChatStory.Node.talk(
                        m("💰 Rs 3,000 credited! See, you profited Rs 1,000!",
                                "Now the PRO group: deposit Rs 20,000 and get Rs 40,000 back. Only 10 minutes left, hurry!"),
                        c("Deposit Rs 20,000", "lost"),
                        c("This is a trap. Stop and report it", "report")))

                .add("no", ChatStory.Node.safe(
                        m("Many people earn this much, don't miss out! 😊"),
                        "Smart. Easy money for simple tasks is a red flag. Real jobs pay for real skills and never charge you to start."))

                .add("stop", ChatStory.Node.safe(
                        m("Sir, why stop? Your earnings are waiting!"),
                        "You got out in time. The first small payouts are bait to build trust before the big deposit. Block the number and do not reply."))

                .add("report", ChatStory.Node.safe(
                        m("Don't be silly, it's guaranteed profit! Send now!"),
                        "Well done. Task scams pay small amounts first, then push bigger deposits until you are out of money. Reporting on 1930 or cybercrime.gov.in helps others too."))

                .add("lost", ChatStory.Node.scammed(
                        m("Payment received ✅",
                                "Oh no, your withdrawal is stuck. Pay Rs 10,000 'tax' to release your Rs 40,000.",
                                "(The group disappears soon after.)"),
                        "This is a classic task scam. Small early payouts build trust, then the deposits grow, and then come fake fees to withdraw. You never get the money back. Call 1930 immediately if it happens."));
    }
}