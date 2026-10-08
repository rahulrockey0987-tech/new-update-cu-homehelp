import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import StatusBadge from '../../components/common/StatusBadge';
import {
  User, ShieldCheck, Clock, MapPin, Building, Headphones,
  LogOut, ChevronRight, Star, Award, Briefcase, Settings
} from 'lucide-react';

export const Profile = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    if (confirm('Are you sure you want to sign out of HomeHelp Professional?')) {
      await logout();
      navigate('/login');
    }
  };

  const menuSections = [
    {
      title: 'Compliance & Verification',
      items: [
        { label: 'KYC & Government Documents', icon: ShieldCheck, path: '/kyc', badge: user?.kycStatus },
        { label: 'Bank Account & Payouts', icon: Building, path: '/profile/bank' },
      ]
    },
    {
      title: 'Operations & Availability',
      items: [
        { label: 'Working Days & Shift Hours', icon: Clock, path: '/profile/availability' },
        { label: 'Hyderabad Service Zones', icon: MapPin, path: '/profile/service-areas', count: `${user?.serviceAreas?.length || 0} zones` },
      ]
    },
    {
      title: 'Partner Support',
      items: [
        { label: 'Help Center & Safety Escalations', icon: Headphones, path: '/support' },
      ]
    }
  ];

  return (
    <div className="p-4 space-y-4 pb-24">
      {/* Header Card */}
      <div className="bg-white rounded-3xl p-5 border border-slate-200 shadow-sm">
        <div className="flex items-center gap-4">
          <img
            src={user?.profilePhoto || 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150&auto=format&fit=crop&q=80'}
            alt="Partner avatar"
            className="w-16 h-16 rounded-2xl object-cover ring-2 ring-blue-500 shadow-sm"
          />
          <div className="flex-1 min-w-0">
            <h2 className="text-lg font-black text-slate-900 truncate">{user?.fullName}</h2>
            <p className="text-xs text-slate-500 truncate">{user?.phone} • {user?.city}</p>
            <div className="mt-1.5 flex items-center gap-2">
              <StatusBadge status={user?.status} type="worker" />
              <div className="flex items-center gap-1 text-xs text-amber-600 font-bold bg-amber-50 px-2 py-0.5 rounded-full border border-amber-200">
                <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-500" />
                <span>{user?.rating || 4.9}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Career Stats */}
        <div className="grid grid-cols-3 gap-2 mt-5 pt-4 border-t border-slate-100 text-center">
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[10px] text-slate-400 font-bold uppercase">Completed</span>
            <p className="text-base font-black text-slate-800 mt-0.5">{user?.totalJobsCompleted || 0}</p>
          </div>
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[10px] text-slate-400 font-bold uppercase">Experience</span>
            <p className="text-base font-black text-slate-800 mt-0.5">{user?.experienceYears || 1} yrs</p>
          </div>
          <div className="p-2 rounded-2xl bg-slate-50">
            <span className="text-[10px] text-slate-400 font-bold uppercase">Acceptance</span>
            <p className="text-base font-black text-emerald-600 mt-0.5">{user?.acceptanceRate || 95}%</p>
          </div>
        </div>
      </div>

      {/* Menu Sections */}
      {menuSections.map((section, idx) => (
        <div key={idx} className="space-y-2">
          <span className="text-[11px] font-extrabold uppercase tracking-wider text-slate-400 px-1">
            {section.title}
          </span>
          <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden divide-y divide-slate-100">
            {section.items.map((item, itemIdx) => {
              const Icon = item.icon;
              return (
                <div
                  key={itemIdx}
                  onClick={() => navigate(item.path)}
                  className="p-4 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition active:bg-slate-100"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
                      <Icon className="w-4 h-4" />
                    </div>
                    <span className="text-xs font-bold text-slate-800">{item.label}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    {item.badge && <StatusBadge status={item.badge} type="kyc" size="sm" />}
                    {item.count && (
                      <span className="text-xs text-slate-400 font-medium">{item.count}</span>
                    )}
                    <ChevronRight className="w-4 h-4 text-slate-300" />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      ))}

      {/* Sign Out Button */}
      <button
        onClick={handleLogout}
        className="w-full py-3.5 bg-rose-50 hover:bg-rose-100 text-rose-700 rounded-2xl border border-rose-200 text-xs font-bold flex items-center justify-center gap-2 transition active:scale-95"
      >
        <LogOut className="w-4 h-4" />
        <span>Sign Out of Partner Portal</span>
      </button>

      <div className="text-center pt-2">
        <span className="text-[11px] text-slate-400">HomeHelp Professional v1.2.0 • Build Hyderabad</span>
      </div>
    </div>
  );
};
export default Profile;
