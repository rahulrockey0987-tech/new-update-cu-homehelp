import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { jobApi } from '../../api/jobApi';
import { earningsApi } from '../../api/earningsApi';
import StatusBadge from '../../components/common/StatusBadge';
import { JobStatus, WorkerStatus, KycStatus } from '../../constants/status';
import {
  IndianRupee, Briefcase, Clock, Navigation, CheckCircle2,
  AlertTriangle, ArrowRight, ShieldCheck, MapPin, ChevronRight, Star,
  BellRing, X, Play, Pause, Volume2, Mic
} from 'lucide-react';

export const Dashboard = () => {
  const { user, isVerifiedWorker } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [jobs, setJobs] = useState([]);
  const [earnings, setEarnings] = useState(null);
  const [activeJob, setActiveJob] = useState(null);
  const [incomingOffer, setIncomingOffer] = useState(null);
  const [actionMessage, setActionMessage] = useState(null);
  const [isPlayingVoice, setIsPlayingVoice] = useState(false);
  const [voiceProgress, setVoiceProgress] = useState(0);

  useEffect(() => {
    let timer;
    if (isPlayingVoice) {
      setVoiceProgress(0);
      const interval = 100;
      const totalSteps = 40;
      let step = 0;
      timer = setInterval(() => {
        step++;
        setVoiceProgress((step / totalSteps) * 100);
        if (step >= totalSteps) {
          setIsPlayingVoice(false);
          setVoiceProgress(0);
          clearInterval(timer);
        }
      }, interval);
    }
    return () => clearInterval(timer);
  }, [isPlayingVoice]);

  const loadDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      const [jobsRes, earningsRes] = await Promise.all([
        jobApi.getJobs(),
        earningsApi.getEarningsSummary()
      ]);

      if (jobsRes.success && jobsRes.data) {
        setJobs(jobsRes.data);
        // Find current active job
        const active = jobsRes.data.find(j =>
          [JobStatus.ASSIGNED, JobStatus.ACCEPTED, JobStatus.EN_ROUTE, JobStatus.ARRIVED, JobStatus.OTP_VERIFIED, JobStatus.SERVICE_STARTED].includes(j.status)
        );
        setActiveJob(active || null);

        // Find incoming job offer
        const offer = jobsRes.data.find(j => j.status === JobStatus.OFFERED);
        setIncomingOffer(offer || null);
      }

      if (earningsRes.success && earningsRes.data) {
        setEarnings(earningsRes.data);
      }
    } catch (err) {
      console.error('Dashboard load error', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDashboardData();
  }, [loadDashboardData]);

  const handleAcceptOffer = async (jobId) => {
    try {
      const res = await jobApi.acceptJob(jobId);
      if (res.success) {
        setActionMessage({ type: 'success', text: 'Job accepted! Opening job workflow...' });
        setTimeout(() => {
          navigate(`/jobs/${jobId}`);
        }, 800);
      }
    } catch (err) {
      setActionMessage({ type: 'error', text: err.message || 'Job no longer available' });
      loadDashboardData();
    }
  };

  const handleRejectOffer = async (jobId) => {
    try {
      await jobApi.rejectJob(jobId, 'Technician busy with other commitment');
      setIncomingOffer(null);
      loadDashboardData();
    } catch (err) {
      console.error(err);
    }
  };

  const todayCompletedCount = jobs.filter(j => j.status === JobStatus.COMPLETED && j.scheduledDate === new Date().toISOString().split('T')[0]).length;
  const pendingRequestsCount = incomingOffer ? 1 : 0;

  return (
    <div className="p-4 space-y-4">
      {/* Toast feedback */}
      {actionMessage && (
        <div className={`p-3 rounded-2xl text-xs font-bold flex items-center justify-between shadow-lg animate-in slide-in-from-top duration-200 ${
          actionMessage.type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'
        }`}>
          <span>{actionMessage.text}</span>
          <button onClick={() => setActionMessage(null)} className="p-1"><X className="w-4 h-4" /></button>
        </div>
      )}

      {/* Verification Warning Alert if Worker is not fully active */}
      {!isVerifiedWorker && (
        <div className="bg-amber-50 border border-amber-200 rounded-3xl p-4 shadow-sm">
          <div className="flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <h4 className="text-xs font-bold text-amber-900">Verification Pending</h4>
              <p className="text-[11px] text-amber-700 mt-0.5 leading-relaxed">
                Your profile is in <span className="font-semibold">{user?.kycStatus || 'PENDING'}</span> status. Complete KYC verification to start receiving live jobs in your area.
              </p>
              <button
                onClick={() => navigate('/onboarding')}
                className="mt-2.5 px-3 py-1.5 bg-amber-600 hover:bg-amber-700 text-white rounded-xl text-xs font-bold shadow-sm transition"
              >
                Complete Verification Flow
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Worker Greeting & Today Overview */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm relative overflow-hidden">
        <div className="flex items-start justify-between">
          <div>
            <span className="text-xs text-slate-500 font-medium">Good morning,</span>
            <h2 className="text-xl font-black text-slate-900 tracking-tight">{user?.fullName}</h2>
            <div className="flex items-center gap-2 mt-1.5">
              <StatusBadge status={user?.status} type="worker" />
              <div className="flex items-center gap-1 text-xs text-amber-600 font-bold bg-amber-50 px-2 py-0.5 rounded-full border border-amber-200">
                <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-500" />
                <span>{user?.rating || 4.9}</span>
              </div>
            </div>
          </div>

          <img
            src={user?.profilePhoto || 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150&auto=format&fit=crop&q=80'}
            alt="Partner avatar"
            className="w-13 h-13 rounded-2xl object-cover ring-2 ring-slate-100 shadow-sm"
          />
        </div>

        {/* Quick Numbers Bar */}
        <div className="grid grid-cols-3 gap-2 mt-5 pt-4 border-t border-slate-100 text-center">
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[11px] text-slate-500 font-medium">Today's Pay</span>
            <p className="text-base font-black text-emerald-600 mt-0.5">₹{earnings?.todayEarnings || 0}</p>
          </div>
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[11px] text-slate-500 font-medium">Jobs Done</span>
            <p className="text-base font-black text-slate-800 mt-0.5">{todayCompletedCount}</p>
          </div>
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[11px] text-slate-500 font-medium">Requests</span>
            <p className="text-base font-black text-blue-600 mt-0.5">{pendingRequestsCount}</p>
          </div>
        </div>
      </div>

      {/* NEW WORK REQUEST (Partner App Live Offer & Acceptance System) */}
      {incomingOffer && (
        <div className="bg-gradient-to-br from-blue-700 to-indigo-900 text-white rounded-3xl p-5 shadow-xl shadow-blue-900/25 border border-blue-500 relative overflow-hidden animate-in fade-in zoom-in-95">
          <div className="flex items-center justify-between pb-3 border-b border-blue-600/60">
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-ping" />
              <span className="text-xs uppercase font-extrabold tracking-wider text-blue-200">
                New Work Request
              </span>
            </div>
            <span className="text-xs font-mono font-semibold bg-blue-800/80 px-2 py-0.5 rounded-lg text-blue-200">
              {incomingOffer.id}
            </span>
          </div>

          <div className="my-3.5">
            <div className="flex items-center justify-between">
              <h3 className="text-base font-black tracking-tight leading-snug">{incomingOffer.serviceTitle}</h3>
              <span className="text-xs font-black bg-amber-400 text-slate-950 px-2 py-0.5 rounded-md">
                ₹{incomingOffer.ratePerHour || 150}/hr
              </span>
            </div>
            <div className="flex items-center gap-1.5 text-xs text-blue-100 mt-1">
              <MapPin className="w-3.5 h-3.5 text-blue-300 flex-shrink-0" />
              <span className="truncate">{incomingOffer.locality}</span>
            </div>
          </div>

          {/* Customer Voice Recording Player */}
          {incomingOffer.hasVoiceNote && (
            <div className="bg-blue-950/60 border border-blue-600/60 rounded-2xl p-3 my-3">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 rounded-full bg-amber-400/20 text-amber-300 flex items-center justify-center">
                    <Mic className="w-3.5 h-3.5" />
                  </div>
                  <span className="text-xs font-bold text-white">Customer Voice Recording</span>
                </div>
                <span className="text-[11px] font-mono text-blue-200">{incomingOffer.voiceDuration || '14s'}</span>
              </div>

              <div className="flex items-center gap-2.5 bg-blue-900/60 rounded-xl p-2">
                <button
                  onClick={() => setIsPlayingVoice(!isPlayingVoice)}
                  className="w-8 h-8 rounded-full bg-emerald-400 text-slate-950 flex items-center justify-center shadow-md hover:bg-emerald-300 transition active:scale-95 flex-shrink-0"
                  title={isPlayingVoice ? "Pause Voice" : "Play Voice"}
                >
                  {isPlayingVoice ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 ml-0.5" />}
                </button>

                <div className="flex-1">
                  <div className="flex items-center justify-between text-[10px] text-blue-200 font-medium mb-1">
                    <span>{isPlayingVoice ? "Playing Voice Request..." : "Tap to Play Voice"}</span>
                    <span>₹{incomingOffer.ratePerHour || 150}/hr</span>
                  </div>
                  {/* Waveform Bars */}
                  <div className="flex items-center gap-1 h-3.5">
                    {[12, 24, 18, 30, 22, 28, 14, 26, 32, 20, 16, 24].map((h, i) => (
                      <div
                        key={i}
                        className={`flex-1 rounded-full transition-all duration-150 ${
                          isPlayingVoice && (i / 12) * 100 <= voiceProgress
                            ? 'bg-emerald-400'
                            : 'bg-blue-600'
                        }`}
                        style={{ height: isPlayingVoice ? `${Math.max(4, (h * (voiceProgress % 30 + 10)) / 25)}px` : `${h / 2.5}px` }}
                      />
                    ))}
                  </div>
                </div>
              </div>

              {incomingOffer.specialInstructions && (
                <p className="text-[11px] text-blue-200 mt-2 font-medium">
                  "{incomingOffer.specialInstructions}"
                </p>
              )}
            </div>
          )}

          {/* Quick Metrics Grid */}
          <div className="grid grid-cols-4 gap-1.5 bg-blue-900/50 rounded-2xl p-2.5 text-center my-3 text-xs">
            <div>
              <span className="text-blue-300 text-[10px] block">Distance</span>
              <span className="font-bold text-white text-[11px]">{incomingOffer.distanceKm} km</span>
            </div>
            <div className="border-l border-blue-700/60">
              <span className="text-blue-300 text-[10px] block">Required</span>
              <span className="font-bold text-white text-[11px]">{incomingOffer.requiredHours || 3} Hours</span>
            </div>
            <div className="border-l border-blue-700/60">
              <span className="text-blue-300 text-[10px] block">Rate</span>
              <span className="font-bold text-amber-300 text-[11px]">₹{incomingOffer.ratePerHour || 150}/hr</span>
            </div>
            <div className="border-l border-blue-700/60">
              <span className="text-blue-300 text-[10px] block">Total Est.</span>
              <span className="font-black text-emerald-300 text-[11px]">₹{incomingOffer.earnings}</span>
            </div>
          </div>

          <div className="text-[11px] text-blue-200 mb-3 flex items-center justify-between bg-blue-800/40 px-3 py-1.5 rounded-xl border border-blue-700/40">
            <span>Required Time: <strong className="text-white">{incomingOffer.scheduledTime}</strong></span>
            <span>Landmark: <strong className="text-white">{incomingOffer.landmark || 'Near Metro'}</strong></span>
          </div>

          {/* Partner Action Buttons: Accept / Reject */}
          <div className="flex items-center gap-2.5 pt-1">
            <button
              onClick={() => handleRejectOffer(incomingOffer.id)}
              className="flex-1 py-3 bg-blue-950/80 hover:bg-rose-900/60 hover:border-rose-500 text-blue-200 hover:text-white text-xs font-bold rounded-xl border border-blue-700 transition active:scale-95"
            >
              Reject
            </button>
            <button
              onClick={() => handleAcceptOffer(incomingOffer.id)}
              className="flex-2 py-3 bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-xs font-black rounded-xl shadow-lg shadow-emerald-500/30 flex items-center justify-center gap-1.5 transition active:scale-95"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Accept (₹{incomingOffer.earnings})</span>
            </button>
          </div>
        </div>
      )}

      {/* ACTIVE JOB BANNER (Critical Screen Anchor) */}
      {activeJob ? (
        <div className="bg-white rounded-3xl p-5 border-2 border-blue-500 shadow-md">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-blue-600 animate-pulse" />
              <span className="text-xs font-extrabold uppercase tracking-wider text-blue-700">
                Active Job In Progress
              </span>
            </div>
            <StatusBadge status={activeJob.status} type="job" />
          </div>

          <h3 className="text-sm font-bold text-slate-900 mb-1">{activeJob.serviceTitle}</h3>
          <p className="text-xs text-slate-500 mb-3 flex items-center gap-1">
            <MapPin className="w-3.5 h-3.5 text-slate-400" />
            <span className="truncate">{activeJob.locality}</span>
          </p>

          <div className="flex items-center justify-between p-3 bg-slate-50 rounded-2xl border border-slate-100 text-xs mb-4">
            <div>
              <span className="text-slate-400 text-[10px] block">Customer</span>
              <span className="font-semibold text-slate-800">{activeJob.customerName}</span>
            </div>
            <div className="text-right">
              <span className="text-slate-400 text-[10px] block">Schedule</span>
              <span className="font-semibold text-slate-800">{activeJob.scheduledTime}</span>
            </div>
          </div>

          <button
            onClick={() => navigate(`/jobs/${activeJob.id}`)}
            className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold flex items-center justify-center gap-2 shadow-md shadow-blue-500/20 transition active:scale-95"
          >
            <Navigation className="w-4 h-4" />
            <span>Open Job Operational Screen</span>
            <ChevronRight className="w-4 h-4 ml-auto" />
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-3xl p-5 border border-slate-200 text-center py-6">
          <div className="w-12 h-12 bg-slate-50 text-slate-400 rounded-2xl flex items-center justify-center mx-auto mb-2">
            <Briefcase className="w-6 h-6" />
          </div>
          <h4 className="text-xs font-bold text-slate-700">No Active Job at this moment</h4>
          <p className="text-[11px] text-slate-400 mt-0.5">
            {user?.isOnline ? 'You are Online. Nearby customer requests will appear above.' : 'You are currently Offline. Turn online to receive requests.'}
          </p>
        </div>
      )}

      {/* Quick Action Tiles */}
      <div className="grid grid-cols-2 gap-3">
        <div
          onClick={() => navigate('/jobs')}
          className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm cursor-pointer hover:border-blue-300 transition"
        >
          <div className="w-9 h-9 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center mb-2.5">
            <Briefcase className="w-5 h-5" />
          </div>
          <h4 className="text-xs font-bold text-slate-800">Job Schedule</h4>
          <p className="text-[11px] text-slate-500 mt-0.5">View today, tomorrow & history</p>
        </div>

        <div
          onClick={() => navigate('/earnings')}
          className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm cursor-pointer hover:border-blue-300 transition"
        >
          <div className="w-9 h-9 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center mb-2.5">
            <IndianRupee className="w-5 h-5" />
          </div>
          <h4 className="text-xs font-bold text-slate-800">Earnings & Payouts</h4>
          <p className="text-[11px] text-slate-500 mt-0.5">Pending: ₹{earnings?.pendingPayout || 0}</p>
        </div>

        <div
          onClick={() => navigate('/kyc')}
          className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm cursor-pointer hover:border-blue-300 transition"
        >
          <div className="w-9 h-9 rounded-2xl bg-purple-50 text-purple-600 flex items-center justify-center mb-2.5">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <h4 className="text-xs font-bold text-slate-800">KYC & Compliance</h4>
          <p className="text-[11px] text-slate-500 mt-0.5">{user?.kycStatus || 'Check status'}</p>
        </div>

        <div
          onClick={() => navigate('/profile/availability')}
          className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm cursor-pointer hover:border-blue-300 transition"
        >
          <div className="w-9 h-9 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center mb-2.5">
            <Clock className="w-5 h-5" />
          </div>
          <h4 className="text-xs font-bold text-slate-800">Working Shifts</h4>
          <p className="text-[11px] text-slate-500 mt-0.5">Working days & hours</p>
        </div>
      </div>
    </div>
  );
};
export default Dashboard;
