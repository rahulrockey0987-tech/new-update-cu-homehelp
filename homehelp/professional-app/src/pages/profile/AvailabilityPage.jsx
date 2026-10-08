import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { professionalApi } from '../../api/professionalApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { ArrowLeft, Clock, Calendar, Check, Save } from 'lucide-react';

export const AvailabilityPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);

  const [availability, setAvailability] = useState({
    days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    startTime: '08:30',
    endTime: '19:00',
    breakStartTime: '13:00',
    breakEndTime: '14:00'
  });

  const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  useEffect(() => {
    const fetchAvail = async () => {
      try {
        setLoading(true);
        const res = await professionalApi.getAvailability();
        if (res.success && res.data?.days) {
          setAvailability(res.data);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAvail();
  }, []);

  const toggleDay = (d) => {
    setAvailability(prev => ({
      ...prev,
      days: prev.days.includes(d) ? prev.days.filter(item => item !== d) : [...prev.days, d]
    }));
  };

  const handleSave = async () => {
    setSaving(true);
    setSuccess(false);
    try {
      const res = await professionalApi.updateAvailability(availability);
      if (res.success) {
        setSuccess(true);
        setTimeout(() => setSuccess(false), 3000);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading availability..." />;

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
        <h1 className="text-xl font-black text-slate-900 tracking-tight">Availability & Shifts</h1>
        <p className="text-xs text-slate-500">Configure your active working calendar and hours</p>
      </div>

      {success && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs font-semibold flex items-center gap-2">
          <Check className="w-4 h-4 text-emerald-600" />
          <span>Availability schedule saved! Job dispatch system will match you accordingly.</span>
        </div>
      )}

      {/* Days Selection */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-3">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Working Days in Week
        </h3>
        <div className="grid grid-cols-4 gap-2">
          {daysOfWeek.map(d => {
            const active = availability.days.includes(d);
            return (
              <button
                key={d}
                type="button"
                onClick={() => toggleDay(d)}
                className={`py-3 rounded-2xl text-xs font-bold border transition ${
                  active
                    ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                    : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                }`}
              >
                {d.slice(0, 3)}
              </button>
            );
          })}
        </div>
      </div>

      {/* Shift Timing */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm space-y-4">
        <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-500">
          Shift Working Hours
        </h3>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Shift Start</label>
            <input
              type="time"
              value={availability.startTime}
              onChange={(e) => setAvailability({ ...availability, startTime: e.target.value })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-semibold"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Shift End</label>
            <input
              type="time"
              value={availability.endTime}
              onChange={(e) => setAvailability({ ...availability, endTime: e.target.value })}
              className="w-full p-3 bg-slate-50 border border-slate-300 rounded-xl text-sm font-semibold"
            />
          </div>
        </div>

        <div className="pt-2 border-t border-slate-100">
          <span className="text-[11px] font-bold text-slate-600 block mb-2">Rest / Lunch Break</span>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-[11px] text-slate-500 mb-1">Break Start</label>
              <input
                type="time"
                value={availability.breakStartTime}
                onChange={(e) => setAvailability({ ...availability, breakStartTime: e.target.value })}
                className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
              />
            </div>
            <div>
              <label className="block text-[11px] text-slate-500 mb-1">Break End</label>
              <input
                type="time"
                value={availability.breakEndTime}
                onChange={(e) => setAvailability({ ...availability, breakEndTime: e.target.value })}
                className="w-full p-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs"
              />
            </div>
          </div>
        </div>
      </div>

      <button
        onClick={handleSave}
        disabled={saving}
        className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl text-xs font-bold flex items-center justify-center gap-2 shadow-md shadow-blue-500/20 transition active:scale-95"
      >
        <Save className="w-4 h-4" />
        <span>{saving ? 'Saving...' : 'Save Shift Settings'}</span>
      </button>
    </div>
  );
};
export default AvailabilityPage;
