import React, { useState, useEffect } from 'react';
import { earningsApi } from '../../api/earningsApi';
import { payoutApi } from '../../api/payoutApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { PayoutStatus } from '../../constants/status';
import {
  IndianRupee, TrendingUp, Calendar, Building, CheckCircle2,
  Clock, ArrowUpRight, ShieldCheck, ChevronRight
} from 'lucide-react';

export const EarningsDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [summary, setSummary] = useState(null);
  const [payouts, setPayouts] = useState([]);
  const [bank, setBank] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [earnRes, payRes, bankRes] = await Promise.all([
          earningsApi.getEarningsSummary(),
          payoutApi.getPayouts(),
          payoutApi.getBankDetails()
        ]);

        if (earnRes.success) setSummary(earnRes.data);
        if (payRes.success) setPayouts(payRes.data);
        if (bankRes.success) setBank(bankRes.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <LoadingSpinner message="Calculating partner earnings & settlements..." />;

  return (
    <div className="p-4 space-y-4 pb-24">
      {/* Page Title */}
      <div>
        <h1 className="text-xl font-black text-slate-900 tracking-tight">Partner Earnings</h1>
        <p className="text-xs text-slate-500">Transparent breakdown of jobs, deductions, and bank payouts</p>
      </div>

      {/* Main Net Earnings Card */}
      <div className="bg-gradient-to-br from-slate-900 via-blue-950 to-blue-900 text-white rounded-3xl p-6 shadow-xl relative overflow-hidden border border-slate-800">
        <span className="text-xs text-blue-200 font-medium">Pending Payout Balance</span>
        <div className="flex items-baseline gap-2 mt-1 mb-4">
          <span className="text-3xl font-black tracking-tight text-white">₹{summary?.pendingPayout || 0}</span>
          <span className="text-xs text-emerald-400 font-bold bg-emerald-500/20 px-2 py-0.5 rounded-full">
            Auto-Settles Weekly
          </span>
        </div>

        {/* Bank info linked */}
        {bank && (
          <div className="bg-white/10 backdrop-blur rounded-2xl p-3 flex items-center justify-between text-xs border border-white/10">
            <div className="flex items-center gap-2">
              <Building className="w-4 h-4 text-blue-300" />
              <div>
                <span className="font-semibold block">{bank.bankName}</span>
                <span className="text-slate-300 font-mono">{bank.maskedAccount}</span>
              </div>
            </div>
            <span className="text-[10px] font-bold text-emerald-300 bg-emerald-400/20 px-2 py-0.5 rounded-full">
              Verified
            </span>
          </div>
        )}
      </div>

      {/* Time-based earnings cards */}
      <div className="grid grid-cols-3 gap-2 text-center">
        <div className="bg-white p-3.5 rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">Today</span>
          <span className="text-base font-black text-slate-800 mt-1 block">₹{summary?.todayEarnings || 0}</span>
        </div>
        <div className="bg-white p-3.5 rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">This Week</span>
          <span className="text-base font-black text-slate-800 mt-1 block">₹{summary?.weeklyEarnings || 0}</span>
        </div>
        <div className="bg-white p-3.5 rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider block">This Month</span>
          <span className="text-base font-black text-slate-800 mt-1 block">₹{summary?.monthlyEarnings || 0}</span>
        </div>
      </div>

      {/* Accounting Breakdown: Gross vs Commission vs Net */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Lifetime Financial Accounting
        </h3>

        <div className="space-y-2.5 text-xs">
          <div className="flex justify-between items-center text-slate-600">
            <span>Customer Gross Billings</span>
            <span className="font-mono font-bold text-slate-800">₹{summary?.lifetimeGross || 0}</span>
          </div>
          <div className="flex justify-between items-center text-slate-600">
            <span>Platform Service Fee & GST (20%)</span>
            <span className="font-mono font-medium text-rose-600">-₹{summary?.lifetimePlatformFee || 0}</span>
          </div>
          <div className="flex justify-between items-center text-slate-600 border-t border-slate-100 pt-2 font-bold">
            <span className="text-slate-800">Total Worker Net Pay</span>
            <span className="font-mono text-emerald-600 text-sm">₹{summary?.lifetimeNet || 0}</span>
          </div>
        </div>
      </div>

      {/* Settlement Payout History */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Bank Payout Statements
        </h3>

        <div className="space-y-2.5">
          {payouts.map(pay => (
            <div
              key={pay.id}
              className="p-3 bg-slate-50 rounded-2xl border border-slate-200 flex items-center justify-between text-xs"
            >
              <div>
                <div className="flex items-center gap-1.5 font-bold text-slate-800">
                  <span>{pay.id}</span>
                  <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${
                    pay.status === PayoutStatus.PAID
                      ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                      : 'bg-amber-50 text-amber-700 border border-amber-200'
                  }`}>
                    {pay.status}
                  </span>
                </div>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Ref: <span className="font-mono">{pay.referenceId}</span> • {pay.date}
                </p>
              </div>

              <div className="text-right">
                <span className="text-sm font-black text-slate-900 block">₹{pay.amount}</span>
                <span className="text-[10px] text-slate-400">Direct NEFT/UPI</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
export default EarningsDashboard;
