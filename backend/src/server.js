const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const dotenv = require('dotenv');

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/homehelp_customer_db';

// Middleware
app.use(cors());
app.use(express.json());

// Routes
const authRoutes = require('./routes/authRoutes');
app.use('/api/auth', authRoutes);

// Health check
app.get('/api/health', (req, res) => {
  res.json({
    status: 'online',
    service: 'HomeHelp Customer Auth Service',
    database: mongoose.connection.readyState === 1 ? 'connected' : 'in-memory-fallback',
    timestamp: new Date().toISOString(),
  });
});

// Database connection
if (process.env.MONGODB_URI) {
  mongoose
    .connect(MONGODB_URI)
    .then(() => console.log('✅ Connected to MongoDB customer database'))
    .catch((err) => console.warn('⚠️ MongoDB connection warning (running in in-memory mode):', err.message));
}

// Start server if executed directly
if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`🚀 HomeHelp Customer Auth API running on http://localhost:${PORT}`);
    console.log(`Routes available:`);
    console.log(`- POST /api/auth/register`);
    console.log(`- POST /api/auth/verify-otp`);
    console.log(`- POST /api/auth/login`);
    console.log(`- POST /api/auth/forgot-password`);
    console.log(`- POST /api/auth/reset-password`);
    console.log(`- GET  /api/auth/me`);
  });
}

module.exports = app;
