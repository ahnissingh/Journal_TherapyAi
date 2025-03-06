📋 Project TODO - Journal Therapy App

This document outlines the pending features, enhancements, and improvements for the Journal Therapy App across different modules.
1️⃣ User Module
✨ Features & Enhancements

Forgot Password: Implement a secure forgot password flow (email-based or OTP-based recovery).
Refresh Token Support: Move away from single-use JWT. Add proper refresh token handling for better session management.

    Pagination for Admin Controller: Add paginated API support for admin-level endpoints (user listings, search, etc.).

2️⃣ Journal Module
✨ Features & Enhancements

Journal Pagination: Add paginated responses for user journals to improve performance and scalability.

    Journaling Reminders: Implement configurable reminders (push/email) to nudge users to write journals based on their preferences.

3️⃣ Analysis Module
✨ Features & Enhancements

Previous Report Comparison: When generating reports, include:

    Comparison with previous report.
    Breakdown of reports by frequency (daily, weekly, biweekly, monthly).

Improve Async Handling: Review and optimize async logic to ensure non-blocking operations (consider moving to proper job queues if needed).

    Profile-based Scheduler:
        For development, use a faster cron schedule for quick testing.
        For production, ensure correct, stable schedules aligned with actual user preferences.

4️⃣ Chatbot Module
✨ Features & Enhancements

Improve Memory Storage Logic: Review and optimize how chat memory is persisted (consider session-based caching or batching for performance).
Vector Storage for Chats:

    Store chat conversations into a vector database.
    Enable semantic analysis of chat history for better personalized therapy reports and insights.
