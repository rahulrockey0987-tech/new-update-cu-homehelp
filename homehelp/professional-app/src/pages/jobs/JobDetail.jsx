import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { jobApi } from '../../api/jobApi';
import StatusBadge from '../../components/common/StatusBadge';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import SosModal from '../../components/common/SosModal';
import { JobStatus } from '../../constants/status';
import {
  MapPin, Phone, MessageSquare, Navigation, CheckCircle2,
  Clock, ShieldAlert, ArrowLeft, Camera, Plus, AlertCircle,
  HelpCircle, ChevronRight, FileText, CheckSquare, Square,
  Play, Pause, Mic
} from 'lucide-react';

export const JobDetail = () => {
  const { jobId } = useParams();
  const navigate = useNavigate();

  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState(null);
  const [actionSuccess, setActionSuccess] = useState(null);

  // OTP modal
  const [otpInput, setOtpInput] = useState('');
  const [showOtpDialog, setShowOtpDialog] = useState(false);

  // Additional charge modal
  const [showChargeModal, setShowChargeModal] = useState(false);
  const [chargeData, setChargeData] = useState({ title: 'Spare Filter Replacement', amount: '450', reason: 'Existing copper pipe flared joint was oxidized.' });

  // Safety SOS
  const [sosOpen, setSosOpen] = useState(false);

  // Voice playback
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

  const fetchJob = async () => {
    try {
      setLoading(true);
      const res = await jobApi.getJobById(jobId);
      if (res.success && res.data) {
        setJob(res.data);
      }
    } catch (err) {
      setActionError(err.message || 'Job not found');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJob();
  }, [jobId]);

  const handleStartEnRoute = async () => {
    setActionLoading(true);
    setActionError(null);
    try {
      const res = await jobApi.startEnRoute(job.id);
      if (res.success) {
        setJob(res.data);
        setActionSuccess('Status updated: En Route to customer');
      }
    } catch (err) {
      setActionError(err.message || 'Failed to update status');
    } finally {
      setActionLoading(false);
    }
  };

  const handleMarkArrived = async () => {
    setActionLoading(true);
    setActionError(null);
    try {
      const res = await jobApi.markArrived(job.id);
      if (res.success) {
        setJob(res.data);
        setShowOtpDialog(true);
        setActionSuccess('Arrived at premises! Ask customer for their 4-digit arrival OTP.');
      }
    } catch (err) {
      setActionError(err.message || 'Failed to mark arrival');
    } finally {
      setActionLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setActionError(null);
    try {
      const res = await jobApi.verifyOtp(job.id, otpInput);
      if (res.success) {
        setJob(res.data);
        setShowOtpDialog(false);
        setActionSuccess('OTP verified successfully! You may now begin the service.');
      }
    } catch (err) {
      setActionError(err.message || 'Invalid OTP code');
    } finally {
      setActionLoading(false);
    }
  };

  const handleStartService = async () => {
    setActionLoading(true);
    setActionError(null);
    try {
      const res = await jobApi.startService(job.id);
      if (res.success) {
        setJob(res.data);
        setActionSuccess('Service timer started. Complete all required checklist items.');
      }
    } catch (err) {
      setActionError(err.message || 'Could not start service');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCompleteService = async () => {
    setActionLoading(true);
    setActionError(null);
    try {
      const res = await jobApi.completeService(job.id, {
        notes: 'Service successfully executed according to quality checklist.',
        proofPhotos: ['https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=400&auto=format&fit=crop&q=80']
      });
      if (res.success) {
        setJob(res.data);
        setActionSuccess('Service completed! Job recorded for earnings calculation.');
      }
    } catch (err) {
      setActionError(err.message || 'Completion failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRequestCharge = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    try {
      const res = await jobApi.requestAdditionalCharge(job.id, chargeData);
      if (res.success) {
        setShowChargeModal(false);
        setActionSuccess('Additional charge sent to customer app for authorization.');
        fetchJob();
      }
    } catch (err) {
      setActionError(err.message || 'Charge request failed');
    } finally {
      setActionLoading(false);
    }
  };

  const toggleChecklist = (id) => {
    if (!job?.checklist) return;
    const updated = job.checklist.map(c => c.id === id ? { ...c, completed: !c.completed } : c);
    setJob({ ...job, checklist: updated });
  };

  if (loading) return <LoadingSpinner message="Loading operational job view..." />;
  if (!job) return <div className="p-6 text-center text-sm text-slate-500">Job not found.</div>;

  return (
    <div className="p-4 space-y-4 pb-28">
      {/* Top Bar */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate('/jobs')}
          className="flex items-center gap-1 text-xs font-bold text-slate-600 hover:text-slate-900"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>All Jobs</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setSosOpen(true)}
            className="p-2 rounded-full bg-red-50 text-red-600 border border-red-200"
            title="Safety SOS"
          >
            <ShieldAlert className="w-4 h-4" />
          </button>
          <StatusBadge status={job.status} type="job" size="md" />
        </div>
      </div>

      {/* Messages */}
      {actionSuccess && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs font-semibold flex items-center justify-between">
          <span>{actionSuccess}</span>
          <button onClick={() => setActionSuccess(null)} className="text-emerald-900 font-bold ml-2">✕</button>
        </div>
      )}

      {actionError && (
        <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 rounded-2xl text-xs font-semibold flex items-center justify-between">
          <span>{actionError}</span>
          <button onClick={() => setActionError(null)} className="text-rose-900 font-bold ml-2">✕</button>
        </div>
      )}

      {/* Job Card Title & Earnings */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm">
        <div className="flex justify-between items-start mb-2">
          <span className="text-xs font-mono font-bold text-slate-400">Ref: {job.id}</span>
          <span className="text-lg font-black text-emerald-600">₹{job.earnings}</span>
        </div>
        <h2 className="text-base font-black text-slate-900 leading-snug">{job.serviceTitle}</h2>
        <div className="flex items-center gap-3 text-xs text-slate-500 mt-2 font-medium">
          <span className="flex items-center gap-1">
            <Clock className="w-3.5 h-3.5 text-slate-400" />
            <span>{job.scheduledTime} ({job.estimatedDuration})</span>
          </span>
        </div>
      </div>

      {/* Customer & Location Details */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3.5">
        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
          <div>
            <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Customer</span>
            <h3 className="text-sm font-bold text-slate-800">{job.customerName}</h3>
          </div>
          <div className="flex items-center gap-2">
            <a
              href={`tel:${job.customerPhone}`}
              className="p-2.5 rounded-full bg-blue-50 text-blue-600 border border-blue-200 hover:bg-blue-100 transition"
              title="Masked Audio Call"
            >
              <Phone className="w-4 h-4" />
            </a>
            <button
              onClick={() => alert(`In-app masked chat open with ${job.customerName}`)}
              className="p-2.5 rounded-full bg-slate-100 text-slate-600 hover:bg-slate-200 transition"
              title="Chat"
            >
              <MessageSquare className="w-4 h-4" />
            </button>
          </div>
        </div>

        <div>
          <span className="text-[10px] uppercase font-bold text-slate-400 tracking-wider">Service Address</span>
          <p className="text-xs font-semibold text-slate-800 mt-0.5">{job.locality}</p>
          {job.landmark && (
            <p className="text-[11px] text-slate-500 mt-0.5">Landmark: {job.landmark}</p>
          )}
        </div>

        {/* Map & Navigation Launcher */}
        <div className="bg-slate-50 p-3 rounded-2xl border border-slate-200 flex items-center justify-between">
          <div className="flex items-center gap-2 text-xs text-slate-700">
            <Navigation className="w-4 h-4 text-blue-600" />
            <span>{job.distanceKm || '2.4'} km away • ETA ~12 mins</span>
          </div>
          <a
            href={`https://maps.google.com/?q=${encodeURIComponent(job.locality + ', Hyderabad')}`}
            target="_blank"
            rel="noopener noreferrer"
            className="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold shadow-sm transition active:scale-95"
          >
            Start GPS Map
          </a>
        </div>

        {/* Voice Note Player */}
        {job.hasVoiceNote && (
          <div className="bg-blue-50/80 border border-blue-200 p-3 rounded-2xl">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-1.5 text-xs font-bold text-blue-900">
                <Mic className="w-4 h-4 text-blue-600" />
                <span>Customer Voice Request</span>
              </div>
              <span className="text-[10px] font-mono font-semibold bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                {job.voiceDuration || '14s'}
              </span>
            </div>

            <div className="flex items-center gap-2.5 bg-white rounded-xl p-2.5 border border-blue-100 shadow-sm">
              <button
                onClick={() => setIsPlayingVoice(!isPlayingVoice)}
                className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-sm hover:bg-blue-700 transition active:scale-95 flex-shrink-0"
              >
                {isPlayingVoice ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4 ml-0.5" />}
              </button>

              <div className="flex-1">
                <div className="flex items-center justify-between text-[11px] text-slate-600 font-medium mb-1">
                  <span>{isPlayingVoice ? "Playing Voice Request..." : "Tap to Play Voice"}</span>
                  <span className="font-bold text-slate-800">{job.ratePerHour ? `₹${job.ratePerHour}/hr` : ''}</span>
                </div>
                <div className="w-full bg-slate-100 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="bg-blue-600 h-full transition-all duration-100"
                    style={{ width: `${voiceProgress}%` }}
                  />
                </div>
              </div>
            </div>
          </div>
        )}

        {job.specialInstructions && (
          <div className="bg-amber-50/70 border border-amber-200 p-3 rounded-2xl text-xs text-amber-900">
            <span className="font-bold block mb-0.5">Customer Instructions:</span>
            <p>{job.specialInstructions}</p>
          </div>
        )}
      </div>

      {/* Service Inspection Checklist */}
      {job.checklist && job.checklist.length > 0 && (
        <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm">
          <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500 mb-3">
            Service Quality Checklist
          </h3>
          <div className="space-y-2.5">
            {job.checklist.map(item => (
              <div
                key={item.id}
                onClick={() => toggleChecklist(item.id)}
                className="flex items-start gap-2.5 p-2 rounded-xl hover:bg-slate-50 cursor-pointer transition text-xs"
              >
                {item.completed ? (
                  <CheckSquare className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
                ) : (
                  <Square className="w-4 h-4 text-slate-400 flex-shrink-0 mt-0.5" />
                )}
                <span className={item.completed ? 'line-through text-slate-400 font-medium' : 'text-slate-700 font-medium'}>
                  {item.label}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Additional Charges Section */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
            Extra Work & Parts
          </h3>
          {[JobStatus.OTP_VERIFIED, JobStatus.SERVICE_STARTED].includes(job.status) && (
            <button
              onClick={() => setShowChargeModal(true)}
              className="text-xs text-blue-600 font-bold flex items-center gap-1 hover:underline"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Add Charge</span>
            </button>
          )}
        </div>

        {job.additionalCharges && job.additionalCharges.length > 0 ? (
          <div className="space-y-2">
            {job.additionalCharges.map(chg => (
              <div key={chg.id} className="p-3 bg-slate-50 rounded-2xl border border-slate-200 text-xs flex justify-between items-center">
                <div>
                  <h4 className="font-bold text-slate-800">{chg.title}</h4>
                  <p className="text-[11px] text-slate-500">{chg.reason}</p>
                </div>
                <div className="text-right">
                  <span className="font-bold text-slate-900 block">₹{chg.amount}</span>
                  <span className="text-[10px] text-amber-600 font-semibold">{chg.status}</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-xs text-slate-400">
            No extra parts or charges added. Workers cannot charge arbitrarily without customer authorization.
          </p>
        )}
      </div>

      {/* STICKY BOTTOM ACTION WORKFLOW (Authoritative Lifecycle Controls) */}
      <div className="fixed bottom-16 left-0 right-0 z-30 p-3 bg-white/95 backdrop-blur border-t border-slate-200 safe-bottom">
        <div className="max-w-lg mx-auto">
          {/* Step 1: Assigned / Accepted -> En Route */}
          {([JobStatus.ASSIGNED, JobStatus.ACCEPTED].includes(job.status)) && (
            <button
              onClick={handleStartEnRoute}
              disabled={actionLoading}
              className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl font-bold text-sm shadow-lg shadow-blue-500/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <Navigation className="w-4 h-4" />
              <span>{actionLoading ? 'Updating...' : "Start Navigation (I'm En Route)"}</span>
            </button>
          )}

          {/* Step 2: En Route -> Mark Arrived */}
          {job.status === JobStatus.EN_ROUTE && (
            <button
              onClick={handleMarkArrived}
              disabled={actionLoading}
              className="w-full py-3.5 bg-amber-500 hover:bg-amber-600 text-slate-900 rounded-2xl font-black text-sm shadow-lg shadow-amber-500/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <MapPin className="w-4 h-4" />
              <span>{actionLoading ? 'Recording...' : "I've Arrived at Customer Location"}</span>
            </button>
          )}

          {/* Step 3: Arrived -> Enter OTP */}
          {job.status === JobStatus.ARRIVED && (
            <button
              onClick={() => setShowOtpDialog(true)}
              className="w-full py-3.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-2xl font-bold text-sm shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Enter Customer 4-Digit OTP</span>
            </button>
          )}

          {/* Step 4: OTP Verified -> Start Service */}
          {job.status === JobStatus.OTP_VERIFIED && (
            <button
              onClick={handleStartService}
              disabled={actionLoading}
              className="w-full py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl font-bold text-sm shadow-lg shadow-emerald-600/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <Clock className="w-4 h-4" />
              <span>{actionLoading ? 'Starting...' : 'Start Service Now'}</span>
            </button>
          )}

          {/* Step 5: Service Started -> Complete Service */}
          {job.status === JobStatus.SERVICE_STARTED && (
            <button
              onClick={handleCompleteService}
              disabled={actionLoading}
              className="w-full py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl font-black text-sm shadow-lg shadow-emerald-600/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <CheckCircle2 className="w-5 h-5" />
              <span>{actionLoading ? 'Finalizing...' : 'Complete Service (Upload Proof & Finish)'}</span>
            </button>
          )}

          {/* Completed State */}
          {job.status === JobStatus.COMPLETED && (
            <div className="w-full py-3 bg-emerald-50 border border-emerald-300 rounded-2xl text-center text-xs font-bold text-emerald-800 flex items-center justify-center gap-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
              <span>Service Finished & Payment Settled</span>
            </div>
          )}
        </div>
      </div>

      {/* OTP DIALOG MODAL */}
      {showOtpDialog && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 w-full max-w-sm shadow-2xl animate-in zoom-in-95">
            <h3 className="text-base font-black text-slate-900 mb-1">Verify Customer Arrival OTP</h3>
            <p className="text-xs text-slate-500 mb-4 leading-relaxed">
              Ask the customer for the 4-digit security code shown in their HomeHelp App. (Demo OTP for testing: <span className="font-mono font-bold text-blue-600">{job.otp || '4829'}</span>)
            </p>

            <form onSubmit={handleVerifyOtp} className="space-y-4">
              <input
                type="text"
                maxLength={4}
                autoFocus
                required
                value={otpInput}
                onChange={(e) => setOtpInput(e.target.value.replace(/\D/g, ''))}
                placeholder="4-digit code"
                className="w-full text-center tracking-[0.5em] font-mono text-2xl font-bold py-3 bg-slate-50 border border-slate-300 rounded-2xl focus:ring-2 focus:ring-blue-500 focus:outline-none"
              />

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setShowOtpDialog(false)}
                  className="flex-1 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={actionLoading || otpInput.length < 4}
                  className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold shadow-md shadow-blue-500/20 transition active:scale-95 disabled:opacity-50"
                >
                  {actionLoading ? 'Verifying...' : 'Verify OTP'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ADDITIONAL CHARGE MODAL */}
      {showChargeModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl p-6 w-full max-w-sm shadow-2xl animate-in zoom-in-95">
            <h3 className="text-base font-black text-slate-900 mb-1">Request Extra Charge</h3>
            <p className="text-xs text-slate-500 mb-4">
              Requires customer in-app approval before adding to the final bill.
            </p>

            <form onSubmit={handleRequestCharge} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Item / Work Description</label>
                <input
                  type="text"
                  required
                  value={chargeData.title}
                  onChange={(e) => setChargeData({ ...chargeData, title: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Additional Cost (₹)</label>
                <input
                  type="number"
                  required
                  value={chargeData.amount}
                  onChange={(e) => setChargeData({ ...chargeData, amount: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-bold font-mono"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 mb-1">Reason for Extra Charge</label>
                <textarea
                  rows={2}
                  required
                  value={chargeData.reason}
                  onChange={(e) => setChargeData({ ...chargeData, reason: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowChargeModal(false)}
                  className="flex-1 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={actionLoading}
                  className="flex-1 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold"
                >
                  Send for Approval
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* SOS Modal */}
      <SosModal isOpen={sosOpen} onClose={() => setSosOpen(false)} activeJob={job} />
    </div>
  );
};
export default JobDetail;
