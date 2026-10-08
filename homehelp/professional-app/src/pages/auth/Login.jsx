import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Wrench, Phone, Lock, ArrowRight, ShieldCheck, UserCheck } from 'lucide-react';
import { WorkerStatus, KycStatus } from '../../constants/status';
import { setStored, STORAGE_KEYS } from '../../services/mockStorage';

export const Login = () => {
  const [identifier, setIdentifier] = useState('+91 98765 43210');
  const [password, setPassword] = useState('password123');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setIsSubmitting(true);
    try {
      const res = await login({ identifier, password });
      if (res.success) {
        navigate('/dashboard');
      } else {
        setErrorMessage(res.error || 'Invalid credentials');
      }
    } catch (err) {
      setErrorMessage(err.message || 'Login failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  const setTestAccount = (type) => {
    if (type === 'verified') {
      setIdentifier('+91 98765 43210');
      setPassword('password123');
    } else {
      // Unverified account for testing onboarding
      const unverified = {
        id: 'pro_new_demo',
        fullName: 'Kiran Kumar',
        phone: '+91 98111 22334',
        email: 'kiran.k@example.com',
        city: 'Hyderabad',
        serviceCategory: 'cleaning',
        skills: ['Deep Cleaning'],
        status: WorkerStatus.REGISTERED,
        kycStatus: KycStatus.NOT_STARTED,
        isOnline: false,
        totalJobsCompleted: 0
      };
      setStored(STORAGE_KEYS.USER, unverified);
      setIdentifier('+91 98111 22334');
      setPassword('password123');
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col justify-center px-5 py-8 max-w-lg mx-auto">
      {/* Brand Header */}
      <div className="text-center mb-8">
        <div className="w-16 h-16 bg-blue-600 rounded-2xl mx-auto flex items-center justify-center shadow-lg shadow-blue-500/30 mb-3">
          <Wrench className="w-8 h-8 text-white" />
        </div>
        <h1 className="text-2xl font-black tracking-tight">HomeHelp Pro</h1>
        <p className="text-slate-400 text-sm mt-1">Worker & Technician Partner Platform</p>
      </div>

      <div className="bg-slate-800/90 backdrop-blur rounded-3xl p-6 border border-slate-700/60 shadow-xl">
        <h2 className="text-lg font-bold text-white mb-2">Partner Sign In</h2>
        <p className="text-xs text-slate-400 mb-6">
          Access your bookings, service assignments, and daily payouts.
        </p>

        {errorMessage && (
          <div className="bg-red-500/10 border border-red-500/30 text-red-300 text-xs p-3 rounded-xl mb-4">
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Registered Phone / Email
            </label>
            <div className="relative">
              <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
              <input
                type="text"
                required
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                placeholder="+91 98765 43210"
                className="w-full pl-10 pr-3 py-3 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className="text-xs font-semibold text-slate-300">Password</label>
              <Link to="/forgot-password" className="text-xs text-blue-400 hover:underline">
                Forgot password?
              </Link>
            </div>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-10 pr-3 py-3 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full mt-2 bg-blue-600 hover:bg-blue-500 text-white font-bold py-3.5 rounded-xl shadow-lg shadow-blue-600/30 flex items-center justify-center gap-2 transition active:scale-95"
          >
            <span>{isSubmitting ? 'Signing in...' : 'Sign In to Portal'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        <div className="mt-6 pt-5 border-t border-slate-700/60 text-center">
          <p className="text-xs text-slate-400">
            Want to become a HomeHelp Professional?{' '}
            <Link to="/register" className="text-blue-400 font-semibold hover:underline">
              Register as Partner
            </Link>
          </p>
        </div>
      </div>

      {/* Quick Role Switcher for Testing Lifecycle */}
      <div className="mt-6 bg-slate-800/40 rounded-2xl p-3 border border-slate-700/40 text-xs">
        <span className="text-slate-400 font-semibold block mb-2">Demo Testing Profiles:</span>
        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={() => setTestAccount('verified')}
            className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-left border border-slate-700 transition"
          >
            <div className="flex items-center gap-1 text-emerald-400 font-bold text-[11px]">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Verified Worker</span>
            </div>
            <p className="text-[10px] text-slate-400 mt-0.5">AC Expert (Ready for Jobs)</p>
          </button>

          <button
            onClick={() => setTestAccount('unverified')}
            className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-left border border-slate-700 transition"
          >
            <div className="flex items-center gap-1 text-amber-400 font-bold text-[11px]">
              <UserCheck className="w-3.5 h-3.5" />
              <span>New Applicant</span>
            </div>
            <p className="text-[10px] text-slate-400 mt-0.5">Needs Onboarding / KYC</p>
          </button>
        </div>
      </div>
    </div>
  );
};
export default Login;
