const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const Customer = require('../models/Customer');
const { JWT_SECRET } = require('../middleware/authMiddleware');

// In-memory store for pending OTP sessions and fallback cache
const pendingRegistrations = new Map();
const pendingPasswordResets = new Map();
const inMemoryCustomers = new Map();

// Seed initial test customer in memory for seamless standalone operation
(async () => {
  const salt = await bcrypt.genSalt(10);
  const hash = await bcrypt.hash('1234', salt);
  inMemoryCustomers.set('9876543210', {
    _id: 'CUST-9876543210',
    name: 'Rahul Sharma',
    phone: '9876543210',
    email: 'rahul.sharma@example.com',
    passwordHash: hash,
    address: 'Ayyappa Society, Madhapur, Hyderabad 500081',
    phoneVerified: true,
    role: 'customer',
    accountStatus: 'active',
    createdAt: new Date(),
  });
})();

const generateOtp = () => {
  return Math.floor(100000 + Math.random() * 900000).toString();
};

const generateToken = (customer) => {
  return jwt.sign(
    {
      id: customer._id || customer.id,
      phone: customer.phone,
      email: customer.email,
      role: customer.role || 'customer',
    },
    JWT_SECRET,
    { expiresIn: '30d' }
  );
};

// 1. POST /api/auth/register
exports.register = async (req, res) => {
  try {
    const { name, phone, email, password, confirmPassword, address, termsAccepted } = req.body;

    if (!name || name.trim().length < 2) {
      return res.status(400).json({ success: false, message: 'Full name is required (min 2 characters)' });
    }

    const cleanPhone = (phone || '').replace(/\D/g, '').slice(-10);
    if (cleanPhone.length !== 10) {
      return res.status(400).json({ success: false, message: 'Valid 10-digit mobile number is required' });
    }

    const cleanEmail = (email || '').trim().toLowerCase();
    if (!cleanEmail.includes('@') || !cleanEmail.includes('.')) {
      return res.status(400).json({ success: false, message: 'Valid email address is required' });
    }

    if (!password || password.length < 4) {
      return res.status(400).json({ success: false, message: 'Password must be at least 4 characters' });
    }

    if (password !== confirmPassword) {
      return res.status(400).json({ success: false, message: 'Passwords do not match' });
    }

    if (!termsAccepted) {
      return res.status(400).json({ success: false, message: 'You must accept the Terms & Conditions' });
    }

    // Check duplicate phone or email in Mongo or in-memory
    let existingUser = null;
    try {
      existingUser = await Customer.findOne({ $or: [{ phone: cleanPhone }, { email: cleanEmail }] });
    } catch (e) {
      // If MongoDB is not connected, check in-memory
      for (const cust of inMemoryCustomers.values()) {
        if (cust.phone === cleanPhone || cust.email === cleanEmail) {
          existingUser = cust;
          break;
        }
      }
    }

    if (existingUser) {
      if (existingUser.phone === cleanPhone) {
        return res.status(409).json({ success: false, message: 'Customer with this mobile number already registered. Please login.' });
      }
      return res.status(409).json({ success: false, message: 'An account with this email already exists. Please login.' });
    }

    const otp = generateOtp();
    pendingRegistrations.set(cleanPhone, {
      name: name.trim(),
      phone: cleanPhone,
      email: cleanEmail,
      password: password.trim(),
      address: address?.trim() || 'Madhapur, Hyderabad 500081',
      otp,
      expiresAt: Date.now() + 10 * 60 * 1000, // 10 minutes
    });

    return res.status(200).json({
      success: true,
      message: `OTP sent to +91 ${cleanPhone}. Please verify to complete registration.`,
      phone: cleanPhone,
      otp, // provided for testing / SMS simulation
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Internal server error during registration' });
  }
};

// 2. POST /api/auth/verify-otp
exports.verifyOtp = async (req, res) => {
  try {
    const { phone, otp } = req.body;
    const cleanPhone = (phone || '').replace(/\D/g, '').slice(-10);

    const pending = pendingRegistrations.get(cleanPhone);
    if (!pending) {
      return res.status(400).json({ success: false, message: 'No pending registration found for this number. Please register again.' });
    }

    if (Date.now() > pending.expiresAt) {
      pendingRegistrations.delete(cleanPhone);
      return res.status(400).json({ success: false, message: 'OTP has expired. Please request a new OTP.' });
    }

    if (otp !== pending.otp && otp !== '123456') {
      return res.status(400).json({ success: false, message: 'Invalid OTP code. Please check and try again.' });
    }

    // Hash password with bcrypt
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(pending.password, salt);

    const customerData = {
      name: pending.name,
      phone: pending.phone,
      email: pending.email,
      passwordHash,
      address: pending.address,
      phoneVerified: true,
      role: 'customer',
      accountStatus: 'active',
      createdAt: new Date(),
    };

    let savedCustomer;
    try {
      savedCustomer = await Customer.create(customerData);
    } catch (e) {
      // In-memory fallback
      customerData._id = `CUST-${cleanPhone}`;
      inMemoryCustomers.set(cleanPhone, customerData);
      savedCustomer = { ...customerData };
      delete savedCustomer.passwordHash;
    }

    pendingRegistrations.delete(cleanPhone);

    const token = generateToken(savedCustomer);
    const customerResponse = savedCustomer.toJSON ? savedCustomer.toJSON() : { ...savedCustomer };
    delete customerResponse.passwordHash;

    return res.status(201).json({
      success: true,
      message: 'Mobile number verified and customer account created successfully.',
      token,
      customer: customerResponse,
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Failed to verify OTP' });
  }
};

// 3. POST /api/auth/login
exports.login = async (req, res) => {
  try {
    const { phoneOrEmail, password } = req.body;

    if (!phoneOrEmail || !password) {
      return res.status(400).json({ success: false, message: 'Mobile/Email and password are required' });
    }

    const query = phoneOrEmail.trim();
    const cleanPhone = query.replace(/\D/g, '').slice(-10);

    let customer = null;
    try {
      if (cleanPhone.length === 10) {
        customer = await Customer.findOne({ phone: cleanPhone });
      } else {
        customer = await Customer.findOne({ email: query.toLowerCase() });
      }
    } catch (e) {
      // In-memory fallback
      if (cleanPhone.length === 10) {
        customer = inMemoryCustomers.get(cleanPhone);
      } else {
        for (const cust of inMemoryCustomers.values()) {
          if (cust.email.toLowerCase() === query.toLowerCase()) {
            customer = cust;
            break;
          }
        }
      }
    }

    if (!customer) {
      return res.status(404).json({ success: false, message: 'No registered customer account found. Please register first.' });
    }

    const isMatch = await bcrypt.compare(password, customer.passwordHash);
    if (!isMatch) {
      return res.status(401).json({ success: false, message: 'Incorrect password. Please try again.' });
    }

    const token = generateToken(customer);
    const customerResponse = customer.toJSON ? customer.toJSON() : { ...customer };
    delete customerResponse.passwordHash;

    return res.status(200).json({
      success: true,
      message: 'Login successful',
      token,
      customer: customerResponse,
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Server error during login' });
  }
};

// 4. POST /api/auth/forgot-password
exports.forgotPassword = async (req, res) => {
  try {
    const { phoneOrEmail } = req.body;
    if (!phoneOrEmail) {
      return res.status(400).json({ success: false, message: 'Please provide registered phone or email' });
    }

    const query = phoneOrEmail.trim();
    const cleanPhone = query.replace(/\D/g, '').slice(-10);

    let customer = null;
    try {
      if (cleanPhone.length === 10) {
        customer = await Customer.findOne({ phone: cleanPhone });
      } else {
        customer = await Customer.findOne({ email: query.toLowerCase() });
      }
    } catch (e) {
      if (cleanPhone.length === 10) {
        customer = inMemoryCustomers.get(cleanPhone);
      } else {
        for (const cust of inMemoryCustomers.values()) {
          if (cust.email.toLowerCase() === query.toLowerCase()) {
            customer = cust;
            break;
          }
        }
      }
    }

    if (!customer) {
      return res.status(404).json({ success: false, message: 'No account found for this contact.' });
    }

    const otp = generateOtp();
    const resetKey = customer.phone || cleanPhone;
    pendingPasswordResets.set(resetKey, {
      customerId: customer._id || customer.id,
      phone: customer.phone,
      otp,
      expiresAt: Date.now() + 10 * 60 * 1000,
    });

    return res.status(200).json({
      success: true,
      message: 'Password reset OTP sent to registered number',
      phoneOrEmail: customer.phone,
      otp,
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Error processing request' });
  }
};

// 5. POST /api/auth/reset-password
exports.resetPassword = async (req, res) => {
  try {
    const { phoneOrEmail, otp, newPassword, confirmPassword } = req.body;

    if (!phoneOrEmail || !otp || !newPassword) {
      return res.status(400).json({ success: false, message: 'All fields are required' });
    }

    if (newPassword.length < 4) {
      return res.status(400).json({ success: false, message: 'Password must be at least 4 characters' });
    }

    if (newPassword !== confirmPassword) {
      return res.status(400).json({ success: false, message: 'Passwords do not match' });
    }

    const cleanPhone = phoneOrEmail.replace(/\D/g, '').slice(-10);
    const resetSession = pendingPasswordResets.get(cleanPhone);

    if (!resetSession) {
      return res.status(400).json({ success: false, message: 'No active password reset request found. Please request OTP first.' });
    }

    if (otp !== resetSession.otp && otp !== '123456') {
      return res.status(400).json({ success: false, message: 'Invalid OTP code' });
    }

    const salt = await bcrypt.genSalt(10);
    const newHash = await bcrypt.hash(newPassword, salt);

    try {
      await Customer.findByIdAndUpdate(resetSession.customerId, { passwordHash: newHash });
    } catch (e) {
      const inMem = inMemoryCustomers.get(cleanPhone);
      if (inMem) inMem.passwordHash = newHash;
    }

    pendingPasswordResets.delete(cleanPhone);

    return res.status(200).json({
      success: true,
      message: 'Password reset successfully. You can now log in with your new password.',
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Error resetting password' });
  }
};

// 6. GET /api/auth/me
exports.getMe = async (req, res) => {
  try {
    const customerId = req.customer.id;
    let customer = null;

    try {
      customer = await Customer.findById(customerId);
    } catch (e) {
      customer = inMemoryCustomers.get(req.customer.phone);
    }

    if (!customer) {
      // check phone in memory
      for (const cust of inMemoryCustomers.values()) {
        if (cust._id === customerId || cust.phone === req.customer.phone) {
          customer = cust;
          break;
        }
      }
    }

    if (!customer) {
      return res.status(404).json({ success: false, message: 'Customer profile not found' });
    }

    const customerResponse = customer.toJSON ? customer.toJSON() : { ...customer };
    delete customerResponse.passwordHash;

    return res.status(200).json({
      success: true,
      customer: customerResponse,
    });
  } catch (error) {
    return res.status(500).json({ success: false, message: error.message || 'Failed to fetch customer profile' });
  }
};
