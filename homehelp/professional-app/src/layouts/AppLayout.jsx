import React, { useState, useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Header from '../components/common/Header';
import BottomNav from '../components/common/BottomNav';
import { jobApi } from '../api/jobApi';
import { notificationApi } from '../api/notificationApi';
import { JobStatus } from '../constants/status';

export const AppLayout = () => {
  const [activeJobCount, setActiveJobCount] = useState(0);
  const [unreadNotifs, setUnreadNotifs] = useState(0);
  const location = useLocation();

  useEffect(() => {
    const fetchCounters = async () => {
      try {
        const [jobsRes, notifsRes] = await Promise.all([
          jobApi.getJobs(),
          notificationApi.getNotifications()
        ]);
        if (jobsRes.success && jobsRes.data) {
          const ongoing = jobsRes.data.filter(j => 
            [JobStatus.ASSIGNED, JobStatus.ACCEPTED, JobStatus.EN_ROUTE, JobStatus.ARRIVED, JobStatus.OTP_VERIFIED, JobStatus.SERVICE_STARTED].includes(j.status)
          );
          setActiveJobCount(ongoing.length);
        }
        if (notifsRes.success && notifsRes.data) {
          setUnreadNotifs(notifsRes.data.filter(n => !n.read).length);
        }
      } catch (e) {
        // Soft fail
      }
    };

    fetchCounters();
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-slate-100 flex justify-center text-slate-900">
      {/* Mobile-sized shell centered on wider screens */}
      <div className="w-full max-w-lg bg-slate-50 min-h-screen flex flex-col shadow-2xl relative border-x border-slate-200">
        <Header unreadNotificationsCount={unreadNotifs} />

        <main className="flex-1 pb-20 overflow-y-auto">
          <Outlet />
        </main>

        <BottomNav activeJobCount={activeJobCount} />
      </div>
    </div>
  );
};
export default AppLayout;
