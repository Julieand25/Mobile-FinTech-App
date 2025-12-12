// functions/index.js
// Load environment variables FIRST, before anything else
require('dotenv').config();

const { onCall } = require('firebase-functions/v2/https');
const { onSchedule } = require('firebase-functions/v2/scheduler');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// Verify environment variables are loaded
console.log('===========================================');
console.log('Environment Check:');
console.log('Gmail User:', process.env.GMAIL_USER ? '✓ Loaded' : '✗ Missing');
console.log('Gmail Password:', process.env.GMAIL_PASSWORD ? '✓ Loaded' : '✗ Missing');
console.log('===========================================');

// Configure email transport ONCE at startup
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.GMAIL_USER,
        pass: process.env.GMAIL_PASSWORD
    }
});

/**
 * Send OTP Email - Firebase Functions v2
 */
exports.sendOtpEmail = onCall(async (request) => {
    console.log('===========================================');
    console.log('📧 sendOtpEmail function called!');
    console.log('Request data:', JSON.stringify(request.data, null, 2));
    console.log('===========================================');
    
    const { email, otp } = request.data;

    if (!email || !otp) {
        console.error('❌ Missing required fields:', { email: !!email, otp: !!otp });
        throw new Error('Email and OTP are required');
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        console.error('❌ Invalid email format:', email);
        throw new Error('Invalid email format');
    }

    console.log('✓ Validation passed');
    console.log('✓ Email:', email);
    console.log('✓ OTP:', otp);

    const mailOptions = {
        from: 'Halal Finance App <noreply@halalfinance.com>',
        to: email,
        subject: 'Your Verification Code - Halal Finance',
        html: `
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                    }
                    .header {
                        background: linear-gradient(135deg, #10B881 0%, #0E9788 100%);
                        color: white;
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                        font-weight: 600;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .otp-box {
                        background: #f8f9fa;
                        border: 2px solid #10B981;
                        border-radius: 12px;
                        padding: 30px;
                        text-align: center;
                        margin: 30px 0;
                    }
                    .otp-code {
                        font-size: 42px;
                        font-weight: bold;
                        color: #10B981;
                        letter-spacing: 10px;
                        font-family: 'Courier New', monospace;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        background-color: #f8f9fa;
                        text-align: center;
                        padding: 20px;
                        color: #6c757d;
                        font-size: 14px;
                        border-top: 1px solid #e9ecef;
                    }
                    @media only screen and (max-width: 600px) {
                        .otp-code {
                            font-size: 32px;
                            letter-spacing: 6px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Email Verification</h1>
                    </div>
                    
                    <div class="content">
                        <p>Assalamu Alaikum,</p>
                        
                        <p>Thank you for choosing <strong>Halal Finance App</strong>. To complete your verification, please use the following One-Time Password (OTP):</p>
                        
                        <div class="otp-box">
                            <div class="otp-code">${otp}</div>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Important:</strong> This code will expire in <strong>5 minutes</strong>. Do not share this code with anyone.
                        </div>
                        
                        <p>If you didn't request this verification code, please ignore this email.</p>
                        
                        <p style="margin-top: 30px;">
                            Best regards,<br>
                            <strong>Halal Finance Team</strong>
                        </p>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated message, please do not reply.</p>
                        <p>© ${new Date().getFullYear()} Halal Finance App. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        `,
        text: `
            Assalamu Alaikum,
            
            Your Halal Finance verification code is: ${otp}
            
            This code will expire in 5 minutes.
            
            If you didn't request this code, please ignore this email.
            
            Best regards,
            Halal Finance Team
        `
    };

    try {
        console.log('📤 Attempting to send email...');
        console.log('From:', mailOptions.from);
        console.log('To:', mailOptions.to);
        
        await transporter.sendMail(mailOptions);
        
        console.log('✅ Email sent successfully!');
        
        // Log to Firestore
        await admin.firestore().collection('email_logs').add({
            email: email,
            type: 'otp_verification',
            sentAt: admin.firestore.FieldValue.serverTimestamp(),
            success: true
        });
        
        console.log('✅ Email log saved to Firestore');
        console.log('===========================================');
        
        return { 
            success: true,
            message: 'OTP sent successfully'
        };
        
    } catch (error) {
        console.error('===========================================');
        console.error('❌ ERROR sending email:');
        console.error('Error type:', error.constructor.name);
        console.error('Error message:', error.message);
        console.error('Error stack:', error.stack);
        console.error('===========================================');
        
        // Log error to Firestore
        await admin.firestore().collection('email_logs').add({
            email: email,
            type: 'otp_verification',
            sentAt: admin.firestore.FieldValue.serverTimestamp(),
            success: false,
            error: error.message,
            errorStack: error.stack
        });
        
        throw new Error(`Failed to send verification email: ${error.message}`);
    }
});

// Cleanup expired OTPs daily
exports.cleanupExpiredOtps = onSchedule('every 24 hours', async (event) => {
    const now = Date.now();
    const fiveMinutesAgo = now - (5 * 60 * 1000);
    
    try {
        const expiredOtps = await admin.firestore()
            .collection('otp_verifications')
            .where('createdAt', '<', fiveMinutesAgo)
            .get();
        
        const batch = admin.firestore().batch();
        expiredOtps.docs.forEach(doc => {
            batch.delete(doc.ref);
        });
        
        await batch.commit();
        console.log(`Cleaned up ${expiredOtps.size} expired OTP documents`);
        
    } catch (error) {
        console.error('Error cleaning up OTPs:', error);
    }
});