import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { payoutApi } from '../../api/payoutApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { ArrowLeft, Building, ShieldCheck, Check, Save, AlertCircle } from 'lucide-react';

export const BankDetailsPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [isEditing, setIsEditing] = useState(false);

  const [bank, setBank] = useState({
    accountHolder: '',
    bankName: '',
    accountNumber: '',
    maskedAccount: '',
    ifsc: '',
    isVerified: true
  });

  useEffect(() => {
    const fetchBank = async () => {
      try {
        setLoading(true);
        const res = await payoutApi.getBankDetails();
        if (res.success && res.data) {
          setBank(res.data);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchBank();
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSuccess(false);
    try {
      const res = await payoutApi.updateBankDetails(bank);
      if (res.success) {
        setBank(res.data);
        setIsEditing(false);
        setSuccess(true);
        setTimeout(() => setSuccess(false), 4000);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading verified bank details..." />;

  return (
    <div className="p-4 space-y-4 pb-20">
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate('/profile')}
          className="flex items-center gap-1 text-xs font-bold text-slate-600 hover:text-slate-900"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Profile</span>
        </button>
      </div>

      <div>
        <h1 className="text-xl font-black text-slate-900 tracking-tight">Settlement Bank Account</h1>
        <p className="text-xs text-slate-500">Earnings from completed jobs are directly disbursed here</p>
      </div>

      {success && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs font-semibold flex items-center gap-2">
          <Check className="w-4 h-4 text-emerald-600" />
          <span>Bank details updated. Undergoing verification check.</span>
        </div>
      )}

      {/* Verified Bank Card */}
      <div className="bg-gradient-to-br from-blue-900 to-indigo-950 text-white rounded-3xl p-6 shadow-xl relative overflow-hidden border border-blue-800">
        <div className="flex justify-between items-start mb-6">
          <div className="flex items-center gap-2.5">
            <Building className="w-6 h-6 text-blue-300" />
            <span className="font-extrabold text-sm tracking-wide">{bank.bankName || 'Verified Bank'}</span>
          </div>
          <span className={`text-[10px] font-bold px-2.5 py-1 rounded-full ${
            bank.isVerified ? 'bg-emerald-400/20 text-emerald-300 border border-emerald-400/30' : 'bg-amber-400/20 text-amber-300'
          }`}>
            {bank.isVerified ? 'Verified Account' : 'Verification In-Progress'}
          </span>
        </div>

        <div className="space-y-4">
          <div>
            <span className="text-[10px] uppercase font-bold text-blue-300 tracking-wider block">Account Number</span>
            <span className="font-mono text-lg font-bold tracking-wider">
              {bank.maskedAccount || '••••••••' + (bank.accountNumber?.slice(-4) || '9284')}
            </span>
          </div>

          <div className="flex justify-between items-end pt-2 border-t border-blue-800/80">
            <div>
              <span className="text-[10px] uppercase font-bold text-blue-300 tracking-wider block">Beneficiary</span>
              <span className="text-xs font-bold">{bank.accountHolder}</span>
            </div>
            <div className="text-right">
              <span className="text-[10px] uppercase font-bold text-blue-300 tracking-wider block">IFSC</span>
              <span className="font-mono text-xs font-bold uppercase">{bank.ifsc}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Edit Form */}
      {isEditing ? (
        <form onSubmit={handleSave} className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3.5">
          <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
            Update Settlement Details
          </h3>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Account Holder Name</label>
            <input
              type="text"
              required
              value={bank.accountHolder}
              onChange={(e) => setBank({ ...bank, accountHolder: e.target.value })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Bank Name</label>
            <input
              type="text"
              required
              value={bank.bankName}
              onChange={(e) => setBank({ ...bank, bankName: e.target.value })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Account Number</label>
            <input
              type="text"
              required
              value={bank.accountNumber}
              onChange={(e) => setBank({ ...bank, accountNumber: e.target.value })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-mono"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">IFSC Code</label>
            <input
              type="text"
              required
              value={bank.ifsc}
              onChange={(e) => setBank({ ...bank, ifsc: e.target.value.toUpperCase() })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-mono uppercase"
            />
          </div>

          <div className="flex gap-2 pt-2">
            <button
              type="button"
              onClick={() => setIsEditing(false)}
              className="flex-1 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl text-xs shadow-md shadow-blue-500/20"
            >
              {saving ? 'Updating...' : 'Save & Verify'}
            </button>
          </div>
        </form>
      ) : (
        <button
          onClick={() => setIsEditing(true)}
          className="w-full py-3.5 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-2xl text-xs font-bold transition active:scale-95"
        >
          Change Bank Account
        </button>
      )}
    </div>
  );
};
export default BankDetailsPage;
