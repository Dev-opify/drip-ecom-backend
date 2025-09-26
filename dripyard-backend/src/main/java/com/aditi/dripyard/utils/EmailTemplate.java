package com.aditi.dripyard.utils;

public class EmailTemplate {

    private static final String BASE_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
                .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }
                .header { background-color: #000000; padding: 20px; text-align: center; }
                .header h1 { color: #ffffff; margin: 0; }
                .content { padding: 20px; color: #333333; }
                .footer { background-color: #f8f8f8; padding: 20px; text-align: center; font-size: 12px; color: #666666; }
                .button { display: inline-block; padding: 10px 20px; background-color: #000000; color: #ffffff; 
                         text-decoration: none; border-radius: 5px; margin: 10px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>%s</h1>
                </div>
                <div class="content">
                    %s
                </div>
                <div class="footer">
                    © %d Dripyard. All rights reserved.<br>
                    Contact us at: dev.dripyard@gmail.com
                </div>
            </div>
        </body>
        </html>
        """;

    public static String orderConfirmation(String orderDetails) {
        String content = String.format("""
            <h2>Thank you for your order!</h2>
            <p>We're excited to confirm your order with Dripyard.</p>
            <div class="order-details">
                %s
            </div>
            <p>If you have any questions, please don't hesitate to contact us.</p>
            """, orderDetails);

        return String.format(BASE_TEMPLATE, "Order Confirmation", content, java.time.Year.now().getValue());
    }

    public static String passwordReset(String resetToken) {
        String content = String.format("""
            <h2>Password Reset Request</h2>
            <p>You've requested to reset your password. Here's your reset token:</p>
            <p style="font-size: 18px; font-weight: bold; text-align: center; padding: 10px; 
                      background-color: #f8f8f8; border-radius: 5px;">%s</p>
            <p>If you didn't request this reset, please ignore this email or contact support.</p>
            """, resetToken);

        return String.format(BASE_TEMPLATE, "Password Reset", content, java.time.Year.now().getValue());
    }

    public static String welcomeEmail(String username) {
        String content = String.format("""
            <h2>Welcome to Dripyard!</h2>
            <p>Hello %s,</p>
            <p>Thank you for joining Dripyard. We're excited to have you as part of our community.</p>
            <p>Start exploring our collection now:</p>
            <p style="text-align: center;">
                <a href="https://dripyard.com/shop" class="button">Start Shopping</a>
            </p>
            """, username);

        return String.format(BASE_TEMPLATE, "Welcome to Dripyard", content, java.time.Year.now().getValue());
    }
}
