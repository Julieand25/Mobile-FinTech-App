// functions/index.js
const { onCall } = require('firebase-functions/v2/https');
const { onSchedule } = require('firebase-functions/v2/scheduler');
const { onRequest } = require('firebase-functions/v2/https');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
const axios = require('axios');

// ✅ ALWAYS load .env (recommended by Firebase for new projects)
require('dotenv').config();

admin.initializeApp();

// Get credentials from environment
const getGmailCredentials = () => {
    return {
        user: process.env.GMAIL_USER,
        password: process.env.GMAIL_PASSWORD
    };
};

// ✨ Get Finverse credentials from environment
const getFinverseCredentials = () => {
    const clientId = process.env.FINVERSE_CLIENT_ID;
    const clientSecret = process.env.FINVERSE_CLIENT_SECRET;
    const redirectUri = process.env.FINVERSE_REDIRECT_URI;
    
    console.log('🔍 Finverse Config Check:');
    console.log('Client ID exists:', !!clientId);
    console.log('Client Secret exists:', !!clientSecret);
    console.log('Redirect URI:', redirectUri);
    
    return {
        clientId: clientId,
        clientSecret: clientSecret,
        redirectUri: redirectUri,
        baseUrl: 'https://api.prod.finverse.net'
    };
};

console.log('==========================================');
console.log('Function initialization');
console.log('Gmail User configured:', !!process.env.GMAIL_USER);
console.log('Gmail Password configured:', !!process.env.GMAIL_PASSWORD);
console.log('Finverse Client ID configured:', !!process.env.FINVERSE_CLIENT_ID);
console.log('Finverse Client Secret configured:', !!process.env.FINVERSE_CLIENT_SECRET);
console.log('==========================================');

/**
 * Send OTP Email - Firebase Functions v2
 * IMPORTANT: This function allows unauthenticated calls for user registration
 */
exports.sendOtpEmail = onCall(
    {
        // Allow unauthenticated calls (needed for registration)
        invoker: 'public'
    },
    async (request) => {
        console.log('==========================================');
        console.log('📧 sendOtpEmail called!');
        console.log('Timestamp:', new Date().toISOString());
        console.log('Authenticated:', !!request.auth);
        console.log('==========================================');
        
        const { email, otp } = request.data;

        // Validate input
        if (!email || !otp) {
            console.error('❌ Missing required fields');
            throw new Error('Email and OTP are required');
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            console.error('❌ Invalid email format:', email);
            throw new Error('Invalid email format');
        }

        console.log('✅ Validation passed');
        console.log('Target email:', email);
        console.log('OTP length:', otp.length);

        // Get credentials
        const credentials = getGmailCredentials();
        
        if (!credentials.user || !credentials.password) {
            console.error('❌ Gmail credentials not configured');
            throw new Error('Email service not configured properly');
        }

        console.log('✅ Gmail credentials loaded');
        console.log('Gmail user:', credentials.user);

        // Configure transporter
        console.log('Creating email transporter...');
        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: credentials.user,
                pass: credentials.password
            }
        });

        const mailOptions = {
            from: `Halal Finance App <${credentials.user}>`,
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
            console.log('📤 Sending email...');
            console.log('From:', mailOptions.from);
            console.log('To:', mailOptions.to);
            
            const info = await transporter.sendMail(mailOptions);
            
            console.log('✅✅✅ EMAIL SENT SUCCESSFULLY!');
            console.log('Message ID:', info.messageId);
            console.log('Response:', info.response);
            
            // Log to Firestore
            await admin.firestore().collection('email_logs').add({
                email: email,
                type: 'otp_verification',
                sentAt: admin.firestore.FieldValue.serverTimestamp(),
                success: true,
                messageId: info.messageId
            });
            
            console.log('✅ Logged to Firestore');
            console.log('==========================================');
            
            return { 
                success: true,
                message: 'OTP sent successfully',
                messageId: info.messageId
            };
            
        } catch (error) {
            console.error('==========================================');
            console.error('❌❌❌ FAILED TO SEND EMAIL');
            console.error('Error type:', error.constructor.name);
            console.error('Error message:', error.message);
            console.error('Error code:', error.code);
            console.error('Error response:', error.response);
            console.error('==========================================');
            
            // Log error to Firestore
            await admin.firestore().collection('email_logs').add({
                email: email,
                type: 'otp_verification',
                sentAt: admin.firestore.FieldValue.serverTimestamp(),
                success: false,
                error: error.message,
                errorCode: error.code
            });
            
            throw new Error(`Failed to send verification email: ${error.message}`);
        }
    }
);

/**
 * ✨ Reset User Password - Firebase Functions v2
 * This function allows password reset after OTP verification
 * IMPORTANT: Requires OTP verification token for security
 */
exports.resetUserPassword = onCall(
    {
        // Allow unauthenticated calls (user is resetting password, not logged in)
        invoker: 'public'
    },
    async (request) => {
        console.log('==========================================');
        console.log('🔑 resetUserPassword called!');
        console.log('Timestamp:', new Date().toISOString());
        console.log('==========================================');
        
        const { email, newPassword, otpVerificationToken } = request.data;

        // Validate input
        if (!email || !newPassword || !otpVerificationToken) {
            console.error('❌ Missing required fields');
            throw new Error('Email, new password, and verification token are required');
        }

        // Validate email format
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            console.error('❌ Invalid email format:', email);
            throw new Error('Invalid email format');
        }

        // Validate password strength
        if (newPassword.length < 6) {
            console.error('❌ Password too weak');
            throw new Error('Password must be at least 6 characters long');
        }

        console.log('✅ Input validation passed');
        console.log('Target email:', email);

        try {
            // Step 1: Verify the OTP token from Firestore
            console.log('🔍 Verifying OTP token...');
            const otpDoc = await admin.firestore()
                .collection('password_reset_tokens')
                .doc(email)
                .get();

            if (!otpDoc.exists) {
                console.error('❌ No password reset token found');
                throw new Error('Invalid or expired verification. Please request a new OTP.');
            }

            const tokenData = otpDoc.data();
            const now = Date.now();
            const tokenAge = now - tokenData.createdAt;
            const fifteenMinutes = 15 * 60 * 1000; // 15 minutes in milliseconds

            // Check if token matches
            if (tokenData.token !== otpVerificationToken) {
                console.error('❌ Token mismatch');
                throw new Error('Invalid verification token');
            }

            // Check if token is expired (15 minutes)
            if (tokenAge > fifteenMinutes) {
                console.error('❌ Token expired');
                // Delete expired token
                await admin.firestore()
                    .collection('password_reset_tokens')
                    .doc(email)
                    .delete();
                throw new Error('Verification token expired. Please request a new OTP.');
            }

            console.log('✅ OTP token verified successfully');

            // Step 2: Get user by email
            console.log('🔍 Finding user in Firebase Auth...');
            const userRecord = await admin.auth().getUserByEmail(email);
            console.log('✅ User found:', userRecord.uid);

            // Step 3: Update password using Admin SDK
            console.log('🔄 Updating password...');
            await admin.auth().updateUser(userRecord.uid, {
                password: newPassword
            });
            console.log('✅✅✅ PASSWORD UPDATED SUCCESSFULLY!');

            // Step 4: Delete the used token
            await admin.firestore()
                .collection('password_reset_tokens')
                .doc(email)
                .delete();
            console.log('✅ Token deleted');

            // Step 5: Log the password reset
            await admin.firestore().collection('password_reset_logs').add({
                email: email,
                uid: userRecord.uid,
                resetAt: admin.firestore.FieldValue.serverTimestamp(),
                success: true
            });
            console.log('✅ Logged to Firestore');

            console.log('==========================================');
            
            return { 
                success: true,
                message: 'Password reset successfully'
            };
            
        } catch (error) {
            console.error('==========================================');
            console.error('❌❌❌ FAILED TO RESET PASSWORD');
            console.error('Error type:', error.constructor.name);
            console.error('Error message:', error.message);
            console.error('Error code:', error.code);
            console.error('==========================================');
            
            // Log error to Firestore
            await admin.firestore().collection('password_reset_logs').add({
                email: email,
                resetAt: admin.firestore.FieldValue.serverTimestamp(),
                success: false,
                error: error.message,
                errorCode: error.code
            });
            
            throw new Error(error.message || 'Failed to reset password');
        }
    }
);

// =============================================================================
// ✨ NEW: FINVERSE INTEGRATION FUNCTIONS
// =============================================================================

/**
 * Get Finverse Link URL (Corrected Method)
 * Returns the URL to open for bank linking
 */
exports.getFinverseConnectUrl = onCall(
    {
        // Require authentication
        enforceAppCheck: false
    },
    async (request) => {
        console.log('==========================================');
        console.log('🔗 getFinverseConnectUrl called!');
        console.log('==========================================');
        
        if (!request.auth) {
            throw new Error('Authentication required');
        }
        
        const userId = request.auth.uid;
        const credentials = getFinverseCredentials();
        
        if (!credentials.clientId || !credentials.clientSecret) {
            throw new Error('Finverse credentials not configured');
        }
        
        try {
            // Step 1: Get Customer Access Token
            console.log('🔑 Getting customer access token...');
            const tokenResponse = await axios.post(
                `${credentials.baseUrl}/auth/token`,
                new URLSearchParams({
                    client_id: credentials.clientId,
                    client_secret: credentials.clientSecret,
                    grant_type: 'client_credentials'
                }),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }
            );
            
            const customerAccessToken = tokenResponse.data.access_token;
            console.log('✅ Customer access token obtained');
            
            // Step 2: Generate Link Token
            console.log('🔗 Generating link token...');
            const linkTokenResponse = await axios.post(
                `${credentials.baseUrl}/link/token`,
                {
                    client_id: credentials.clientId,
                    user_id: userId,
                    redirect_uri: credentials.redirectUri,
                    state: userId,
                    response_mode: 'query',
                    response_type: 'code',
                    grant_type: 'client_credentials',
                    products_requested: 'DATA'
                },
                {
                    headers: {
                        'Authorization': `Bearer ${customerAccessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            const linkUrl = linkTokenResponse.data.link_url;
            console.log('✅ Link URL generated:', linkUrl);
            
            return {
                success: true,
                connectUrl: linkUrl
            };
            
        } catch (error) {
            console.error('❌ Error generating link URL:', error.response?.data || error.message);
            throw new Error(`Failed to generate link URL: ${error.message}`);
        }
    }
);

/**
 * Finverse Callback Handler
 * Handles the redirect from Finverse after user links their bank
 */
exports.finverseCallback = onRequest(
    {
        cors: true
    },
    async (req, res) => {
        console.log('==========================================');
        console.log('🔄 finverseCallback called!');
        console.log('Query params:', req.query);
        console.log('==========================================');
        
        const { code, state: userId } = req.query;
        
        if (!code) {
            return res.status(400).send(`
                <html>
                    <body style="font-family: Arial; text-align: center; padding: 50px;">
                        <h2 style="color: #EF5350;">❌ Bank Linking Failed</h2>
                        <p>No authorization code received.</p>
                    </body>
                </html>
            `);
        }
        
        try {
            const credentials = getFinverseCredentials();
            
            // Exchange code for access token
            console.log('🔑 Exchanging code for access token...');
            const tokenResponse = await axios.post(
                `${credentials.baseUrl}/v1/oauth/token`,
                {
                    grant_type: 'authorization_code',
                    code: code,
                    redirect_uri: credentials.redirectUri
                },
                {
                    auth: {
                        username: credentials.clientId,
                        password: credentials.clientSecret
                    },
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            );
            
            const { access_token, refresh_token, expires_in } = tokenResponse.data;
            console.log('✅ Access token received');
            
            // Store token in Firestore
            await admin.firestore()
                .collection('finverse_tokens')
                .doc(userId)
                .set({
                    accessToken: access_token,
                    refreshToken: refresh_token,
                    expiresAt: admin.firestore.Timestamp.fromMillis(
                        Date.now() + (expires_in * 1000)
                    ),
                    linkedAt: admin.firestore.FieldValue.serverTimestamp()
                });
            
            console.log('✅ Token stored for user:', userId);
            
            // Fetch and store account details immediately
            try {
                const accountsResponse = await axios.get(
                    `${credentials.baseUrl}/v1/accounts`,
                    {
                        headers: {
                            'Authorization': `Bearer ${access_token}`
                        }
                    }
                );
                
                const accounts = accountsResponse.data.accounts || [];
                console.log(`✅ Fetched ${accounts.length} accounts`);
                
                // Store accounts in Firestore
                if (accounts.length > 0) {
                    await admin.firestore()
                        .collection('finverse_accounts')
                        .doc(userId)
                        .set({
                            accounts: accounts,
                            lastSynced: admin.firestore.FieldValue.serverTimestamp()
                        });
                    console.log('✅ Accounts stored');
                }
            } catch (accountError) {
                console.error('⚠️ Failed to fetch accounts initially:', accountError.message);
                // Don't fail the linking process if this fails
            }
            
            // Success page
            res.send(`
                <html>
                    <body style="font-family: Arial; text-align: center; padding: 50px;">
                        <div style="max-width: 400px; margin: 0 auto;">
                            <div style="width: 80px; height: 80px; background: #10B881; border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;">
                                <span style="color: white; font-size: 40px;">✓</span>
                            </div>
                            <h2 style="color: #10B881;">Bank Account Linked!</h2>
                            <p style="color: #666;">Your bank account has been successfully connected.</p>
                            <p style="color: #999; font-size: 14px; margin-top: 30px;">You can close this window and return to the app.</p>
                        </div>
                        <script>
                            // Try to close after 2 seconds
                            setTimeout(() => {
                                window.close();
                            }, 2000);
                        </script>
                    </body>
                </html>
            `);
            
        } catch (error) {
            console.error('❌ Token exchange error:', error.response?.data || error.message);
            
            res.status(500).send(`
                <html>
                    <body style="font-family: Arial; text-align: center; padding: 50px;">
                        <h2 style="color: #EF5350;">❌ Bank Linking Failed</h2>
                        <p>Unable to complete bank linking. Please try again.</p>
                        <p style="color: #999; font-size: 12px;">${error.message}</p>
                    </body>
                </html>
            `);
        }
    }
);

/**
 * Get Bank Accounts
 * Fetches accounts from Finverse for the authenticated user
 */
exports.getFinverseAccounts = onCall(
    {
        enforceAppCheck: false
    },
    async (request) => {
        console.log('==========================================');
        console.log('📊 getFinverseAccounts called!');
        console.log('==========================================');
        
        if (!request.auth) {
            throw new Error('Authentication required');
        }
        
        const userId = request.auth.uid;
        
        try {
            const credentials = getFinverseCredentials();
            
            // Get token from Firestore
            const tokenDoc = await admin.firestore()
                .collection('finverse_tokens')
                .doc(userId)
                .get();
            
            if (!tokenDoc.exists) {
                return {
                    success: false,
                    hasLinkedAccount: false,
                    message: 'No linked account found'
                };
            }
            
            const tokenData = tokenDoc.data();
            
            // Check if token is expired
            if (tokenData.expiresAt.toMillis() < Date.now()) {
                console.log('⚠️ Token expired, needs refresh');
                // TODO: Implement token refresh
                throw new Error('Access token expired');
            }
            
            // Fetch accounts from Finverse
            const response = await axios.get(
                `${credentials.baseUrl}/v1/accounts`,
                {
                    headers: {
                        'Authorization': `Bearer ${tokenData.accessToken}`
                    }
                }
            );
            
            const accounts = response.data.accounts || [];
            console.log(`✅ Fetched ${accounts.length} accounts`);
            
            // Update cache in Firestore
            await admin.firestore()
                .collection('finverse_accounts')
                .doc(userId)
                .set({
                    accounts: accounts,
                    lastSynced: admin.firestore.FieldValue.serverTimestamp()
                }, { merge: true });
            
            return {
                success: true,
                hasLinkedAccount: true,
                accounts: accounts
            };
            
        } catch (error) {
            console.error('❌ Error fetching accounts:', error.message);
            throw new Error(`Failed to fetch accounts: ${error.message}`);
        }
    }
);

/**
 * Get Transactions
 * Fetches transactions from Finverse
 */
exports.getFinverseTransactions = onCall(
    {
        enforceAppCheck: false
    },
    async (request) => {
        console.log('==========================================');
        console.log('💳 getFinverseTransactions called!');
        console.log('==========================================');
        
        if (!request.auth) {
            throw new Error('Authentication required');
        }
        
        const userId = request.auth.uid;
        const { accountId } = request.data || {};
        
        try {
            const credentials = getFinverseCredentials();
            
            // Get token from Firestore
            const tokenDoc = await admin.firestore()
                .collection('finverse_tokens')
                .doc(userId)
                .get();
            
            if (!tokenDoc.exists) {
                throw new Error('No linked account found');
            }
            
            const tokenData = tokenDoc.data();
            
            // Build URL
            const url = accountId 
                ? `${credentials.baseUrl}/v1/accounts/${accountId}/transactions`
                : `${credentials.baseUrl}/v1/transactions`;
            
            // Fetch transactions
            const response = await axios.get(url, {
                headers: {
                    'Authorization': `Bearer ${tokenData.accessToken}`
                }
            });
            
            const transactions = response.data.transactions || [];
            console.log(`✅ Fetched ${transactions.length} transactions`);
            
            // Store transactions in Firestore
            await admin.firestore()
                .collection('finverse_transactions')
                .doc(userId)
                .set({
                    transactions: transactions,
                    lastSynced: admin.firestore.FieldValue.serverTimestamp()
                }, { merge: true });
            
            return {
                success: true,
                transactions: transactions,
                count: transactions.length
            };
            
        } catch (error) {
            console.error('❌ Error fetching transactions:', error.message);
            throw new Error(`Failed to fetch transactions: ${error.message}`);
        }
    }
);

/**
 * Check Finverse Link Status
 * Checks if user has linked a bank account
 */
exports.checkFinverseStatus = onCall(
    {
        enforceAppCheck: false
    },
    async (request) => {
        if (!request.auth) {
            throw new Error('Authentication required');
        }
        
        const userId = request.auth.uid;
        
        try {
            const tokenDoc = await admin.firestore()
                .collection('finverse_tokens')
                .doc(userId)
                .get();
            
            const hasLinkedAccount = tokenDoc.exists;
            
            return {
                success: true,
                hasLinkedAccount: hasLinkedAccount,
                userId: userId
            };
            
        } catch (error) {
            console.error('❌ Error checking status:', error.message);
            throw new Error('Failed to check link status');
        }
    }
);

/**
 * Unlink Finverse Account
 * Removes bank account connection
 */
exports.unlinkFinverseAccount = onCall(
    {
        enforceAppCheck: false
    },
    async (request) => {
        console.log('==========================================');
        console.log('🔓 unlinkFinverseAccount called!');
        console.log('==========================================');
        
        if (!request.auth) {
            throw new Error('Authentication required');
        }
        
        const userId = request.auth.uid;
        
        try {
            // Delete token
            await admin.firestore()
                .collection('finverse_tokens')
                .doc(userId)
                .delete();
            
            // Delete cached accounts
            await admin.firestore()
                .collection('finverse_accounts')
                .doc(userId)
                .delete();
            
            // Delete cached transactions
            await admin.firestore()
                .collection('finverse_transactions')
                .doc(userId)
                .delete();
            
            console.log('✅ Account unlinked for user:', userId);
            
            return {
                success: true,
                message: 'Account unlinked successfully'
            };
            
        } catch (error) {
            console.error('❌ Error unlinking account:', error.message);
            throw new Error('Failed to unlink account');
        }
    }
);

// =============================================================================
// CLEANUP FUNCTIONS
// =============================================================================

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

// Cleanup expired password reset tokens daily
exports.cleanupExpiredPasswordResetTokens = onSchedule('every 24 hours', async (event) => {
    const now = Date.now();
    const fifteenMinutesAgo = now - (15 * 60 * 1000);
    
    try {
        const expiredTokens = await admin.firestore()
            .collection('password_reset_tokens')
            .where('createdAt', '<', fifteenMinutesAgo)
            .get();
        
        const batch = admin.firestore().batch();
        expiredTokens.docs.forEach(doc => {
            batch.delete(doc.ref);
        });
        
        await batch.commit();
        console.log(`Cleaned up ${expiredTokens.size} expired password reset tokens`);
        
    } catch (error) {
        console.error('Error cleaning up password reset tokens:', error);
    }
});