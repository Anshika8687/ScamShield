package com.example.scamorsafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {

    private static List<Question> all() {
        List<Question> q = new ArrayList<>();

        // =====================================================
        //                       SCAMS
        // =====================================================
        q.add(new Question("SMS",
                "Dear customer, your SBI account will be BLOCKED today. Update KYC immediately: http://sbi-kyc-update.xyz/login",
                true,
                "Fake KYC threat with a suspicious link. Banks never ask you to update KYC through SMS links."));
        q.add(new Question("SMS",
                "Congratulations! You won Rs 25,00,000 in the KBC lottery. Send Rs 5,000 processing fee to claim your prize.",
                true,
                "Real lotteries never ask you to pay a fee first."));
        q.add(new Question("SMS",
                "Your electricity will be disconnected tonight at 9:30 PM. Call 98XXXXXX10 immediately to pay pending bill.",
                true,
                "Urgency and threat to make you panic. Verify only through the official app or website."));
        q.add(new Question("SMS",
                "Hi, I am from HR. Earn Rs 5,000 daily working from home. Just pay Rs 1,500 registration fee. WhatsApp now!",
                true,
                "Fake job offer. Genuine companies never charge you a fee to hire you."));
        q.add(new Question("SMS",
                "Rs 2,000 cashback received on your UPI. Enter your UPI PIN to accept the money.",
                true,
                "You never need to enter a UPI PIN to RECEIVE money. The PIN is only for sending."));
        q.add(new Question("EMAIL",
                "From: support@paypa1-security.com\nSubject: Account limited\nWe noticed unusual activity. Log in within 24 hours to avoid permanent suspension.",
                true,
                "Look at the sender's domain: 'paypa1' uses the number 1 instead of the letter l. Also it creates false urgency."));
        q.add(new Question("EMAIL",
                "From: hr@company-careers.info\nSubject: Offer Letter\nCongratulations! You are selected. Send your Aadhaar, PAN and Rs 3,000 for ID card to proceed.",
                true,
                "Fake offer letter asking for money and personal documents."));
        q.add(new Question("EMAIL",
                "Subject: Refund pending\nYour income tax refund of Rs 18,540 is ready. Click the link and enter your card details to receive it.",
                true,
                "Government departments never ask for card details to give you a refund."));
        q.add(new Question("SMS",
                "Hello, this is Amazon customer care. Please install AnyDesk so we can fix your refund problem.",
                true,
                "Screen-sharing apps give scammers full access to your phone and bank apps. Never install them for 'support'."));
        q.add(new Question("URL",
                "http://192.168.4.7/paytm/login",
                true,
                "Uses an IP address instead of a real domain, and no HTTPS."));
        q.add(new Question("URL",
                "https://amaz0n-offers.in.verify-account.com/login",
                true,
                "The real domain here is 'verify-account.com'. 'amaz0n' is only a fake subdomain with a zero instead of 'o'."));
        q.add(new Question("URL",
                "http://bit.ly/free-recharge-99",
                true,
                "Shortened link that hides the real destination, promising a freebie."));

        // ----- new scams -----
        q.add(new Question("SMS",
                "Your parcel is held at customs. Pay Rs 49 redelivery fee here to release it: http://india-post-track.top/pay",
                true,
                "Fake courier fee scam. The link is not an official India Post domain, and the small amount is meant to steal your card details."));
        q.add(new Question("SMS",
                "Your SIM will be deactivated in 2 hours as per TRAI rules. Call 1800-XXX-XXXX now to avoid disconnection.",
                true,
                "TRAI does not send such threats. Scammers use fear of a SIM block to get your personal details."));
        q.add(new Question("SMS",
                "Hey! I'm locked out of my WhatsApp. A code is coming to your phone, please send it to me quickly.",
                true,
                "Account takeover trick. Even if it looks like a friend's message, never share verification codes with anyone."));
        q.add(new Question("SMS",
                "I want to buy your sofa listed on OLX. I have sent a QR code, scan it and enter your PIN to receive the advance.",
                true,
                "Scanning a QR code and entering a PIN only SENDS money. Receiving money never needs a PIN."));
        q.add(new Question("SMS",
                "Join our VIP stock tips group. Guaranteed 300% profit in 7 days. Limited seats, deposit Rs 10,000 today!",
                true,
                "No one can guarantee profit. Fake investment groups pressure you with 'limited seats' and big promises."));
        q.add(new Question("SMS",
                "This is the Cyber Police. A parcel in your name had illegal items. You are under digital arrest. Stay on this video call and do not tell anyone.",
                true,
                "'Digital arrest' does not exist. Police never arrest people on video calls or ask you to keep it secret. Call 1930 if this happens."));
        q.add(new Question("SMS",
                "Instant loan Rs 50,000 approved! Install this app and allow access to your contacts and gallery to get the money.",
                true,
                "Predatory loan apps steal your data and later blackmail you with your contacts and photos."));
        q.add(new Question("EMAIL",
                "From: billing@netflix-payments.co\nSubject: Payment failed\nYour Netflix membership is on hold. Update your payment method within 12 hours.",
                true,
                "Fake sender domain and a short deadline. Go to the official app yourself instead of clicking email links."));
        q.add(new Question("URL",
                "https://sbi.co.in.kyc-update.info/login",
                true,
                "The real domain is the last part, 'kyc-update.info', not 'sbi.co.in'. Attackers put a real name at the start to trick you."));
        q.add(new Question("URL",
                "http://hdfcbank-secure-login.net/verify?id=48291&session=expired",
                true,
                "Fake domain with security-sounding words, no HTTPS, and a request to 'verify'."));

        // =====================================================
        //                        SAFE
        // =====================================================
        q.add(new Question("SMS",
                "483920 is your OTP for login. Do not share it with anyone. It is valid for 10 minutes. -HDFC Bank",
                false,
                "A normal OTP message. It does not ask you to click anything or call anyone. Just never share the OTP."));
        q.add(new Question("SMS",
                "Rs 500.00 debited from A/c XX1234 on 12-Sep via UPI. If not you, call the number on the back of your card.",
                false,
                "A normal transaction alert with no link and no pressure."));
        q.add(new Question("SMS",
                "Your Swiggy order #48291 has been delivered. Enjoy your meal!",
                false,
                "A simple status update with no link, no request, and no urgency."));
        q.add(new Question("EMAIL",
                "From: no-reply@github.com\nSubject: New sign-in\nA new sign-in was detected on your account. If this was you, no action is needed.",
                false,
                "Correct sender domain and no request for your password. It only informs you."));
        q.add(new Question("EMAIL",
                "From: noreply@irctc.co.in\nSubject: Ticket booked\nYour ticket PNR 4521367890 is confirmed for 14-Oct.",
                false,
                "A genuine booking confirmation from the official domain with no suspicious request."));
        q.add(new Question("URL",
                "https://www.sbi.co.in",
                false,
                "Official domain, HTTPS, and no strange characters."));
        q.add(new Question("URL",
                "https://www.amazon.in/gp/help/customer/display.html",
                false,
                "The real domain is amazon.in and the connection is HTTPS."));
        q.add(new Question("URL",
                "https://www.wikipedia.org",
                false,
                "A well-known, correctly spelled domain using HTTPS."));

        // ----- new safe -----
        q.add(new Question("SMS",
                "Your Amazon package will arrive today. Share the delivery OTP 5921 only with the delivery agent at your door.",
                false,
                "This OTP is meant for the agent in person, with no link and no request to reply. Just never share it over a call or message."));
        q.add(new Question("SMS",
                "Recharge of Rs 299 on 98XXXXXX10 is successful. Plan valid for 28 days. Thank you for choosing Jio.",
                false,
                "A normal confirmation with no link and no action needed from you."));
        q.add(new Question("SMS",
                "Your LPG cylinder booking is confirmed. Delivery expected within 2 days. -Indane",
                false,
                "A simple confirmation message with no link, no payment request, and no urgency."));
        q.add(new Question("SMS",
                "Dear student, your semester exam hall ticket is available on the college portal. Login with your usual ID to download.",
                false,
                "It asks you to use the portal you already know, with no link and no request for personal details."));
        q.add(new Question("EMAIL",
                "From: no-reply@accounts.google.com\nSubject: Security alert\nNew sign-in on a Windows device. If this was you, you don't need to do anything.",
                false,
                "Correct Google domain, and the email only informs you without asking for a password or any payment."));
        q.add(new Question("EMAIL",
                "From: statements@hdfcbank.net\nSubject: Your account statement\nYour monthly statement is attached. It is password protected with your date of birth.",
                false,
                "No links, no urgency, and no request for details. A password-protected statement is standard bank practice."));
        q.add(new Question("EMAIL",
                "From: orders@flipkart.com\nSubject: Your order has shipped\nYour order will reach you by Friday. Track it in the Flipkart app.",
                false,
                "A normal shipping update from the official domain that points you to the app rather than asking for details."));
        q.add(new Question("URL",
                "https://uidai.gov.in",
                false,
                "The official Aadhaar website. A .gov.in domain with HTTPS is a good sign."));
        q.add(new Question("URL",
                "https://www.irctc.co.in",
                false,
                "The official railway ticketing website with the correct spelling and HTTPS."));
        q.add(new Question("URL",
                "https://www.cybercrime.gov.in",
                false,
                "The official government portal for reporting cyber crime in India."));

        return q;
    }

    public static List<Question> getRandom(int count) {
        List<Question> list = all();
        Collections.shuffle(list);
        return new ArrayList<>(list.subList(0, Math.min(count, list.size())));
    }
}