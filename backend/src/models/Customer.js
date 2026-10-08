const mongoose = require('mongoose');

const customerSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: [true, 'Customer name is required'],
      trim: true,
      minlength: 2,
      maxlength: 100,
    },
    phone: {
      type: String,
      required: [true, '10-digit mobile number is required'],
      unique: true,
      trim: true,
    },
    email: {
      type: String,
      required: [true, 'Email is required'],
      unique: true,
      lowercase: true,
      trim: true,
    },
    passwordHash: {
      type: String,
      required: [true, 'Password hash is required'],
    },
    address: {
      type: String,
      required: [true, 'Location or address is required'],
      trim: true,
      default: 'Madhapur, Hyderabad 500081',
    },
    phoneVerified: {
      type: Boolean,
      default: false,
    },
    role: {
      type: String,
      enum: ['customer'],
      default: 'customer',
    },
    accountStatus: {
      type: String,
      enum: ['active', 'suspended', 'pending_verification'],
      default: 'active',
    },
  },
  {
    timestamps: { createdAt: 'createdAt', updatedAt: 'updatedAt' },
  }
);

// Never expose passwordHash in toJSON
customerSchema.methods.toJSON = function () {
  const obj = this.toObject ? this.toObject() : { ...this };
  delete obj.passwordHash;
  delete obj.__v;
  return obj;
};

// Safe compile for Mongoose or in-memory fallback
let Customer;
try {
  Customer = mongoose.model('Customer', customerSchema);
} catch (e) {
  Customer = mongoose.models.Customer;
}

module.exports = Customer;
