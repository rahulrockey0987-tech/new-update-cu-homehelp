import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Bell, ShieldAlert, Power, AlertCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import SosModal from './SosModal';

export const Header = ({ unreadNotificationsCount = 0 }) => {
  const { user, isVerifiedWorker, toggleOnline } = useAuth();
  const [sosOpen, setSosOpen] = useState(false);
  const [toggleLoading, setToggleLoading] = useState(false);
  const [toggleError, setToggleError] = useState(null);
  const navigate = useNavigate();

  const handleToggle = async () => {
    if (!isVerifiedWorker) {
      setToggleError('Only verified professionals can go online.');
      setTimeout(() => setToggleError(null), 3500);
      return;
    }
    setToggleLoading(true);
    setToggleError(null);
    const res = await toggleOnline();
    if (!res?.success && res?.error) {
      setToggleError(res.error);
      setTimeout(() => setToggleError(null), 4000);
    }
    setToggleLoading(false);
  };

  const isOnline = user?.isOnline;

  return (
    <>
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-slate-200">
        <div className="max-w-lg mx-auto px-4 h-16 flex items-center justify-between">
          {/* Logo & Worker ID */}
          <div className="flex items-center gap-2.5 cursor-pointer" onClick={() => navigate('/dashboard')}>
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-700 to-blue-500 text-white flex items-center justify-center font-black text-lg shadow-md shadow-blue-200">
              H
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-extrabold text-base tracking-tight text-slate-900">HomeHelp</span>
                <span className="text-[10px] uppercase font-bold tracking-wider px-1.5 py-0.5 rounded bg-blue-100 text-blue-700">PRO</span>
              </div>
              <p className="text-[11px] text-slate-500 font-medium">
                {user?.fullName?.split(' ')[0] || 'Worker'} • {user?.city || 'Hyderabad'}
              </p>
            </div>
          </div>

          {/* Quick Actions: Online Toggle, SOS, Notifications */}
          <div className="flex items-center gap-2">
            {/* Online / Offline switch */}
            <button
              onClick={handleToggle}
              disabled={toggleLoading}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full border text-xs font-bold transition-all shadow-sm active:scale-95 ${
                isOnline
                  ? 'bg-emerald-50 border-emerald-300 text-emerald-700 hover:bg-emerald-100'
                  : 'bg-slate-100 border-slate-300 text-slate-600 hover:bg-slate-200'
              }`}
              title={isOnline ? 'Online - Receiving jobs' : 'Offline - Unavailable for new jobs'}
            >
              <span className={`w-2 h-2 rounded-full ${isOnline ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'}`} />
              <span>{isOnline ? 'Online' : 'Offline'}</span>
            </button>

            {/* Emergency SOS button */}
            <button
              onClick={() => setSosOpen(true)}
              className="p-2 rounded-full bg-red-50 text-red-600 border border-red-200 hover:bg-red-100 active:scale-90 transition relative"
              title="Emergency Safety Protocol"
            >
              <ShieldAlert className="w-5 h-5" />
            </button>

            {/* Notifications */}
            <button
              onClick={() => navigate('/notifications')}
              className="p-2 rounded-full text-slate-600 hover:text-slate-900 hover:bg-slate-100 active:scale-90 transition relative"
              title="Notifications"
            >
              <Bell className="w-5 h-5" />
              {unreadNotificationsCount > 0 && (
                <span className="absolute top-1.5 right-1.5 w-2.5 h-2.5 rounded-full bg-blue-600 ring-2 ring-white" />
              )}
            </button>
          </div>
        </div>

        {/* Floating warning banner if verification is needed to go online */}
        {toggleError && (
          <div className="bg-amber-50 border-b border-amber-200 px-4 py-2 flex items-center justify-between text-xs text-amber-800">
            <div className="flex items-center gap-1.5">
              <AlertCircle className="w-4 h-4 text-amber-600 flex-shrink-0" />
              <span>{toggleError}</span>
            </div>
            <button
              onClick={() => navigate('/kyc')}
              className="font-bold underline text-amber-900 ml-2"
            >
              Complete KYC
            </button>
          </div>
        )}
      </header>

      {/* SOS Modal */}
      <SosModal isOpen={sosOpen} onClose={() => setSosOpen(false)} />
    </>
  );
};
export default Header;
