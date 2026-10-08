import React, { useState } from 'react';
import { AlertTriangle, PhoneCall, ShieldAlert, X, CheckCircle2 } from 'lucide-react';
import { supportApi } from '../../api/supportApi';

export const SosModal = ({ isOpen, onClose, activeJob = null }) => {
  const [submitting, setSubmitting] = useState(false);
  const [dispatched, setDispatched] = useState(null);
  const [reason, setReason] = useState('Safety Concern / Threat');

  if (!isOpen) return null;

  const handleTriggerSos = async () => {
    setSubmitting(true);
    try {
      const payload = {
        reason,
        jobReference: activeJob?.id || 'NO_ACTIVE_JOB',
        customerArea: activeJob?.locality || 'Unknown Area',
        timestamp: new Date().toISOString(),
        location: {
          lat: 17.4483,
          lng: 78.3915
        }
      };
      const res = await supportApi.triggerSos(payload);
      if (res.success) {
        setDispatched(res.data);
      }
    } catch (err) {
      console.error('SOS dispatch error', err);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-4">
      <div className="bg-white rounded-3xl w-full max-w-md p-6 shadow-2xl border border-red-100 animate-in fade-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between pb-3 border-b border-slate-100">
          <div className="flex items-center gap-2.5 text-red-600 font-bold text-lg">
            <ShieldAlert className="w-6 h-6 animate-pulse" />
            <span>Worker Emergency SOS</span>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-slate-600 hover:bg-slate-100"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {dispatched ? (
          <div className="py-6 text-center">
            <div className="w-16 h-16 bg-red-50 text-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
              <CheckCircle2 className="w-9 h-9" />
            </div>
            <h4 className="text-xl font-bold text-slate-900 mb-1">Emergency Team Alerted</h4>
            <p className="text-sm text-slate-600 mb-4">
              Incident Ref: <span className="font-mono font-semibold text-red-600">{dispatched.incidentId}</span>
            </p>
            <div className="p-4 bg-red-50 rounded-2xl border border-red-100 text-left mb-6">
              <p className="text-xs text-red-800 font-medium leading-relaxed">
                A dedicated HomeHelp safety supervisor is calling you immediately. Your current coordinates and active booking context have been shared with local platform managers.
              </p>
            </div>
            <a
              href={`tel:${dispatched.safetyHelpline}`}
              className="w-full flex items-center justify-center gap-2 bg-red-600 text-white font-bold py-3.5 px-4 rounded-xl shadow-lg shadow-red-200 hover:bg-red-700 active:scale-95 transition"
            >
              <PhoneCall className="w-5 h-5" />
              <span>Call Rapid Safety: {dispatched.safetyHelpline}</span>
            </a>
          </div>
        ) : (
          <div className="py-4">
            <p className="text-sm text-slate-600 mb-4">
              Use this if you feel unsafe, face verbal/physical abuse, or are in immediate danger.
            </p>

            {activeJob && (
              <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200 text-xs text-slate-600 mb-4">
                <span className="font-semibold text-slate-800">Linked Job:</span> {activeJob.id} - {activeJob.serviceTitle}
                <div className="truncate text-slate-500">{activeJob.locality}</div>
              </div>
            )}

            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
              Incident Type
            </label>
            <select
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-medium text-slate-800 mb-6 focus:ring-2 focus:ring-red-500 focus:outline-none"
            >
              <option value="Safety Concern / Threat">Physical Threat or Hostile Situation</option>
              <option value="Harassment / Verbal Abuse">Customer Misbehavior or Harassment</option>
              <option value="Medical Emergency">Worker Health Emergency / Injury</option>
              <option value="Unsafe Premises">Unsafe Property or Hazardous Conditions</option>
            </select>

            <div className="space-y-3">
              <button
                onClick={handleTriggerSos}
                disabled={submitting}
                className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-3.5 rounded-xl shadow-lg shadow-red-200 flex items-center justify-center gap-2 transition active:scale-95"
              >
                <AlertTriangle className="w-5 h-5" />
                <span>{submitting ? 'Triggering Emergency...' : 'Trigger Safety Alert Now'}</span>
              </button>
              <button
                onClick={onClose}
                className="w-full bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold py-3 rounded-xl text-sm transition"
              >
                Cancel
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
export default SosModal;
