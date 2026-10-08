import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, Phone, CheckCircle2 } from 'lucide-react';

export const ForgotPassword = () => {
  const [phone, setPhone] = useState('');
  const [sent, setSent] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (phone) {
      setSent(true);
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col justify-center px-5 py-8 max-w-lg mx-auto">
      <div className="bg-slate-800/90 rounded-3xl p-6 border border-slate-700/60 shadow-xl">
        <Link to="/login" className="inline-flex items-center gap-1.5 text-xs text-blue-400 font-semibold mb-6 hover:underline">
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Sign In</span>
        </Link>

        {sent ? (
          <div className="text-center py-6">
            <CheckCircle2 className="w-12 h-12 text-emerald-400 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-white mb-2">Password Reset Link Sent</h3>
            <p className="text-xs text-slate-400 mb-6">
              If an account is associated with <span className="text-white font-medium">{phone}</span>, you will receive an SMS with verification instructions.
            </p>
            <Link
              to="/login"
              className="inline-block bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold py-3 px-6 rounded-xl transition"
            >
              Return to Login
            </Link>
          </div>
        ) : (
          <div>
            <h2 className="text-lg font-bold text-white mb-2">Reset Password</h2>
            <p className="text-xs text-slate-400 mb-6">
              Enter your registered mobile number to receive a secure password recovery code.
            </p>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                  Mobile Number
                </label>
                <div className="relative">
                  <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                  <input
                    type="tel"
                    required
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="+91 98765 43210"
                    className="w-full pl-10 pr-3 py-3 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold py-3.5 rounded-xl shadow-lg shadow-blue-600/30 transition active:scale-95 text-sm"
              >
                Send Recovery OTP
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};
export default ForgotPassword;
